package com.one.cbsl.ui.attendance.movement

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

import com.one.cbsl.CbslMain
import com.one.cbsl.R
import com.one.cbsl.databinding.FragmentMovementBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.movement.adapter.MovementAdapter
import com.one.cbsl.ui.attendance.movement.viewmodel.MovementViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager
import com.one.cbsl.utils.permissions.PermissionRequest
import com.one.cbsl.utils.permissions.PermissionRequestHandler

import java.util.*

class MovementFragment : Fragment(), MovementAdapter.OpitionListener,
    PermissionRequest.RequestCustomPermissionGroup, View.OnClickListener {

    private var _binding: FragmentMovementBinding? = null
    private var viewModel: MovementViewModel? = null
    var lat: Double? = null
    var lng: Double? = null
    var lastlocation: Location? = null
    var isFlag: Boolean = false
    private lateinit var mLocationRequest: LocationRequest
    private val INTERVAL: Long = 1000
    private val FASTEST_INTERVAL: Long = 500

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLocationRequest = LocationRequest.create().apply { }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMovementBinding.inflate(inflater, container, false)
        bindViewModel()
        return binding.root
    }

    private fun bindViewModel() {
        viewModel = ViewModelProvider(
            this, ViewModelFactory(NetworkApiHelper(RetrofitBuilder.apiService))
        )[MovementViewModel::class.java]
        getLocation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DialogUtils.showProgressDialog(requireActivity(), "loading...")
        binding.tvStart.setOnClickListener(this)
        binding.tvCompleteMovement.setOnClickListener(this)
        binding.btnAdd.setOnClickListener(this)
        binding.tvOtStationStart.setOnClickListener(this)

        checkTour()

        checkAllPermission()
        binding.tvMovementDate.text = Constants.getTodayData()
        binding.tvMovementDate.setOnClickListener {
            Constants.getDateSelection(requireActivity()) { selectedDate ->
                // Handle the selected date here
                binding.tvMovementDate.text = selectedDate
                getMovement(binding.tvMovementDate.text.toString())

            }
        }
        getMovement("")
        binding.btnAdd.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isCheckIn", true)
            findNavController().navigate(
                R.id.navigate_to_check_in,
                bundle
            )
        }

    }

    private fun checkTour() {
        if (SessionManager.getInstance().getString(Constants.IsTourActive) == "0") {
            binding.llStartMovement.visibility = View.VISIBLE
            binding.tvCompleteMovement.visibility = View.GONE
        } else {
            binding.llStartMovement.visibility = View.GONE
            binding.tvCompleteMovement.visibility = View.VISIBLE
        }
    }

    private fun getMovement(date: String) {
        viewModel?.getMyMovementData(
            date
        )?.observe(viewLifecycleOwner, Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    //  DialogUtils.dismissDialog()
                    try {
                        resources.data?.let { response ->
                            binding
                                .rvMyMovement.adapter = MovementAdapter(resources.data, this)
                            //    viewModel.getAttendanceStatus()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Fetching Location")
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())
                }
            }
        })
    }


    override fun onItemClick(
        movementCode: String?, location: String?, reason: String?, task: String?, checkOut: String?
    ) {
        if (checkOut == "Pending") {
            val bundle = Bundle()
            bundle.putBoolean("isCheckIn", false)
            bundle.putString("movementCode", movementCode)
            bundle.putString("fromLocation", location)
            bundle.putString("reason", reason)
            bundle.putString("task", task)
            findNavController().navigate(R.id.navigate_to_check_in, bundle)
        } else {
            DialogUtils.showFailedDialog(requireActivity(), "Already CheckOut")
        }
    }


    override fun onClick(view: View?) {
        when (view) {
            binding.tvStart -> {
                if (binding.tvLocation.text.toString().isEmpty()) {
                    checkAllPermission()
                    DialogUtils.showFailedDialog(requireActivity(), "No Location Found,Check Again")
                } else {
                    val builder = AlertDialog.Builder(activity)
                    builder.setMessage("Are you sure to Start One day local movement?")
                        .setCancelable(false).setPositiveButton("Start Local movement") { _, _ ->
                            startMovement("0")
                        }.setNegativeButton("Cancel") { dialog, _ ->
                            // Dismiss the dialog
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
                }
            }

            binding.tvOtStationStart -> {
                if (binding.tvLocation.text.toString().isEmpty()) {
                    checkAllPermission()
                } else {
                    val builder = AlertDialog.Builder(activity)
                    builder.setMessage("Are you sure to start Tour?").setCancelable(false)
                        .setPositiveButton("Start Tour") { _, _ ->
                            startMovement("1")
                        }.setNegativeButton("Cancel") { dialog, _ ->
                            // Dismiss the dialog
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()

                }

            }

            binding.tvCompleteMovement -> {
                //   checkAllPermission()
                val builder = AlertDialog.Builder(activity)
                builder.setMessage("Are you sure want to Complete Tour?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        completeTour()
                    }
                    .setNegativeButton("No") { dialog, id ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()

            }

            binding.btnAdd -> {
                checkAllPermission()
            }

        }
    }

    private fun startMovement(type: String) {
        if (binding.tvLocation.text.toString() != "") {
            viewModel?.startMovement(
                binding.tvLocation.text.toString(),
                lat.toString(),
                lng.toString(), type
            )?.observe(viewLifecycleOwner, Observer { resources ->
                when (resources) {
                    is Resource.Success -> {
                        DialogUtils.dismissDialog()
                        try {
                            resources.data?.let { response ->
                                DialogUtils.showSuccessDialog(
                                    requireActivity(),
                                    response[0].status,
                                    CbslMain::class.java
                                )
                                //    viewModel.getAttendanceStatus()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(requireActivity(), "Movement Loading")
                    }

                    is Resource.Error -> {
                        DialogUtils.showFailedDialog(
                            requireActivity(),
                            resources.message.toString()
                        )
                    }
                }
            })
        } else {
            DialogUtils.showFailedDialog(requireActivity(), "No Location Found")
            getLocation()
        }
    }

    private fun completeTour() {
        viewModel?.completeTour()?.observe(requireActivity(), Observer { resources ->
            when (resources) {
                is Resource.Success -> {
                    try {
                        DialogUtils.dismissDialog()
                        DialogUtils.showSuccessDialog(
                            requireActivity(),
                            resources.data?.get(0)?.status.toString(), CbslMain::class.java
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is Resource.Loading -> {
                    DialogUtils.showProgressDialog(requireActivity(), "Processing...")
                }

                is Resource.Error -> {
                    DialogUtils.showFailedDialog(requireActivity(), resources.message.toString())
                }
            }

        })
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
            try {
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

            } catch (e: Exception) {
                e.printStackTrace()
            }

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
            DialogUtils.dismissDialog()
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val address = geocoder.getFromLocation(lat!!, lng!!, 1)
            binding.tvLocation.text = address?.get(0)?.getAddressLine(0)?.toString()
        } catch (e: Exception) {
            e.printStackTrace();
        }
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


}