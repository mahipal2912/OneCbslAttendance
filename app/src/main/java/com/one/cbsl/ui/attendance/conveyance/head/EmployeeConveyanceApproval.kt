package com.one.cbsl.ui.attendance.conveyance.head

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.one.cbsl.databinding.FragmentHodEmpConveyanceBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.conveyance.adapter.HodConveyEmpDetailAdapter
import com.one.cbsl.ui.attendance.conveyance.viewmodel.ConveyanceViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager

class EmployeeConveyanceApproval : Fragment(), HodConveyEmpDetailAdapter.OpitionListener {

    private lateinit var _binding: FragmentHodEmpConveyanceBinding
    private var viewModel: ConveyanceViewModel? = null
    private val binding get() = _binding

    companion object {
        fun newInstance() = EmployeeConveyanceApproval()
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


        _binding = FragmentHodEmpConveyanceBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fetchData()

    }

    private fun fetchData() {
        viewModel?.getFinalHeadEmpConveyanceDetail(
            SessionManager.getInstance().getString("EmpId"),
            SessionManager.getInstance().getString(Constants.Status),
            SessionManager.getInstance().getString(Constants.FromDate),
            SessionManager.getInstance().getString(Constants.ToDate)
        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->

                        try {
                            binding.rvMyConvyence.adapter =
                                HodConveyEmpDetailAdapter(this, requireActivity(), response)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Getting Conveyance ")
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())
                }
            }
        })
    }


    override fun onItemClick(con_id: String, markStatus: String, amt: String, remarks: String) {
        fetchData()
        viewModel?.updateFinalHeadEmpConveyance(
            con_id,
            markStatus,
            amt,
            remarks
        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    try {
                        DialogUtils.dismissDialog()

                        resources.data?.let { response ->
                            Toast.makeText(context, response[0].Status, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Updating...")
                }

                is Resource.Error -> {
                    //Handle Error
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())

                }
            }
        })
    }

    override fun onImageItemClick(url: String) {
        Log.d("test", "")
        /*  Glide.with(requireActivity()).load(url).into(iv_final_conveyance_image)
          iv_final_conveyance_image.visibility = View.VISIBLE*/
    }

}