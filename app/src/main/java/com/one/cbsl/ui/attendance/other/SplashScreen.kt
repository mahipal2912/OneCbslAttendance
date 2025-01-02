package com.one.cbsl.ui.attendance.other

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.one.cbsl.CbslMain
import com.one.cbsl.MainActivityListener
import com.one.cbsl.R
import com.one.cbsl.face.FaceDetectionActivity
import com.one.cbsl.face.activityhyh.FaceActivity
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.SessionManager

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

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