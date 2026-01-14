package com.one.cbsl.ui.attendance.other

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.one.cbsl.CbslMain
import com.one.cbsl.MainActivityListener
import com.one.cbsl.R
import com.one.cbsl.face.FaceDetectionActivity
import com.one.cbsl.face.activityhyh.FaceActivity
import com.one.cbsl.utils.Cbsl
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.SessionManager

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        SessionManager.getInstance().putBoolean(Constants.IsChangeServer, false)
        if (Constants.isDeveloperModeEnabled(Cbsl.getInstance())) {
            showDevModeDialog()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                if (!SessionManager.getInstance().getBoolean(Constants.isLogin)) {
                    val intent = Intent(this@SplashScreen, LoginActivity::class.java)
                    startActivity(intent)
                    finish()  // Call finish() to close the SplashScreen activity
                } else {
                    val intent = Intent(this@SplashScreen, CbslMain::class.java)
                    startActivity(intent)
                    finish()
                }
            }, 2000)
        }

    }
    private fun showDevModeDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.dev_mode_title))
            .setMessage(getString(R.string.dev_mode_message))
            .setCancelable(false) // can't dismiss by tapping outside or back
            .setPositiveButton(getString(R.string.dev_mode_exit)) { _, _ ->
                // exit the app (removes all activities)
                finishAffinity()
            }
            .create()

        // Prevent back key dismiss on older devices
        dialog.setOnKeyListener { _, keyCode, _ ->
            // return true to consume event; but setCancelable(false) is usually enough
            true
        }

        dialog.show()
    }
}
