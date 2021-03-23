package vn.icheck.android.screen.user.edit_review

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.product_review.ProductReviewInteractor
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.product_detail.ICDataProductDetail
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.util.ick.logError
import java.io.File
import java.lang.Exception

class EditReviewViewModel : ViewModel() {
    private var productId: Long = -1L
    private var fromHome: String? = null
    private var productBarcode: String? = null
    private var postion: Int = -1

    var onReviewData = MutableLiveData<ICProductMyReview>()
    var onStatusMessage = MutableLiveData<ICMessageEvent>()
    var onDataProduct = MutableLiveData<ICDataProductDetail>()
    var onPostReviewSuccess = MutableLiveData<ICPost>()

    val interactor = ProductReviewInteractor()
    val productInteractor = ProductInteractor()

    var currentProduct: ICDataProductDetail? = null
    private var messageCriteria = ""
    private var listCriteria = mutableListOf<ICReqCriteriaReview>()
    private var listImageString = mutableListOf<String>()
    private var listImageFile = mutableListOf<File>()
    var createReview = false

    private var pageId = if (SettingManager.getPostPermission() != null) {
        if (SettingManager.getPostPermission()!!.type == Constant.PAGE || SettingManager.getPostPermission()!!.type == Constant.ENTERPRISE) {
            SettingManager.getPostPermission()!!.id
        } else {
            null
        }
    } else {
        null
    }

    val getProductId: Long
        get() = productId

    val getFromHome: String?
        get() = fromHome

    val getProductBarcode: String?
        get() = productBarcode

    val getPosition: Int
        get() = postion


    fun getData(intent: Intent) {
        onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOADING, null))
        productId = try {
            intent.getLongExtra(Constant.DATA_1, -1L)
        } catch (e: Exception) {
            -1L
        }

        productBarcode = try {
            intent.getStringExtra(Constant.DATA_2)
        } catch (e: Exception) {
            null
        }

        fromHome = try {
            intent.getStringExtra(Constant.DATA_3)
        } catch (e: Exception) {
            null
        }

        postion = try {
            intent.getIntExtra(Constant.DATA_4, -1)
        } catch (e: Exception) {
            -1
        }

        if (productId != -1L) {
            getReview()
            getProduct()
        } else {
            onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.BACK, null))
        }
    }

    private fun getReview() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.BACK, null))
            return
        }

        ProductReviewInteractor().getMyReview(productId, pageId, object : ICNewApiListener<ICResponse<ICProductMyReview>> {
            override fun onSuccess(obj: ICResponse<ICProductMyReview>) {
                onReviewData.postValue(obj.data!!)
                onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING, null))

            }

            override fun onError(error: ICResponseCode?) {
                onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.BACK, null))
            }
        })
    }

    private fun getProduct() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
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

    fun uploadImage(message: String, criteria: MutableList<ICReqCriteriaReview>, attachment: MutableList<Any>) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR,
                    ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().applicationContext.getString(R.string.ket_noi_mang_cua_ban_co_van_de_vui_long_thu_lai), null, -1)))
            return
        }

        listImageString.clear()
        listImageFile.clear()

        this.listCriteria = criteria
        this.messageCriteria = message

        for (item in attachment) {
            if (item is File) {
                listImageFile.add(item)
            }
            if (item is String) {
                listImageString.add(item)
            }
        }

        onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOADING, null))

        if (listImageFile.isNotEmpty()) {
            uploadImageToServer()
        } else {
            postReview(messageCriteria, listCriteria)
        }
    }

    private fun uploadImageToServer() {
        viewModelScope.launch {
            val listCall = mutableListOf<Deferred<Any?>>()
            listImageFile.forEach {
                listCall.add(async {
                    try {
                        val response = withTimeout(60000) { ImageHelper.uploadMediaV2(it) }
                        if (!response.data?.src.isNullOrEmpty()) {
                            listImageString.add(response.data?.src!!)
                        }
                    } catch (e: Exception) {
                        logError(e)
                    }
                })
            }
            listCall.awaitAll()

            if (!listImageString.isNullOrEmpty()) {
                postReview(messageCriteria, listCriteria)
            }else{
                onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR,
                        ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, -1)))
            }
        }
    }

    fun postReview(message: String, criteria: MutableList<ICReqCriteriaReview>) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR,
                    ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().applicationContext.getString(R.string.ket_noi_mang_cua_ban_co_van_de_vui_long_thu_lai), null, -1)))
            return
        }

        onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOADING, null))


        val listImage = mutableListOf<ICMedia>()
        for (item in listImageString) {
            listImage.add(ICMedia(item, if (item.contains(".mp4")) {
                Constant.VIDEO
            } else {
                Constant.IMAGE
            }))
        }

        ProductReviewInteractor().postReview(productId, message, criteria, listImage, pageId, object : ICNewApiListener<ICResponse<ICPost>> {
            override fun onSuccess(obj: ICResponse<ICPost>) {
                onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING, null))
                onPostReviewSuccess.postValue(obj.data)
            }

            override fun onError(error: ICResponseCode?) {
                val message = if (error?.message.isNullOrEmpty()) {
                    ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                } else {
                    error?.message
                }
                onStatusMessage.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR,
                        ICError(R.drawable.ic_error_request, message, null, -1)))
            }
        })

    }

}