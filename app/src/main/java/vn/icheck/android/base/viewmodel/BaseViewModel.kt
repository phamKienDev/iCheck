package vn.icheck.android.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withTimeoutOrNull
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.Result
import vn.icheck.android.network.base.ResultHelper

open class BaseViewModel : ViewModel() {

    suspend fun <T : ICResponseCode> getRequest(call: suspend () -> T): T? {
        return try {
            withTimeoutOrNull(30000L) { call.invoke() }
        } catch (e: Exception) {
            null
        }
    }

    fun <T : ICResponseCode> request(call: suspend () -> T) = liveData(Dispatchers.IO) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            emit(Result.stopLoading())
            emit(Result.errorNetwork(data = null, message = ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return@liveData
        }

        emit(Result.loading())

        try {
            val response = withTimeoutOrNull(30000L) { call.invoke() }
            emit(Result.stopLoading())
            emit(Result.success(data = response))
        } catch (exception: Exception) {
            ResultHelper.checkRequestError(exception)?.message?.let { message ->
                emit(Result.stopLoading())
                emit(Result.errorRequest(data = null, message = message))
            }
        }
    }

    fun <T : ICResponseCode> request(timeOut: Long, call: suspend () -> T) = liveData(Dispatchers.IO) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            emit(Result.stopLoading())
            emit(Result.errorNetwork(data = null, message = ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return@liveData
        }

        emit(Result.loading())

        try {
            val response = withTimeoutOrNull(timeOut) { call.invoke() }
            emit(Result.stopLoading())
            emit(Result.success(data = response))
        } catch (exception: Exception) {
            ResultHelper.checkRequestError(exception)?.message?.let { message ->
                emit(Result.stopLoading())
                emit(Result.errorRequest(data = null, message = message))
            }
        }
    }
}