package vn.icheck.android.screen.user.information_product

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.models.ICInformationProduct

class InformationProductViewModel : ViewModel() {
    val interaction = ProductInteractor()

    val onError = MutableLiveData<ICError>()
    val liveData = MutableLiveData<ICInformationProduct>()

    var code = ""
    var productId = 0L

    fun getCollectionID(intent: Intent?) {
        code = try {
            intent?.getStringExtra(Constant.DATA_1) ?: ""
        } catch (e: Exception) {
            ""
        }
        productId = try {
            intent?.getLongExtra(Constant.DATA_2, 0L) ?: 0L
        } catch (e: Exception) {
            -1L
        }

        if (code.isEmpty() || productId == 0L) {
            onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
        } else {
            getInformationProduct()
        }
    }

    fun getInformationProduct() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        viewModelScope.launch {
            interaction.getInformationProduct(code, productId, object : ICNewApiListener<ICResponse<ICInformationProduct>> {
                override fun onSuccess(obj: ICResponse<ICInformationProduct>) {
                    liveData.postValue(obj.data)
                }

                override fun onError(error: ICResponseCode?) {
                    onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
                }
            })
        }
    }
}