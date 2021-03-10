package vn.icheck.android.screen.user.list_shop_variant.viewmodel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.cart.CartInteractor
import vn.icheck.android.network.models.ICRespCart
import vn.icheck.android.network.models.ICShopVariantV2
import vn.icheck.android.network.models.product.detail.ICProductVariant
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.util.kotlin.ToastUtils

class ListShopVariantViewModel : ViewModel() {

    val listData = MutableLiveData<MutableList<ICShopVariantV2>>()
    val successData = MutableLiveData<String>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()
    val errorData = MutableLiveData<String>()

    private val cartInteraction = CartInteractor()
    private val cartHelper = CartHelper()

    fun getDataIntent(intent: Intent?) {
        val list = getList(intent?.getStringExtra(Constant.DATA_1)) ?: mutableListOf()
        listData.postValue(list)
    }

    private fun getList(json: String?): MutableList<ICShopVariantV2>? {
        if (json.isNullOrEmpty()) {
            return null
        }

        return try {
            val listType = object : TypeToken<MutableList<ICShopVariantV2>>() {}.type
            JsonHelper.gson.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }
    }

    fun addCart(id: Long) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        cartInteraction.addCart(id, 1, object : ICApiListener<ICRespCart> {
            override fun onSuccess(obj: ICRespCart) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                cartHelper.saveCart(obj)
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COUNT_CART))
                successData.postValue(ICheckApplication.getInstance().getString(R.string.them_vao_gio_hang_thanh_cong))
            }

            override fun onError(error: ICBaseResponse?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                val message = error?.message ?: ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                errorData.postValue(message)
            }
        })
    }

}