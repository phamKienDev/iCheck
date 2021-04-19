package vn.icheck.android.screen.user.product_detail.product

import android.content.Intent
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.component.BottomModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.commentpost.ICCommentPostMore
import vn.icheck.android.component.image_video_slider.ICImageVideoSliderModel
import vn.icheck.android.component.image_video_slider.ICMediaType
import vn.icheck.android.component.image_video_slider.MediaLogic
import vn.icheck.android.component.infomation_contribution.ContributrionModel
import vn.icheck.android.component.my_contribution.MyContributionViewModel
import vn.icheck.android.component.noimage.NoImageModel
import vn.icheck.android.component.product.certifications.CertificationsModel
import vn.icheck.android.component.product.emty_qa.EmptyQAModel
import vn.icheck.android.component.product.enterprise.EnterpriseModelV2
import vn.icheck.android.component.product.header.ProductHeaderModelV2
import vn.icheck.android.component.product.infor_contribution.InformationContributionModel
import vn.icheck.android.component.product.mbtt.MbttModel
import vn.icheck.android.component.product.notverified.ProductNotVerifiedModel
import vn.icheck.android.component.product.npp.DistributorModel
import vn.icheck.android.component.product.related_product.RelatedProductModel
import vn.icheck.android.component.product.vendor.VendorModel
import vn.icheck.android.component.product_list_review.ProductListReviewModel
import vn.icheck.android.component.product_question_answer.ProductQuestionModel
import vn.icheck.android.component.product_review.my_review.MyReviewModel
import vn.icheck.android.component.product_review.submit_review.SubmitReviewModel
import vn.icheck.android.component.shopvariant.product_detail.ShopProductModel
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.model.category.CategoryAttributesItem
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.ads.AdsRepository
import vn.icheck.android.network.feature.post.PostInteractor
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.feature.product_review.ProductReviewInteractor
import vn.icheck.android.network.feature.setting.SettingRepository
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.product_detail.ICAlertProduct
import vn.icheck.android.network.models.product_detail.ICBasicInforProduct
import vn.icheck.android.network.models.product_detail.ICDataProductDetail
import vn.icheck.android.network.models.product_detail.ICManager
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.home_page.model.ICListHomeItem
import vn.icheck.android.screen.user.product_detail.product.model.IckReviewSummaryModel
import vn.icheck.android.util.kotlin.HideWebUtils

class IckProductDetailViewModel : BaseViewModel() {
    private val productRepository = ProductInteractor()
    private val reviewInteraction = ProductReviewInteractor()
    private val settingInteraction = SettingRepository()
    private val adsRepository = AdsRepository()
    private val postInteractor = PostInteractor()

    var barcode = ""
    var isScan = false
    var productID = 0L
    var typeReload: String? = null

    private val layoutHelper = LayoutHelper()

    val onSetTitle = MutableLiveData<String>()
    val onDataProduct = MutableLiveData<ICDataProductDetail>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()
    val errorRequest = MutableLiveData<ICError>()
    val alertProduct = MutableLiveData<ICAlertProduct>()
    val onProductNotFound = MutableLiveData<Boolean>()
    val onProductAdminDeactivate = MutableLiveData<Boolean>()
    val onProductBusinessDeactivate = MutableLiveData<Boolean>()
    val onProductBusinessDeactivateNotManager = MutableLiveData<Boolean>()
    val onClearData = MutableLiveData<Boolean>()
    val onAddLayout = MutableLiveData<ICLayout>()
    val onUpdateLayout = MutableLiveData<ICLayout>()
    val onUpdateListLayout = MutableLiveData<ICListHomeItem>()
    val onUpdateAds = MutableLiveData<Boolean>()
    val onUpdateQuestion = MutableLiveData<ICLayout>()
    val onUpdateReview = MutableLiveData<ICLayout>()
    val errorMessage = MutableLiveData<String>()
    val onDetailPost = MutableLiveData<ICPost>()
    val onMyReviewData = MutableLiveData<ICProductMyReview>()
    val onRegisterBuyProduct = MutableLiveData<Boolean>()

    val onUrlDistributor = MutableLiveData<String>()

    val onAddHolderInput = MutableLiveData<ICLayout>()

    var code = ""

    var productDetail: ICDataProductDetail? = null

    var infoProduct: ICBasicInforProduct? = null

    var verifyProduct = false
    var enableContribution = false
    private var owner: ICOwner? = null
    var urlMyReview: String? = null
    var reviewSummaryData: ICReviewSummary? = null

    var urlBuy: String? = null

    private var totalRequest = 0
    private var totalError = 0

    val onShareLink = MutableLiveData<Any?>()
    val onShareLinkProduct = MutableLiveData<String>()
    val listMedia = arrayListOf<ICMedia>()

    /*Transparency*/
    var onPostTransparency = MutableLiveData<ICTransparency>()
    val listInfo = arrayListOf<CategoryAttributesItem>()

    fun getData(intent: Intent?) {
        barcode = intent?.getStringExtra(Constant.DATA_1) ?: ""
        isScan = intent?.getBooleanExtra(Constant.DATA_2, false) ?: false
        productID = intent?.getLongExtra(Constant.DATA_3, 0) ?: 0

//        if (barcode.isNotEmpty()) {
//            onSetTitle.postValue(barcode)
//        }

        getProductLayout()
    }

    fun getProductLayout(isUpdate: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            errorRequest.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        dispose()

        if (barcode.isNotEmpty() || productID != 0L) {
            if (typeReload != null) {
                getProductLayoutByID(isUpdate)
            } else {
                if (barcode.isNotEmpty()) {
                    getProductLayoutByBarcode(isUpdate)
                } else {
                    typeReload = "productId"
                    getProductLayoutByID(isUpdate)
                }
            }
        } else {
            statusCode.postValue(ICMessageEvent.Type.BACK)
        }
    }

    private fun getProductLayoutByBarcode(isUpdate: Boolean) {
        if (isScan && !isUpdate) {
            productRepository.scanProduct(barcode, object : ICNewApiListener<ICLayoutData<JsonObject>> {
                override fun onSuccess(obj: ICLayoutData<JsonObject>) {
                    checkProductLayout(isUpdate, obj)
                }

                override fun onError(error: ICResponseCode?) {
                    if (error?.message.isNullOrEmpty()) {
                        error?.message = ICheckApplication.getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai)
                    }
                    errorRequest.postValue(ICError(R.drawable.ic_error_request, error?.message
                            ?: ICheckApplication.getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai)))
                }
            })
        } else {
            productRepository.getProductDetail(barcode, object : ICNewApiListener<ICLayoutData<JsonObject>> {
                override fun onSuccess(obj: ICLayoutData<JsonObject>) {
                    checkProductLayout(isUpdate, obj)
                }

                override fun onError(error: ICResponseCode?) {
                    if (error?.message.isNullOrEmpty()) {
                        error?.message = ICheckApplication.getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai)
                    }
                    errorRequest.postValue(ICError(R.drawable.ic_error_request, error?.message
                            ?: ICheckApplication.getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai)))
                }
            })
        }
    }

    fun getProductLayoutByID(isUpdate: Boolean) {
        productRepository.getProductDetail(productID, object : ICNewApiListener<ICLayoutData<JsonObject>> {
            override fun onSuccess(obj: ICLayoutData<JsonObject>) {
                checkProductLayout(isUpdate, obj)
            }

            override fun onError(error: ICResponseCode?) {
                if (error?.message.isNullOrEmpty()) {
                    error?.message = ICheckApplication.getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai)
                }
                errorRequest.postValue(ICError(R.drawable.ic_error_request, error?.message
                        ?: ICheckApplication.getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai)))
            }
        })
    }

    private fun checkProductLayout(isUpdate: Boolean, obj: ICLayoutData<JsonObject>) {
        productDetail = JsonHelper.parseJson(obj.data!!, ICDataProductDetail::class.java)
        enableContribution = layoutHelper.getEnableContribution(obj.data, "enableContribution")
                ?: false

        if (productID == 0L) {
            productID = productDetail?.id ?: 0
        }

        if (productDetail?.alert != null) {
            alertProduct.postValue(productDetail?.alert)
        }

        if (barcode.isEmpty() && !productDetail?.basicInfo?.barcode.isNullOrEmpty()) {
            barcode = productDetail?.basicInfo?.barcode!!
        }

        if (barcode.isNotEmpty()) {
            onSetTitle.postValue(barcode)
        }

        if (productDetail?.status == "ok" && productDetail?.state == "active") {
            checkBookMark()
            getProductLayoutData(isUpdate, obj, productDetail)
            onDataProduct.postValue(productDetail)
        } else if (productDetail?.status == "notFound") {
            onProductNotFound.postValue(true)
        } else if (productDetail?.state == "adminDeactive") {
            onProductAdminDeactivate.postValue(true)
        } else if (productDetail?.state == "businessDeactive") {
            if (productDetail?.manager?.code != 4) {
                onProductBusinessDeactivate.postValue(true)
            } else {
                onProductBusinessDeactivateNotManager.postValue(true)
            }
        } else {
            statusCode.postValue(ICMessageEvent.Type.BACK)
        }
    }

    private fun checkBookMark() {
        productRepository.checkBookmark(productID, object : ICNewApiListener<ICResponse<ICBookmark>> {
            override fun onSuccess(obj: ICResponse<ICBookmark>) {
                if (obj.data != null) EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UPDATE_BOOKMARK, true))
                else EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UPDATE_BOOKMARK, false))
            }

            override fun onError(error: ICResponseCode?) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UPDATE_BOOKMARK, false))
            }
        })
    }

    fun addBookMark() {
        if (productID == 0L) {
            return
        }

        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            errorMessage.postValue(ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        productRepository.addBookmark(productID, object : ICNewApiListener<ICResponseCode> {
            override fun onSuccess(obj: ICResponseCode) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UPDATE_BOOKMARK, true))
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorMessage.postValue(error?.message
                        ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun deleteBookMark() {
        if (productID == 0L) {
            return
        }

        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            errorMessage.postValue(ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        productRepository.deleteBookmark(productID, object : ICNewApiListener<ICResponseCode> {
            override fun onSuccess(obj: ICResponseCode) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UPDATE_BOOKMARK, false))
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorMessage.postValue(error?.message
                        ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    private fun getProductLayoutData(isUpdate: Boolean, obj: ICLayoutData<JsonObject>, productDetail: ICDataProductDetail?) {
        viewModelScope.launch {
            if (!isUpdate) onClearData.value = true

            totalRequest = obj.layout?.size ?: 0
            totalError = 0

            for (layout in obj.layout ?: mutableListOf()) {
                when (layout.id.toString()) {
                    "attachment-1" -> {
                        if (!isUpdate) getAttachment(obj.data, layout)
                    }
                    "info-product-1" -> {
                        if (!isUpdate) getInfoProduct(obj.data, layout, productDetail)
                    }
                    "review-summary-1" -> {
                        getReviewSummary(isUpdate, obj.data, layout)
                    }
                    "contribution-1" -> {
                        getContribution(isUpdate, obj.data, layout)
                    }
                    "verification-1" -> {
                        if (!isUpdate) getVerification(obj.data, layout)
                    }
                    "certificate-1" -> {
                        if (!isUpdate) getCertificate(obj.data, layout)
                    }
                    "transparency-1" -> {
                        if (!isUpdate) getTransparency(obj.data, layout, productDetail)
                    }
                    "vendor-1" -> {
                        if (!isUpdate) getListVendors(obj.data, layout)
                    }
                    "distributor-1" -> {
                        if (!isUpdate) getListDistributors(obj.data, layout)
                    }
                    "owner-1" -> {
                        if (!isUpdate) getOwner(obj.data, layout, productDetail)
                    }
                    "shop-1" -> {
                        if (!isUpdate) getListShops(obj.data, layout)
                    }
                    "my-review-1" -> {
                        if (!isUpdate) getMyReview(isUpdate, obj.data, layout)
                    }
                    "information-1" -> {
                        if (!isUpdate) getListInformation(obj.data, layout)
                    }
                    "reviews-1" -> {
                        getListReviews(isUpdate, obj.data, layout)
                    }
                    "qa-1" -> {
                        getListQuestion(isUpdate, obj.data, layout)
                    }
                    "owner-products-1" -> {
                        if (!isUpdate) getListOwnerProduct(obj.data, layout)
                    }
                    "related-products-1" -> {
                        if (!isUpdate) getListRelatedProduct(obj.data, layout)
                    }
                    "ads-1" -> {
                        if (!isUpdate) {
                            onAddLayout.value = layout.apply {
                                viewType = ICViewTypes.ADS_TYPE
                                subType = ICViewTypes.ADS_TYPE
                            }
                            getOrUpdateAds(layout)
                        }
                    }
                    "contact-1" -> {
                        if (!isUpdate) getContact(layout)
                    }
                    "product-contribute-info-1" -> {
                        if (!isUpdate) {
                            getContributionInfo(obj.data, layout)
                        }
                    }
                    "ecommerce-1" -> {
                        if (!isUpdate) getProductsECommerce(layout)
                    }
                }
            }
        }
    }


    private fun getAttachment(data: JsonObject?, layout: ICLayout) {
        listMedia.clear()
        listMedia.addAll(layoutHelper.getListMedia(data, layout.key) ?: arrayListOf())
        if (!listMedia.isNullOrEmpty()) {
            val listData = arrayListOf<MediaLogic>()

            for (media in listMedia) {
                if (!media.content.isNullOrEmpty() && !media.type.isNullOrEmpty()) {
                    if (media.type == "image") {
                        listData.add(MediaLogic(media.content, ICMediaType.TYPE_IMAGE))
                    } else if (media.type == "video") {
                        listData.add(MediaLogic(media.content, ICMediaType.TYPE_VIDEO).apply {
                            exoPlayer = SimpleExoPlayer.Builder(ICheckApplication.getInstance()).build()
                            setMs(DefaultDataSourceFactory(ICheckApplication.getInstance(), Util.getUserAgent(ICheckApplication.getInstance(), "iCheck")))
                        })
                    }
                }
            }

            layout.viewType = ICViewTypes.IMAGE_VIDEO_SLIDER
            layout.data = ICImageVideoSliderModel(listData, viewModelScope)
            onAddLayout.value = layout
        } else {
            layout.viewType = ICViewTypes.HOLDER_NO_IMAGE
            layout.data = NoImageModel()
            onAddLayout.value = layout
        }
    }

    private fun getInfoProduct(data: JsonObject?, layout: ICLayout, productDetail: ICDataProductDetail?) {
        val basicProduct = layoutHelper.getObject(data, layout.key, ICBasicInforProduct::class.java)
        if (basicProduct != null) {
            barcode = basicProduct.barcode ?: ""
            onSetTitle.postValue(barcode)
            HideWebUtils.showWeb("Product_Detail", basicProduct.barcode, productID)
            infoProduct = basicProduct
            productDetail?.barcode = basicProduct.barcode ?: ""
            layout.viewType = ICViewTypes.HEADER_TYPE
            layout.data = ProductHeaderModelV2(basicProduct, productDetail)
            onAddLayout.value = layout
        }
    }

    private fun getReviewSummary(isUpdate: Boolean, data: JsonObject?, layout: ICLayout) {
        layout.viewType = ICViewTypes.REVIEW_SUMMARY_TYPE
        val reviewSummary = layoutHelper.getObject(data, layout.key, ICReviewSummary::class.java)

        if (reviewSummary != null) {
            layout.data = IckReviewSummaryModel(productID, reviewSummary)
            reviewSummaryData = reviewSummary
            if (!isUpdate) onAddLayout.value = layout
            else onUpdateLayout.value = layout
        } else if (!layout.request.url.isNullOrEmpty()) {
            if (!isUpdate) onAddLayout.value = layout

            productRepository.getReviewSummary(layout.request.url!!, object : ICNewApiListener<ICResponse<ICReviewSummary>> {
                override fun onSuccess(obj: ICResponse<ICReviewSummary>) {
                    if (obj.data != null) {
                        reviewSummaryData = obj.data
                        layout.data = IckReviewSummaryModel(productID, obj.data!!)
                        onUpdateLayout.value = layout
                    } else {
                        checkTotalError(layout)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    checkTotalError(layout)
                }
            })
        } else if (isUpdate) {
            onAddLayout.value = layout
        }
    }

    private fun getContribution(isUpdate: Boolean, data: JsonObject?, layout: ICLayout) {
        layout.viewType = ICViewTypes.CONTRIBUTE_USER

        val productVerify = layoutHelper.getVerify(data, "verified") ?: false
        if (!productVerify) {
            val contribution = layoutHelper.getObject(data, layout.key, ICProductContribution::class.java)

            if (contribution?.contribution != null) {
                layout.data = ContributrionModel(contribution, productID, productVerify, barcode)
                if (!isUpdate) onAddLayout.value = layout
                else onUpdateLayout.value = layout
            } else if (!layout.request.url.isNullOrEmpty()) {
                if (!isUpdate) onAddLayout.value = layout

                productRepository.getContribution(layout.request.url!!, object : ICNewApiListener<ICResponse<ICProductContribution>> {
                    override fun onSuccess(obj: ICResponse<ICProductContribution>) {
                        if (enableContribution) {
                            if (obj.data?.userContributions.isNullOrEmpty()) {
                                layout.viewType = ICViewTypes.EMPTY_CONTRIBUTE_USER
                                layout.data = ContributrionModel(ICProductContribution(), productID, productVerify, barcode)
                            } else {
                                obj.data?.apply {
                                    for (i in (userContributions?.size ?: 0) - 1 downTo 0) {
                                        if (userContributions?.get(i) == this.contribution?.user?.avatar) {
                                            userContributions?.removeAt(i)
                                            count = (count ?: 1) - 1
                                        }
                                    }
                                }
                                layout.data = ContributrionModel(obj.data!!, productID, productVerify, barcode)
                            }
                            onUpdateLayout.value = layout
                        } else {
                            checkTotalError(layout)
                        }
                    }

                    override fun onError(error: ICResponseCode?) {
                        checkTotalError(layout)
                    }
                })
            } else if (isUpdate) {
                onAddLayout.value = layout
            }
        } else {
            val owner = layoutHelper.getObject(data, "owner", ICOwner::class.java)
            val manager = layoutHelper.getObject(data, "manager", ICManager::class.java)

            layout.data = ContributrionModel(null, productID, productVerify, barcode, owner, manager)
            if (!isUpdate) onAddLayout.value = layout
            else onUpdateLayout.value = layout
        }
    }

    private fun getVerification(data: JsonObject?, layout: ICLayout) {
        verifyProduct = layoutHelper.getVerify(data, layout.key) ?: false

        if (verifyProduct) {
            layout.viewType = ICViewTypes.VERIFIED_TYPE
            onAddLayout.value = layout
        } else {
            layout.viewType = ICViewTypes.NOT_VERIFIED_TYPE
            onAddLayout.value = layout
        }

        settingInteraction.getClientSettingSocialVerify("message_verify", verifyProduct, productID, object : ICNewApiListener<ICResponse<ICClientSetting>> {
            override fun onSuccess(obj: ICResponse<ICClientSetting>) {
                if (obj.data != null) {
                    if (verifyProduct) {
                        layout.data = obj.data.apply {
                            owner = productDetail?.owner
                        }
                    } else {
                        layout.data = ProductNotVerifiedModel(productDetail, obj.data!!)
                    }
                    onUpdateLayout.value = layout
                } else {
                    checkTotalError(layout)
                }
            }

            override fun onError(error: ICResponseCode?) {
                checkTotalError(layout)
            }
        })
    }

    private fun getCertificate(data: JsonObject?, layout: ICLayout) {
        val castCertificate = layoutHelper.getListString(data, layout.key)

        if (!castCertificate.isNullOrEmpty()) {
            layout.viewType = ICViewTypes.CHUNG_CHI_TYPE
            layout.data = CertificationsModel(castCertificate)
            onAddLayout.value = layout
        }
    }

    private fun getTransparency(data: JsonObject?, layout: ICLayout, productDetail: ICDataProductDetail?) {
        if (productDetail?.verified != null && productDetail?.verified == false) {
            layout.viewType = ICViewTypes.TRANSPARENCY_TYPE
            val transparency = layoutHelper.getObject(data, layout.key, ICTransparency::class.java)

            if (transparency != null) {
                layout.data = MbttModel(transparency, productID)
                onAddLayout.value = layout
            } else if (!layout.request.url.isNullOrEmpty()) {
                onAddLayout.value = layout

                productRepository.getTransparency(layout.request.url!!, object : ICNewApiListener<ICResponse<ICTransparency>> {
                    override fun onSuccess(obj: ICResponse<ICTransparency>) {
                        if (obj.data != null) {
                            layout.data = MbttModel(obj.data!!, productID)
                            onUpdateLayout.value = layout
                        } else {
                            checkTotalError(layout)
                        }
                    }

                    override fun onError(error: ICResponseCode?) {
                        checkTotalError(layout)
                    }
                })
            }
        }
    }

    private fun getListVendors(data: JsonObject?, layout: ICLayout) {
        layout.viewType = ICViewTypes.VENDOR_TYPE
        val listData = layoutHelper.getListVendor(data, layout.key)

        if (!listData.isNullOrEmpty()) {
            layout.data = VendorModel(listData, null)
            onAddLayout.value = layout
        } else if (!layout.request.url.isNullOrEmpty()) {
            onAddLayout.value = layout

            productRepository.getListPage(layout.request.url!!, object : ICNewApiListener<ICResponse<ICListResponse<ICPage>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICPage>>) {
                    if (!obj.data?.rows.isNullOrEmpty()) {
                        layout.data = VendorModel(obj.data!!.rows, null)
                        onUpdateLayout.value = layout
                    } else {
                        checkTotalError(layout)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    checkTotalError(layout)
                }
            })
        }
    }

    private fun getListDistributors(data: JsonObject?, layout: ICLayout) {
        layout.viewType = ICViewTypes.DISTRIBUTOR
        val distributors = layoutHelper.getListVendor(data, layout.key)

        if (!distributors.isNullOrEmpty()) {
            layout.data = DistributorModel(distributors, productID)
            onAddLayout.value = layout
        } else if (!layout.request.url.isNullOrEmpty()) {
            onAddLayout.value = layout
            onUrlDistributor.postValue(layout.request.url!!)

            productRepository.getListPage(layout.request.url!!, object : ICNewApiListener<ICResponse<ICListResponse<ICPage>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICPage>>) {
                    if (!obj.data?.rows.isNullOrEmpty()) {
                        layout.data = DistributorModel(obj.data!!.rows, productID)
                        onUpdateLayout.value = layout
                    } else {
                        checkTotalError(layout)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    checkTotalError(layout)
                }
            })
        }
    }

    private fun getOwner(data: JsonObject?, layout: ICLayout, productDetail: ICDataProductDetail?) {
        owner = layoutHelper.getObject(data, layout.key, ICOwner::class.java)

        if (owner != null) {
            layout.viewType = ICViewTypes.ENTERPRISE_TYPE
            layout.data = if (verifyProduct) {
                if (owner!!.verified == true) {
                    EnterpriseModelV2(owner!!, R.drawable.ic_verified_24px, R.color.colorPrimary)
                } else {
                    EnterpriseModelV2(owner!!, null, R.color.colorPrimary)
                }
            } else {
                if (owner!!.verified == true) {
                    EnterpriseModelV2(owner!!, null, R.color.colorPrimary)
                } else {
                    EnterpriseModelV2(owner!!, R.drawable.ic_not_verified_24px, R.color.colorDisableText)
                }
            }

            onAddLayout.value = layout
        } else {
            if (productDetail?.enableContribution == true) {
                layout.viewType = ICViewTypes.EMPTY_CONTRIBUTION_INTERPRISE_TYPE
                layout.data = InformationContributionModel(barcode)
                onAddLayout.value = layout
            }
            if (productDetail?.unverifiedOwner != null) {
                val newLayout = ICLayout()
                newLayout.viewType = ICViewTypes.ENTERPRISE_TYPE
                newLayout.data = EnterpriseModelV2(productDetail.unverifiedOwner, R.drawable.ic_not_verified_24px, R.color.colorDisableText)
                onAddLayout.value = newLayout
            }
        }
    }

    private fun getListShops(data: JsonObject?, layout: ICLayout) {
        layout.viewType = ICViewTypes.SHOP_VARIANT_TYPE
        val listData = layoutHelper.getListShopVariant(data, layout.key)

        if (!listData.isNullOrEmpty()) {
            layout.data = ShopProductModel(listData)
            onAddLayout.value = layout
        } else if (!layout.request.url.isNullOrEmpty()) {
            onAddLayout.value = layout

            productRepository.getListShopVariant(layout.request.url!!, object : ICNewApiListener<ICResponse<ICListResponse<ICShopVariantV2>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICShopVariantV2>>) {
                    if (!obj.data?.rows.isNullOrEmpty()) {
                        layout.data = ShopProductModel(obj.data!!.rows)
                        onUpdateLayout.value = layout
                    } else {
                        checkTotalError(layout)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    checkTotalError(layout)
                }
            })
        }
    }

    private fun getMyReview(isUpdate: Boolean, data: JsonObject?, layout: ICLayout) {
        urlMyReview = layout.request.url
        val obj = layoutHelper.getObject(data, layout.key, ICProductMyReview::class.java)

        if (obj != null) {
            if (obj.myReview != null) {
                layout.viewType = ICViewTypes.MY_REVIEW_TYPE
                layout.data = MyReviewModel(obj)
                if (!isUpdate) onAddLayout.value = layout
                else onUpdateReview.value = layout
            } else if (!obj.criteria.isNullOrEmpty()) {
                layout.viewType = ICViewTypes.SUBMIT_REVIEW_TYPE
                layout.data = SubmitReviewModel(obj.criteria!!, productID)
                if (!isUpdate) onAddLayout.value = layout
                else onUpdateReview.value = layout
            } else if (isUpdate) {
                onUpdateReview.value = layout
            }
        } else if (!layout.request.url.isNullOrEmpty()) {
            if (!isUpdate) onAddLayout.value = layout

            val pageId = if (SettingManager.getPostPermission() != null) {
                if (SettingManager.getPostPermission()!!.type == Constant.PAGE || SettingManager.getPostPermission()!!.type == Constant.ENTERPRISE) {
                    SettingManager.getPostPermission()!!.id
                } else {
                    null
                }
            } else {
                null
            }

            productRepository.getMyReview(layout.request.url!!, pageId, object : ICNewApiListener<ICResponse<ICProductMyReview>> {
                override fun onSuccess(obj: ICResponse<ICProductMyReview>) {
                    if (obj.data != null) {
                        if (obj.data!!.myReview != null) {
                            layout.viewType = ICViewTypes.MY_REVIEW_TYPE
                            layout.data = MyReviewModel(obj.data!!)
                            if (!isUpdate) onUpdateLayout.value = layout
                            else onUpdateReview.value = layout
                        } else if (!obj.data!!.criteria.isNullOrEmpty()) {
                            layout.viewType = ICViewTypes.SUBMIT_REVIEW_TYPE
                            layout.data = SubmitReviewModel(obj.data!!.criteria!!, productID)
                            if (!isUpdate) onUpdateLayout.value = layout
                            else onUpdateReview.value = layout
                        }
                    } else {
                        checkTotalError(layout)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    checkTotalError(layout)
                }
            })
        } else if (isUpdate) {
            onUpdateReview.value = layout
        }
    }

    private fun getListInformation(data: JsonObject?, layout: ICLayout) {
        layout.viewType = ICViewTypes.DESCRIPTION_TYPE
        val listData = layoutHelper.getListInformation(data, layout.key)
        val productImage = productDetail?.media?.lastOrNull()?.content

        if (!listData.isNullOrEmpty()) {
            for (information in listData) {
                if (!information.shortContent.isNullOrEmpty() && !information.image.isNullOrEmpty()) {
                    information.productID = productID
                    information.productImage = productImage
                    onAddLayout.value = ICLayout(layout.id, layout.key, layout.request, layout.custom, null, layout.viewType, information)
                } else if (!information.shortContent.isNullOrEmpty()) {
                    information.productID = productID
                    information.productImage = productImage
                    onAddLayout.value = ICLayout(layout.id, layout.key, layout.request, layout.custom, null, layout.viewType, information)
                } else if (!information.image.isNullOrEmpty()) {
                    information.productID = productID
                    information.productImage = productImage
                    onAddLayout.value = ICLayout(layout.id, layout.key, layout.request, layout.custom, null, layout.viewType, information)
                }
            }
        } else if (!layout.request.url.isNullOrEmpty()) {
            onAddLayout.value = layout

            productRepository.getListInformation(layout.request.url!!, object : ICNewApiListener<ICResponse<ICListResponse<ICProductInformations>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICProductInformations>>) {
                    if (!obj.data?.rows.isNullOrEmpty()) {
                        val list = mutableListOf<ICLayout>()
                        for (information in obj.data?.rows!!) {
                            if (!information.shortContent.isNullOrEmpty() && !information.image.isNullOrEmpty()) {
                                information.productID = productID
                                information.productImage = productImage
                                onAddLayout.value = ICLayout(layout.id, layout.key, layout.request, layout.custom, null, layout.viewType, information)
                            } else if (!information.shortContent.isNullOrEmpty()) {
                                information.productID = productID
                                information.productImage = productImage
                                onAddLayout.value = ICLayout(layout.id, layout.key, layout.request, layout.custom, null, layout.viewType, information)
                            } else if (!information.image.isNullOrEmpty()) {
                                information.productID = productID
                                information.productImage = productImage
                                onAddLayout.value = ICLayout(layout.id, layout.key, layout.request, layout.custom, null, layout.viewType, information)
                            }
                        }
                        onUpdateListLayout.value = ICListHomeItem(layout.id.toString(), mutableListOf(), list)
                    } else {
                        checkTotalError(layout)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    checkTotalError(layout)
                }
            })
        }
    }

    private fun getListReviews(isUpdate: Boolean, data: JsonObject?, layout: ICLayout) {
        layout.viewType = ICViewTypes.LIST_REVIEWS_TYPE
        val listData = layoutHelper.getReviewsProduct(data, layout.key)

        if (listData != null) {
            layout.data = ProductListReviewModel(listData, listData.size, productID)
            if (!isUpdate) onAddLayout.value = layout
            else onUpdateLayout.value = layout
        } else if (!layout.request.url.isNullOrEmpty()) {
            if (!isUpdate) onAddLayout.value = layout

            productRepository.getListReview(layout.request.url!!, object : ICNewApiListener<ICResponse<ICListResponse<ICPost>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICPost>>) {
                    val count = obj.data?.count ?: reviewSummaryData?.ratingCount?.toInt()
                    if (!obj.data?.rows.isNullOrEmpty()) {
                        layout.data = ProductListReviewModel(obj.data?.rows!!, count?:1, productID)
                        onUpdateLayout.value = layout
                    } else {
                        checkTotalError(layout)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    checkTotalError(layout)
                }
            })
        } else if (isUpdate) {
            onUpdateLayout.value = layout
        }
    }

    private fun getListQuestion(isUpdate: Boolean, data: JsonObject?, layout: ICLayout) {
        val listData = layoutHelper.getListQuestion(data, layout.key)

        if (listData != null) {
            if (!listData.isNullOrEmpty()) {
                for (item in listData) {
                    item.isReply = true
                }
                layout.viewType = ICViewTypes.QUESTIONS_ANSWER_TYPE
                layout.data = getListQuestion(ICListResponse(listData.size, listData))
            } else {
                layout.viewType = ICViewTypes.EMPTY_QA_TYPE
                layout.data = EmptyQAModel(productID)
            }

            if (!isUpdate) onAddLayout.value = layout
            else onUpdateQuestion.value = layout
        } else if (!layout.request.url.isNullOrEmpty()) {
            if (!isUpdate) onAddLayout.value = layout

            productRepository.getListQuestion(layout.request.url!!, object : ICNewApiListener<ICResponse<ICListResponse<ICProductQuestion>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICProductQuestion>>) {
                    if (obj.data?.rows != null) {
                        if (obj.data!!.rows.isNotEmpty()) {
                            for (item in obj.data!!.rows) {
                                item.isReply = true
                            }
                            layout.viewType = ICViewTypes.QUESTIONS_ANSWER_TYPE
                            layout.data = getListQuestion(obj.data!!)
                        } else {
                            layout.viewType = ICViewTypes.EMPTY_QA_TYPE
                            layout.data = EmptyQAModel(productID)
                        }

                        if (!isUpdate) onUpdateLayout.value = layout
                        else onUpdateQuestion.value = layout
                    } else {
                        checkTotalError(layout)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    checkTotalError(layout)
                }
            })
        }
    }

    private fun getListQuestion(response: ICListResponse<ICProductQuestion>): ProductQuestionModel {
        val listData = mutableListOf<Any>()

        // Danh sách câu trả lời của câu hỏi
        val listChild = mutableListOf<ICProductQuestion>()

        for (parentItem in response.rows) {
            // câu hỏi sẽ margin top 10dp
            parentItem.marginTop = SizeHelper.size5

            // Add các câu trả lời của câu hỏi con vào list
            listChild.addAll(parentItem.replies ?: mutableListOf())
            parentItem.replies = null

            listData.add(parentItem)

            for (childItem in listChild) {
                // Add các câu trả lời con vào danh sách tổng
                childItem.parentID = parentItem.id
                childItem.marginTop = SizeHelper.size3
                childItem.marginStart = SizeHelper.size36
                listData.add(childItem)
            }

            // Nếu tổng câu trả lời lớn hơn danh sách câu trả lời đang hiện thì add thêm option
            if (parentItem.replyCount > listChild.size) {
                listData.add(ICCommentPostMore(parentItem.id, parentItem.replyCount, listChild.size, 0, ""))
            }

            // Xóa các câu trả lời  để sử dụng cho các câu hỏi sau
            listChild.clear()
        }

//        response.rows = listData
        return ProductQuestionModel(ICListResponse(response.count, listData), productID, barcode)
    }

    private fun getListOwnerProduct(data: JsonObject?, layout: ICLayout) {
        layout.viewType = ICViewTypes.OWNER_PRODUCT_TYPE
        val listData = layoutHelper.getListProductTrend(data, layout.key)

        if (!listData.isNullOrEmpty()) {
            val url = APIConstants.socialHost + APIConstants.Product.GET_RELATED_PRODUCT_SOCIAL.replace("{id}", owner?.id.toString())
            val params = hashMapOf<String, Any>().apply { put("empty_product", 0) }
            layout.data = RelatedProductModel(ICViewTypes.OWNER_PRODUCT_TYPE, url, params, "Sản phẩm cùng doanh nghiệp sở hữu", listData)
            onAddLayout.value = layout
        } else if (!layout.request.url.isNullOrEmpty()) {
            onAddLayout.value = layout

            productRepository.getListTrend(layout.request.url!!, object : ICNewApiListener<ICResponse<ICListResponse<ICProductTrend>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICProductTrend>>) {
                    if (!obj.data?.rows.isNullOrEmpty()) {
                        val url = APIConstants.socialHost + "social/api/products/$productID/owner"
                        val params = hashMapOf<String, Any>().apply {
                            put("empty_product", 0)
                        }
                        layout.data = RelatedProductModel(ICViewTypes.OWNER_PRODUCT_TYPE, url, params, "Sản phẩm cùng doanh nghiệp sở hữu", obj.data?.rows!!)
                        onUpdateLayout.value = layout
                    } else {
                        checkTotalError(layout)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    checkTotalError(layout)
                }
            })
        }
    }

    private fun getListRelatedProduct(data: JsonObject?, layout: ICLayout) {
        layout.viewType = ICViewTypes.RELATED_PRODUCT_TYPE
        val listData = layoutHelper.getListProductTrend(data, layout.key)

        if (!listData.isNullOrEmpty()) {
            val url = APIConstants.socialHost + APIConstants.Product.GET_RELATED_PRODUCT_SOCIAL.replace("{id}", productID.toString())
            val params = hashMapOf<String, Any>().apply { put("empty_product", 0) }
            layout.data = RelatedProductModel(ICViewTypes.RELATED_PRODUCT_TYPE, url, params, "Sản phẩm liên quan", listData)
            onAddLayout.value = layout
        } else if (!layout.request.url.isNullOrEmpty()) {
            onAddLayout.value = layout

            productRepository.getListTrend(layout.request.url!!, object : ICNewApiListener<ICResponse<ICListResponse<ICProductTrend>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICProductTrend>>) {
                    if (!obj.data?.rows.isNullOrEmpty()) {
                        val url = APIConstants.socialHost + APIConstants.Product.GET_RELATED_PRODUCT_SOCIAL.replace("{id}", productID.toString())
                        val params = hashMapOf<String, Any>().apply { put("empty_product", 0) }
                        layout.data = RelatedProductModel(ICViewTypes.RELATED_PRODUCT_TYPE, url, params, "Sản phẩm liên quan", obj.data?.rows!!)
                        onUpdateLayout.value = layout
                    } else {
                        checkTotalError(layout)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    checkTotalError(layout)
                }
            })
        }
    }

    fun getOrUpdateAds(layout: ICLayout? = null) {
        if (Constant.getlistAdsNew().isEmpty() && layout != null) {
            adsRepository.dispose()
            adsRepository.getAds(object : ICNewApiListener<ICResponse<ICListResponse<ICAdsNew>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICAdsNew>>) {
                    if (!obj.data?.rows.isNullOrEmpty()) {
                        for (i in obj.data!!.rows.size - 1 downTo 0) {
                            if (obj.data!!.rows[i].data.isNullOrEmpty()) {
                                obj.data!!.rows.removeAt(i)
                            }
                        }
                        Constant.getlistAdsNew().clear()
                        Constant.setListAdsNew(obj.data!!.rows)

                        onUpdateAds.value = true
                    } else {
                        checkTotalError(layout)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    checkTotalError(layout)
                }
            })
        } else {
            onUpdateAds.value = true
        }
    }

    private fun getContact(layout: ICLayout) {
        layout.viewType = ICViewTypes.TYPE_BOTTOM
        onAddLayout.value = layout

        settingInteraction.getSystemSetting(null, "product-detail", object : ICNewApiListener<ICResponse<ICListResponse<ICClientSetting>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICClientSetting>>) {
                if (!obj.data?.rows.isNullOrEmpty() && !verifyProduct) {
                    for (i in obj.data!!.rows.size - 1 downTo 0) {
                        if (obj.data!!.rows[i].key == "product-detail.url-mua-hang") {
                            urlBuy = obj.data!!.rows[i].value
                            obj.data!!.rows.removeAt(i)
                        }
                    }

                    if (obj.data!!.rows.isNotEmpty()) {
                        layout.data = BottomModel(obj.data!!.rows)
                        onUpdateLayout.value = layout
                    } else {
                        checkTotalError(layout)
                    }
                } else {
                    checkTotalError(layout)
                }
            }

            override fun onError(error: ICResponseCode?) {
                checkTotalError(layout)
            }
        })
    }

    private fun getContributionInfo(data: JsonObject?, layout: ICLayout) {
        listInfo.clear()
        listInfo.addAll(layoutHelper.getContributionInfoList(data, layout.key) ?: arrayListOf())
        layoutHelper.getContributionInfoList(data, layout.key)

        if (!listInfo.isNullOrEmpty()) {
            layout.viewType = ICViewTypes.MY_CONTRIBUTION
            layout.data = MyContributionViewModel(arrayListOf<CategoryAttributesItem>().apply {
                addAll(listInfo)
            })
            onAddLayout.value = layout
        }
    }

    private fun getProductsECommerce(layout: ICLayout) {
        layout.viewType = ICViewTypes.PRODUCT_ECCOMMERCE_TYPE
        onAddLayout.value = layout

        productRepository.getProductsECommerce(layout.request.url, productID, object : ICNewApiListener<ICResponse<ICListResponse<ICProductECommerce>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICProductECommerce>>) {
                if (!obj.data?.rows.isNullOrEmpty()) {
                    layout.data = obj.data!!.rows
                    layout.key = barcode
                }

                onUpdateLayout.value
            }

            override fun onError(error: ICResponseCode?) {
                checkTotalError(layout)
            }
        })

//        viewModelScope.launch {
//            val productsECommerce = try {
//                    productRepository.getProductsECommerce(layout.request.url, productID)
//                } catch (e: Exception) {
//                    null
//                }
//
//            if (!productsECommerce?.data?.rows.isNullOrEmpty()) {
//                layout.data = productsECommerce!!.data!!.rows
//                layout.key = barcode
//            }
//
//            onUpdateLayout.value
//        }
    }

    @Synchronized
    private fun checkTotalError(layout: ICLayout) {
        onUpdateLayout.value = layout
        totalError++

        if (totalError == totalRequest) {
            errorRequest.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai)))
        }
    }

    fun postTransparency(yesOrno: Boolean, productId: Long) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        productRepository.postTransparency(yesOrno, productId, object : ICNewApiListener<ICResponse<ICTransparency>> {
            override fun onSuccess(obj: ICResponse<ICTransparency>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.data != null) {
                    onPostTransparency.postValue(obj.data)
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorMessage.postValue(error?.message
                        ?: ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun updateData() {
        Handler().postDelayed({
            getProductLayout(true)
        }, 1000)
    }

    fun getProductShareLink(id: Long) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            errorMessage.postValue(ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        productRepository.getProductShareLink(id, object : ICNewApiListener<ICResponse<String>> {
            override fun onSuccess(obj: ICResponse<String>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (!obj.data.isNullOrEmpty()) {
                    onShareLinkProduct.postValue(obj.data)
                } else {
                    errorMessage.postValue(ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorMessage.postValue(error?.message
                        ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun getProductShareLink(objPost: ICPost? = null) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            errorMessage.postValue(ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        productRepository.getProductShareLink(productID, object : ICNewApiListener<ICResponse<String>> {
            override fun onSuccess(obj: ICResponse<String>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                obj.data?.let { response ->
                    if (objPost != null) {
                        objPost.link = response
                        onShareLink.postValue(objPost)
                    } else {
                        onShareLink.postValue(response)
                    }
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorMessage.postValue(error?.message
                        ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun getPostDetail(postId: Long) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            return
        }

        postInteractor.getPostDetail(postId, object : ICNewApiListener<ICResponse<ICPost>> {
            override fun onSuccess(obj: ICResponse<ICPost>) {
                onDetailPost.postValue(obj.data)
            }

            override fun onError(error: ICResponseCode?) {

            }
        })
    }

    fun reloadMyReview(url: String? = urlMyReview) {
        if (!url.isNullOrEmpty()) {
            val pageId = if (SettingManager.getPostPermission() != null) {
                if (SettingManager.getPostPermission()!!.type == Constant.PAGE || SettingManager.getPostPermission()!!.type == Constant.ENTERPRISE) {
                    SettingManager.getPostPermission()!!.id
                } else {
                    null
                }
            } else {
                null
            }

            productRepository.getMyReview(url, pageId, object : ICNewApiListener<ICResponse<ICProductMyReview>> {
                override fun onSuccess(obj: ICResponse<ICProductMyReview>) {
                    if (obj.data != null) {
                        onMyReviewData.postValue(obj.data)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                }
            })
        }
    }


    fun registerBuyProduct() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            errorMessage.postValue(ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        productRepository.registerBuyProduct(productID, null, object : ICNewApiListener<ICResponse<JsonObject>> {
            override fun onSuccess(obj: ICResponse<JsonObject>) {
                if (obj.data != null) {
                    onRegisterBuyProduct.postValue(true)
                }
            }

            override fun onError(error: ICResponseCode?) {
                errorMessage.postValue(if (error?.message.isNullOrEmpty()) {
                    ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                } else {
                    error?.message
                })
            }
        })

    }

    fun dispose() {
        productRepository.dispose()
        reviewInteraction.dispose()
        settingInteraction.dispose()
        adsRepository.dispose()
    }
}
