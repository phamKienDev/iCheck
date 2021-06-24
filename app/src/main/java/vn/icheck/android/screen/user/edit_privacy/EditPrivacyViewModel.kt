package vn.icheck.android.screen.user.edit_privacy

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.product_review.ProductReviewInteractor
import vn.icheck.android.network.models.ICPrivacy
import java.lang.Exception

class EditPrivacyViewModel : ViewModel() {
    private val interaction = ProductReviewInteractor()

    val onStatus = MutableLiveData<ICMessageEvent.Type>()
    val onSetPrivacy = MutableLiveData<MutableList<ICPrivacy>>()
    val onEditSuccess = MutableLiveData<Int>()
    val onGetDataError = MutableLiveData<ICError>()
    val onRequestError = MutableLiveData<String>()
    val onShowMessage = MutableLiveData<String>()

    private val listPrivacy = mutableListOf<ICPrivacy>()
    private var postId = -1L

    fun getData(intent: Intent) {
        postId = try {
            intent.getLongExtra(Constant.DATA_1, -1)
        } catch (e: Exception) {
            -1
        }
        if (postId != -1L) {
            getPrivacy()
        } else {
            onGetDataError.postValue(ICError(R.drawable.ic_error_request,  getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
        }
    }

    fun getPrivacy() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onGetDataError.postValue(ICError(R.drawable.ic_error_network,  getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interaction.getPrivacy(postId, object : ICNewApiListener<ICResponse<ICListResponse<ICPrivacy>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICPrivacy>>) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                listPrivacy.addAll(obj.data!!.rows)
                onSetPrivacy.postValue(listPrivacy)
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onGetDataError.postValue(ICError(R.drawable.ic_error_request,  getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }
        })
    }

    fun editPrivacy(selectedPosition: Int) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onShowMessage.postValue( getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        val privacy = if (selectedPosition >= 0 && selectedPosition < listPrivacy.size) {
            listPrivacy[selectedPosition]
        } else {
            null
        }

        if (privacy == null) {
            onShowMessage.postValue( getString(R.string.vui_long_chon_quyen_rieng_tu))
            return
        }

        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interaction.postPrivacy(postId, privacy.privacyElementId, object : ICNewApiListener<ICResponse<Boolean>> {
            override fun onSuccess(obj: ICResponse<Boolean>) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                obj.data?.let {
                    onEditSuccess.postValue(1)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onRequestError.postValue( getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }
}