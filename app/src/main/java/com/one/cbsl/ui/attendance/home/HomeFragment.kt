package com.one.cbsl.ui.attendance.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.one.cbsl.ui.complain.adapter.ComplaintMainAdapter
import com.one.cbsl.ui.complain.viewmodel.ComplaintViewModel
import com.one.cbsl.utils.Cbsl
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.Constants.Companion.isDeveloperModeEnabled
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager
import com.one.cbsl.utils.permissions.PermissionRequest
import com.one.cbsl.utils.permissions.PermissionRequestHandler
import kotlin.system.exitProcess

class HomeFragment : Fragment(), HomeAdapter.OpitionListener,
    PermissionRequest.RequestCustomPermissionGroup, ComplaintMainAdapter.OpitionListener {

    private var _binding: FragmentHomeBinding? = null
    private var homeViewModel: HomeViewModel? = null
    private var complaintViewModel: ComplaintViewModel? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        YoYo.with(Techniques.FadeIn).repeat(YoYo.INFINITE).delay(1500).playOn(_binding?.ivPunch)
        bindView()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isDeveloperModeEnabled(Cbsl.getInstance())) {
            showDevModeDialog()
        }
        else {
            loadDashboard()
        }
        if (SessionManager.getInstance()
                .getString(Constants.COMPANY) == "SOLAR" || SessionManager.getInstance()
                .getString(Constants.COMPANY) == "CBM" || SessionManager.getInstance()
                .getString(Constants.COMPANY) == "CBMPL" || SessionManager.getInstance()
                .getString(Constants.COMPANY) == "CSSPL" || SessionManager.getInstance()
                .getString(Constants.COMPANY) == "CBSPL"
        ){
            loadComplaintDashboard()
        } else {
            binding.llComplaintDashboard.visibility = View.GONE
        }
        checkAllPermission()
        loadTodayAttendance()
        checkDeviceRegistered()
        _binding?.ivPunch?.setOnClickListener {
            if (binding.tvTodayDate.text.equals(Constants.getTodayData())) {
                SessionManager.getInstance().putBoolean(Constants.isPunchIn, false)
            } else {
                SessionManager.getInstance().putBoolean(Constants.isPunchIn, true)
            }
            //startActivity(Intent(activity,FaceRecognizeActivity::class.java))
            if (SessionManager.getInstance().getString(Constants.faceEnabled) == "0") {
                findNavController().navigate(R.id.navigate_to_mark_attendance)
            } else {
                findNavController().navigate(R.id.home_to_face_capture)
            }
            //findNavController().navigate(R.id.home_to_face_capture)
            //findNavController().navigate(R.id.navigate_to_face_webview)


        }

    }

    private fun checkAllPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionRequestHandler.requestCustomPermissionGroup(
                this,
                "",
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA
            )
        } else {
            PermissionRequestHandler.requestCustomPermissionGroup(
                this,
                "",
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA

            )
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
                                findNavController().navigate(R.id.home_to_change_pass)
                            }
                            binding.rvDashboard.adapter = HomeAdapter(
                                response?.get(0)!!, this
                            )
                            SessionManager.getInstance()
                                .putString(Constants.faceEnabled, response[0].faceEnabled)
                            SessionManager.getInstance()
                                .putString(Constants.VerifyType, response[0].faceData)

                            SessionManager.getInstance()
                                .putString(Constants.faceDomain, response[0].faceDomain)

                            if (response[0].faceEnabled == "1") {
                                if (response[0].faceData == "0") {

                                    findNavController().navigate(R.id.home_to_face_capture)

                                } else {
                                    SessionManager.getInstance()
                                        .putString(Constants.VerifyType, response[0].faceData)/*  SessionManager.getInstance()
                                      .putString("embed", response[0]?.faceData)*/
                                }
                            }
                            if (response[0].IsProfileUpdated == "0") {
                                //loadPdf()
                                findNavController().navigate(R.id.home_to_profile_change)
                            } else {
                                SessionManager.getInstance()
                                    .putString(Constants.IMAGE, response[0].IsProfileUpdated)
                                (activity as CbslMain?)!!.updateValues()
                            }

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
        complaintViewModel?.getComplaintDashboardData()
            ?.observe(requireActivity(), Observer { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Log.d("loading", "")
                    }

                    is Resource.Success -> {
                        try {
                            val response = resource.data
                            if (response?.get(0)?.Pending != null) {
                                binding.rvComplaintDashboard.adapter = ComplaintMainAdapter(
                                    response?.get(0)!!, this
                                )
                                binding.llComplaintDashboard.visibility = View.VISIBLE
                                SessionManager.getInstance().putString(
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
                        Log.d("", "")
                    }
                }
            })
    }

    private fun checkDeviceRegistered() {
        homeViewModel?.checkDevice()?.observe(requireActivity(), Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Log.d("loading", "")
                }

                is Resource.Success -> {
                    try {
                        val response = resource.data
                        if (response?.get(0)?.Status.toString() == "Not Found") {
                            SessionManager.getInstance()
                                .putBoolean(Constants.isLoginByDevice, false)
                            Toast.makeText(
                                activity, "Device Id changed!! Login Again", Toast.LENGTH_LONG
                            ).show()
                            SessionManager.getInstance().resetData()
                            (activity as CbslMain?)!!.updateValues()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                is Resource.Error -> {
                    Log.d("error", "")

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
                    if (resource.message.toString()
                            .contains("timeout") ||resource.message.toString()
                            .contains("Cancel") || resource.message.toString().contains("Error:")
                    ) {
                        val status =
                            !SessionManager.getInstance().getBoolean(Constants.IsChangeServer)
                        SessionManager.getInstance().putBoolean(Constants.IsChangeServer, status)

                        startActivity(Intent(activity, CbslMain::class.java))

                    }
                }
            }
        })
    }

    @SuppressLint("HardwareIds")
    private fun bindView() {
        homeViewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory(NetworkApiHelper(RetrofitBuilder.getApi()))
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

            Constants.PAYHISTORY -> {
                findNavController().navigate(R.id.navigation_to_pay_history)
            }

            Constants.LeavePlan -> {
                if (SessionManager.getInstance().getString(Constants.UserTypeID) != "2") {
                    findNavController().navigate(R.id.track_leave_plan_admin)
                } else {
                    findNavController().navigate(R.id.home_to_leave_fragment)
                }
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

    override fun onAllCustomPermissionGroupGranted() {
        Log.d("", "")
    }

    override fun onCustomPermissionGroupDenied() {
        checkAllPermission()
    }
    private fun showDevModeDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dev_mode_title))
            .setMessage(getString(R.string.dev_mode_message))
            .setCancelable(false) // can't dismiss by tapping outside or back
            .setPositiveButton(getString(R.string.dev_mode_exit)) { _, _ ->
                // exit the app (removes all activities)
                exitProcess(0);
            }
            .create()

        // Prevent back key dismiss on older devices
        dialog.setOnKeyListener { _, keyCode, _ ->
            // return true to consume event; but setCancelable(false) is usually enough
            true
        }

        dialog.show()
    }
}
