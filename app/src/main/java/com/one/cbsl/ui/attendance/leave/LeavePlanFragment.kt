package com.one.cbsl.ui.attendance.leave

import android.R
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.one.cbsl.CbslMain
import com.one.cbsl.databinding.FragmentLeavePlanBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.leave.adapter.MyLeaveAdapter
import com.one.cbsl.ui.attendance.leave.viewmodel.LeaveViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource

class LeavePlanFragment : Fragment() {

    private var _binding: FragmentLeavePlanBinding? = null
    private var viewModel: LeaveViewModel? = null
    private val binding get() = _binding!!
    private val list = arrayOf("Annual Leave", "Casual Leave", "Sick Leave")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory(NetworkApiHelper(RetrofitBuilder.getApi()))
        )[LeaveViewModel::class.java]

        _binding = FragmentLeavePlanBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.spinLeaveType.adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            list
        )

        binding.etFromDate.setOnClickListener {
            Constants.getPostDateSelection(requireActivity()) { selectDate ->
                binding.etFromDate.text = selectDate
            }
        }
        binding.etToDate.setOnClickListener {
            Constants.getPostDateSelection(requireActivity()) { selectDate ->
                binding.etToDate.text = selectDate
            }
        }

        binding.tvSave.setOnClickListener {

            when {
                TextUtils.isEmpty(binding.etFromDate.text.toString()) -> {
                    DialogUtils.showFailedDialog(requireContext(), "Enter From Date")
                }

                TextUtils.isEmpty(binding.etToDate.text.toString()) -> {
                    DialogUtils.showFailedDialog(requireContext(), "Enter To Date")
                }

                TextUtils.isEmpty(binding.etRemark.text.toString().trim()) -> {
                    DialogUtils.showFailedDialog(requireContext(), "Enter a Remark")
                }

                else -> {
                    saveLeavePlan()
                }
            }
        }
    }

    private fun saveLeavePlan() {
        viewModel?.leavePlanSave(
            binding.etFromDate.text.toString(),
            binding.etToDate.text.toString(),
            binding.etRemark.text.toString(),
            binding.spinLeaveType.selectedItem.toString()

        )?.observe(requireActivity(), Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Marking Leave")
                }

                is Resource.Success -> {
                    try {
                        DialogUtils.dismissDialog()
                        if (resource.data != null) {
                            DialogUtils.showSuccessDialog(
                                requireContext(),
                                resource.data[0].status.toString(),
                                CbslMain::class.java
                            )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}