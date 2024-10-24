package com.one.cbsl.ui.attendance.other

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.one.cbsl.CbslMain
import com.one.cbsl.R
import com.one.cbsl.databinding.ActivityLoginBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.other.model.LoginModel
import com.one.cbsl.ui.attendance.other.viewmodel.LoginViewModel
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.Resource
import com.one.cbsl.utils.SessionManager
import com.one.cbsl.utils.permissions.PermissionRequest
import com.one.cbsl.utils.permissions.PermissionRequestHandler


class LoginActivity : AppCompatActivity(),
    PermissionRequest.RequestCustomPermissionGroup {
    private var _binding: ActivityLoginBinding? = null

    private var loginViewModel: LoginViewModel? = null
    private val binding get() = _binding!!
    var deviceId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView()
        _binding?.memberLogin?.setOnClickListener {

            when {
                TextUtils.isEmpty(binding.memberId.text.toString()) -> {
                    Toast.makeText(
                        this@LoginActivity, "UserName Can't Left Blank", Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.memberPass.text.toString()) -> {
                    Toast.makeText(
                        this@LoginActivity, "Password Can't Left Blank", Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {

                    SessionManager.getInstance().putString(Constants.DEVICE_ID, deviceId)
                    if (deviceId.isNullOrEmpty()) {
                        deviceId == null
                    }
                    loginUser(deviceId)
                }
            }

        }
        checkAllPermission()
    }

    private fun loginUser(deviceId: String?) {
        DialogUtils.showProgressDialog(this, "Fetching Data")
        loginViewModel?.getLoginResponse(
            binding.memberId.text.toString(),
            binding.memberPass.text.toString(),
            deviceId.toString()
        )
            ?.observe(this, Observer { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        DialogUtils.showProgressDialog(this, "Fetching Data")
                    }

                    is Resource.Success -> {
                        try {
                            DialogUtils.dismissDialog()
                            // Handle success
                            val response = resource.data

                            // Handle success
                            if (response?.get(0)?.loginStatus == "valid") {
                                setSessionData(response)
                                DialogUtils.showSuccessDialog(
                                    this, "Success", CbslMain::class.java
                                )
                            } else {
                                DialogUtils.showFailedDialog(
                                    this, response?.get(0)?.loginStatus.toString()
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    is Resource.Error -> {
                        // Handle error
                        DialogUtils.showFailedDialog(this, resource.message.toString())
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
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            PermissionRequestHandler.requestCustomPermissionGroup(
                this,
                "",
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }

    }


    private fun setSessionData(loginResponse: List<LoginModel>) {
        SessionManager.getInstance().putBoolean(Constants.isLoginByDevice, true)
        SessionManager.getInstance().putBoolean(Constants.isLogin, true)

        SessionManager.getInstance()
            .putString(Constants.UserName, loginResponse[0].employeeName)

        SessionManager.getInstance()
            .putString(Constants.COMPANY, loginResponse[0].companyName)

        SessionManager.getInstance()
            .putString(Constants.UserId, loginResponse[0].userId)

        SessionManager.getInstance()
            //.putString(Constants.EmpCode, loginResponse[0].employeeCode)
            .putString(Constants.EmpCode, loginResponse[0].employeeCode)
        //.putString(Constants.EmpCode, "1051359309")

        SessionManager.getInstance()
            .putString(Constants.UserTypeID, loginResponse[0].userTypeId)

        SessionManager.getInstance()
            .putString(Constants.Mobile, loginResponse[0].mobileNo)
    }

    @SuppressLint("HardwareIds")
    private fun bindView() {
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginViewModel = ViewModelProvider(
            this, ViewModelFactory(NetworkApiHelper(RetrofitBuilder.apiService))
        )[LoginViewModel::class.java]

        deviceId = Settings.Secure.getString(
            this@LoginActivity.contentResolver, Settings.Secure.ANDROID_ID
        )

    }

    override fun onAllCustomPermissionGroupGranted() {
        Log.d("", "Permission granted")
    }

    override fun onCustomPermissionGroupDenied() {
        checkAllPermission()
    }

}