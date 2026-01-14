package com.one.cbsl.ui.attendance.hodattendance

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.one.cbsl.R
import com.one.cbsl.databinding.FragmentFacilityAttendanceHodBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.conveyance.adapter.HodFacilityConveyanceAdapter
import com.one.cbsl.ui.attendance.conveyance.model.HodAttendanceTypeResponse
import com.one.cbsl.ui.attendance.conveyance.viewmodel.ConveyanceViewModel
import com.one.cbsl.ui.attendance.hodattendance.adapter.HodFacilityAttendanceAdapter
import com.one.cbsl.ui.attendance.punchattendance.viewmodel.AttendanceViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager
import java.util.*
import kotlin.collections.ArrayList

class FacilityWiseAttendance : Fragment(), HodFacilityAttendanceAdapter.OpitionListener {
    lateinit var list: List<HodAttendanceTypeResponse>

    private lateinit var _binding: FragmentFacilityAttendanceHodBinding
    private var viewModel: AttendanceViewModel? = null


    private val binding get() = _binding

    companion object {
        fun newInstance() = FacilityWiseAttendance()
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


        _binding = FragmentFacilityAttendanceHodBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpViewModel()
        list = ArrayList()
        binding.tvAttendaneceDate.text = Constants.getCurrentDate()

        binding.tvAttendaneceDate.setOnClickListener {
            showDateDialog(binding.tvAttendaneceDate)
        }

        binding.tvAttendaneceDate.text = SessionManager.getInstance().getString(Constants.FromDate)

        setupObserver()


        binding.btnSearch.setOnClickListener {
            SessionManager.getInstance()
                .putString(Constants.FromDate, binding.tvAttendaneceDate.text.toString())

            setupObserver()
        }

    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(NetworkApiHelper(RetrofitBuilder.getApi()))
        )[AttendanceViewModel::class.java]

    }

    private fun showDateDialog(tvDate: TextView?) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            requireActivity(),
            { view, year, monthOfYear, dayOfMonth ->
                tvDate!!.text = "" + year + "/" + (monthOfYear + 1) + "/" + dayOfMonth

            },
            year,
            month,
            day
        )
        dpd.datePicker.maxDate = System.currentTimeMillis();
        dpd.show()
    }


    private fun setupObserver() {
        viewModel?.getHodFacilityWiseAttendance(
            binding.tvAttendaneceDate.text.toString(),
            ""
        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    try {
                        DialogUtils.dismissDialog()
                        resources.data?.let { response ->

                            binding.rvConyFacility.adapter =
                                HodFacilityAttendanceAdapter(this, requireActivity(), response)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(
                        requireActivity(),
                        "Fetching Data"
                    )
                }

                is Resource.Error -> {
                    //Handle Error
                    DialogUtils.showFailedDialog(
                        requireActivity(),
                        resources.message.toString()
                    )

                }
            }
        })
    }


    override fun onItemClick(facilityid: String, status: String) {
        SessionManager.getInstance().putString(Constants.FacilityId, facilityid)
        val bundle: Bundle = Bundle()
        bundle.putString("facilityid", facilityid)
        findNavController().navigate(R.id.hod_facility_to_emp_list_attendance, bundle)

    }


}