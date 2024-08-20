package com.example.demoguideview

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.comp_practice_project.ui.guide_view.GuideView
import com.example.comp_practice_project.ui.guide_view.config.DismissType
import com.example.comp_practice_project.ui.guide_view.listener.GuideListener
import com.example.demoguideview.canvas_screen.CanvasActivity
import com.example.demoguideview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.btnGuide.setOnClickListener {
            viewModel.openBottomSheet()
//            val dialog = BottomSheetStep(object : BottomSheetListener {
//                override fun onStepClicked() {
//                    GuideView.Builder(this@MainActivity)
//                        .setTitle("Guide View")
//                        .setContentText("This is a count view")
//                        .setTargetView(binding.tvCount)
//                        .setDismissType(DismissType.outside)
//                        .setGuideListener(object : GuideListener {
//                            override fun onDismiss(view: View?) {
//                                Toast.makeText(this@MainActivity, "finish", Toast.LENGTH_SHORT).show()
//                            }
//                        })
//                        .build().show()
//                }
//            })
//            dialog.show(supportFragmentManager, "fragment")
        }
        binding.btnGoToCanvas.setOnClickListener {
            val intent = Intent(this, CanvasActivity::class.java)
            startActivity(intent)
        }
        viewModel.isShowBottomSheet.observe(this) {
            if (it) {
                binding.layoutBottomSheet.visibility = View.VISIBLE
                GuideView.Builder(this@MainActivity)
                    .setTitle("Guide View")
                    .setContentText("This is a count view")
                    .setTargetView(binding.layoutBottomSheet)
                    .setGuideListener(object : GuideListener {
                        override fun onDismiss(view: View?) {
                            Toast.makeText(this@MainActivity, "finish", Toast.LENGTH_SHORT).show()
                        }
                    })
                    .build().show()
            } else {
                binding.layoutBottomSheet.visibility = View.GONE
            }
        }

        binding.layoutBottomSheet.setOnClickListener {
            Log.e(">>>>", "btnNextStep click")
            GuideView.Builder(this@MainActivity)
                .setTitle("Guide View")
                .setContentText("This is a count view")
                .setTargetView(binding.tvCount)
                .setGuideListener(object : GuideListener {
                    override fun onDismiss(view: View?) {
                        Toast.makeText(this@MainActivity, "finish", Toast.LENGTH_SHORT).show()
                    }
                })
                .build().show()
        }
    }
}