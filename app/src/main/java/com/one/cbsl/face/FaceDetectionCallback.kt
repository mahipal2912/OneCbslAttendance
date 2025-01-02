package com.one.cbsl.face

import android.graphics.Point
import android.graphics.Rect
import com.google.mlkit.vision.face.Face

interface FaceDetectionCallback {
    fun onFacesDetected(faceCount: Int, faceLandmarks: List<Face>, rect: Rect)
}

data class FaceLandmarks(
    val leftEye: Point?,
    val rightEye: Point?,
    val nose: Point?,
    val mouthLeft: Point?,
    val mouthRight: Point?
)