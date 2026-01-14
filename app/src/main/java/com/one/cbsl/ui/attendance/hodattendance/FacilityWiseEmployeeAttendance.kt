package com.one.cbsl.ui.attendance.hodattendance

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
import com.one.cbsl.ui.attendance.hodattendance.adapter.HodAttendanceAdapter
import com.one.cbsl.ui.attendance.punchattendance.viewmodel.AttendanceViewModel

import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager

class FacilityWiseEmployeeAttendance : Fragment(), HodAttendanceAdapter.OptionListener {

    private lateinit var _binding: FragmentFacilityWiseEmployeeListBinding
    private var viewModel: AttendanceViewModel? = null


    private val binding get() = _binding

    companion object {
        fun newInstance() = FacilityWiseEmployeeAttendance()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(NetworkApiHelper(RetrofitBuilder.getApi()))
            )[AttendanceViewModel::class.java]


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
        viewModel?.getHodFacilityEmployeeListAttendance(
            SessionManager.getInstance().getString(Constants.FacilityId),
            "",
            SessionManager.getInstance().getString(Constants.FromDate),""
        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    try {
                        DialogUtils.dismissDialog()
                        resources.data?.let { response ->
                            binding.rvConyEmpDetail.adapter =
                                HodAttendanceAdapter( this,requireActivity(), response)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
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



    override fun onItemClick(userid:String,employeeCode: String, facility: String,registerType:String) {
        SessionManager.getInstance().putString(Constants.HodVerifyType,registerType)
        SessionManager.getInstance().putString(Constants.HodEmpID,userid)
        SessionManager.getInstance().putString(Constants.HodEmpCode,employeeCode)
        SessionManager.getInstance().putString(Constants.HodCityName,facility)
        findNavController().navigate(R.id.hod_attendance_to_face_capture)
    }

}