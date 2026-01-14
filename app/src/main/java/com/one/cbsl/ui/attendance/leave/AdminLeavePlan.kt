package com.one.cbsl.ui.attendance.leave

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.one.cbsl.databinding.FragmentAdminLeavePlanBinding
import com.one.cbsl.databinding.FragmentLocalClaimBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.conveyance.viewmodel.ConveyanceViewModel
import com.one.cbsl.ui.attendance.leave.adapter.AdminLeaveAdapter
import com.one.cbsl.ui.attendance.leave.adapter.MyLeaveAdapter
import com.one.cbsl.ui.attendance.leave.viewmodel.LeaveViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager


class AdminLeavePlan : Fragment(), AdminLeaveAdapter.OpitionListener {
    companion object {
        fun newInstance() = AdminLeavePlan()
    }

    private var _binding: FragmentAdminLeavePlanBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LeaveViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(NetworkApiHelper(RetrofitBuilder.getApi()))
        )[LeaveViewModel::class.java]

        _binding = FragmentAdminLeavePlanBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.ivBack.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun setupObserver() {

        viewModel.getMyLeavePlan(1).observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Leave")
                }

                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    if (resource.data != null) {
                        binding.rvMyLeave.adapter =
                            AdminLeaveAdapter(resource.data, this, requireActivity())
                    }
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                }
            }
        }
        )

    }

    override fun onResume() {
        super.onResume()
        setupObserver()

    }

    override fun onItemClick(leaveid: String?, userid: String?, status: String?) {
        viewModel.LeavePlanUpdate(
            SessionManager.getInstance().getString(Constants.UserId),
            leaveid!!,
            userid!!,
            status!!
        ).observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Leave")
                }

                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    if (resource.data != null) {
                        Toast.makeText(
                            requireActivity(),
                            "" + resource.data[0].status,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                }
            }
        }
        )
    }


}