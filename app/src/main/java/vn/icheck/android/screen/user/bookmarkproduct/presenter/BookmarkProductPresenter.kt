package vn.icheck.android.screen.user.bookmarkproduct.presenter

import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentPresenter
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.history.HistoryInteractor
import vn.icheck.android.network.models.ICHistory_Product
import vn.icheck.android.screen.user.bookmarkproduct.view.IBookmarkProductView

class BookmarkProductPresenter(val view: IBookmarkProductView) : BaseFragmentPresenter(view) {
    val listener = HistoryInteractor()

    var offset = 0

    fun getListProductBookmark(isLoadMore: Boolean, lat: String?, lon: String?){
        if (NetworkHelper.isNotConnected(view.mContext)){
            view.onGetListProductBookmarkError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (!isLoadMore){
            offset = 0
        }

        listener.getListBookmarkProduct(offset, lat, lon, object : ICApiListener<ICListResponse<ICHistory_Product>> {
            override fun onSuccess(obj: ICListResponse<ICHistory_Product>) {
                offset += APIConstants.LIMIT

                if (!obj.rows.isNullOrEmpty()){
                    view.onGetListProductBookmarkSuccess(obj.rows, isLoadMore)
                }else{
                    view.onGetListProductBookmarkSuccess(mutableListOf(), isLoadMore)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                view.onGetListProductBookmarkError(message)
            }
        })
    }
}