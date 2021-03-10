package vn.icheck.android.screen.user.shopreview.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.shop.ShopInteractor
import vn.icheck.android.network.models.ICCriteriaShop
import vn.icheck.android.network.models.ICReqShopReview
import vn.icheck.android.network.models.ICRespShopReview
import vn.icheck.android.network.models.upload.UploadResponse
import vn.icheck.android.screen.user.shopreview.entity.ShopReviewImage
import vn.icheck.android.screen.user.shopreview.view.IOrderReviewView
import java.io.File

/**
 * Created by VuLCL on 2/2/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class OrderReviewPresenter(val view: IOrderReviewView) : BaseActivityPresenter(view) {
    private val shopInteraction = ShopInteractor()

    private var shopID: Long = -1

    fun getShopID(intent: Intent?) {
        shopID = try {
            intent?.getLongExtra(Constant.DATA_1, -1) ?: -1
        } catch (e: Exception) {
            -1
        }

        getListCriteria()
    }

    fun getListCriteria() {
        if (shopID == -1L) {
            view.onGetShopIDError()
            return
        }

        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetCriteriaError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            return
        }

        view.onShowLoading()

        shopInteraction.getShopCriterias(shopID, object : ICApiListener<ICListResponse<ICCriteriaShop>> {
            override fun onSuccess(obj: ICListResponse<ICCriteriaShop>) {
                view.onCloseLoading()
                view.onGetCriteriaSuccess(obj.rows)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                view.onGetCriteriaError(message)
            }
        })
    }

    fun uploadImage(file: File) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        view.onShowLoading()

        ImageHelper.uploadMedia( file, object : ICApiListener<UploadResponse> {
            override fun onSuccess(obj: UploadResponse) {
                view.onCloseLoading()

                val shopReviewImage = ShopReviewImage(file.absolutePath, obj.fileId, obj.src)
                view.onAddImageSuccess(shopReviewImage)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(message)
            }
        })
    }

    fun reviewShop(listCriteria: MutableList<ICCriteriaShop.Criteria>,
                   note: String, listImage: MutableList<ShopReviewImage>) {
        if (shopID == -1L) {
            view.onGetShopIDError()
        }

        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        val criterias = mutableListOf<ICReqShopReview.Criteria>()
        for (it in listCriteria) {
            if (it.rating == 0F) {
                val message = view.mContext.getString(R.string.error_shop_review_rating, it.name)
                showError(message)
                return
            }

            criterias.add(ICReqShopReview.Criteria(it.id, it.rating))
        }

        val mNote = if (note.isNotEmpty()) {
            note
        } else {
            null
        }

        val images: MutableList<String>?

        if (listImage.isNotEmpty()) {
            images = mutableListOf()

            for (it in listImage) {
                images.add(it.fileID)
            }
        } else {
            images = null
        }

        val body = ICReqShopReview(shopID, "shop", criterias, mNote, images)

        view.onShowLoading()

        shopInteraction.reviewShop(body, object : ICApiListener<ICRespShopReview> {
            override fun onSuccess(obj: ICRespShopReview) {
                view.onCloseLoading()
                view.onReviewSuccess()
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(message)
            }
        })
    }
}