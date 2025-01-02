package com.one.cbsl.face

import android.graphics.Rect
import android.util.Log
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.one.cbsl.face.cameraxx.BaseImageAnalyzer
import com.one.cbsl.face.cameraxx.GraphicOverlay
import java.io.IOException

class FaceContourDetectionProcessor(
    private val view: GraphicOverlay,
    private val textView: TextView  ,  private val callback: FaceDetectionCallback

) :
    BaseImageAnalyzer<List<Face>>() {

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(realTimeOpts)

    override val graphicOverlay: GraphicOverlay
        get() = view

    override fun detectInImage(image: InputImage): Task<List<Face>> {
        return detector.process(image)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: $e")
        }
    }

    override fun onSuccess(
        results: List<Face>,
        graphicOverlay: GraphicOverlay,
        rect: Rect
    ) {
        graphicOverlay.clear()
        if (results.isEmpty()) {
            // Handle the case when no faces are detected, if needed
            callback.onFacesDetected(0, emptyList(), rect)
            return
        }

        // Iterate over detected faces
        graphicOverlay.postInvalidate()
        callback.onFacesDetected(results.size, results,rect)
    }

    override fun onFailure(e: Exception) {
        Log.w(TAG, "Face Detector failed.$e")
    }

    companion object {
        private const val TAG = "FaceDetectorProcessor"
    }

}