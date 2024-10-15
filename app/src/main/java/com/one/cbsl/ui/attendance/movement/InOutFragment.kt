package com.one.cbsl.ui.attendance.movement

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.one.cbsl.CbslMain
import com.one.cbsl.databinding.FragmentMoveInOutBinding
import com.one.cbsl.databinding.FragmentMyattendnanceBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.movement.model.TaskResponse
import com.one.cbsl.ui.attendance.movement.viewmodel.MovementViewModel
import com.one.cbsl.ui.attendance.punchattendance.adapter.AttendanceAdapter
import com.one.cbsl.ui.attendance.punchattendance.viewmodel.AttendanceViewModel
import com.one.cbsl.ui.complain.model.AssignClientResponse
import com.one.cbsl.ui.complain.model.ComplainClientResponse
import com.one.cbsl.ui.complain.model.GetComplainResponse
import com.one.cbsl.ui.complain.model.PendingInstallResponse
import com.one.cbsl.ui.complain.viewmodel.ComplaintViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager
import com.one.cbsl.utils.permissions.PermissionRequest
import com.one.cbsl.utils.permissions.PermissionRequestHandler

import java.util.*

class InOutFragment : Fragment(), View.OnClickListener,
    PermissionRequest.RequestCustomPermissionGroup {

    private var _binding: FragmentMoveInOutBinding? = null
    private var viewModel: MovementViewModel? = null
    private var complaintViewModel: ComplaintViewModel? = null
    var lat: Double? = null
    var lng: Double? = null
    var tourId = ""
    var clientId: String = ""
    var clientCode: String = ""
    var complaintNo: String = ""
    var machineNumber: String = ""
    var clientName: String = ""
    var clientPlaceName: String = ""
    var complaintType: String = ""
    var taskName = ""
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMoveInOutBinding.inflate(inflater, container, false)
        bindViewModel()
        bindDropDownSelection()
        return binding.root
    }

    private fun bindDropDownSelection() {
        binding.spinTask.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as TaskResponse
                    taskName = selectedItem.TaskName.toString()
                    if (taskName.contains("Complaint") || taskName.contains("PMR") || taskName.contains(
                            "Installation"
                        )
                    ) {
                        binding.llComplaintLayout.visibility = View.VISIBLE
                        loadComplaintNumber(taskName)
                    } else {
                        binding.llComplaintLayout.visibility = View.GONE
                    }
                    //        loadBranchName(cityId!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        binding.spinClientName.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as ComplainClientResponse
                    val taskResponse: TaskResponse = binding.spinTask.selectedItem as TaskResponse

                    clientId = selectedItem.ClientId.toString()
                    clientName = selectedItem.ClientName.toString()
                    if (taskName.contains("Complaint")) {
                        getClientDetails("1")
                        binding.tvHeader.text = "Complaint Number"
                    }
                    if (taskName.contains("PMR")) {
                        getClientDetails("2")
                        binding.tvHeader.text = "Complaint Number"
                    }
                    if (taskName.contains("Install")) {
                        getClientDetails("3")
                        binding.tvHeader.text = "Machine Number"
                    }
                    //        loadBranchName(cityId!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.d("djd", "wefg")
            }

        }
        binding.spinPlaceName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                try {
                    val selectedItem = parent?.getItemAtPosition(position) as AssignClientResponse



                    clientCode = selectedItem.ClientId.toString()
                    clientPlaceName = selectedItem.ClientName.toString()
                    if (taskName.contains("Complaint")) {
                        getMachineDetail("0")
                        binding.tvHeader.text = "Complaint Number"
                    }
                    if (taskName.contains("PMR")) {
                        getMachineDetail("PMR_GET")
                        binding.tvHeader.text = "Complaint Number"
                    }
                    if (taskName.contains("Install")) {
                        getPending("0")
                        binding.tvHeader.text = "Machine Number"
                    }
                    //        loadBranchName(cityId!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.d("djd", "wefg")
            }

        }
        binding.spinComplaintName.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    try {

                        if (taskName.contains("Install")) {
                            val selectedItem =
                                parent?.getItemAtPosition(position) as PendingInstallResponse
                            clientPlaceName = selectedItem.BranchName.toString()
                            complaintNo = ""
                            machineNumber = selectedItem.MachineNumber.toString()
                            complaintType = "Machine Installation"
                        } else {
                            val selectedItem =
                                parent?.getItemAtPosition(position) as GetComplainResponse
                            clientPlaceName = selectedItem.ClientName.toString()
                            complaintNo = selectedItem.Complain_tNumber.toString()
                            machineNumber = selectedItem.Machine_Number.toString()
                            complaintType = selectedItem.ComplaintType_Name.toString()
                        }

                        //        loadBranchName(cityId!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.d("djd", "wefg")
                }

            }

    }

    private fun bindViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(NetworkApiHelper(RetrofitBuilder.apiService))
        )[MovementViewModel::class.java]

        complaintViewModel =
            if (SessionManager.getInstance().getString(Constants.COMPANY) == "SOLAR") {
                ViewModelProvider(
                    requireActivity(),
                    ViewModelFactory(NetworkApiHelper(RetrofitBuilder.solarApiService))
                )[ComplaintViewModel::class.java]
            } else {

                ViewModelProvider(
                    requireActivity(),
                    ViewModelFactory(NetworkApiHelper(RetrofitBuilder.bmdApiService))
                )[ComplaintViewModel::class.java]
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        DialogUtils.showProgressDialog(requireActivity(), "loading...")
        binding.tvCheckIn.setOnClickListener(this)
        binding.tvCheckOut.setOnClickListener(this)
        binding.btnFetch.setOnClickListener(this)
        checkMovement()
        checkAllPermission()

        getTask()
    }

    private fun checkMovement() {
        if (arguments?.getBoolean("isCheckIn")!!) {
            binding.tvCheckIn.visibility = View.VISIBLE
            binding.tvCheckOut.visibility = View.GONE
        } else {
            binding.llComplaintLayout.visibility = View.GONE
            binding.tvCheckIn.visibility = View.GONE
            binding.tvCheckOut.visibility = View.VISIBLE
            binding.spinTask.isEnabled = false;
            binding.spinTaskLayout.visibility = View.GONE
            binding.tvTaskLayout.visibility = View.VISIBLE
            binding.etFromLocation.setText(arguments?.getString("fromLocation"))
            binding.tvTask.text = arguments?.getString("task")
            binding.etRemark.setText(arguments?.getString("reason"))
            binding.etRemark.isEnabled = false
            binding.etFromLocation.isEnabled = false
            binding.etToLocation.isEnabled = false
        }
    }

    private fun checkAllPermission() {
        PermissionRequestHandler.requestCustomPermissionGroup(
            this,
            "",
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (isLocationEnabled()) {
            mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                val location: Location = task.result
                lat = location.latitude
                lng = location.longitude
                val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                val list: MutableList<Address>? =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                binding.etToLocation.text =
                    Editable.Factory.getInstance().newEditable(list?.get(0)?.getAddressLine(0))
                DialogUtils.dismissDialog()

            }
        } else {
            Toast.makeText(requireActivity(), "Please turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)

        }

    }


    override fun onAllCustomPermissionGroupGranted() {
        getLocation()
    }

    override fun onCustomPermissionGroupDenied() {
        PermissionRequestHandler.requestCustomPermissionGroup(
            this,
            "",
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.tvCheckIn -> {
                when {
                    TextUtils.isEmpty(binding.etFromLocation.text.toString()) -> {
                        Toast.makeText(
                            activity,
                            "From Location Can't Left Blank",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    TextUtils.isEmpty(binding.etToLocation.text.toString()) -> {
                        Toast.makeText(activity, "To Location Can't Left Blank", Toast.LENGTH_SHORT)
                            .show()
                    }

                    TextUtils.isEmpty(binding.etRemark.text.toString()) -> {
                        Toast.makeText(activity, "Reason Can't Left Blank", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        val taskResponse: TaskResponse =
                            binding.spinTask.selectedItem as TaskResponse
                        if (taskName.contains("Complaint") || taskName.contains("PMR") || taskName.contains(
                                "Installation"
                            )
                        ) {
                            if (machineNumber != "" || complaintNo != "") {
                                checkIn(
                                    taskResponse.TaskId.toString(),
                                    complaintNo,
                                    machineNumber,
                                    clientName,
                                    clientPlaceName,
                                    complaintType
                                )
                            } else {
                                Toast.makeText(
                                    requireActivity(),
                                    "No $taskName found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            checkIn(taskResponse.TaskId.toString(), "", "", "", "", "")
                        }
                    }


                }

            }

            binding.tvCheckOut -> {
                checkOutMovement()
            }

            binding.btnFetch -> {
                checkAllPermission()
            }

        }
    }

    private fun getTask() {
        viewModel?.getTask()?.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resource.data?.let { response ->
                        val aa = ArrayAdapter(
                            requireContext(),
                            R.layout.simple_spinner_item,
                            response
                        )
                        binding.spinTask.adapter = aa
                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Task")
                }

                is Resource.Error -> {
                    //Handle Error
                    DialogUtils.showFailedDialog(requireActivity(), resource.message.toString())
                }
            }
        })

        viewModel?.getLastLocation()?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        if (arguments?.getBoolean("isCheckIn")!!) {
                            binding.etFromLocation.setText(response[0].LocationAddress)
                            tourId = response[0].tourid
                        }

                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Location")
                }

                is Resource.Error -> {
                    //Handle Error
                    if (arguments?.getBoolean("isCheckIn")!!) {
                        checkAllPermission()
                    }

                }
            }
        })

    }

    private fun checkIn(
        taskId: String,
        complaint: String,
        machine: String,
        client: String,
        clientPlace: String,
        Type: String
    ) {
        viewModel?.checkInMovement(
            SessionManager.getInstance().getString(Constants.UserId),
            binding.etFromLocation.text.toString(),
            binding.etToLocation.text.toString(),
            taskId,
            binding.etRemark.text.toString(),
            binding.etToLocation.text.toString(),
            lat.toString(),
            lng.toString(), tourId, complaint,
            machine,
            client,
            clientPlace,
            Type
        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        DialogUtils.showSuccessDialog(
                            requireActivity(),
                            response[0].status.toString(),
                            CbslMain::class.java
                        )
                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Check In Progress")

                }

                is Resource.Error -> {
                    //Handle Error
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())
                }
            }
        })
    }

    private fun checkOutMovement() {
        viewModel?.movementCheckout(
            arguments?.getString("movementCode").toString(),
            binding.etToLocation.text.toString(),
            lat.toString(),
            lng.toString()
        )?.observe(requireActivity(), Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        DialogUtils.showSuccessDialog(
                            requireActivity(),
                            response[0].status.toString(),
                            CbslMain::class.java
                        )
                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Check Out Progress")

                }

                is Resource.Error -> {
                    //Handle Error
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())
                }
            }
        }
        )
    }

    private fun loadCustomerName() {
        complaintViewModel?.getCustomerName(
            "999", "0"
        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        try {
                            binding.spinClientName.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                response
                            )

                            clientId = response[0].ClientId.toString()


                        } catch (e: Exception) {
                        }

                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetch Client Progress")

                }

                is Resource.Error -> {
                    //Handle Error
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())
                }
            }

        })
    }

    private fun loadComplaintNumber(taskName: String) {
        loadCustomerName()

    }

    private fun getPending(branchCode: String) {
        complaintViewModel?.getPendingInstallation(
            SessionManager.getInstance().getString(Constants.COMPLAINT_USERID),
            "0",
            "0",
            clientCode

        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        try {
                            binding.spinComplaintName.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                response
                            )
                            if (response[0].status == null) {
                                machineNumber = response[0].MachineNumber.toString()
                                complaintNo = ""
                            } else {
                                machineNumber = ""
                                complaintNo = ""
                            }

                        } catch (e: Exception) {
                        }
                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching data")

                }

                is Resource.Error -> {
                    //Handle Error
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())
                }
            }

        })
    }

    private fun getMachineDetail(types: String) {
        complaintViewModel?.getComplainDetails(
            clientId, types, clientCode
        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        try {
                            binding.spinComplaintName.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                response
                            )
                            if (response[0].Status == null) {
                                complaintNo = response[0].Complain_tNumber.toString()
                                machineNumber = response[0].Machine_Number.toString()
                                clientPlaceName = response[0].ClientName.toString()
                                complaintType = response[0].ComplaintType_Name.toString()
                            } else {
                                machineNumber = ""
                                complaintNo = ""

                            }
                        } catch (e: Exception) {
                        }
                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Data")

                }

                is Resource.Error -> {
                    //Handle Error
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())
                }
            }
        })
    }

    private fun getClientDetails(types: String) {
        complaintViewModel?.getClientDetails(
            "USERWISE_BRANCH", types, clientId
        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    DialogUtils.dismissDialog()
                    resources.data?.let { response ->
                        try {
                            binding.spinPlaceName.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                response
                            )
                            if (response[0].Status == null) {
                                clientPlaceName = response[0].ClientName.toString()
                                clientCode = response[0].ClientId.toString()
                            } else {
                                clientPlaceName = ""
                                complaintNo = ""
                                machineNumber = ""
                                binding.spinComplaintName.adapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_item,
                                    response
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Data")

                }

                is Resource.Error -> {
                    //Handle Error
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