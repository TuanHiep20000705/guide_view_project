package com.example.demoguideview

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.example.demoguideview.databinding.BottomSheetStepBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

interface BottomSheetListener {
    fun onStepClicked()
}

class BottomSheetStep(
    val listener: BottomSheetListener
): BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetStepBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.setBackgroundColor(Color.TRANSPARENT)
            }
        }
        return dialog
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetStepBinding.inflate(layoutInflater)
        binding.btnNextStep.setOnClickListener {
            listener.onStepClicked()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}