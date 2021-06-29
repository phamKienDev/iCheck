package vn.icheck.android.screen.user.report

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
import vn.icheck.android.network.feature.order.OrderInteractor
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.models.product.report.ICReportForm

class ReportViewModel : ViewModel() {
    private val orderInteractor = OrderInteractor()
    private val productInteraction = ProductInteractor()


    var onData = MutableLiveData<MutableList<ICReportForm>>()
    var onReport = MutableLiveData<Any>()
    var onError = MutableLiveData<ICError>()
    var onState = MutableLiveData<ICMessageEvent.Type>()


    private var type = -1
    private var id: Long? = null

    fun getData(intent: Intent) {
        type = intent.getIntExtra(Constant.DATA_1, 1)
        id = intent.getLongExtra(Constant.DATA_2, -1)

        when (type) {
            ReportActivity.ORDER -> {
                getReasonOrder()
            }
            ReportActivity.PRODUCT -> {
                getReasonProduct()
            }
            else -> {
                getReasonOrder()
            }
        }
    }

    private fun getReasonOrder() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        onState.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)
        orderInteractor.getListReportOrder(object : ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICReportForm>>) {
                onState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onData.postValue(obj.data?.rows)
            }

            override fun onError(error: ICResponseCode?) {
                onState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }

    private fun getReasonProduct() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        onState.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        productInteraction.getListReportProductForm(object : ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICReportForm>>) {
                onState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onData.postValue(obj.data?.rows)
            }

            override fun onError(error: ICResponseCode?) {
                onState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }

    fun report(listIdReason: MutableList<Int>, message: String){
        if(type==ReportActivity.ORDER){
            reportOrder(listIdReason, message)
        }else{
            reportProduct(listIdReason, message)
        }
    }


    private fun reportOrder(listIdReason: MutableList<Int>, message: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        if (id != -1L && id != null) {
            onState.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)
            orderInteractor.postReportOrder(id!!, listIdReason, message, object : ICNewApiListener<ICResponse<Any>> {
                override fun onSuccess(obj: ICResponse<Any>) {
                    onState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    if (obj.data is Int || obj.data is Double) {
                        onReport.postValue(1)
                    } else {
                        onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.da_report_don_hang_truoc_do)))
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    onState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    onError.postValue(ICError(R.drawable.ic_error_request, error?.message
                            ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
                }
            })
        }
    }

    private fun reportProduct(listReasonID: MutableList<Int>, otherReason: String?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        if (id == -1L) {
            onError.postValue(ICError(R.drawable.ic_error_request,getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            return
        }

        onState.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)


        productInteraction.reportProduct(id!!, listReasonID, otherReason, object : ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICReportForm>>) {
                onState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onReport.postValue(1)
            }

            override fun onError(error: ICResponseCode?) {
                onState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onError.postValue(ICError(R.drawable.ic_error_request, error?.message
                    ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }

}