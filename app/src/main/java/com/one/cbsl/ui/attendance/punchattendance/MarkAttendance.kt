package com.one.cbsl.ui.attendance.punchattendance

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.one.cbsl.CbslMain
import com.one.cbsl.R
import com.one.cbsl.databinding.FragmentMarkAttendanceBinding
import com.one.cbsl.localdb.AttendanceMaster
import com.one.cbsl.localdb.DbRepository
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.punchattendance.model.AttendanceResponse
import com.one.cbsl.ui.attendance.punchattendance.model.AttendanceTypeResponse
import com.one.cbsl.ui.attendance.punchattendance.viewmodel.AttendanceViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager
import com.one.cbsl.utils.Utils
import com.one.cbsl.utils.permissions.PermissionRequest
import com.one.cbsl.utils.permissions.PermissionRequestHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.util.*

class MarkAttendance : Fragment(), OnMapReadyCallback,
    PermissionRequest.RequestCustomPermissionGroup, View.OnClickListener {
    private var previousCircle: Circle? = null

    private var _binding: FragmentMarkAttendanceBinding? = null
    private var viewModel: AttendanceViewModel? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    var lastlocation: Location? = null
    var isFlag: Boolean = false
    private lateinit var mLocationRequest: LocationRequest
    private val INTERVAL: Long = 3000
    private val FASTEST_INTERVAL: Long = 500
    private var marker: Marker? = null // Global marker variable

    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    var lat: Double? = null
    var attendanceTypeId: String = "1"
    var lng: Double? = null
    var recordsList: List<AttendanceMaster> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLocationRequest = LocationRequest.create().apply { }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(
            this, ViewModelFactory(NetworkApiHelper(RetrofitBuilder.apiService))
        )[AttendanceViewModel::class.java]

        _binding = FragmentMarkAttendanceBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DialogUtils.showProgressDialog(requireActivity(), "Fetching Location")

        initMapView()

        checkAllPermission()
        checkPunchIn()

        binding.ivCurrentLoc.setOnClickListener(this)
        binding.llPunch.setOnClickListener(this)
        binding.llPunchOut.setOnClickListener(this)

        binding.spinAttendanceType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.attendance_type_list)
        )
        //getAttendanceType()

        binding.spinAttendanceType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, pos: Int, id: Long
                ) {
                    try {
                        attendanceTypeId = (pos + 1).toString()
                        /*    val response = parent.getItemAtPosition(pos) as AttendanceTypeResponse
                            attendanceTypeId = response.AttendanceTypeId*/
                        if (attendanceTypeId == "2") {
                            binding.llOdLayout.visibility = View.VISIBLE
                        } else {
                            binding.llOdLayout.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } // to close the onItemSelected


                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }


    }

    private fun initMapView() {
        try {
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPunchIn() {
        if (SessionManager.getInstance().getBoolean(Constants.isPunchIn)) {
            binding.llPunchOut.visibility = View.VISIBLE
            binding.llPunch.visibility = View.GONE
        } else {
            binding.llPunchOut.visibility = View.GONE
            binding.llPunch.visibility = View.VISIBLE
        }
    }

    private fun checkAllPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionRequestHandler.requestCustomPermissionGroup(
                this,
                "",
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,

                )
        } else {
            PermissionRequestHandler.requestCustomPermissionGroup(
                this,
                "",
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,



                )
        }

    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        isFlag=false
        if (isLocationEnabled()) {
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            mLocationRequest.interval = INTERVAL
            mLocationRequest.fastestInterval = FASTEST_INTERVAL

            // Create LocationSettingsRequest object using location request
            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest)
            builder.setAlwaysShow(true)
            val locationSettingsRequest = builder.build()

            val settingsClient = LocationServices.getSettingsClient(requireActivity())
            settingsClient.checkLocationSettings(locationSettingsRequest)

            mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            // new Google API SDK v11 uses getFusedLocationProviderClient(this)
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback, Looper.getMainLooper()
            )


        } else {
            Toast.makeText(requireActivity(), "Please turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    fun onLocationChanged(location: Location?) {
        try {
            lastlocation = location
            lat = lastlocation!!.latitude
            lng = lastlocation!!.longitude
            getAddress()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun getAddress() {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val address = geocoder.getFromLocation(lat!!, lng!!, 1)
            binding.tvCurrentLoc.text = address?.get(0)?.getAddressLine(0)?.toString()
            addMarkerToCurrentLocation()
        } catch (e: Exception) {
            if (!isFlag) {
                DialogUtils.dismissDialog()
            }
            e.printStackTrace();
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun addMarkerToCurrentLocation() {
        try {
            val zoomLevel = 18.0f
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(lat!!, lng!!), zoomLevel
                )
            )
            updateMarker(
                LatLng(
                    lat!!, lng!!
                )
            )

            if (!isFlag) {
                DialogUtils.dismissDialog()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun updateMarker(location: LatLng) {
        // Remove the existing marker if it exists
        marker?.remove()

        // Add a new marker to the updated location

        marker = mMap.addMarker(
            MarkerOptions().position(location)

        )
        previousCircle?.remove()

// Add a new circle and keep a reference to it
        previousCircle = mMap.addCircle(
            CircleOptions().center(location)  // Center the circle on the marker's location
                .radius(50.0)  // Set the radius
                .strokeColor(Color.BLUE)  // Set the border color
                .strokeWidth(5f)  // Set the width of the border
                .fillColor(Color.argb(100, 0, 0, 255))
        )
    }


    private fun getAttendanceType() {
        viewModel?.getAttendanceType()?.observe(requireActivity(), Observer { resource ->
            when (resource) {
                is Resource.Loading -> {
                }

                is Resource.Success -> {
                    try {
                        //   DialogUtils.dismissDialog()
                        resource.data?.let { response ->
                            val aa = ArrayAdapter(
                                requireContext(), android.R.layout.simple_spinner_item, response
                            )
                            binding.spinAttendanceType.adapter = aa
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


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.apply {
            clear()
            mapType = GoogleMap.MAP_TYPE_SATELLITE
            isBuildingsEnabled = true
            uiSettings.isZoomControlsEnabled = false
        }
        getLocation()
    }


    override fun onAllCustomPermissionGroupGranted() {
        getLocation()
    }

    override fun onCustomPermissionGroupDenied() {
        checkAllPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.llPunchOut -> {
                SessionManager.getInstance().putBoolean(Constants.isPunchIn, false)
                if (binding.tvCurrentLoc.text.toString().isEmpty()) {
                    DialogUtils.showFailedDialog(requireActivity(), "No Location Please Try Again")
                    checkAllPermission()
                } else {
                    try {
                        if (attendanceTypeId == "2") {
                            if (binding.etOdClientName.text.toString()
                                    .trim().length > 3 && binding.etPurpose.text.toString()
                                    .trim().length > 3
                            ) {
                                punchOutAttendance()
                            } else {
                                DialogUtils.showFailedDialog(
                                    requireActivity(),
                                    "Enter Client Name or Purpose",
                                )
                            }
                        } else {
                            punchOutAttendance()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            binding.llPunch -> {
                try {
                    SessionManager.getInstance().putBoolean(Constants.isPunchIn, true)
                    if (binding.tvCurrentLoc.text.toString().isEmpty()) {
                        DialogUtils.showFailedDialog(
                            requireActivity(), "No Location Please Try Again"
                        )
                        checkAllPermission()
                    } else {
                        if (attendanceTypeId == "2") {
                            if (binding.etOdClientName.text.toString()
                                    .trim().length > 3 && binding.etPurpose.text.toString()
                                    .trim().length > 3
                            ) {
                                punchInAttendance()
                            } else {
                                Toast.makeText(
                                    requireActivity(),
                                    "Enter Client Name or Purpose",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            punchInAttendance()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.ivCurrentLoc -> {
                DialogUtils.showProgressDialog(requireActivity(), "Fetching Location")

                getLocation()
            }
        }
    }

    private fun punchInAttendance() {
        isFlag = true

        viewModel?.punchInAttendance(
            binding.tvCurrentLoc.text.toString(),
            lat.toString(),
            lng.toString(),
            attendanceTypeId.toString(),
            binding.etPurpose.text.toString(),
            binding.etOdClientName.text.toString()
        )?.observe(requireActivity(), Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    try {

                        resources.data?.let { response ->
                            continueProcess(response, "PunchIn")

                            //    viewModel.getAttendanceStatus()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Punch In Process..")
                }

                is Resource.Error -> {
                    //Handle Error
                    punchOfflineAttendance(0);
                }
            }
        })
    }

    private fun punchOutAttendance() {
        viewModel?.punchoutAttendancebyId(
            binding.tvCurrentLoc.text.toString(),
            attendanceTypeId.toString(),
            lat.toString(),
            lng.toString(),
            binding.etPurpose.text.toString(),
            binding.etOdClientName.text.toString(),
        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    try {
                        DialogUtils.dismissDialog()
                        resources.data?.let { response ->
                            continueProcess(response, "PunchOut")
                            //    viewModel.getAttendanceStatus()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "PunchOut in Progress")
                }

                is Resource.Error -> {
                    //Handle Error
                    punchOutAttendance(0)
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())
                }
            }
        })
    }

    private fun continueProcess(response: List<AttendanceResponse>, s: String) {
        if (response[0].MarkStatus.toString().contains("RADIUS")) {
            DialogUtils.showFailedDialog(
                requireActivity(), "You are not in Office Radius"
            )
        } else {
            DialogUtils.showSuccessDialog(
                requireActivity(), "$s Successfully", CbslMain::class.java
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            109 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }

    private fun punchOfflineAttendance(i: Int) {
        DbRepository.getInstance(requireActivity()).punchAttendance(
            attendanceTypeId.toString(),
            Utils.getTodayDate(),
            Utils.getCurrentTime(),
            binding.tvCurrentLoc.text.toString(),
            lat.toString(),
            lng.toString(),
            i
        )
        DialogUtils.showSuccessDialog(
            requireActivity(), "Attendance Marked Successfully", CbslMain::class.java
        )
    }

    private fun punchOutAttendance(i: Int) {
        DbRepository.getInstance(requireActivity()).punchOut(
            Utils.getTodayDate(),
            Utils.getCurrentTime(),
            binding.tvCurrentLoc.text.toString()
        )
    }

    private fun loadOfflineAttendance() {
        DbRepository.getInstance(requireActivity()).appDatabase
            .crcDao().getAttendance(0)
            .observe(requireActivity(),
                Observer<List<AttendanceMaster>> { t ->
                    try {
                        recordsList = t

                    } catch (e: Exception) {

                        e.printStackTrace()
                    }
                })
    }

    private fun synData() {
        for (i in recordsList.indices) {

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
            )?.observe(viewLifecycleOwner, Observer { resources ->
                when (resources) {
                    is Resource.Success -> {
                        try {
                            DialogUtils.dismissDialog()
                            resources.data?.let { response ->
                                if (response[0].MarkStatus != "ERROR...") {
                                    Toast.makeText(
                                        requireContext(),
                                        response[0].MarkStatus,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    CoroutineScope(Dispatchers.IO).launch {
                                        DbRepository.getInstance(requireActivity()).appDatabase.crcDao()
                                            .updateStatus(
                                                recordsList[i].id,
                                                Utils.getTodayDate(),
                                                response[0].MarkStatus
                                            )

                                    }
                                }
                                //    viewModel.getAttendanceStatus()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Punch In Process..")
                    }

                    is Resource.Error -> {
                        //Handle Error
                        DialogUtils.showFailedDialog(
                            requireActivity(),
                            resources.message.toString()
                        )
                        punchOfflineAttendance(0);
                    }
                }
            });
        }
    }
}


