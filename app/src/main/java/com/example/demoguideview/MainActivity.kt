package com.example.demoguideview

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.comp_practice_project.ui.guide_view.GuideView
import com.example.comp_practice_project.ui.guide_view.config.DismissType
import com.example.comp_practice_project.ui.guide_view.listener.GuideListener
import com.example.demoguideview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGuide.setOnClickListener {
            GuideView.Builder(this@MainActivity)
                .setTitle("Guide View")
                .setContentText("This is a count view")
                .setTargetView(binding.tvCount)
                .setDismissType(DismissType.outside)
                .setGuideListener(object : GuideListener {
                    override fun onDismiss(view: View?) {
                        Toast.makeText(this@MainActivity, "finish", Toast.LENGTH_SHORT).show()
                    }
                })
                .build().show()
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
    }
}