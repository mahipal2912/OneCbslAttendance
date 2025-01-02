package com.one.cbsl.ui.attendance.profile

import android.annotation.SuppressLint
import android.graphics.Bitmap
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.ParsedRequestListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.one.cbsl.R
import com.one.cbsl.databinding.MyProfileFragmentBinding
import com.one.cbsl.networkcall.NetworkApiHelper
import com.one.cbsl.networkcall.RetrofitBuilder
import com.one.cbsl.networkcall.base.ViewModelFactory
import com.one.cbsl.ui.attendance.punchattendance.model.AttendanceResponse
import com.one.cbsl.utils.Cbsl
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.SessionManager
import com.one.cbsl.utils.Utils
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MyProfileFragment : Fragment() {

    companion object {
        fun newInstance() = MyProfileFragment()
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: MyProfileFragmentBinding // Declare binding variable
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
        binding = MyProfileFragmentBinding.inflate(inflater, container, false) // Initialize binding
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
        Glide.with(this)
            .load(SessionManager.getInstance().getString(Constants.IMAGE))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(binding.profileImage) // Use binding to access views
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.etUserName.text = SessionManager.getInstance().getString(Constants.UserName)
        binding.etEmail.text = SessionManager.getInstance().getString(Constants.Email)
        binding.etMobile.text = SessionManager.getInstance().getString(Constants.Mobile)

        binding.tvSave.setOnClickListener {
            uploadImage()
        }

        binding.startBtn.setOnClickListener {
            takePhoto()
        }
        binding.tvChangePass.setOnClickListener {
            findNavController().navigate(R.id.changePasswordFragment)
        }
        binding.tvChangeImage.setOnClickListener {
            binding.cameraLayout.visibility = View.VISIBLE
            binding.llProfileLayout.visibility = View.GONE
            startCamera()

        }
        binding.closeBtn.setOnClickListener {
            binding.cameraLayout.visibility = View.GONE
            binding.llProfileLayout.visibility = View.VISIBLE
        }

    }

    @SuppressLint("CheckResult")
    private fun uploadImage() {
        binding.progressBar.visibility = View.VISIBLE // Use binding for progressBar
        AndroidNetworking.initialize(requireContext())
        AndroidNetworking.post("https://hrisapi.cbslgroup.in/webmethods/apiwebservice.asmx/uploadUserImage")
            .addBodyParameter("userId", SessionManager.getInstance().getString(Constants.UserId))
            .addBodyParameter("empcode", SessionManager.getInstance().getString(Constants.EmpCode))
            .addBodyParameter("address", binding.etAddress.text.toString())
            .addBodyParameter("imageLocation", image_base)
            .addBodyParameter("AuthHeader", Constants.AUTH_HEADER)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsObjectList(AttendanceResponse::class.java, object: ParsedRequestListener<List<AttendanceResponse>> {

                override fun onResponse(userList: List<AttendanceResponse>?) {
                    try {
                        Toast.makeText(requireActivity(), userList?.get(0)?.MarkStatus , Toast.LENGTH_SHORT)
                            .show()
                        binding.progressBar.visibility = View.GONE
                        findNavController().navigate(R.id.navigation_home)

                    } catch (e: Exception) {
                        binding.progressBar.visibility = View.GONE
                        e.printStackTrace()
                    }
                }

                override fun onError(anError: ANError?) {
                    binding.progressBar.visibility = View.GONE

                }

            })

    }


    //camera funcationality
    private fun takePhoto() {

        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT,
                Locale.US
            ).format(System.currentTimeMillis()) + ".jpeg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireActivity()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    photoUri = Uri.fromFile(photoFile)
                    image_base =
                        getEncoded64ImageStringFromBitmap(Utils.getBitmap(photoUri)).toString()
                    binding.profileImage.setImageBitmap(Utils.getBitmap(photoUri))

                    binding.llProfileLayout.visibility = View.VISIBLE
                    binding.cameraLayout.visibility = View.GONE
                }
            })
    }

    fun getEncoded64ImageStringFromBitmap(bitmap: Bitmap): String? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        val byteFormat = stream.toByteArray()
        // get the base 64 string
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP)
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

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        val filesDir = File(requireContext().externalCacheDir!!.path)
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

}