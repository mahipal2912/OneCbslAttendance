package com.one.cbsl.ui.attendance.punchattendance

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.YuvImage
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Pair
import android.util.Size
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController

import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import com.one.cbsl.CbslMain
import com.one.cbsl.R
import com.one.cbsl.face.activityhyh.SimilarityClassifier
import com.one.cbsl.utils.Constants
import com.one.cbsl.utils.SessionManager
import com.one.cbsl.utils.Utils

import org.tensorflow.lite.Interpreter

import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.ReadOnlyBufferException
import java.nio.channels.FileChannel
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import kotlin.experimental.inv

class FaceRecognizeActivitynew : AppCompatActivity() {

    private lateinit var detector: FaceDetector

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var previewView: PreviewView
    private lateinit var facePreview: ImageView
    private lateinit var tfLite: Interpreter
    private lateinit var recoName: TextView
    private lateinit var previewInfo: TextView
    private lateinit var textAbovePreview: TextView
    private lateinit var recognize: Button
    private lateinit var cameraSwitch: Button
    private lateinit var actions: Button
    private lateinit var addFace: ImageButton
    private lateinit var cameraSelector: CameraSelector
    

    private var developerMode = false
    private var distance = 0.6f
    private var start = true
    private var flipX = false

    private val context: Context = this
    private var camFace = CameraSelector.LENS_FACING_FRONT // Default to front camera

    private var intValues: IntArray? = null
    private val inputSize = 112 // Input size for the model
    private var isModelQuantized = false
    private lateinit var embeddings: Array<FloatArray>
    private val IMAGE_MEAN = 128.0f
    private val IMAGE_STD = 128.0f
    private val OUTPUT_SIZE = 192 // Output size of the model

    private  val SELECT_PICTURE = 1
    private lateinit var cameraProvider: ProcessCameraProvider
    private  val MY_CAMERA_REQUEST_CODE = 100

    private  val modelFile = "mobile_face_net.tflite" // Model name

    private val registered = HashMap<String, SimilarityClassifier.Recognition>() // Saved faces


    companion object {
        private const val MY_CAMERA_REQUEST_CODE = 100
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_recognize)

        facePreview = findViewById(R.id.imageView)
        recoName = findViewById(R.id.textView)
        previewInfo = findViewById(R.id.textView2)
        textAbovePreview = findViewById(R.id.textAbovePreview)
        addFace = findViewById(R.id.imageButton)
        addFace.visibility = View.INVISIBLE

        val defaultRecognition = SimilarityClassifier.Recognition("0", "", -1f).apply {
            setExtra(Utils.stringToFloatArray(SessionManager.getInstance().getString("embed")))
        }
        registered[SessionManager.getInstance().getString(Constants.UserName)] = defaultRecognition

        recoName.visibility = View.VISIBLE
        facePreview.visibility = View.INVISIBLE
        previewInfo.text = ""

        recognize = findViewById(R.id.button3)
        cameraSwitch = findViewById(R.id.button5)
        actions = findViewById(R.id.button2)
        textAbovePreview.text = "Recognized Face:"

        // Camera Permission
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_REQUEST_CODE)
        }

        // On-screen Action Button
        actions.setOnClickListener {
            showActionDialog()
        }
        cameraSwitch.setOnClickListener {
            camFace = if (camFace == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
            flipX = false
            cameraProvider.unbindAll()
            cameraBind()
        }

        addFace.setOnClickListener {
           // addFace()
        }

        recognize.setOnClickListener {
            start = true
            textAbovePreview.text = "Recognized Face:"
            recognize.text = "Add Face"
            addFace.visibility = View.INVISIBLE
            recoName.visibility = View.VISIBLE
            facePreview.visibility = View.INVISIBLE
            previewInfo.text = ""
        }

        // Load model
        try {
            tfLite = Interpreter(loadModelFile(this, modelFile))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Initialize Face Detector
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.8f)
            .enableTracking()
            .build()

        detector = FaceDetection.getClient(highAccuracyOpts)

        cameraBind()
    }

    private fun developerMode() {
        developerMode = !developerMode
        val message = if (developerMode) {
            "Developer Mode ON"
        } else {
            "Developer Mode OFF"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun showActionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Action:")

        val names = arrayOf(
            "View Recognition List",
            "Update Recognition List",
            "Save Recognitions",
            "Load Recognitions",
            "Clear All Recognitions",
            "Import Photo (Beta)",
            "Hyperparameters",
            "Developer Mode"
        )

        builder.setItems(names) { dialog, which ->
            when (which) {
                2 -> insertToSP(registered, 0) // Save all
                3 -> registered.putAll(readFromSP())
                5 -> loadPhoto()
                7 -> developerMode()
            }
        }

        builder.setPositiveButton("OK", null)
        builder.setNegativeButton("Cancel", null)

        val dialog = builder.create()
        dialog.show()
    }

    

    // Handle camera permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            val message = if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                "Camera permission granted"
            } else {
                "Camera permission denied"
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    // Load model file
    @Throws(IOException::class)
    private fun loadModelFile(activity: Activity, modelFile: String): MappedByteBuffer {
        val fileDescriptor = activity.assets.openFd(modelFile)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // Bind camera and preview view
    private fun cameraBind() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        previewView = findViewById(R.id.previewView)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()
        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(camFace)
            .build()

        preview.setSurfaceProvider(previewView.createSurfaceProvider())

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(640, 480))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // Latest frame is shown
            .build()

        val executor = Executors.newSingleThreadExecutor()
        imageAnalysis.setAnalyzer(executor) { imageProxy ->
            try {
                Thread.sleep(0) // Refresh camera preview every 10ms (adjust as required)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            val mediaImage = imageProxy.image
            val image: InputImage? = mediaImage?.let {
                InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)
            }

            // Process acquired image to detect faces
            image?.let {
                detector.process(it)
                    .addOnSuccessListener { faces ->
                        if (faces.isNotEmpty()) {
                            val face = faces[0]
                            val frameBmp = toBitmap(mediaImage)
                            val rot = imageProxy.imageInfo.rotationDegrees
                            val frameBmp1 = rotateBitmap(frameBmp, rot, false, false)

                            val boundingBox = RectF(face.boundingBox)
                            val croppedFace = getCropBitmapByCPU(frameBmp1, boundingBox)
                            val scaled = getResizedBitmap(croppedFace, 112, 112)

                            // Analyze landmarks
                            val landmarks = face.allLandmarks
                            val landmarksDetected = analyzeLandmarks(landmarks)

                            if (landmarksDetected) {
                                if (start) {
                                    recognizeImage(scaled) // Send scaled bitmap to create face embeddings.
                                }
                            } else {
                                recoName.text = "Face not detected!"
                            }
                        } else {
                            recoName.text = if (registered.isEmpty()) "Add Face" else "No Face Detected!"
                        }
                    }
                    .addOnFailureListener {
                        // Handle failure here
                    }
                    .addOnCompleteListener {
                        imageProxy.close() // Close ImageProxy to acquire the next frame
                    }
            }
        }

        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)
    }

    // Helper method to analyze landmarks and check if required ones are detected
    private fun analyzeLandmarks(landmarks: List<FaceLandmark>): Boolean {
        var hasAllLandmarks = true
        val requiredLandmarks = listOf(
            FaceLandmark.LEFT_EYE, FaceLandmark.MOUTH_RIGHT, FaceLandmark.MOUTH_BOTTOM,
            FaceLandmark.RIGHT_EYE, FaceLandmark.NOSE_BASE, FaceLandmark.LEFT_EAR,
            FaceLandmark.RIGHT_EAR, FaceLandmark.MOUTH_LEFT, FaceLandmark.LEFT_CHEEK,
            FaceLandmark.RIGHT_CHEEK
        )

        requiredLandmarks.forEach { requiredLandmark ->
            if (landmarks.none { it.landmarkType == requiredLandmark }) {
                hasAllLandmarks = false
            }
        }
        return hasAllLandmarks
    }
    // Recognize face image
    fun recognizeImage(bitmap: Bitmap) {
        // Set face preview image
        facePreview.setImageBitmap(bitmap)

        // Create ByteBuffer to store normalized image
        val imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
        imgData.order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputSize * inputSize)

        // Get pixel values from Bitmap to normalize
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        imgData.rewind()

        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixelValue = intValues[i * inputSize + j]
                if (isModelQuantized) {
                    // Quantized model
                    imgData.put((pixelValue shr 16 and 0xFF).toByte())
                    imgData.put((pixelValue shr 8 and 0xFF).toByte())
                    imgData.put((pixelValue and 0xFF).toByte())
                } else { // Float model
                    imgData.putFloat(((pixelValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    imgData.putFloat(((pixelValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    imgData.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                }
            }
        }

        // imgData is input to our model
        val inputArray = arrayOf(imgData)

        val outputMap = mutableMapOf<Int, Any>()
        outputMap[0] = embeddings

        // Run model
        tfLite.runForMultipleInputsOutputs(inputArray, outputMap)

        var distanceLocal = Float.MAX_VALUE
        var label = "?"

        // Compare new face with saved faces
        if (registered.isNotEmpty()) {
            val nearest = findNearest(embeddings[0]) // Find 2 closest matching faces

            nearest[0]?.let {
                val name = it.first // Get name and distance of closest matching face
                distanceLocal = it.second!!
                if (developerMode) {
                    if (distanceLocal < distance) {
                        recoName.text = "Nearest: $name\nDist: %.3f".format(distanceLocal) +
                                "\n2nd Nearest: ${nearest[1]?.first}\nDist: %.3f".format(nearest[1]?.second)
                    } else {
                        recoName.text = "Unknown\nDist: %.3f".format(distanceLocal) +
                                "\nNearest: $name\nDist: %.3f".format(distanceLocal) +
                                "\n2nd Nearest: ${nearest[1]?.first}\nDist: %.3f".format(nearest[1]?.second)
                    }
                } else {
                    Toast.makeText(context, "$distanceLocal  $distance", Toast.LENGTH_SHORT).show()
                    if (distanceLocal < distance) {
                        recoName.text = name
                        /*val navController = findNavController(R.id.nav_host_fragment_activity_main)
                        navController.navigate(R.id.navigate_to_mark_attendance)*/
                  //      findNavController().navigate(R.id.navigate_to_mark_attendance)
                    } else {
                        recoName.text = "Unknown"
                    }
                }
            }
        }
    }

    // Compare faces by distance between face embeddings
    private fun findNearest(emb: FloatArray): List<Pair<String, Float?>> {
        val neighbourList = mutableListOf<Pair<String, Float?>>()
        var ret: Pair<String, Float?>? = null // to get closest match
        var prevRet: Pair<String, Float?>? = null // to get second closest match

        for ((name, recognition) in registered) {
            val knownEmb = (recognition.extra as Array<FloatArray>)[0]

            var distance = 0f
            for (i in emb.indices) {
                val diff = emb[i] - knownEmb[i]
                distance += diff * diff
            }
            distance = kotlin.math.sqrt(distance)

            if (ret == null || distance < ret.second!!) {
                prevRet = ret
                ret = Pair(name, distance)
            }
        }

        prevRet = prevRet ?: ret
        neighbourList.add(ret!!)
        neighbourList.add(prevRet!!)

        return neighbourList
    }

    // Resize Bitmap
    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val scaleWidth = newWidth.toFloat() / bm.width
        val scaleHeight = newHeight.toFloat() / bm.height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, false)
        bm.recycle()
        return resizedBitmap
    }

    // Crop Bitmap by rectangle
    private fun getCropBitmapByCPU(source: Bitmap, cropRectF: RectF): Bitmap {
        val resultBitmap = Bitmap.createBitmap(cropRectF.width().toInt(), cropRectF.height().toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)
        val paint = Paint(Paint.FILTER_BITMAP_FLAG).apply { color = Color.WHITE }
        canvas.drawRect(RectF(0f, 0f, cropRectF.width(), cropRectF.height()), paint)

        val matrix = Matrix()
        matrix.postTranslate(-cropRectF.left, -cropRectF.top)
        canvas.drawBitmap(source, matrix, paint)

        if (source.isRecycled.not()) {
            source.recycle()
        }

        return resultBitmap
    }

    // Rotate Bitmap by degrees and optionally flip
    private fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Int, flipX: Boolean, flipY: Boolean): Bitmap {
        val matrix = Matrix().apply {
            postRotate(rotationDegrees.toFloat())
            postScale(if (flipX) -1f else 1f, if (flipY) -1f else 1f)
        }
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        // Recycle the old bitmap if it's not the same as the rotated bitmap
        if (rotatedBitmap != bitmap) {
            bitmap.recycle()
        }
        return rotatedBitmap
    }
    private fun YUV_420_888toNV21(image: Image): ByteArray {
        val width = image.width
        val height = image.height
        val ySize = width * height
        val uvSize = width * height / 4

        val nv21 = ByteArray(ySize + uvSize * 2)

        val yBuffer = image.planes[0].buffer // Y
        val uBuffer = image.planes[1].buffer // U
        val vBuffer = image.planes[2].buffer // V

        var rowStride = image.planes[0].rowStride
        require(image.planes[0].pixelStride == 1)

        var pos = 0

        if (rowStride == width) { // likely
            yBuffer.get(nv21, 0, ySize)
            pos += ySize
        } else {
            var yBufferPos = -rowStride // not an actual position
            for (row in 0 until height) {
                yBufferPos += rowStride
                yBuffer.position(yBufferPos.toInt())
                yBuffer.get(nv21, pos, width)
                pos += width
            }
        }

        rowStride = image.planes[2].rowStride
        val pixelStride = image.planes[2].pixelStride

        require(rowStride == image.planes[1].rowStride)
        require(pixelStride == image.planes[1].pixelStride)

        if (pixelStride == 2 && rowStride == width && uBuffer[0] == vBuffer[1]) {
            // maybe V and U planes overlap as per NV21
            val savePixel = vBuffer[1]
            try {
                vBuffer.put(1, savePixel.inv())
                if (uBuffer[0] == savePixel.inv()) {
                    vBuffer.put(1, savePixel)
                    vBuffer.position(0)
                    uBuffer.position(0)
                    nv21[pos++] = vBuffer[1]
                    uBuffer.get(nv21, pos, uBuffer.remaining())
                    return nv21 // shortcut
                }
            } catch (ex: ReadOnlyBufferException) {
                // cannot check if vBuffer and uBuffer overlap
            }
            vBuffer.put(1, savePixel)
        }

        // Save U and V pixel by pixel
        for (row in 0 until height / 2) {
            for (col in 0 until width / 2) {
                val vuPos = col * pixelStride + row * rowStride
                nv21[pos++] = vBuffer[vuPos]
                nv21[pos++] = uBuffer[vuPos]
            }
        }

        return nv21
    }

    private fun toBitmap(image: Image): Bitmap {
        val nv21 = YUV_420_888toNV21(image)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)

        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    // Save Faces to Shared Preferences. Conversion of Recognition objects to JSON string
    private fun insertToSP(jsonMap: HashMap<String, SimilarityClassifier.Recognition>, mode: Int) {
        if (mode == 1) { // mode: 0:save all, 1:clear all, 2:update all
            jsonMap.clear()
        } else if (mode == 0) {
            jsonMap.putAll(readFromSP())
        }
        val jsonString = Gson().toJson(jsonMap)
        SessionManager.getInstance().putString("map", jsonString)
        Toast.makeText(context, "Recognitions Saved", Toast.LENGTH_SHORT).show()
    }

    // Load Faces from Shared Preferences. JSON String to Recognition object
    private fun readFromSP(): HashMap<String, SimilarityClassifier.Recognition> {
        val defValue = Gson().toJson(HashMap<String, SimilarityClassifier.Recognition>())
        val result = SimilarityClassifier.Recognition("0", "", -1f).apply {
            extra = Utils.stringToFloatArray(SessionManager.getInstance().getString("embed"))
        }
        registered["testZ"] = result
        start = true

        val json = SessionManager.getInstance().getMapString("map", defValue)
        val token = object : TypeToken<HashMap<String, SimilarityClassifier.Recognition>>() {}
        val retrievedMap: HashMap<String, SimilarityClassifier.Recognition> = Gson().fromJson(json, token.type)

        // Convert types back to expected format (e.g., double to float)
        for (entry in retrievedMap.entries) {
            val output = Array(1) { FloatArray(OUTPUT_SIZE) }
            val arrayList = entry.value.extra as ArrayList<*>
            val list = arrayList[0] as ArrayList<*>
            for (counter in list.indices) {
                output[0][counter] = (list[counter] as Double).toFloat()
            }
            entry.value.extra = output
        }

        Toast.makeText(context, "Recognitions Loaded", Toast.LENGTH_SHORT).show()
        return retrievedMap
    }

    // Load Photo from phone storage
    private fun loadPhoto() {
        start = false
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE)
    }

    // Similar Analyzing Procedure
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                val selectedImageUri: Uri? = data?.data
                try {
                    val frameBmp = getBitmapFromUri(selectedImageUri!!)
                    val impphoto = InputImage.fromBitmap(frameBmp, 0)
                    detector.process(impphoto)
                        .addOnSuccessListener { faces ->
                            if (faces.isNotEmpty()) {
                                recognize.setText("Recognize")
                                addFace.visibility = View.VISIBLE
                                recoName.visibility = View.INVISIBLE
                                facePreview.visibility = View.VISIBLE
                                previewInfo.text = "1. Bring Face in view of Camera.\n\n2. Your Face preview will appear here.\n\n3. Click Add button to save face."
                                val face = faces[0]

                                // Crop and resize bitmap
                                val boundingBox = RectF(face.boundingBox)
                                val croppedFace = getCropBitmapByCPU(frameBmp, boundingBox)
                                val scaled = getResizedBitmap(croppedFace, 112, 112)

                                recognizeImage(scaled)
                               // addFace()
                                Thread.sleep(100) // May need to handle this differently
                            }
                        }.addOnFailureListener { e ->
                            start = true
                            Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show()
                        }
                    facePreview.setImageBitmap(frameBmp)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r") ?: throw IOException("Failed to open file descriptor.")
        val fileDescriptor = parcelFileDescriptor.fileDescriptor
        return BitmapFactory.decodeFileDescriptor(fileDescriptor).also {
            parcelFileDescriptor.close()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, CbslMain::class.java))
    }

}
