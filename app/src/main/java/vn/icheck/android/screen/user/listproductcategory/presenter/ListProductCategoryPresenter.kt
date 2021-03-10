package vn.icheck.android.screen.user.listproductcategory.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.business.BusinessInteractor
import vn.icheck.android.network.feature.category.CategoryInteractor
import vn.icheck.android.network.models.ICCategory
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.screen.user.listproductcategory.view.IListProductCategoryView

class ListProductCategoryPresenter(val view: IListProductCategoryView) : BaseActivityPresenter(view) {
    private val listenerProduct = BusinessInteractor()
    private val categoryInteraction = CategoryInteractor()
    var offset = 0
    var collectionID = -1L

    fun getCollectionID(intent: Intent?) {
        collectionID = try {
            intent?.getLongExtra(Constant.DATA_1, -1L) ?: -1L
        } catch (e: Exception) {
            -1L
        }

        if (collectionID == -1L) {
            view.onGetCollectionIDError()
        } else {
            checkInternet()
        }
    }

    fun checkInternet() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDataError(view.mContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        getListCategories()
        getProduct(false)
    }

    private fun getListCategories() {
        categoryInteraction.getListCategoriesChild(collectionID, object : ICApiListener<ICListResponse<ICCategory>> {
            override fun onSuccess(obj: ICListResponse<ICCategory>) {

                if (!obj.rows.isNullOrEmpty()) {
                    view.onGetListCategorySuccess(obj.rows)
                } else {
                    view.onGetListCategorySuccess(mutableListOf())
                }
            }

            override fun onError(error: ICBaseResponse?) {
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                view.onGetDataError(message)
            }
        })
    }

    fun getProduct(isLoadMore: Boolean) {
        if (!isLoadMore) {
            offset = 0
        }

        listenerProduct.getProduct(null, collectionID, offset, APIConstants.LIMIT, object : ICApiListener<ICListResponse<ICProduct>> {
            override fun onSuccess(obj: ICListResponse<ICProduct>) {
                    offset += APIConstants.LIMIT
                    view.onGetProductSuccess(obj.rows ?: mutableListOf(), isLoadMore)
            }

            override fun onError(error: ICBaseResponse?) {
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                view.onGetDataError(message)
            }
        })
    }
}