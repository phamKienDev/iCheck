package vn.icheck.android.screen.user.setting

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.setting.SettingRepository
import vn.icheck.android.network.models.ICSetting
import vn.icheck.android.util.FileUtils
import java.io.File
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

class SettingViewModel : ViewModel() {
    private val settingInteractor = SettingRepository()
    
    val onError = MutableLiveData<ICError>()
    val onNews = MutableLiveData<Boolean>()
    val onCampaign = MutableLiveData<Boolean>()
    val onMessage = MutableLiveData<Boolean>()

    val onSuccess = MutableLiveData<ICSetting>()

    fun getNotifySetting(){
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())){
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        settingInteractor.getNotifySettingSocial(object : ICNewApiListener<ICResponse<ICSetting>> {
            override fun onSuccess(obj: ICResponse<ICSetting>) {
                onSuccess.postValue(obj.data)
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }

    fun postNotifySetting(type: String, isActive: Boolean){
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())){
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }
        
        settingInteractor.postNotifySettingSocial(type, isActive, object : ICNewApiListener<ICResponse<ICSetting>> {
            override fun onSuccess(obj: ICResponse<ICSetting>) {
                getNotifySetting()
                when(type){
                    "NEWS" -> onNews.postValue(isActive)
                    "CAMPAIGN" -> onCampaign.postValue(isActive)
                    "MESSAGE" -> onMessage.postValue(isActive)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }

    fun checkSwitchVibrate(vibrate: Boolean) {
        val vibrator = ICheckApplication.getInstance().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (vibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(500)
            }
        } else {
            vibrator.cancel()
        }
    }

    fun getDirSize(dir: File?): Long {
        var size: Long = 0
        for (file in dir!!.listFiles()) {
            if (file != null && file.isDirectory) {
                size += getDirSize(file)
            } else if (file != null && file.isFile) {
                size += file.length()
            }
        }
        return size
    }

    fun readableFileSize(size: Long): String? {
        if (size <= 0) return "0 Bytes"
        val units = arrayOf("Bytes", "kB", "Mb", "Gb", "Tb")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())).toString() + " " + units[digitGroups]
    }

    fun deleteCache() {
        try {
            val dir: File = ICheckApplication.getInstance().cacheDir
            deleteC(dir)
        } catch (e: Exception) {
        }
    }

    private fun deleteC(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children: Array<String> = dir.list()

            for (i in 0..children.size) {
                val success = FileUtils.deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }

            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }
}