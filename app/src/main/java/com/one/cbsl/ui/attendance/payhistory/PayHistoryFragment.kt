package com.one.cbsl.ui.attendance.payhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.one.cbsl.R
import com.one.cbsl.databinding.FragmentMyVoucherBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.payhistory.PayHistoryAdapter
import com.one.cbsl.ui.attendance.conveyance.viewmodel.ConveyanceViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource

class PayHistoryFragment : Fragment(), PayHistoryAdapter.OpitionListener {

    private var _binding: FragmentMyVoucherBinding? = null
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
                ViewModelFactory(NetworkApiHelper(RetrofitBuilder.getApi()))
            )[ConveyanceViewModel::class.java]

        _binding = FragmentMyVoucherBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDate.text = Constants.getTodayData()
        binding.btnAdd.visibility = View.GONE
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.navigate_to_claim_voucher)
        }
        binding.tvDate.setOnClickListener {
            Constants.getDateSelection(requireActivity()) { selectedDate ->
                // Handle the selected date here
                binding.tvDate.text = selectedDate
                getPayHistory()

            }
        }
        getPayHistory()


    }

    private fun getPayHistory() {
        viewModel?.getPayHistory(binding.tvDate.text.toString())
            ?.observe(
                requireActivity()
            ) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        try {
                            DialogUtils.dismissDialog()
                            if (resource.data != null) {
                                binding.rvMyConvyence.visibility = View.VISIBLE
                                binding.rvMyConvyence.adapter =
                                    PayHistoryAdapter(
                                        resource.data, this
                                    )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Fetching History")


                    }

                    is Resource.Error -> {
                        DialogUtils.dismissDialog()
                        binding.rvMyConvyence.visibility = View.GONE

                    }
                }

            }
    }


    override fun onItemClick(item: String) {
        TODO("Not yet implemented")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}