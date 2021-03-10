package vn.icheck.android.screen.user.scanproduct

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.models.ICProductDetail

/**
 * Created by VuLCL on 8/8/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ScanProductViewModel : ViewModel() {
    private val repository = ProductInteractor()

    val onScanSuccess = MutableLiveData<ICProductDetail>()
    val showMessage = MutableLiveData<String>()

    private var timeScan = System.currentTimeMillis()

    fun scan(barcode: String?) {
        if (System.currentTimeMillis() - timeScan < 1000) {
            showMessage.postValue("")
            return
        }

        timeScan = System.currentTimeMillis()

        if (barcode.isNullOrEmpty()) {
            showMessage.postValue("")
            return
        }

        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            showMessage.postValue(ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        repository.dispose()

        repository.getProductDetailByBarcode(barcode, object : ICNewApiListener<ICResponse<ICProductDetail>> {
            override fun onSuccess(obj: ICResponse<ICProductDetail>) {
                if (obj.data != null) {
                    onScanSuccess.postValue(obj.data!!)
                }
            }

            override fun onError(error: ICResponseCode?) {
                showMessage.postValue("")
            }
        })
    }
}