package com.one.cbsl.ui.attendance.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.one.cbsl.CbslMain
import com.one.cbsl.R
import com.one.cbsl.databinding.FragmentHomeBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.home.adapter.HomeAdapter
import com.one.cbsl.ui.attendance.home.viewmodel.HomeViewModel
import com.one.cbsl.ui.attendance.other.LoginActivity
import com.one.cbsl.ui.complain.adapter.ComplaintMainAdapter
import com.one.cbsl.ui.complain.viewmodel.ComplaintViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager

class HomeFragment : Fragment(), HomeAdapter.OpitionListener, ComplaintMainAdapter.OpitionListener {

    private var _binding: FragmentHomeBinding? = null
    private var homeViewModel: HomeViewModel? = null
    private var complaintViewModel: ComplaintViewModel? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        YoYo.with(Techniques.FadeIn).repeat(YoYo.INFINITE).delay(1500).playOn(_binding?.ivPunch)
        bindView()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (SessionManager.getInstance().getString(Constants.COMPANY) == "SOLAR"
            || SessionManager.getInstance().getString(Constants.COMPANY) == "CBM"
            || SessionManager.getInstance().getString(Constants.COMPANY) == "CBMPL"
            || SessionManager.getInstance().getString(Constants.COMPANY) == "CSSPL"
        ) {
            loadComplaintDashboard()
        } else {
            binding.llComplaintDashboard.visibility = View.GONE
        }

        loadTodayAttendance()
        loadDashboard()
        checkDeviceRegistered()
        _binding?.ivPunch?.setOnClickListener {
            if (binding.tvTodayDate.text.equals(Constants.getTodayData())) {
                SessionManager.getInstance().putBoolean(Constants.isPunchIn, false)
            }
            findNavController().navigate(R.id.navigate_to_mark_attendance)

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as CbslMain?)!!.setDrawerLocked(false)
        (activity as CbslMain?)!!.updateValues()
    }

    private fun loadDashboard() {
        homeViewModel?.getDashboardData()?.observe(requireActivity(), Observer { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    try {
                        val response = resource.data
                        if (resource.data != null) {
                            if (response?.get(0)?.IsPassUpdated == 0) {
                                //                loadPdf()
                                findNavController().navigate(R.id.home_to_change_pass)
                            }

                            if (response?.get(0)?.IsProfileUpdated == "0") {
                                //loadPdf()
                                findNavController().navigate(R.id.home_to_profile_change)
                            } else {
                                SessionManager.getInstance()
                                    .putString(Constants.IMAGE, response?.get(0)?.IsProfileUpdated)
                                (activity as CbslMain?)!!.updateValues()
                            }
                            binding.rvDashboard.adapter =
                                HomeAdapter(
                                    response?.get(0)!!, this
                                )
                            SessionManager.getInstance()
                                .putString(Constants.IsTourActive, response[0].onTour!!)

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Resource.Error -> {}
            }
        })
    }

    private fun loadComplaintDashboard() {
        DialogUtils.showProgressDialog(requireActivity(), "Loading...")
        complaintViewModel?.getComplaintDashboardData()
            ?.observe(requireActivity(), Observer { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Loading...")
                    }

                    is Resource.Success -> {
                        try {
                            DialogUtils.dismissDialog()
                            val response = resource.data
                            if (response?.get(0)?.Pending != null) {
                                binding.rvComplaintDashboard.adapter =
                                    ComplaintMainAdapter(
                                        response?.get(0)!!, this
                                    )
                                binding.llComplaintDashboard.visibility = View.VISIBLE
                                SessionManager.getInstance()
                                    .putString(
                                        Constants.COMPLAINT_USERID,
                                        response[0].CRMUsersId.toString()
                                    )
                                SessionManager.getInstance()
                                    .putString(Constants.GROUP_ID, response[0].GroupTypeId)

                            } else {
                                binding.llComplaintDashboard.visibility = View.GONE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    is Resource.Error -> {
                        DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                    }
                }
            })
    }

    private fun checkDeviceRegistered() {
        DialogUtils.showProgressDialog(requireActivity(), "Loading...")
        homeViewModel?.checkDevice()
            ?.observe(requireActivity(), Observer { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Loading...")


                    }

                    is Resource.Success -> {
                        try {
                            try {
                                val response = resource.data
                                if (response?.get(0)?.Status.toString() == "Not Found") {
                                    SessionManager.getInstance()
                                        .putBoolean(Constants.isLoginByDevice, false)
                                    Toast.makeText(
                                        activity,
                                        "Device Id changed!! Login Again",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    SessionManager.getInstance().resetData()
                                    (activity as CbslMain?)!!.updateValues()
                                } else {
                                    DialogUtils.showFailedDialog(
                                        requireActivity(),
                                        response?.get(0)?.Status.toString()
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            DialogUtils.dismissDialog()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    is Resource.Error -> {
                        DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                    }
                }
            })
    }


    private fun loadTodayAttendance() {
        homeViewModel?.getTodayAttendance()?.observe(requireActivity(), Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Data")
                }

                is Resource.Success -> {
                    try {
                        val response = resource.data
                        _binding?.tvCheckInTime?.text = response?.get(0)?.PunchIn ?: "--:--"
                        _binding?.tvCheckOutTime?.text = response?.get(0)?.PunchOut ?: "--:--"
                        _binding?.tvTodayDate?.text =
                            response?.get(0)?.PunchDate ?: Constants.getTodayData()
                        _binding?.tvWorkingHour?.text = response?.get(0)?.WorkingHours ?: "--:--"
                        DialogUtils.dismissDialog()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Resource.Error -> {
                    // Handle error
                    DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                }
            }
        })
    }

    @SuppressLint("HardwareIds")
    private fun bindView() {
        homeViewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory(NetworkApiHelper(RetrofitBuilder.apiService))
        )[HomeViewModel::class.java]

        complaintViewModel =
            if (SessionManager.getInstance().getString(Constants.COMPANY) == "SOLAR") {
                ViewModelProvider(
                    requireActivity(),
                    ViewModelFactory(NetworkApiHelper(RetrofitBuilder.solarApiService))
                )[ComplaintViewModel::class.java]
            } else {
                ViewModelProvider(
                    requireActivity(),
                    ViewModelFactory(NetworkApiHelper(RetrofitBuilder.bmdApiService))
                )[ComplaintViewModel::class.java]
            }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDashboardClick(item: String) {
        when (item) {
            Constants.Attendance -> {
                findNavController().navigate(R.id.home_to_attendance)
            }

            Constants.Movement -> {
                findNavController().navigate(R.id.navigate_to_my_movement)
            }

            Constants.Conveyance -> {
                findNavController().navigate(R.id.navigate_to_my_conveyance)
            }

            Constants.LeavePlan -> {
                findNavController().navigate(R.id.home_to_leave_fragment)
            }

            Constants.Voucher -> {
                findNavController().navigate(R.id.home_to_voucher_fragment)
            }

            Constants.Complaint -> {
                findNavController().navigate(R.id.home_to_complain_fragment)
            }

            Constants.PendingComplaint -> {
                findNavController().navigate(R.id.home_to_pending_complain_fragment)
            }

            Constants.PendingPmr -> {
                findNavController().navigate(R.id.home_to_pending_pmr_fragment)
            }

            Constants.PendingInstallation -> {
                findNavController().navigate(R.id.home_to_pending_install_fragment)
            }

            Constants.CloseComplaint -> {
                findNavController().navigate(R.id.home_to_complete_complaint_fragment)
            }

            Constants.ApprovalHod -> {
                findNavController().navigate(R.id.home_to_conveyance_hod_approval)
            }
        }
    }

}
