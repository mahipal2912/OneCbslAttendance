package com.one.cbsl.ui.complain.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.one.cbsl.databinding.FragmentCompleteComplaintBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.complain.adapter.AssignComplainAdapter
import com.one.cbsl.ui.complain.adapter.ComplaintLogAdapter
import com.one.cbsl.ui.complain.adapter.CompleteComplainAdapter
import com.one.cbsl.ui.complain.viewmodel.ComplaintViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager

class CompleteComplainFragment : Fragment(), CompleteComplainAdapter.OpitionListener {

    private var _binding: FragmentCompleteComplaintBinding? = null
    private var viewModel: ComplaintViewModel? = null
    var type: String = "Complete"

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        bindView()
        _binding = FragmentCompleteComplaintBinding.inflate(inflater, container, false)


        return binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.complaintInfo.icInfoDialog.setOnClickListener {
            binding.complaintInfo.rlComplainInfoDialog.visibility = View.GONE
        }
        binding.ivClosePdf.setOnClickListener {
            binding.rlPdf.visibility = View.GONE
        }
        binding.complaintLog.icCloseLogDialog.setOnClickListener {
            binding.complaintLog.rlComplainLogDialog.visibility = View.GONE
        }
        binding.tvDate.text = Constants.getTodayData()
        getCompleteComplaint()
        binding.tvDate.setOnClickListener {
            Constants.getDateSelection(requireActivity()) { selectDate ->
                binding.tvDate.text = selectDate
                getCompleteComplaint()
            }
        }
        binding.swComplete.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                type = "PMR"
                getCompleteComplaint()
            } else {
                type = "Complete"
                getCompleteComplaint()
            }
        }
    }

    private fun getCompleteComplaint() {
        viewModel?.getComplainDetails(type, binding.tvDate.text.toString(), "0")
            ?.observe(requireActivity(), Observer { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Fetching Complaint")
                    }
                    is Resource.Success -> {
                        if (resource.data?.get(0)?.Status == null) {
                            binding.rvMachineList.visibility = View.VISIBLE
                            binding.rvMachineList.adapter =
                                CompleteComplainAdapter(resource.data!!, this)
                        } else {
                            binding.rvMachineList.visibility = View.GONE
                        }
                        DialogUtils.dismissDialog()

                    }
                    is Resource.Error -> {
                        DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                    }
                }
            })
    }


    override fun onItemClick(url: String) {
        TODO("Not yet implemented")
    }


    override fun showComplaintLog(ComplaintId: String) {
        viewModel?.getComplaintLog(ComplaintId)?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        binding.complaintLog.rlComplainLogDialog.visibility = View.VISIBLE
                        binding.complaintLog.rvComplaintLog.adapter = ComplaintLogAdapter(response)
                    }
                }
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Getting Log")
                }
                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())
                }
            }
        })

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}