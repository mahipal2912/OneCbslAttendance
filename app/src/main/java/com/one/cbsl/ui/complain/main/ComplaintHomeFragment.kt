package com.one.cbsl.ui.complain.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.one.cbsl.CbslMain
import com.one.cbsl.R
import com.one.cbsl.databinding.FragmentComplaintHomeBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.complain.adapter.ComplaintMainAdapter
import com.one.cbsl.ui.complain.viewmodel.ComplaintViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager

class ComplaintHomeFragment : Fragment(), ComplaintMainAdapter.OpitionListener {

    private var _binding: FragmentComplaintHomeBinding? = null
    private var viewModel: ComplaintViewModel? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentComplaintHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        bindView()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadComplaintDashboard()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as CbslMain?)!!.setDrawerLocked(false)
        (activity as CbslMain?)!!.updateValues()
    }

    private fun loadComplaintDashboard() {
        viewModel?.getComplaintDashboardData()?.observe(requireActivity(), Observer { resource ->
            when (resource) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    try {
                        val response = resource.data
                        if (resource != null) {
                            binding.rvDashboard.adapter =
                                ComplaintMainAdapter(
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

    @SuppressLint("HardwareIds")
    private fun bindView() {
        viewModel = if (SessionManager.getInstance().getString(Constants.COMPANY) == "SOLAR") {
            ViewModelProvider(
                requireActivity(),
                ViewModelFactory(NetworkApiHelper(RetrofitBuilder.solarApiService))
            )[ComplaintViewModel::class.java]
        } else {

            ViewModelProvider(
                requireActivity(), ViewModelFactory(NetworkApiHelper(RetrofitBuilder.bmdApiService))
            )[ComplaintViewModel::class.java]
        }
    }


    override fun onDashboardClick(item: String) {
        when (item) {
            Constants.RegisterComplaint -> {
                findNavController().navigate(R.id.home_to_attendance)
            }
            Constants.PendingComplaint -> {
                findNavController().navigate(R.id.navigate_to_my_movement)
            }
            Constants.PendingPmr -> {
                findNavController().navigate(R.id.navigate_to_my_conveyance)
            }
            Constants.PendingInstallation -> {
                findNavController().navigate(R.id.home_to_leave_fragment)
            }
            Constants.CloseComplaint -> {
                findNavController().navigate(R.id.home_to_voucher_fragment)
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}