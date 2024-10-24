package com.one.cbsl.ui.attendance.punchattendance

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.one.cbsl.R
import com.one.cbsl.databinding.FragmentMyattendnanceBinding
import com.one.cbsl.localdb.AttendanceMaster
import com.one.cbsl.localdb.DbRepository
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.punchattendance.adapter.AttendanceAdapter
import com.one.cbsl.ui.attendance.punchattendance.adapter.OfflineAttendanceAdapter
import com.one.cbsl.ui.attendance.punchattendance.viewmodel.AttendanceViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class AttendanceFragment : Fragment() {

    private var _binding: FragmentMyattendnanceBinding? = null
    private var viewModel: AttendanceViewModel? = null
    var recordsList: ArrayList<AttendanceMaster> = ArrayList()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyattendnanceBinding.inflate(inflater, container, false)
        bindViewModel()
        return binding.root
    }

    private fun bindViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(NetworkApiHelper(RetrofitBuilder.apiService))
        )[AttendanceViewModel::class.java]
        loadOfflineAttendance(0)
        binding.spinAttendanceType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.sync_status)
        )
        binding.spinAttendanceType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, pos: Int, id: Long
                ) {
                    try {
                        loadOfflineAttendance(pos)
                        binding.spinLayout.visibility = View.VISIBLE
                        binding.dataLayout.visibility = View.GONE

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }


        binding.swAttendance.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.spinLayout.visibility = View.VISIBLE
                binding.dataLayout.visibility = View.GONE
                showOfflineData()
                synData()
            } else {
                binding.spinLayout.visibility = View.GONE
                binding.dataLayout.visibility = View.VISIBLE
                loadMyAttendance()
            }
        }
    }

    private fun showOfflineData() {
        binding.rvMyAttendance.adapter =
            OfflineAttendanceAdapter(
                recordsList
            )

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

    private fun loadOfflineAttendance(type: Int) {
        DbRepository.getInstance(requireActivity()).appDatabase
            .crcDao().getAttendance(type)
            .observe(requireActivity(),
                Observer<List<AttendanceMaster>> { t ->
                    try {
                        recordsList.clear()
                        recordsList = t as ArrayList<AttendanceMaster>
                        if (binding.swAttendance.isChecked) {
                            showOfflineData()
                        }
                    } catch (e: Exception) {

                        e.printStackTrace()
                    }
                })
    }

    private fun synData() {
        for (i in recordsList.indices) {
            try {
                viewModel?.syncOfflineAttendance(

                    recordsList[i].AttendanceTypeId,
                    recordsList[i].PunchDate,
                    recordsList[i].PunchIn,
                    recordsList[i].locationAddress,
                    recordsList[i].latitude,
                    recordsList[i].longitude,
                    recordsList[i].CreatedOn,
                    recordsList[i].PunchOut,
                    recordsList[i].PunchOutLocationAddress,
                )?.observe(viewLifecycleOwner, Observer { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            DialogUtils.showProgressDialog(requireActivity(), "Fetching Attendance")
                        }

                        is Resource.Success -> {
                            try {
                                DialogUtils.dismissDialog()

                                resource.data?.let { response ->
                                    if (response[0].MarkStatus?.toString()?.contains( "UNABLE TO PUNCH IN")!=true){
                                        CoroutineScope(Dispatchers.IO).launch {
                                            DbRepository.getInstance(requireActivity()).appDatabase.crcDao()
                                                .updateStatus(recordsList[i].id, Utils.getTodayDate(),response[0].MarkStatus?.toString())
                                        }
                                    }
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        is Resource.Error -> {
                            DialogUtils.showFailedDialog(
                                requireActivity(),
                                resource.message.toString()
                            )
                        }
                    }
                })

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("error--------", e.message.toString())
            }
        }
    }

    private fun loadMyAttendance() {
        viewModel?.getMyAttendanceList(binding.tvAttendaneceDate.text.toString())
            ?.observe(requireActivity(), Observer { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Fetching Attendance")
                    }

                    is Resource.Success -> {
                        try {
                            DialogUtils.dismissDialog()
                            if (resource.data != null) {
                                binding.rvMyAttendance.adapter =
                                    AttendanceAdapter(
                                        resource.data
                                    )
                                synData()
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