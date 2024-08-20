package com.example.demoguideview.canvas_screen

import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.demoguideview.databinding.ActivityCanvasBinding

class CanvasActivity: AppCompatActivity() {
    private lateinit var binding: ActivityCanvasBinding

    private var clickCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCanvasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val canvasView = CanvasView.Builder(this).build()

        binding.btnDrawCanvas.setOnClickListener {
            val rectBottomSheet = Rect()
            binding.layoutBottomSheet.getGlobalVisibleRect(rectBottomSheet)
            binding.layoutBottomSheet.visibility = View.VISIBLE

            val rectTv1 = Rect()
            binding.tv1.getGlobalVisibleRect(rectTv1)
            canvasView.addTransparentArea(RectF(rectTv1))
            canvasView.addTransparentArea(RectF(rectBottomSheet))
            canvasView.show()
//            when (clickCount) {
//                0 -> {
//                    val rectTv1 = Rect()
//                    binding.tv1.getGlobalVisibleRect(rectTv1)
//                    canvasView.addTransparentArea(RectF(rectTv1))
//                    canvasView.addTransparentArea(RectF(rectBottomSheet))
//                    canvasView.show()
//                }
//                1 -> {
//                    val rectTv2 = Rect()
//                    binding.tv2.getGlobalVisibleRect(rectTv2)
//                    canvasView.transparentAreas.clear()
//                    canvasView.addTransparentArea(RectF(rectTv2))
//                    canvasView.addTransparentArea(RectF(rectBottomSheet))
//
//                }
//            }
//            clickCount++
        }

        binding.btnNextStep1.setOnClickListener {
            val rectBottomSheet = Rect()
            binding.layoutBottomSheet.getGlobalVisibleRect(rectBottomSheet)
            val rectTv2 = Rect()
            binding.tv2.getGlobalVisibleRect(rectTv2)
            canvasView.transparentAreas.clear()
            canvasView.addTransparentArea(RectF(rectTv2))
            canvasView.addTransparentArea(RectF(rectBottomSheet))

            canvasView.invalidate()
        }
    }
}