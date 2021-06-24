package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_model

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampRepository
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.detail_stamp_v6_1.ICDetailStampV6_1
import vn.icheck.android.network.models.stamp_hoa_phat.ICGetIdPageSocial
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.network.models.v1.ICProductQuestions
import vn.icheck.android.network.models.v1.ICRelatedProductV1
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.room.entity.ICProductIdInCart
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.model.ICQAStamp
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.model.ICStampItem
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.model.ICViewType

class DetailStampHoaPhatViewModel : ViewModel() {
    private val interactor = DetailStampRepository()
    private val productInteraction = ProductInteractor()

    var onSetbookmark = MutableLiveData<Boolean>()

    val statusCode = MutableLiveData<ICMessageEvent.Type>()
    val errorDataMessage = MutableLiveData<String>()
    val successDataMessage = MutableLiveData<String>()
    val errorDataStatusCode = MutableLiveData<Int>()

    val getIdSocial = MutableLiveData<Long>()

    val onAddData = MutableLiveData<MutableList<ICStampItem>>()
    val onDetailStamp = MutableLiveData<ICDetailStampV6_1.ICObjectDetailStamp>()
    val onBarcodeProduct = MutableLiveData<ICBarcodeProductV1>()

    var stampDetail: ICDetailStampV6_1.ICObjectDetailStamp? = null
    var barcodeProduct: ICBarcodeProductV1? = null
    var criteria: ICCriteria? = null
    var productQuestions: ICProductQuestions? = null
    var productReviews: List<ICProductReviews.ReviewsRow>? = null
    var productRelated: List<ICRelatedProductV1.RelatedProductRow>? = null
    var productAnswers: ICProductQuestions? = null

    private val listData = mutableListOf<ICStampItem>()

    lateinit var codeTem: String

    private var mId: String? = null

    fun getDataIntent(intent: Intent?) {
        codeTem = try {
            intent?.getStringExtra("data") ?: ""
        } catch (e: Exception) {
            ""
        }

        if (codeTem.isNotEmpty()) {
            codeTem = if (codeTem.contains("http")) {
                val separated: List<String> = codeTem.split("/")
                separated[3]
            } else {
                codeTem
            }
        }

        reloadData()
    }

    fun reloadData() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        val id = SessionManager.session.user?.id
        if (id != null) {
            mId = "i-$id"
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        viewModelScope.launch {
            try {
                var lat: String? = null
                var lon: String? = null

                if (APIConstants.LATITUDE != 0.0 && APIConstants.LONGITUDE != 0.0) {
                    lat = APIConstants.LATITUDE.toString()
                    lon = APIConstants.LONGITUDE.toString()
                }

                stampDetail = interactor.getDetailStampHoaPhat(SessionManager.session.user?.phone, mId, codeTem, lat, lon).data
                onDetailStamp.postValue(stampDetail)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (stampDetail != null) {
                try {
                    barcodeProduct = productInteraction.getProductByBarcode(stampDetail!!.barcode.toString())
                    onBarcodeProduct.postValue(barcodeProduct)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            if (barcodeProduct != null) {
                val listJob = mutableListOf<Deferred<Any?>>()

                listJob.add(async {
                    try {
                        criteria = productInteraction.getProductCriteria(barcodeProduct!!.id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })

                listJob.add(async {
                    try {
                        productQuestions = productInteraction.getProductQuestions(barcodeProduct?.id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })

                listJob.add(async {
                    try {
                        productReviews = productInteraction.getProductReviews(barcodeProduct?.id).rows
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })

                listJob.add(async {
                    try {
                        productRelated = productInteraction.getProductRelated(barcodeProduct?.owner?.vendorPage?.id).rows
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })

                for (item in listJob) {
                    item.await()
                }
            }

            if (!productQuestions?.questionsList.isNullOrEmpty()) {
                val filter = productQuestions!!.questionsList.filter { !it.hidden }
                if (filter.isNotEmpty() && filter.get(0).answer_count > 0) {
                    try {
                        productAnswers = productInteraction.getProductAnswers(productQuestions!!.questionsList[0].id)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

            if (stampDetail == null && barcodeProduct == null && criteria == null && productQuestions == null && productReviews == null && productRelated == null && productAnswers == null) {
                errorDataMessage.postValue(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            } else {
                createListStamp()
            }

        }
    }

    private fun createListStamp() {
        listData.clear()

        if (barcodeProduct != null) {
            listData.add(ICStampItem(ICViewType.IMAGE_HEADER_STAMP, barcodeProduct))

            if (criteria?.totalReviews != null && criteria?.productEvaluation != null) {
                barcodeProduct!!.countReviews = criteria!!.totalReviews!!
                barcodeProduct!!.productRate = criteria!!.productEvaluation!!.averagePoint!!
                listData.add(ICStampItem(ICViewType.INFOR_HEADER_STAMP, barcodeProduct))
            } else {
                listData.add(ICStampItem(ICViewType.INFOR_HEADER_STAMP, barcodeProduct))
            }
        }

        if (stampDetail != null) {
            listData.add(ICStampItem(ICViewType.ACCURACY_STAMP, stampDetail))
        }

        if (!stampDetail?.product_link.isNullOrEmpty()) {
            listData.add(ICStampItem(ICViewType.PRODUCT_ECCOMMERCE_TYPE, stampDetail!!.product_link))
        }

        listData.add(ICStampItem(ICViewType.METHOD_STAMP))

        if (!barcodeProduct?.informations.isNullOrEmpty()) {
            listData.add(ICStampItem(ICViewType.INFORMATION_PRODUCT_STAMP, barcodeProduct!!.informations!![0]))
        }

        if (!stampDetail?.product?.infos.isNullOrEmpty()) {
            listData.add(ICStampItem(ICViewType.STEP_BUILD_PRODUCT_STAMP, stampDetail?.product?.infos))
        }

        if (!barcodeProduct?.certificates.isNullOrEmpty()) {
            listData.add(ICStampItem(ICViewType.CERTIFICATE_STAMP, barcodeProduct!!.certificates))
        }

        if (barcodeProduct?.owner != null) {
            listData.add(ICStampItem(ICViewType.DNSH_STAMP, barcodeProduct!!.owner))
        }

        if (criteria?.customerEvaluation != null) {
            listData.add(ICStampItem(ICViewType.USER_REVIEW_STAMP, criteria))
        }

        if (!productReviews.isNullOrEmpty()) {
            if (criteria?.totalReviews != null) {
                barcodeProduct?.countReviews = criteria!!.totalReviews!!
                listData.add(ICStampItem(ICViewType.REVIEWS_TITLE, barcodeProduct))
            }

            for (i in 0 until if (productReviews!!.size > 1) 2 else productReviews!!.size) {
                val review = productReviews!![i]

                listData.add(ICStampItem(ICViewType.REVIEWS_CONTENT, review))

                if (!review.comments.isNullOrEmpty()) {
                    listData.add(ICStampItem(ICViewType.REVIEWS_COMMENT, review.comments!![0]))

                    if (review.comments!!.size > 1) {
                        listData.add(ICStampItem(ICViewType.REVIEWS_COMMENT, review))
                    }
                }
            }
        } else {
            listData.add(ICStampItem(ICViewType.NO_REVIEWS, barcodeProduct))
        }

        if (productQuestions?.questionsList.isNullOrEmpty()) {
            listData.add(ICStampItem(ICViewType.NO_QUESTION_STAMP))
        } else {
            val filter = productQuestions!!.questionsList.filter {
                !it.hidden
            }
            if (filter.isNotEmpty()) {
                if (filter.get(0).answer_count == 0) {
                    listData.add(ICStampItem(ICViewType.QUESTION_ANSWERS_STAMP, ICQAStamp(productQuestions!!.count, productQuestions!!.questionsList.get(0).content, null)))
                } else {
                    if (!productAnswers?.questionsList.isNullOrEmpty()) {
                        listData.add(ICStampItem(ICViewType.QUESTION_ANSWERS_STAMP, ICQAStamp(productQuestions!!.count, productQuestions!!.questionsList.get(0).content, productAnswers!!.questionsList.first().content)))
                    }
                }
            } else {
                listData.add(ICStampItem(ICViewType.NO_QUESTION_STAMP))
            }
        }

        if (!productRelated.isNullOrEmpty()) {
            listData.add(ICStampItem(ICViewType.RELATED_PRODUCT_STAMP, productRelated))
        }

        onAddData.postValue(listData)
    }

    fun postBookmark(lat: String, lon: String) {
        viewModelScope.launch {
//            ICheckApplication.getInstance().icLocationProvider.refresh()
            delay(200)
            try {
                val url = APIConstants.socialHost + APIConstants.Product.ADD_BOOKMARK.replace("{id}", barcodeProduct?.id.toString())
//                val resp = ICNetworkClient.getSimpleApiClient().bookmarkProduct(host, body)
                ICNetworkClient.getNewSocialApi().postRequest(url)
//                barcodeProduct?.userBookmark = resp
                onSetbookmark.postValue(true)
            } catch (e: Exception) {
                onSetbookmark.postValue(false)
                errorDataMessage.postValue(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        }
    }

    fun deleteBookmark() {
        viewModelScope.launch {
            try {
                val host = APIConstants.socialHost + APIConstants.Product.DELETE_BOOKMARK.replace("{id}", barcodeProduct?.id.toString())
                ICNetworkClient.getNewSocialApi().postRequest(host)
//                ICNetworkClient.getSimpleApiClient().deleteProductBookmark(host)
                onSetbookmark.postValue(true)
            } catch (e: Exception) {
                onSetbookmark.postValue(false)
                errorDataMessage.postValue(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        }
    }

    fun addToCart() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            errorDataMessage.postValue(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (barcodeProduct?.id == null) {
            errorDataMessage.postValue(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        productInteraction.addToCartStamp(barcodeProduct?.name, barcodeProduct?.id, barcodeProduct?.image, barcodeProduct?.price
                ?: 0, object : ICNewApiListener<ICResponse<Int>> {
            override fun onSuccess(obj: ICResponse<Int>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.statusCode == "200") {
                    AppDatabase.getDatabase(ICheckApplication.getInstance()).productIdInCartDao().insertProduct(ICProductIdInCart(barcodeProduct?.id
                            ?: 0, barcodeProduct?.price ?: 0,1))
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_PRICE))
                    successDataMessage.postValue(getString(R.string.them_vao_gio_hang_thanh_cong))
                } else {
                    errorDataMessage.postValue(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorDataMessage.postValue(error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun getIdPageSocial(id: Long) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            errorDataMessage.postValue(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        productInteraction.getIdPageSocial(id,object: ICNewApiListener<ICResponse<ICListResponse<ICGetIdPageSocial>>>{
            override fun onSuccess(obj: ICResponse<ICListResponse<ICGetIdPageSocial>>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (!obj.data?.rows.isNullOrEmpty()){
                    obj.data?.rows?.get(0)?.id?.let {
                        getIdSocial.postValue(it)
                    }
                } else {
                    errorDataMessage.postValue(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorDataMessage.postValue(error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

//    fun addToCard() {
//        viewModelScope.launch {
//            try {
//                val body = hashMapOf<String, Any>()
//                if (barcodeProduct?.id != null) {
//                    body["item_id"] = barcodeProduct?.id!!
//                }
//                body["quantity"] = 1
//                val host = APIConstants.socialHost + APIConstants.CARTADD()
//                ICNetworkClient.getNewSocialApi().addProductToCart(host, body)
//                errorDataMessage.postValue(getString(R.string.them_vao_gio_hang_thanh_cong))
//            } catch (e: Exception) {
//                errorDataMessage.postValue(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
//            }
//        }
//    }
}