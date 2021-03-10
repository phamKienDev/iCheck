package vn.icheck.android.screen.user.list_product_review

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.product_review.count_review.CountReviewModel
import vn.icheck.android.component.product_review.header_list_review.HeaderListReviewModel
import vn.icheck.android.component.product_review.list_review.ItemListReviewModel
import vn.icheck.android.component.product_review.my_review.MyReviewModel
import vn.icheck.android.component.product_review.submit_review.SubmitReviewModel
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.product_review.ProductReviewInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.models.product_detail.ICDataProductDetail
import vn.icheck.android.network.util.JsonHelper

class ListProductReviewViewModel : ViewModel() {

    private val interactor = ProductReviewInteractor()
    private val productInteractor = ProductInteractor()
    private var offset = 0
    private var errorCount = 0

    val onReviewSummary = MutableLiveData<ICViewModel>()
    val onUpdateReviewSummary = MutableLiveData<ICViewModel>()
    val onMyReview = MutableLiveData<ICViewModel>()
    val onUpdateMyReview = MutableLiveData<ICViewModel>()
    val onListReview = MutableLiveData<MutableList<ICViewModel>>()
    val onDataProduct = MutableLiveData<ICDataProductDetail>()
    val onShareLinkData = MutableLiveData<ICPost>()
    val onStatusMessage = MutableLiveData<ICMessageEvent>()

    private var productId: Long = -1L
    var currentProduct: ICDataProductDetail? = null


    fun getData(intent: Intent, isUpdate: Boolean = false) {
        productId = try {
            intent.getLongExtra(Constant.DATA_1, -1)
        } catch (e: Exception) {
            -1
        }

        onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOADING, null))

        errorCount = 0
        if (productId != -1L) {
            getReviewSummary()
            getMyReview()
            getListReview()
            if (!isUpdate)
                getDataProduct()
        } else {
            onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR,
                    ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))))
        }
    }

    fun getReviewSummary(isUpdate: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.ON_NO_INTERNET, ICError(R.drawable.ic_error_network,
                    ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))))
            return
        }

        interactor.getReviewSummary(productId, object : ICNewApiListener<ICResponse<ICReviewSummary>> {
            override fun onSuccess(obj: ICResponse<ICReviewSummary>) {
                onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING, null))
                if (isUpdate) {
                    onUpdateReviewSummary.postValue(HeaderListReviewModel(obj.data!!))
                } else {
                    onReviewSummary.postValue(HeaderListReviewModel(obj.data!!))
                }
            }

            override fun onError(error: ICResponseCode?) {
                checkError(error)
            }
        })
    }

    fun getMyReview(isUpdate: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.ON_NO_INTERNET, ICError(R.drawable.ic_error_network,
                    ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))))
            return
        }

        val pageId = if (SettingManager.getPostPermission() != null) {
            if (SettingManager.getPostPermission()!!.type == Constant.PAGE || SettingManager.getPostPermission()!!.type == Constant.ENTERPRISE) {
                SettingManager.getPostPermission()!!.id
            } else {
                null
            }
        } else {
            null
        }

        interactor.getMyReview(productId, pageId, object : ICNewApiListener<ICResponse<ICProductMyReview>> {
            override fun onSuccess(obj: ICResponse<ICProductMyReview>) {
                onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING, null))
                if (!isUpdate) {
                    onMyReview.postValue(if (obj.data!!.myReview != null) {
                        MyReviewModel(obj.data!!)
                    } else {
                        obj.data!!.criteria?.let { SubmitReviewModel(it, productId) }
                    })
                } else {
                    onUpdateMyReview.postValue(if (obj.data!!.myReview != null) {
                        MyReviewModel(obj.data!!)
                    } else {
                        obj.data!!.criteria?.let { SubmitReviewModel(it, productId) }
                    })

                }
            }

            override fun onError(error: ICResponseCode?) {
                checkError(error)
            }
        })
    }


    fun getListReview(isLoadmore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.ON_NO_INTERNET, ICError(R.drawable.ic_error_network,
                    ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))))
            return
        }

        if (!isLoadmore) {
            offset = 0
        }

        interactor.getListReviewNotMe(productId, offset, APIConstants.LIMIT, object : ICNewApiListener<ICResponse<ICListResponse<ICPost>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICPost>>) {
                onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING, null))
                offset += APIConstants.LIMIT
                val list = mutableListOf<ICViewModel>()
                if (!obj.data!!.rows.isNullOrEmpty()) {
                    if (!isLoadmore) {
                        list.add(CountReviewModel(obj.data!!.count))
                    }

                    obj.data!!.rows.first().marginTop = SizeHelper.size10
                    for (i in obj.data!!.rows) {
                        list.add(ItemListReviewModel(i))
                    }
                    onListReview.postValue(list)
                }
            }

            override fun onError(error: ICResponseCode?) {
                checkError(error)
            }
        })
    }

    private fun getDataProduct() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.ON_NO_INTERNET, ICError(R.drawable.ic_error_network,
                    ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))))
            return
        }

        productInteractor.getProductDetail(productId, object : ICNewApiListener<ICLayoutData<JsonObject>> {
            override fun onSuccess(obj: ICLayoutData<JsonObject>) {
                currentProduct = JsonHelper.parseJson(obj.data!!, ICDataProductDetail::class.java)
                onDataProduct.postValue(currentProduct)
            }

            override fun onError(error: ICResponseCode?) {
            }
        })
    }

    fun getShareLink(objMyReview: ICPost) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.ON_NO_INTERNET, ICError(R.drawable.ic_error_network,
                    ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))))
            return
        }
        if (objMyReview.meta?.product?.id != null) {
            interactor.getLinkOfProduct(objMyReview.id, object : ICNewApiListener<ICResponse<String>> {
                override fun onSuccess(obj: ICResponse<String>) {
                    objMyReview.link = obj.data
                    onShareLinkData.postValue(objMyReview)
                }

                override fun onError(error: ICResponseCode?) {
                    onShareLinkData.postValue(null)
                }
            })
        }
    }

    private fun checkError(error: ICResponseCode?) {
        errorCount++
        if (errorCount == 3) {
            val message = if (error?.message.isNullOrEmpty()) {
                ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
            } else {
                error?.message
            }
            onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR,
                    ICError(R.drawable.ic_error_request, message)))
        }
    }
}