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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager
import com.one.cbsl.utils.permissions.PermissionRequest
import com.one.cbsl.utils.permissions.PermissionRequestHandler
import java.util.*

class InOutComplaintFragment : Fragment(), View.OnClickListener,
    PermissionRequest.RequestCustomPermissionGroup {

    private var _binding: FragmentMoveInOutBinding? = null
    private var viewModel: MovementViewModel? = null
    var lat: Double? = null
    var lng: Double? = null
    var tourId = ""
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    var complaintNo: String = ""
    var machineNumber: String = ""
    var clientName: String = ""
    var clientPlaceName: String = ""
    var complaintType: String = ""

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMoveInOutBinding.inflate(inflater, container, false)
        bindViewModel()
        return binding.root
    }

    private fun bindViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(NetworkApiHelper(RetrofitBuilder.apiService))
        )[MovementViewModel::class.java]

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
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
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
                val taskResponse: TaskResponse = binding.spinTask.selectedItem as TaskResponse
                checkIn(
                    taskResponse.TaskId.toString(), complaintNo,
                    machineNumber,
                    clientName,
                    clientPlaceName,
                    complaintType
                )
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
                    try {
                        DialogUtils.dismissDialog()
                        resource.data?.let { response ->
                            val aa = ArrayAdapter(
                                requireContext(),
                                R.layout.simple_spinner_item,
                                response
                            )
                            binding.spinTask.adapter = aa
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
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
                    try {
                        DialogUtils.dismissDialog()
                        resources.data?.let { response ->
                            if (arguments?.getBoolean("isCheckIn")!!) {
                                binding.etFromLocation.setText(response[0].LocationAddress)
                                tourId = response[0].tourid
                            }

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
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
                    try {
                        DialogUtils.dismissDialog()
                        resources.data?.let { response ->
                            DialogUtils.showSuccessDialog(
                                requireActivity(),
                                response[0].status.toString(),
                                CbslMain::class.java
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
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
                    try {
                        DialogUtils.dismissDialog()
                        resources.data?.let { response ->
                            DialogUtils.showSuccessDialog(
                                requireActivity(),
                                response[0].status.toString(),
                                CbslMain::class.java
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}