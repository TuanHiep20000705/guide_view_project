package com.example.demoguideview.canvas_screen

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class CanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
): View(context, attrs, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#80000000") // Màu đen mờ (50% độ mờ)
    }

    val transparentAreas = mutableListOf<RectF>()

    fun addTransparentArea(rect: RectF) {
        transparentAreas.add(rect)
        invalidate() // Yêu cầu vẽ lại view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val layerId = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), paint)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        transparentAreas.forEach { rect ->
            canvas.drawRoundRect(rect, 20f, 20f, paint) // Tạo khung trong suốt với góc bo 20f
        }

        paint.xfermode = null
        canvas.restoreToCount(layerId)
    }

    fun show() {
        this.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        this.isClickable = false
        ((context as Activity).window.decorView as ViewGroup).addView(this)
    }

    class Builder(private val context: Context) {
        fun build(): CanvasView {
            val canvasView = CanvasView(context)
            return canvasView
        }
    }
}