package com.one.cbsl.ui.attendance.leave

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.one.cbsl.R
import com.one.cbsl.databinding.FragmentLeaveBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.leave.adapter.MyLeaveAdapter
import com.one.cbsl.ui.attendance.leave.viewmodel.LeaveViewModel
import com.one.cbsl.ui.attendance.movement.viewmodel.MovementViewModel
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource

class LeaveFragment : Fragment() {

    private var _binding: FragmentLeaveBinding? = null
    private var viewModel: LeaveViewModel? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory(NetworkApiHelper(RetrofitBuilder.getApi()))
        )[LeaveViewModel::class.java]

        _binding = FragmentLeaveBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAdd.setOnClickListener { findNavController().navigate(R.id.navigate_to_mark_leave) }

        getMyLeave()
    }

    private fun getMyLeave() {
        viewModel?.getMyLeavePlan(0)?.observe(requireActivity(), Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Leave")
                }
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    if (resource.data != null) {
                        binding.rvMyLeave.adapter = MyLeaveAdapter(resource.data)
                    }
                }
                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}