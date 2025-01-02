package com.one.cbsl.face.cameraxx

import android.graphics.Rect
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.IOException

class FaceDetection(
    private val graphicOverlayView: GraphicOverlay,
    private val listener: (Status) -> Unit
) : BaseImageAnalyzer<List<Face>>() {

    private val realTimeOpts =
        FaceDetectorOptions.Builder().setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL).build()

    private val detector = FaceDetection.getClient(realTimeOpts)

    override val graphicOverlay: GraphicOverlay
        get() = graphicOverlayView

    override fun detectInImage(image: InputImage): Task<List<Face>> {
        return detector.process(image)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e("Error", "Exception thrown while trying to close Face Detector: $e")
        }
    }

    override fun onSuccess(results: List<Face>, graphicOverlay: GraphicOverlay, rect: Rect) {
        graphicOverlay.clear()
        // If multiple faces then don't draw
        if (results.isNotEmpty()) {
            if (results.size > 1) {
                listener(Status.MULTIPLE_FACES)
            } else {
                for (face in results) {
                    val faceGraphic =
                        FaceBox(graphicOverlay, face, rect, listener)
                    graphicOverlay.add(faceGraphic)
                }
            }
            graphicOverlay.postInvalidate()
        } else {
            listener(Status.NO_FACE)
            Log.e("Error", "Face Detector failed.")
        }
    }



    override fun onFailure(e: Exception) {
        Log.e("Error", "Face Detector failed. $e")
        listener(Status.NO_FACE)
    }
}
