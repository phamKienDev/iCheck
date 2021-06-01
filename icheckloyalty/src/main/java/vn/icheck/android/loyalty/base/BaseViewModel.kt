package vn.icheck.android.loyalty.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.model.ICKError

open class BaseViewModel<T> : ViewModel() {

    val onError = MutableLiveData<ICKError>()
    val onErrorEmpty = MutableLiveData<ICKError>()

    val onAddData = MutableLiveData<MutableList<T>>()
    val onSetData = MutableLiveData<MutableList<T>>()

    var collectionID = -1L
    var offset = 0

    fun getHeaderAlpha(totalScroll: Int, layoutHeader: Int): Float {
        return when {
            totalScroll > 0 -> {
                if (totalScroll <= layoutHeader) {
                    (1f / layoutHeader) * totalScroll
                } else {
                    1f
                }
            }
            totalScroll < 0 -> {
                if (totalScroll < -layoutHeader) {
                    (-1f / layoutHeader) * totalScroll
                } else {
                    0f
                }
            }
            else -> {
                0f
            }
        }
    }

    fun setErrorEmpty(icon: Int, title: String, message: String, textButton: String, backgroundButton: Int, colorButton: Int, colorMessage: Int? = null) {
        onErrorEmpty.postValue(ICKError(icon, title, message, textButton, backgroundButton, colorButton, colorMessage))
    }

    fun checkError(isConnected: Boolean, message: String? = null) {
        if (isConnected) {
            onError.postValue(ICKError(R.drawable.ic_error_request, message
                    ?: ApplicationHelper.getApplicationByReflect().getString(R.string.co_loi_xay_ra_vui_long_thu_lai), "", "", 0, R.color.white))
        } else {
            onError.postValue(ICKError(R.drawable.ic_error_network, ApplicationHelper.getApplicationByReflect().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), "", "", 0, R.color.white))
        }
    }

    fun getString(id: Int): String {
        return ApplicationHelper.getApplicationByReflect().getString(id)
    }
}