package com.one.cbsl.ui.attendance.profile

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.gson.Gson
import com.one.cbsl.R
import com.one.cbsl.databinding.FragmentCaptureFaceBinding
import com.one.cbsl.face.cameraxx.CameraManager
import com.one.cbsl.face.cameraxx.Status
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.punchattendance.model.AttendanceResponse
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.Constants.Companion.saveEmbeding
import com.one.cbsl.utils.DialogUtils
import com.one.cbsl.utils.SessionManager
import com.one.cbsl.utils.Utils
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class FaceVerificationCapture : Fragment() {

    companion object {
        fun newInstance() = FaceVerificationCapture()
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private lateinit var cameraManager: CameraManager

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: FragmentCaptureFaceBinding // Declare binding variable
    private lateinit var viewModel: MyProfileViewModel
    private var photoUri: Uri? = null
    private val PICK_IMAGE_MULTIPLE = 1
    private val PICK_CAM = 2

    private lateinit var imagePath: String
    private val imagesPathList: ArrayList<String> = arrayListOf()
    private val imagesuriList: ArrayList<Uri> = arrayListOf()

    private var profilePic: String = ""
    private var image_base: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupViewModel()
        binding =
            FragmentCaptureFaceBinding.inflate(inflater, container, false) // Initialize binding
        return binding.root
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(NetworkApiHelper(RetrofitBuilder.apiService))
        )[MyProfileViewModel::class.java]


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (SessionManager.getInstance().getString(Constants.VerifyType) == "0") {
            requireActivity().title = "Face Registration"

        } else {
            requireActivity().title = "Identity Verification"
        }

        binding.closeBtn.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
        }

        cameraManager = CameraManager(
            requireContext(),
            binding.surfaceView,
            this,
            binding.graphicOverlayFinder,
            ::checkStatus
        )
        cameraManager.startCamera()
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        //startCamera()

        binding.startBtn.setOnClickListener {
            if (binding.tvWarningText.text == "Face Detected") {
                //  takePhoto()
                cameraManager.takePicture(
                    context = requireContext(),
                    onImageCaptured = { imageResults ->
                        photoUri = Uri.fromFile(imageResults)
                        val bitmap = Utils.getBitmap(photoUri)

                        //  val croppedBitmap = cropBitmap(bitmap)
                        val croppedBitmap = getResizedBitmap(bitmap, 480, 480)
                        image_base = getEncoded64ImageStringFromBitmap(croppedBitmap).toString()
                        val file = imageResults // Assuming imageResults contains the file reference
                        try {
                            if (file.exists()) {
                                val deleted =
                                    file.delete() // Delete the file after creating the bitmap
                                if (deleted) {
                                    Log.d("CameraActivity", "File deleted successfully")
                                } else {
                                    Log.e("CameraActivity", "Failed to delete file")
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        //   saveCroppedImageToFile(croppedBitmap,imageResults)
                        if (SessionManager.getInstance().getString(Constants.VerifyType) == "0") {
                            registerImage()

                        } else {
                            uploadImage()
                        }


                        // Toast.makeText(this, "Image saved to ${imageResults.savedUri}", Toast.LENGTH_SHORT).show()
                    },
                    onError = { exception ->
                        // Handle error
                        Log.e("CameraActivity", "Error capturing image: ${exception.message}")
                    }
                )
            } else {
                Toast.makeText(
                    requireActivity(),
                    binding.tvWarningText.text.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    @SuppressLint("CheckResult")
    private fun uploadImage() {
        binding.progressBar.visibility = View.VISIBLE
        val jsonArray = JSONObject()
        jsonArray.put("image", image_base)
// Use binding for progressBar
        AndroidNetworking.initialize(requireContext())
        AndroidNetworking.post("https://4cce-103-62-239-162.ngrok-free.app/images")
            .addQueryParameter(
                "pickle_file",
                "db/" + SessionManager.getInstance()
                    .getString(Constants.CityName) + "/" + SessionManager.getInstance()
                    .getString(Constants.EmpCode) + ".pickle"
            )
            .addJSONObjectBody(jsonArray)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsObject(
                AttendanceResponse::class.java,
                object : ParsedRequestListener<AttendanceResponse> {

                    override fun onResponse(userList: AttendanceResponse?) {
                        try {
                            Toast.makeText(
                                requireActivity(),
                                userList?.message ?: userList?.error,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            binding.progressBar.visibility = View.GONE
                            findNavController().apply {
                                popBackStack()  // Remove the current fragment from the back stack
                                navigate(R.id.navigate_to_mark_attendance)  // Navigate to the new fragment
                            }

                        } catch (e: Exception) {
                            binding.progressBar.visibility = View.GONE
                            e.printStackTrace()
                        }
                    }

                    override fun onError(anError: ANError?) {
                        anError?.errorBody?.let { errorBody ->
                            // Parse the error body into an AttendanceResponse object
                            val gson = Gson()
                            val errorResponse =
                                gson.fromJson(errorBody, AttendanceResponse::class.java)

                            // Show error message from the parsed error response
                            val errorMessage =
                                errorResponse.message ?: errorResponse.error ?: "Unknown error"
                            DialogUtils.showFailedDialog(
                                requireActivity(),
                                errorMessage
                            )
                        } ?: run {
                            // In case errorBody is null, show a generic error message
                            Toast.makeText(
                                requireActivity(),
                                "Network error: ${anError?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        binding.progressBar.visibility = View.GONE
                        cameraManager.startCamera()

                    }

                })

    }

    @SuppressLint("CheckResult")
    private fun registerImage() {
        binding.progressBar.visibility = View.VISIBLE
        val jsonArray = JSONObject()
        jsonArray.put("image", image_base)
// Use binding for progressBar
        AndroidNetworking.initialize(requireContext())
        AndroidNetworking.post("https://4cce-103-62-239-162.ngrok-free.app/register")
            .addQueryParameter(
                "pickle_file",
                "db/" + SessionManager.getInstance()
                    .getString(Constants.CityName) + "/" + SessionManager.getInstance()
                    .getString(Constants.EmpCode) + ".pickle"
            )
            .addJSONObjectBody(jsonArray)
            .setPriority(Priority.HIGH)
            .build()
            .getAsObject(
                AttendanceResponse::class.java,
                object : ParsedRequestListener<AttendanceResponse> {

                    override fun onResponse(userList: AttendanceResponse) {
                        try {

                            saveEmbeding()
                            binding.progressBar.visibility = View.GONE
                            binding.closeBtn.performClick()

                        } catch (e: Exception) {
                            binding.progressBar.visibility = View.GONE
                            e.printStackTrace()
                        }
                    }

                    override fun onError(anError: ANError?) {
                        Toast.makeText(
                            requireActivity(),
                            "error : " + anError?.message.toString(),
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        binding.progressBar.visibility = View.GONE
                        cameraManager.startCamera()
                    }

                })

    }


    //camera funcationality
    private fun takePhoto() {


        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT,
                Locale.US
            ).format(System.currentTimeMillis()) + ".jpeg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireActivity()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    photoUri = Uri.fromFile(photoFile)
                    val bitmap = Utils.getBitmap(photoUri)
                    //val croppedBitmap = cropBitmap(bitmap)
                    image_base = getEncoded64ImageStringFromBitmap(bitmap).toString()
                    saveCroppedImageToFile(getResizedBitmap(bitmap, 480, 480), photoFile)
                    // Crop the image based on FrameOverlay's bounding box
                    /* val croppedBitmap = cropBitmap(bitmap)

                     image_base =
                         getEncoded64ImageStringFromBitmap(Utils.getBitmap(photoUri)).toString()*/
                    //  uploadImage()
                }
            })
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = (newWidth.toFloat()) / width
        val scaleHeight = (newHeight.toFloat()) / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        bm.recycle()
        return resizedBitmap
    }

    fun cropBitmap(originalBitmap: Bitmap): Bitmap {
        // Get the frameRect from the FrameOverlay
        val frameRect = binding.frameOverlay.getFrameRect()

        if (frameRect == null || originalBitmap.width == 0 || originalBitmap.height == 0) {
            Log.e("Crop", "Invalid frame or bitmap")
            return originalBitmap // Return the original bitmap if something is invalid
        }

        // Make sure the frameRect doesn't exceed the boundaries of the original bitmap
        val left = frameRect.left.coerceAtLeast(0)
        val top = frameRect.top.coerceAtLeast(0)

        // Ensure the width and height don't exceed the bitmap dimensions
        val right = (frameRect.left + frameRect.width()).coerceAtMost(originalBitmap.width)
        val bottom = (frameRect.top + frameRect.height()).coerceAtMost(originalBitmap.height)

        // Ensure that right >= left and bottom >= top
        val width = right - left
        val height = bottom - top

        // Only proceed if the cropping area is valid
        if (width > 0 && height > 0) {
            return Bitmap.createBitmap(originalBitmap, left, top, width, height)
        }

        // If the calculated width or height is zero or negative, return the original bitmap
        Log.e("Crop", "Invalid crop dimensions")
        return originalBitmap
    }

    fun getEncoded64ImageStringFromBitmap(bitmap: Bitmap): String? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        val byteFormat = stream.toByteArray()
        // get the base 64 string
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP)
    }

    fun saveCroppedImageToFile(bitmap: Bitmap, originalFile: File) {
        try {
            val outputStream = FileOutputStream(originalFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
        } catch (e: IOException) {
            Log.e("CameraManager", "Error saving cropped image: ${e.message}")
        }
    }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.surfaceView.createSurfaceProvider())
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e("", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private fun unbindCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun checkStatus(status: Status) {
        Log.e("status", "$status")
        when (status) {
            Status.MULTIPLE_FACES -> {
                binding.tvWarningText.text = "Multiple Faces detected"
            }

            Status.NO_FACE -> {
                binding.tvWarningText.text = "No Face detected"
            }

            Status.FAKE_FACE -> {
                binding.tvWarningText.text = "Fake Face"
            }

            Status.LEFT_EYE_CLOSED -> {
                binding.tvWarningText.text = "LEFT EYE CLOSED"
            }

            Status.RIGHT_EYE_CLOSED -> {
                binding.tvWarningText.text = "RIGHT EYE CLOSED"
            }

            Status.BOTH_EYES_CLOSED -> {
                binding.tvWarningText.text = "BOTH EYES CLOSED"
            }

            Status.VALID_FACE -> {
                binding.tvWarningText.text = "Face Detected"
                binding.startBtn.performClick()
            }


        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        val filesDir = File(requireContext().externalCacheDir!!.path)
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

}