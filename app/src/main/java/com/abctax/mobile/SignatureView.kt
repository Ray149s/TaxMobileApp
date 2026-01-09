package com.abctax.mobile

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Custom view for capturing signatures
 */
class SignatureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val path = Path()
    private val paths = mutableListOf<Path>()
    private var isEmpty = true

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw all completed paths
        paths.forEach { p ->
            canvas.drawPath(p, paint)
        }
        
        // Draw current path
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                isEmpty = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                paths.add(Path(path))
                path.reset()
                invalidate()
            }
        }
        return true
    }

    /**
     * Clears the signature
     */
    fun clear() {
        path.reset()
        paths.clear()
        isEmpty = true
        invalidate()
    }

    /**
     * Checks if the signature is empty
     */
    fun isEmpty(): Boolean = isEmpty

    /**
     * Gets the signature as a bitmap
     */
    fun getSignatureBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        draw(canvas)
        return bitmap
    }
}
