package vn.icheck.android.icheckscanditv6

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ichecklibs.event.ICMessageEvent
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICMyID
import vn.icheck.android.network.models.ICValidStampSocial
import vn.icheck.android.network.models.point_user.ICKAccumulatePoint
import vn.icheck.android.network.models.point_user.ICKPointUser
import java.lang.reflect.InvocationTargetException

class IcheckScanViewModel : ViewModel() {
    private var ickScanModel = ICKScanModel()
    val ickScanModelLiveData = MutableLiveData<ICKScanModel>()
    val userInteractor = UserInteractor()

    //    val onError = MutableLiveData<ICError>()
    val liveData = MutableLiveData<String>()
    var scanOnlyChat = false
    var scanOnly = false
    var reviewOnly = false
    var scanLoyalty = false
    val repository = ProductInteractor()

    var codeScan = ""

    val stampHoaPhat = MutableLiveData<ICValidStampSocial>()
    val stampThinhLong = MutableLiveData<ICValidStampSocial>()
    val showDialogSuggestApp = MutableLiveData<ICValidStampSocial>()
    val checkStampSocial = MutableLiveData<ICValidStampSocial>()
    val stampFake = MutableLiveData<String>()
    val errorQr = MutableLiveData<String>()
    val errorString = MutableLiveData<String>()

    private val redeemRepository = RedeemPointRepository()

    val onAccumulatePoint = MutableLiveData<ICKAccumulatePoint>()
    val onInvalidTarget = MutableLiveData<String>()
    val onUsedTarget = MutableLiveData<String>()
    val onCustomer = MutableLiveData<String>()
    val onErrorString = MutableLiveData<String>()
    var collectionID = -1L
    fun postAccumulatePoint(target: String) {

        redeemRepository.postAccumulatePoint(collectionID, null, target, object : ICApiListener<ICResponse<ICKAccumulatePoint>> {
            override fun onSuccess(obj: ICResponse<ICKAccumulatePoint>) {
                if (obj.statusCode != 200.toString()) {
                    when (obj.status) {
                        "INVALID_TARGET" -> {
                            onInvalidTarget.postValue("Mã QRcode của sản phẩm này\nkhông thuộc chương trình")
                        }
                        "USED_TARGET" -> {
                            onUsedTarget.postValue("Mã QRcode của sản phẩm này\nkhông còn điểm cộng")
                        }
                        "INVALID_CUSTOMER" -> {
                            onCustomer.postValue("Bạn không thuộc danh sách\ntham gia chương trình")
                        }
                        else -> {
                            onErrorString.postValue(obj.data?.message)
                        }
                    }
                } else {
                    updatePoint(collectionID)
                    onAccumulatePoint.postValue(obj.data)
                }
            }

            override fun onError(error: ICBaseResponse?) {
//                checkError(true, error?.message)
            }
        })
    }

    fun updatePoint(campaignID: Long) {
        redeemRepository.getPointUser(campaignID, object : ICApiListener<ICResponse<ICKPointUser>> {
            override fun onSuccess(obj: ICResponse<ICKPointUser>) {
                if (obj.data?.points != null) {
                    SharedLoyaltyHelper(getApplicationByReflect()).putLong("POINT_USER_LOYALTY", obj.data?.points!!)
                }
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UPDATE_POINT, obj.data?.points))
            }

            override fun onError(error: ICBaseResponse?) {

            }
        })
    }

    fun getApplicationByReflect(): Application {
        try {
            @SuppressLint("PrivateApi") val activityThread = Class.forName("android.app.ActivityThread")
            val thread = activityThread.getMethod("currentActivityThread").invoke(null)
            val app = activityThread.getMethod("getApplication").invoke(thread)
                    ?: throw NullPointerException("u should init first")
            return app as Application
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        throw NullPointerException("u should init first")
    }


    fun getMyID() {

//        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
//            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
//            return
//        }

        userInteractor.getMyID(object : ICNewApiListener<ICResponse<ICMyID>> {
            override fun onSuccess(obj: ICResponse<ICMyID>) {
                if (obj.data != null) {
                    if (obj.data!!.myId.isNotEmpty()) {
                        liveData.postValue(obj.data!!.myId)
                    }
                }
            }

            override fun onError(error: ICResponseCode?) {
//                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }
        })
    }

    fun checkQrStampSocial() {
//        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
//            errorString.postValue(ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
//            return
//        }

        if (Patterns.WEB_URL.matcher(codeScan).matches()) {
            if (!codeScan.startsWith("http")) {
                codeScan = "http://$codeScan"
            }
        }

        repository.checkScanQrCode(codeScan, object : ICNewApiListener<ICResponse<ICValidStampSocial>> {
            override fun onSuccess(obj: ICResponse<ICValidStampSocial>) {
                when (obj.data?.theme) {
                    1 -> {
                        stampHoaPhat.postValue(obj.data)
                    }
                    2 -> {
                        stampThinhLong.postValue(obj.data)
                    }
                    else -> {
                        if (obj.data?.suggest_apps.isNullOrEmpty()) {
                            checkStampSocial.postValue(obj.data)
                        } else {
                            showDialogSuggestApp.postValue(obj.data)
                        }
                    }
                }
            }

            override fun onError(error: ICResponseCode?) {
                if (error?.code == 400) {
                    stampFake.postValue("Sản phẩm này có dấu hiệu làm giả sản phẩm chính hãng.\nXin vui lòng liên hệ với đơn vị phân phối chính hãng để được hỗ trợ.")
                } else {
                    errorQr.postValue(codeScan)
                }
            }
        })
    }

    fun setGuide() {
        ickScanModel.showGuide = !ickScanModel.showGuide
        ickScanModelLiveData.postValue(ickScanModel)
    }

    fun offGuide() {
        ickScanModel.showGuide = false
        ickScanModelLiveData.postValue(ickScanModel)
    }

    fun setFlash() {
        ickScanModel.isFlash = !ickScanModel.isFlash
        ickScanModelLiveData.postValue(ickScanModel)
    }

    enum class ScanScreen {
        SCAN,
        SCAN_BUY,
        MY_CODE
    }

    fun dispose() {
        repository.dispose()
    }
}

class SharedLoyaltyHelper(val context: Context, val name: String = "SHARE_NAME") {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun putString(name: String, value: String?) {
        editor.putString(name, value)
        editor.commit()
    }

    fun getString(name: String): String? {
        return sharedPreferences.getString(name, "")
    }

    fun getString(name: String, value: String?): String? {
        return sharedPreferences.getString(name, value)
    }

    fun putLong(name: String, value: Long) {
        editor.putLong(name, value)
        editor.commit()
    }

    fun getLong(name: String): Long {
        return sharedPreferences.getLong(name, 0)
    }

    fun getLong(name: String, value: Long): Long {
        return sharedPreferences.getLong(name, value)
    }

    fun putInt(name: String, value: Int?) {
        editor.putInt(name, value ?: 0)
        editor.commit()
    }

    fun getInt(name: String): Int {
        return sharedPreferences.getInt(name, 0)
    }

    fun getInt(name: String, value: Int): Int {
        return sharedPreferences.getInt(name, value)
    }

    fun putBoolean(name: String, value: Boolean) {
        editor.putBoolean(name, value)
        editor.commit()
    }

    fun getBoolean(name: String): Boolean {
        return sharedPreferences.getBoolean(name, false)
    }

    fun getBoolean(name: String, value: Boolean): Boolean {
        return sharedPreferences.getBoolean(name, value)
    }

    fun clearData() {
        editor.clear()
        editor.commit()
    }

    fun remove(key: String) {
        editor.remove(key)
        editor.commit()
    }
}
