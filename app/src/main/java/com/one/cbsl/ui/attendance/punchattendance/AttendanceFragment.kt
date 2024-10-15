package com.one.cbsl.ui.attendance.punchattendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.one.cbsl.databinding.FragmentMyattendnanceBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.punchattendance.adapter.AttendanceAdapter
import com.one.cbsl.ui.attendance.punchattendance.viewmodel.AttendanceViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource

class AttendanceFragment : Fragment() {

    private var _binding: FragmentMyattendnanceBinding? = null
    private var viewModel: AttendanceViewModel? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindViewModel()
        _binding = FragmentMyattendnanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun bindViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(NetworkApiHelper(RetrofitBuilder.apiService))
        )[AttendanceViewModel::class.java]

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvAttendaneceDate.text = Constants.getTodayData()
        binding.tvAttendaneceDate.setOnClickListener {
            Constants.getDateSelection(requireActivity()) { selectedDate ->
                // Handle the selected date here
                binding.tvAttendaneceDate.text = selectedDate
                loadMyAttendance()

            }
        }
        loadMyAttendance()
    }

    private fun loadMyAttendance() {
        viewModel?.getMyAttendanceList(binding.tvAttendaneceDate.text.toString())
            ?.observe(requireActivity(), Observer { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Fetching Attendance")
                    }
                    is Resource.Success -> {
                        DialogUtils.dismissDialog()
                        if (resource.data != null) {
                            binding.rvMyAttendance.adapter =
                                AttendanceAdapter(
                                    resource.data
                                )
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