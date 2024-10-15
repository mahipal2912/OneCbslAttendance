package com.one.cbsl.ui.attendance.conveyance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.one.cbsl.R
import com.one.cbsl.databinding.FragmentCovneyanceBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.conveyance.adapter.ConveynaceAdapter
import com.one.cbsl.ui.attendance.conveyance.adapter.TourConveyanceAdapter
import com.one.cbsl.ui.attendance.conveyance.adapter.TourLogAdapter
import com.one.cbsl.ui.attendance.conveyance.viewmodel.ConveyanceViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource


class ConveyanceFragment : Fragment(), ConveynaceAdapter.OptionListener,
    TourConveyanceAdapter.OpitionListener {

    private var _binding: FragmentCovneyanceBinding? = null
    private var viewModel: ConveyanceViewModel? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(NetworkApiHelper(RetrofitBuilder.apiService))
            )[ConveyanceViewModel::class.java]

        _binding = FragmentCovneyanceBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvConveyanceDate.text = Constants.getTodayData()

        binding.tvConveyanceDate.setOnClickListener {
            Constants.getDateSelection(requireActivity()) { selectedDate ->
                // Handle the selected date here
                binding.tvConveyanceDate.text = selectedDate
                getConveyance()

            }
        }
        getConveyance()
        binding.tvTourClaim.setOnClickListener {
            findNavController().navigate(R.id.conveyance_to_claim_tour)
        }
        binding.tvLocalClaim.setOnClickListener {
            findNavController().navigate(R.id.conveyance_to_local_claim)
        }

        binding.conveyanceLog.icCloseLogDialog.setOnClickListener {
            binding.conveyanceLog.rlTourLogDialog.visibility = View.GONE
        }

        binding.swConveyance.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.rvMyTourConveyance.visibility = View.VISIBLE
                binding.rvMyConveyance.visibility = View.GONE
                getTourConveyance()

            } else {
                binding.rvMyTourConveyance.visibility = View.GONE
                binding.rvMyConveyance.visibility = View.VISIBLE
                getConveyance()

            }
        }

    }

    private fun getConveyance() {
        viewModel?.getMyConveyanceData(binding.tvConveyanceDate.text.toString())
            ?.observe(
                requireActivity()
            ) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        DialogUtils.dismissDialog()
                        if (resource.data != null) {
                            binding.rvMyConveyance.adapter =
                                ConveynaceAdapter(
                                    resource.data, this
                                )
                        }
                    }
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Fetching Conveyance")


                    }
                    is Resource.Error -> {
                        DialogUtils.showFailedDialog(
                            requireActivity(),
                            resource.message.toString()
                        )
                    }
                }

            }
    }


    private fun getTourConveyance() {
        viewModel?.getMyTourConveyance(binding.tvConveyanceDate.text.toString(), "0")
            ?.observe(
                requireActivity()
            ) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        DialogUtils.dismissDialog()
                        if (resource.data != null) {
                            binding.rvMyTourConveyance.adapter =
                                TourConveyanceAdapter(
                                    resource.data, this
                                )
                        }
                    }
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Fetching Tour ")
                    }
                    is Resource.Error -> {
                        DialogUtils.showFailedDialog(
                            requireActivity(),
                            resource.message.toString()
                        )
                    }
                }

            }
    }

    private fun getTourLog(tourId: String) {
        viewModel?.getMyTourLog(binding.tvConveyanceDate.text.toString(), tourId)
            ?.observe(
                requireActivity()
            ) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        DialogUtils.dismissDialog()
                        if (resource.data?.get(0)?.ConveyanceDate != null) {
                            binding.conveyanceLog.rlTourLogDialog.visibility = View.VISIBLE

                            binding.conveyanceLog.rvTourHistory.adapter =
                                TourLogAdapter(
                                    resource.data
                                )
                        }else
                        {
                            DialogUtils.showFailedDialog(
                                requireActivity(),
                               "No History Found"
                            )
                        }
                    }
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Fetching Tour ")
                    }
                    is Resource.Error -> {
                        DialogUtils.showFailedDialog(
                            requireActivity(),
                            resource.message.toString()
                        )
                    }
                }

            }
    }


    override fun onItemClick(item: String) {
        TODO("Not yet implemented")
    }


    override fun onTourIdClick(tourId: String) {
        getTourLog(tourId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}