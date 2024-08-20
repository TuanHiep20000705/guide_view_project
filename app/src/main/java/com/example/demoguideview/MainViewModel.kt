package com.example.demoguideview

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(
    application: Application
): AndroidViewModel(application) {
    private val _isShowBottomSheet = MutableLiveData(false)
    val isShowBottomSheet: LiveData<Boolean> get() = _isShowBottomSheet

    fun openBottomSheet() {
        _isShowBottomSheet.value = !isShowBottomSheet.value!!
    }
}