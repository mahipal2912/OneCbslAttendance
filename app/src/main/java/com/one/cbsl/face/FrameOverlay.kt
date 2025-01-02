package com.one.cbsl.face

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class FrameOverlay(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val framePaint: Paint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    private val backgroundPaint: Paint = Paint().apply {
        color = Color.argb(100, 0, 0, 0) // Semi-transparent black for the outside area
        style = Paint.Style.FILL
    }
    private var frameRect: Rect? = null

    init {
        // Default frame size, you can adjust this based on your requirement
        frameRect = Rect(100, 300, 1000, 1300) // Example fixed frame location
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val sideLength = Math.min(w, h) * 0.8f // 80% of the smaller dimension
        // Calculate the position to center the square
        val left = (w - sideLength).toInt() / 2
        val top = (h - sideLength).toInt() / 2
        // Create a square frame with calculated size
        frameRect = Rect(left, 300, left + sideLength.toInt(), top + sideLength.toInt() - 200)

        // Invalidate to redraw the view with the updated frame
        invalidate()
    }

    // Set the frame rect manually (e.g., dynamically)
    fun setFrameRect(rect: Rect) {
        this.frameRect = rect
        invalidate()  // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        canvas.let {
            frameRect?.let { rect ->
                it.drawRect(rect, framePaint)
            }
        }
    }
    fun getFrameRect(): Rect? {
        return frameRect
    }
}
