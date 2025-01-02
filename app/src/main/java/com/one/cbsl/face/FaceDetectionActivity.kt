package com.one.cbsl.face

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.one.cbsl.R
import com.one.cbsl.databinding.ActivityFaceDetectionBinding
import com.one.cbsl.face.cameraxx.CameraManager
import com.one.cbsl.face.cameraxx.GraphicOverlay
import com.one.cbsl.face.cameraxx.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class FaceDetectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaceDetectionBinding
    private lateinit var cameraManager: CameraManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createCameraManager()
        // to handle the permission
        cameraPermission()
    }

    private fun openCamera() {
        // this will start the camera if permission is enabled
        cameraManager.startCamera()
    }

    private fun cameraPermission() {
        val cameraPermission = Manifest.permission.CAMERA

        if (ContextCompat.checkSelfPermission(
                this, cameraPermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, cameraPermission
            )
        ) {
            val title = "Permission Required"
            val message = "App needs Camera Permission to detect faces"
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("OK") { dialog, _ ->
                    requestCameraPermissionLauncher.launch(cameraPermission)
                    dialog.dismiss()
                }.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create().show()
        } else {
            requestCameraPermissionLauncher.launch(
                cameraPermission
            )
        }
    }

    private val requestCameraPermissionLauncher = this.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.CAMERA
            )
        ) {
            val title = "Permission required"
            val message =
                "Please allow camera permission to detect faces"
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("Change Settings") { _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", this.packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create().show()
        } else {
            cameraPermission()
        }
    }
    private fun createCameraManager() {
        cameraManager = CameraManager(
            this,
            binding.previewViewFinder,
            this,
            binding.graphicOverlayFinder,
            ::checkStatus
        )
    }

    private fun checkStatus(status: Status) {
        Log.e("status","$status")
        when (status) {
            Status.MULTIPLE_FACES -> {
                binding.tvWarningText.text = "Multiple Faces detected"
            }
            Status.NO_FACE -> {
                binding.tvWarningText.text = "No Face detected"
            }
            Status.LEFT_EYE_CLOSED -> {
                binding.tvWarningText.text = "Left eye is closed"
            }
            Status.RIGHT_EYE_CLOSED -> {
                binding.tvWarningText.text ="Right eye is closed"
            }
            Status.BOTH_EYES_CLOSED->{
                binding.tvWarningText.text = "Both Eyes are closed"
            }
            Status.FAKE_FACE->{
                binding.tvWarningText.text = "Both Eyes are closed"
            }
            Status.VALID_FACE ->{
                binding.tvWarningText.text ="Face Detected"
            }
        }
    }
}