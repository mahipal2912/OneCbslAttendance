package com.one.cbsl.ui.attendance.conveyance.head

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.one.cbsl.R
import com.one.cbsl.databinding.FragmentFacilityWiseEmployeeListBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.conveyance.adapter.HodFacilityEmplistAdapter
import com.one.cbsl.ui.attendance.conveyance.viewmodel.ConveyanceViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager

class FacilityWiseEmployee : Fragment(), HodFacilityEmplistAdapter.OpitionListener {

    private lateinit var _binding: FragmentFacilityWiseEmployeeListBinding
    private var viewModel: ConveyanceViewModel? = null


    private val binding get() = _binding

    companion object {
        fun newInstance() = FacilityWiseEmployee()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(NetworkApiHelper(RetrofitBuilder.apiService))
            )[ConveyanceViewModel::class.java]


        _binding = FragmentFacilityWiseEmployeeListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupEmpDetail()

    }

    override fun onResume() {
        super.onResume()
        setupEmpDetail()
    }

    private fun setupEmpDetail() {
        viewModel?.getHeadFacilityEmployeeList(
            SessionManager.getInstance().getString(Constants.FacilityId),
            SessionManager.getInstance().getString(Constants.Status),
            SessionManager.getInstance().getString(Constants.FromDate),
            SessionManager.getInstance().getString(Constants.ToDate)
        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        binding.rvConyEmpDetail.adapter =
                            HodFacilityEmplistAdapter(requireActivity(), this, response)
                    }
                }
                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireContext(), "Fetching Employee")
                }
                is Resource.Error -> {
                    //Handle Error
                    DialogUtils.showFailedDialog(requireContext(), resources.message.toString())

                }
            }
        })
    }


    override fun onItemClick(userid: String) {
        findNavController().navigate(R.id.head_emp_list_to_conveyance)
    }

}