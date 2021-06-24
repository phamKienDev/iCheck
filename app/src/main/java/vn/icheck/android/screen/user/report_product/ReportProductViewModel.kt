package vn.icheck.android.screen.user.report_product

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.models.product.report.ICReportForm

class ReportProductViewModel : ViewModel() {
    private val interaction = ProductInteractor()

    val listData = MutableLiveData<MutableList<ICReportForm>>()
    val errorData = MutableLiveData<String>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()
    val listReportSuccess = MutableLiveData<MutableList<ICReportForm>>()

    private var productID = 0L

    fun getProductID(intent: Intent?) {
        productID = try {
            intent?.getLongExtra(Constant.DATA_1, -1L) ?: -1L
        } catch (e: Exception) {
            -1L
        }

        getListReason()
    }

    fun getListReason() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interaction.getListReportProductForm(object : ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICReportForm>>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (!obj.data?.rows.isNullOrEmpty()) {
                    listData.postValue(obj.data?.rows)
                } else {
                    errorData.postValue(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                errorData.postValue(message)
            }
        })
    }

    fun sendReport(listReasonID: MutableList<Int>, listReasonName: MutableList<String>, otherReason: String?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        if (productID == -1L) {
            errorData.postValue(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interaction.reportProduct(productID, listReasonID, otherReason, object : ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICReportForm>>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

                val listReason = mutableListOf<ICReportForm>()
                for (it in listReasonName) {
                    listReason.add(ICReportForm(0, it))
                }
                if (!otherReason.isNullOrEmpty()) {
                    listReason.add(ICReportForm(0, otherReason))
                }

                listReportSuccess.postValue(listReason)
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                val message = error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                errorData.postValue(message)
            }
        })
    }
}