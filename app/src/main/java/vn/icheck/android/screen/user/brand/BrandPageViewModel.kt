package vn.icheck.android.screen.user.brand

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICError
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.RelationshipHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.ICPageTrend
import vn.icheck.android.screen.user.listdistributor.BaseModelList

class BrandPageViewModel : ViewModel() {
    private val brandInteractor = PageRepository()

    var onError = MutableLiveData<ICError>()
    var liveData = MutableLiveData<BaseModelList<ICPageTrend>>()

    var collectionID: Long = -1
    var offset = 0

    fun getCollectionID(intent: Intent?) {
        collectionID = try {
            intent?.getLongExtra(Constant.DATA_1, -1) ?: -1
        } catch (e: Exception) {
            -1
        }

        if (collectionID == -1L) {
            DialogHelper.showNotification(ICheckApplication.getInstance(), R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                override fun onDone() {
                    ICheckApplication.currentActivity()?.onBackPressed()
                }
            })
        } else {
            getWidgetBrand(false)
        }
    }

    fun getWidgetBrand(isLoadMore: Boolean) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        brandInteractor.getBrands(collectionID, offset, object : ICNewApiListener<ICResponse<ICListResponse<ICPageTrend>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICPageTrend>>) {
                offset += APIConstants.LIMIT

                RelationshipHelper.isFollowBrand(obj.data?.rows!!)

                liveData.postValue(
                        BaseModelList(isLoadMore, obj.data?.rows ?: mutableListOf(), null, null))
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }
        })
    }
}