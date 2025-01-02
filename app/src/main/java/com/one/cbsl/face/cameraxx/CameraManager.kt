package com.one.cbsl.face.cameraxx

import android.content.Context
import android.graphics.Rect
import android.icu.text.SimpleDateFormat
import android.os.Environment
import android.util.Log
import android.widget.TextView
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.face.Face

import com.google.mlkit.vision.face.FaceLandmark
import com.one.cbsl.R
import com.one.cbsl.face.FaceContourDetectionProcessor
import com.one.cbsl.face.FaceDetectionCallback

import java.io.File
import java.util.Locale

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(
    private val context: Context,
    private val previewView: PreviewView,
    private val lifecycleOwner: LifecycleOwner,
    private val graphicOverlay: GraphicOverlay,
    private val listener: ((Status) -> Unit),
) {

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService
    private var cameraSelectorOption = CameraSelector.LENS_FACING_FRONT
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyzer: ImageAnalysis? = null

    init {
        createNewExecutor()
    }

    private fun createNewExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()
                preview = Preview.Builder().build()

                imageCapture = ImageCapture.Builder().build()

                imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also {
                        it.setAnalyzer(cameraExecutor, selectAnalyzer())
                    }

                val cameraSelector =
                    CameraSelector.Builder().requireLensFacing(cameraSelectorOption).build()

                setCameraConfig(cameraProvider, cameraSelector)

            }, ContextCompat.getMainExecutor(context)
        )
    }

    // Custom analyzer
    private fun selectAnalyzer(): ImageAnalysis.Analyzer {
        return FaceDetection(graphicOverlay, listener)
    }

    private fun setCameraConfig(
        cameraProvider: ProcessCameraProvider?, cameraSelector: CameraSelector
    ) {
        try {
            cameraProvider?.unbindAll()
            camera = cameraProvider?.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageCapture, imageAnalyzer
            )
            preview?.setSurfaceProvider(
                previewView.createSurfaceProvider()
            )
        } catch (e: Exception) {
            Log.e("Error", "Use case binding failed", e)
        }
    }
    fun takePicture(
        context: Context,
        onImageCaptured: (file: File) -> Unit,
        onError: (exception: Exception) -> Unit
    ) {

        cameraExecutor = Executors.newSingleThreadExecutor()
        val photoFile = File(
            getOutputDirectory(),
            SimpleDateFormat(
                "yyyy-MM-dd-HH-mm-ss-SSS",
                Locale.US
            ).format(System.currentTimeMillis()) + ".jpeg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onImageCaptured(photoFile)
                    stopPreview()
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            }
        )
    }

    private fun stopPreview() {
        try {
            // Unbind all use cases, including the preview, to stop the camera
            cameraProvider?.unbindAll()

            // Optionally, you can also reset the preview and imageCapture for a new session
            preview?.clear()
            imageCapture = null
            preview = null
        } catch (e: Exception) {
            Log.e("CameraManager", "Error stopping preview: ${e.message}", e)
        }
    }
    private fun getOutputDirectory(): File {
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        val filesDir = File(context.externalCacheDir!!.path)
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun createFile(context: Context): File {
        val cacheDir = context.cacheDir // Use cache directory instead of external storage
        return File(cacheDir, "${System.currentTimeMillis()}.jpg")
    }
}