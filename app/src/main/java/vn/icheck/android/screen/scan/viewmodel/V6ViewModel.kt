package vn.icheck.android.screen.scan.viewmodel

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.popup.PopupInteractor
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICMyID
import vn.icheck.android.network.models.ICPopup
import vn.icheck.android.network.models.ICValidStampSocial
import vn.icheck.android.screen.scan.model.ICKScanModel

class V6ViewModel: ViewModel() {
    private val popupRepository = PopupInteractor()
    private var ickScanModel = ICKScanModel()
    val ickScanModelLiveData = MutableLiveData<ICKScanModel>()
    val userInteractor = UserInteractor()

    val onError = MutableLiveData<ICError>()
    val liveData = MutableLiveData<String>()
    var scanOnlyChat = false
    var scanOnly = false
    var scanOnlyLoyalty = false
    var reviewOnly = false
    val repository = ProductInteractor()

    var codeScan = ""

    val stampHoaPhat = MutableLiveData<ICValidStampSocial>()
    val stampThinhLong = MutableLiveData<ICValidStampSocial>()
    val showDialogSuggestApp = MutableLiveData<ICValidStampSocial>()
    val checkStampSocial = MutableLiveData<ICValidStampSocial>()
    val stampFake = MutableLiveData<String>()
    val errorQr = MutableLiveData<String>()
    val errorString = MutableLiveData<String>()
    val onPopupAds = MutableLiveData<ICPopup>()


    fun getMyID() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        userInteractor.getMyID(object : ICNewApiListener<ICResponse<ICMyID>> {
            override fun onSuccess(obj: ICResponse<ICMyID>) {
                if (obj.data != null) {
                    if (obj.data!!.myId.isNotEmpty()) {
                        liveData.postValue(obj.data!!.myId)
                    }
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }
        })
    }

    fun getPopup() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            return
        }

        popupRepository.getPopup(null, Constant.SCAN, object : ICNewApiListener<ICResponse<ICPopup>> {
            override fun onSuccess(obj: ICResponse<ICPopup>) {
                if (obj.data != null) {
                    onPopupAds.postValue(obj.data!!)
                }
            }
            override fun onError(error: ICResponseCode?) {
            }
        })
    }


    fun checkQrStampSocial() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            errorString.postValue(ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

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
                if (error?.code == 400 || error?.status == "400") {
                    stampFake.postValue(ICheckApplication.getError(error.message))
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