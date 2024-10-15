package com.one.cbsl

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

import com.one.cbsl.adapter.MainOptionsAdapter
import com.one.cbsl.databinding.ActivityCbslMainBinding
import com.one.cbsl.ui.attendance.other.LoginActivity
import com.one.cbsl.utils.Cbsl
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.SessionManager
import com.one.cbsl.utils.Utils
import java.io.File

class CbslMain : AppCompatActivity(), MainActivityListener, MainOptionsAdapter.OptionListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityCbslMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCbslMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarCbslMain.toolbar)
        binding.header.tvEdit.setOnClickListener {
            closeDrawer()
            findNavController(R.id.nav_host_fragment_content_cbsl_main).navigate(R.id.changeProfile)
        }
        checkAppUpdate()

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController =
            Navigation.findNavController(this, R.id.nav_host_fragment_content_cbsl_main)
        // Passing each menu ID as a set fof Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_leave
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_cbsl_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun setDrawerLocked(shouldLock: Boolean) {
        if (shouldLock) {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    override fun closeDrawer() {
        binding.drawerLayout.close()

    }

    @SuppressLint("WrongConstant")
    override fun openDrawer() {
        binding.drawerLayout.openDrawer(Gravity.START)
    }

    override fun updateValues() {
        binding.header.headerName.text = SessionManager.getInstance().getString(Constants.UserName)
        binding.header.headerEmpCode.text =
            SessionManager.getInstance().getString(Constants.EmpCode)
        Glide.with(Cbsl.getInstance()).load(SessionManager.getInstance().getString(Constants.IMAGE))
            .into(binding.header.imageView)
        binding.rvMainOptions.adapter = MainOptionsAdapter(this)
        if (!SessionManager.getInstance().getBoolean(Constants.isLogin)) {
            val intent = Intent(this@CbslMain, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    override fun isDrawerOpened(): Boolean {
        return (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
    }

    override fun onItemClick(item: String) {
        when (item) {
            Constants.Attendance -> {
                findNavController(R.id.nav_host_fragment_content_cbsl_main).navigate(R.id.home_to_attendance)
            }

            Constants.Movement -> {
                findNavController(R.id.nav_host_fragment_content_cbsl_main).navigate(R.id.navigate_to_my_movement)
            }

            Constants.Conveyance -> {
                findNavController(R.id.nav_host_fragment_content_cbsl_main).navigate(R.id.navigate_to_my_conveyance)
            }

            Constants.LeavePlan -> {
                findNavController(R.id.nav_host_fragment_content_cbsl_main).navigate(R.id.home_to_leave_fragment)
            }

            Constants.Voucher -> {
                findNavController(R.id.nav_host_fragment_content_cbsl_main).navigate(R.id.home_to_voucher_fragment)
            }

            Constants.ApprovalHod -> {
                if (SessionManager.getInstance().getString(Constants.UserTypeID) != "2") {
                    findNavController(R.id.nav_host_fragment_content_cbsl_main).navigate(R.id.home_to_conveyance_hod_approval)
                } else {
                    DialogUtils.showFailedDialog(this, "Access Denied")
                }
            }

            Constants.ApprovalHead -> {
                if (SessionManager.getInstance().getString(Constants.UserTypeID) != "2") {
                    findNavController(R.id.nav_host_fragment_content_cbsl_main).navigate(R.id.home_to_conveyance_head_approval)
                } else {
                    DialogUtils.showFailedDialog(this, "Access Denied")
                }
            }

            Constants.Complaint -> {
                findNavController(R.id.nav_host_fragment_content_cbsl_main).navigate(R.id.home_to_complain_fragment)
            }
        }
        closeDrawer()
    }

    private fun checkAppUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)

// Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    101
                )
            }
        }
    }

    private fun updateAlert() {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Update Available")
        builder.setCancelable(false)
        //set message for alert dialog
        builder.setMessage(R.string.app_update_text)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Update") { dialogInterface, which ->
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
        //performing cancel action
        /*  builder.setNeutralButton("Cancel") { dialogInterface, which ->

          }
  */
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    @SuppressLint("HardwareIds")
    override fun onResume() {
        super.onResume()
        val deviceId: String =
            Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        SessionManager.getInstance().putString(Constants.DEVICE_ID, deviceId)
    }
}