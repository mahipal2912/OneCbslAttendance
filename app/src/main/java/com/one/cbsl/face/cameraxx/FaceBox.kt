package com.one.cbsl.face.cameraxx

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.google.mlkit.vision.face.Face

class FaceBox(
    overlay: GraphicOverlay,
    private val face: Face,
    private val imageRect: Rect,
    private val listener: (Status) -> Unit
) : GraphicOverlay.FaceBox(overlay) {

    private val facePositionPaint: Paint
    private val idPaint: Paint
    private val boxPaint: Paint

    init {
        val selectedColor = Color.WHITE
        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor
        idPaint = Paint()
        idPaint.color = selectedColor
        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = 5.0f
    }

    private val greenBoxPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 5.0f
    }
    private val redBoxPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5.0f
    }

    override fun draw(canvas: Canvas?) {
        val rect = calculateRect(
            imageRect.height().toFloat(), imageRect.width().toFloat(), face.boundingBox
        )
        val leftEyeProbability = leftEyeProbability()
        val rightEyeProbability = rightEyeProbability()

        when {
            // both eyes are closed
            leftEyeProbability <= 0.6 && rightEyeProbability() <= 0.6 -> {
                listener(Status.BOTH_EYES_CLOSED)
                canvas?.drawRect(rect, redBoxPaint)
            }
            // left eye is closed
            leftEyeProbability <= 0.6 -> {
                listener(Status.LEFT_EYE_CLOSED)
                canvas?.drawRect(rect, redBoxPaint)
            }
            // right is closed
            rightEyeProbability <= 0.6 -> {
                listener(Status.RIGHT_EYE_CLOSED)
                canvas?.drawRect(rect, redBoxPaint)
            }
            // valid face, set face box color green
            else -> {
                listener(Status.VALID_FACE)
                canvas?.drawRect(rect, greenBoxPaint)
            }
        }
    }

    fun is2DImage(face: Face): Boolean {
        val headRotation = calculateHeadRotation(face)  // Calculate head pose
        val leftEyeOpen = leftEyeProbability()
        val rightEyeOpen = rightEyeProbability()

        // Check if head rotation is zero and no blinking (could indicate a 2D image)
        if (headRotation == 0.0f && leftEyeOpen < 0.5 && rightEyeOpen < 0.5) {
            return true // Likely a 2D image
        }
        return false
    }

    fun calculateHeadRotation(face: Face): Float {
        // You would calculate the head pose here based on the landmarks
        return 0.0f // Placeholder for head rotation calculation
    }

    private fun leftEyeProbability(): Float {
        var probability = 0.0F
        if (face.leftEyeOpenProbability != null) {
            val leftEyeOpenProb = face.leftEyeOpenProbability
            probability = leftEyeOpenProb!!
        }
        return probability
    }

    private fun rightEyeProbability(): Float {
        var probability = 0.0F
        if (face.rightEyeOpenProbability != null) {
            val rightEyeOpenProb = face.rightEyeOpenProbability
            probability = rightEyeOpenProb!!
        }
        return probability
    }
}