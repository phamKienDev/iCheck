package vn.icheck.android.screen.user.page_details

import android.os.Bundle
import android.os.Handler
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.gson.JsonObject
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.image_video_slider.ICImageVideoSliderModel
import vn.icheck.android.component.image_video_slider.ICMediaType
import vn.icheck.android.component.image_video_slider.MediaLogic
import vn.icheck.android.component.product.related_product.RelatedProductModel
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.ads.AdsRepository
import vn.icheck.android.network.feature.campaign.CampainsInteractor
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.feature.post.PostInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.network.models.upload.UploadResponse
import vn.icheck.android.screen.user.home_page.model.ICListHomeItem
import vn.icheck.android.util.ick.logError
import java.io.File
import kotlin.collections.set

open class PageDetailViewModel : ViewModel() {
    val pageInteraction = PageRepository()
    val campaignInteraction = CampainsInteractor()
    val adsRepository = AdsRepository()
    val postInteractor = PostInteractor()
    val layoutHelper = LayoutHelper()
    val cartHelper = CartHelper()

    var listReportForm = MutableLiveData<MutableList<ICReportForm>>()
    val listReportSuccess = MutableLiveData<MutableList<ICReportForm>>()

    val onClearData = MutableLiveData<String>()
    val onAddData = MutableLiveData<ICLayout>()
    val onUpdateData = MutableLiveData<ICLayout>()
    val onUpdateListData = MutableLiveData<ICListHomeItem>()
    val onUpdateAds = MutableLiveData<Boolean>()
    val onUpdatePageSuccess = MutableLiveData<String>()
    val onUpdatePageError = MutableLiveData<Int>()
    val onPageName = MutableLiveData<String>()
    val onDetailPost = MutableLiveData<ICPost>()
    val onDeletePost = MutableLiveData<Long>()

    val onUpdatePost = MutableLiveData<MutableList<ICLayout>>()
    var offsetPost = 0
    var pathGetPost = ""

    val onError = MutableLiveData<ICError>()
    val onShowMessage = MutableLiveData<String>()
    val onUpdateState = MutableLiveData<ICMessageEvent.Type>()

    private var totalRequest = 0
    private var errorRequest = 0
    var pageID = -1L
    var pageType = Constant.PAGE_BRAND_TYPE

    var pageDetail: ICPageDetail? = null
    var pageOverview: ICPageOverview? = null


    val onErrorProduct = MutableLiveData<ICError>()
    val addHorizontal = MutableLiveData<RelatedProductModel>()
    val addVertical = MutableLiveData<MutableList<RelatedProductModel>>()
    var countError = 0

    var isFollowPage = false


    fun getData(bundle: Bundle?) {
        pageID = bundle?.getLong(Constant.DATA_1, -1) ?: -1
        pageType = bundle?.getInt(Constant.DATA_2, Constant.PAGE_BRAND_TYPE)
                ?: Constant.PAGE_BRAND_TYPE

        if (pageID == -1L) {
            onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai)))
        } else {
            getLayoutPage()
        }
    }

    fun getLayoutPage() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        pageInteraction.dispose()
        campaignInteraction.dispose()
        onClearData.postValue("")

        pageInteraction.getLayoutPage(pageID, Constant.getPageType(pageType), object : ICNewApiListener<ICLayoutData<JsonObject>> {
            override fun onSuccess(obj: ICLayoutData<JsonObject>) {
                obj.data?.get("name")?.asString?.let { name ->
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UPDATE_PAGE_NAME, name))
                    onPageName.postValue(name)
                }

                for (layout in obj.layout ?: mutableListOf()) {
                    when (layout.id.toString()) {
                        "page-attachments-1" -> {
                            try {
                                val listAttachments = mutableListOf<MediaLogic>()
                                val attachments: List<ICMedia>? = layoutHelper.getListMedia(obj.data, layout.key)
                                if (!attachments.isNullOrEmpty()) {

                                    for (att in attachments) {
                                        if (att.content.isNullOrEmpty()) {
                                            listAttachments.add(MediaLogic(null, ICMediaType.TYPE_IMAGE).apply {
                                                bgDefault = getDrawable()
                                            })
                                        } else {
                                            if (att.type == "image") {
                                                listAttachments.add(MediaLogic(att.content, ICMediaType.TYPE_IMAGE))
                                            } else {
                                                listAttachments.add(MediaLogic(att.content, ICMediaType.TYPE_VIDEO).also {
                                                    it.exoPlayer = SimpleExoPlayer.Builder(ICheckApplication.getInstance()).build()
                                                    it.setMs(DefaultDataSourceFactory(ICheckApplication.getInstance(), Util.getUserAgent(ICheckApplication.getInstance().applicationContext, "icheck")))
                                                })
                                            }
                                        }
                                    }
                                } else {
                                    listAttachments.add(MediaLogic(null, ICMediaType.TYPE_IMAGE).apply {
                                        bgDefault = getDrawable()
                                    })
                                }
                                layout.viewType = ICViewTypes.IMAGE_VIDEO_SLIDER
                                layout.data = ICImageVideoSliderModel(listAttachments, viewModelScope).apply {
                                    setPageId(pageID)
                                }
                                onAddData.value = layout
                            } catch (e: Exception) {
                                logError(e)
                            }
                        }
                        "page-overview-1" -> {
                            val sub = if (obj.data?.has("unsubscribeNotice") == true) {
                                obj.data!!.get("unsubscribeNotice").asBoolean
                            } else {
                                false
                            }

                            layoutHelper.getObject(obj.data, layout.key, ICPageOverview::class.java)?.let { pageOverview ->
                                pageOverview.isFollow = isFollowPage
                                pageOverview.unsubscribeNotice = sub
                                layout.viewType = ICViewTypes.HEADER_INFOR_PAGE
                                layoutHelper.getButtonOfPage(obj.data)?.let { buttons ->
                                    pageOverview.buttonConfigs?.clear()
                                    if (pageOverview.buttonConfigs.isNullOrEmpty())
                                        pageOverview.buttonConfigs = arrayListOf()
                                    pageOverview.buttonConfigs?.addAll(buttons)
                                }
                                layoutHelper.getObject(obj.data, "pageDetail", ICPageDetail::class.java)?.let { pageDetail ->
                                    pageOverview.pageDetail = pageDetail
                                }
                                layout.data = pageOverview
                                this@PageDetailViewModel.pageOverview = pageOverview
                                onAddData.value = layout
                            }
                        }
                        "page-follow-invitation-1" -> {
                            layoutHelper.getVerify(obj.data, "isIgnoreInvite")?.let { isIgnoreInvite ->
                                if (!isIgnoreInvite) {
                                    layoutHelper.getObject(obj.data, "pageOverview", ICPageOverview::class.java)?.let { pageOverview ->
                                        layout.viewType = ICViewTypes.INVITE_FOLLOW_TYPE
                                        this@PageDetailViewModel.pageOverview?.isIgnoreInvite = isIgnoreInvite
                                        onAddData.value = layout

                                        //đợi kết quả firebase
                                        Handler().postDelayed({
                                            layout.data = this@PageDetailViewModel.pageOverview
                                            onUpdateData.value = layout
                                        }, 500)
                                    }
                                }
                            }
                        }
                        "page-detail-1" -> {
                            layoutHelper.getObject(obj.data, layout.key, ICPageDetail::class.java)?.let { pageDetail ->
                                layout.viewType = ICViewTypes.WIDGET_DETAIL
                                pageDetail.icPageOverView = this@PageDetailViewModel.pageOverview
                                layout.data = pageDetail
                                this@PageDetailViewModel.pageDetail = pageDetail
                                onAddData.value = layout
                            }
                        }
                        "brand-1" -> {
                            try {
                                val listBrands = layoutHelper.getListPageTrends(obj.data, layout.key)
                                layout.viewType = ICViewTypes.WIDGET_BRAND
                                if (!listBrands.isNullOrEmpty()) {
                                    layout.data = listBrands
                                    onAddData.value = layout
                                } else if (!layout.request.url.isNullOrEmpty()) {
                                    onAddData.value = layout
                                    getWidgetBrand(layout)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        "page-camp-1" -> {
                            try {
                                val listCamps = layoutHelper.getListCampaigns(obj.data, layout.key)
                                layout.viewType = ICViewTypes.CAMPAIGNS
                                if (!listCamps.isNullOrEmpty()) {
                                    layout.data = listCamps
                                    onAddData.value = layout
                                } else if (!layout.request.url.isNullOrEmpty()) {
                                    onAddData.value = layout
                                    getWidgetCampaigns(layout)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        "page-image-assets-1" -> {
                            try {
                                val listMedia = layoutHelper.getListMediaPage(obj.data, layout.key)
                                layout.viewType = ICViewTypes.IMAGE_ASSETS_PAGE
                                if (!listMedia.isNullOrEmpty()) {
                                    layout.data = ICImageAsset(obj.data?.get("name")?.asString, listMedia, obj.data?.get("id")?.asLong)
                                    onAddData.value = layout
                                } else if (!layout.request.url.isNullOrEmpty()) {
                                    onAddData.value = layout
                                    getImageAssetsPage(layout, pageID, obj.data?.get("name")?.asString)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        "page-relate-1" -> {
                            try {
                                val listRelatedPage = layoutHelper.getListRelatedPage(obj.data, layout.key)
                                layout.viewType = ICViewTypes.RELATED_PAGE_TYPE
                                if (!listRelatedPage.isNullOrEmpty()) {
                                    layout.data = listRelatedPage
                                    onAddData.value = layout
                                } else if (!layout.request.url.isNullOrEmpty()) {
                                    onAddData.value = layout
                                    getRelatedPage(layout)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        "page-highlight-product-1" -> {
                            try {
                                val listProduct = layoutHelper.getListProductTrend(obj.data, layout.key)
                                layout.viewType = ICViewTypes.HIGLIGHT_PRODUCTS_PAGE
                                if (!listProduct.isNullOrEmpty()) {
                                    val url = APIConstants.socialHost + APIConstants.Social.GET_OWNER_PRODUCT_SOCIAL.replace("{ownerId}", pageID.toString())
                                    val params = hashMapOf<String, Any>()
                                    params["page_id"] = pageID
                                    params["empty_image"] = 0

                                    layout.data = RelatedProductModel(ICViewTypes.HIGLIGHT_PRODUCTS_PAGE, url, params, ICheckApplication.getString(R.string.san_pham_tieu_bieu), listProduct)
                                    onAddData.value = layout
                                } else if (!layout.request.url.isNullOrEmpty()) {
                                    onAddData.value = layout
                                    getHighLightProducts(layout, pageID)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        "page-category-product-1" -> {
                            try {
                                val listCategory = layoutHelper.getListCategoriesProduct(obj.data, layout.key)
                                layout.viewType = ICViewTypes.CATEGORIES_PRODUCTS_PAGE
                                if (!listCategory.isNullOrEmpty()) {
                                    val url = APIConstants.socialHost + APIConstants.Product.GET_RELATED_PRODUCT_SOCIAL.replace("{id}", pageID.toString())
                                    val params = hashMapOf<String, Any>()
                                    params["empty_product"] = 0

                                    for (item in listCategory) {
                                        layout.data = RelatedProductModel(ICViewTypes.CATEGORIES_PRODUCTS_PAGE, url, params, item.name, item.products.toMutableList())
                                        onAddData.value = layout
                                    }
                                } else if (!layout.request.url.isNullOrEmpty()) {
                                    onAddData.value = layout
                                    getCategoriesProduct(layout, pageID)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        "ads-1" -> {
                            onAddData.value = layout.apply {
                                viewType = ICViewTypes.ADS_TYPE
                                subType = ICViewTypes.ADS_TYPE
                            }
                            getOrUpdateAds(layout)
                        }
                        "page-post-list-1" -> {
                            try {
                                if (!layout.request.url.isNullOrEmpty()) {
                                    layout.viewType = ICViewTypes.LIST_POST_TYPE
                                    onAddData.value = layout
                                    pathGetPost = layout.request.url!!
                                    getListPosts()
                                }
                            } catch (e: Exception) {
                            }
                        }
                    }
                }

                totalRequest = obj.layout?.size ?: 0
                errorRequest = 0
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai)))
            }
        })
    }
    @DrawableRes
    fun getDrawable(): Int {
        return when ((0..3).random()) {
            0 -> R.drawable.page_cover_1
            1 -> R.drawable.page_cover_3
            2 -> R.drawable.page_cover_2
            else -> R.drawable.page_cover_4
        }
    }
    private fun getWidgetBrand(layout: ICLayout) {
        pageInteraction.getBrands(layout.request.url!!, 0, object : ICNewApiListener<ICResponse<ICListResponse<ICPageTrend>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICPageTrend>>) {
                finishRequest(true)
                if (!obj.data?.rows.isNullOrEmpty()) {
                    layout.data = obj.data?.rows
                }
                onUpdateData.value = layout
            }

            override fun onError(error: ICResponseCode?) {
                finishRequest(false)
                onUpdateData.value = layout
            }
        })
    }

    private fun getWidgetCampaigns(layout: ICLayout) {
        campaignInteraction.getListCampaign(layout.request.url!!, object : ICNewApiListener<ICResponse<ICListResponse<ICCampaign>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCampaign>>) {
                finishRequest(true)
                if (!obj.data?.rows.isNullOrEmpty()) {
                    layout.data = obj.data?.rows
                }
                onUpdateData.value = layout
            }

            override fun onError(error: ICResponseCode?) {
                finishRequest(false)
                onUpdateData.value = layout
            }
        })
    }

    private fun getImageAssetsPage(layout: ICLayout, pageID: Long?, pageName: String?) {
        pageInteraction.getImageAssetsPage(layout.request.url!!, 0, object : ICNewApiListener<ICResponse<ICListResponse<ICMediaPage>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICMediaPage>>) {
                finishRequest(true)
                if (!obj.data?.rows.isNullOrEmpty()) {
                    layout.data = ICImageAsset(pageName, obj.data?.rows, pageID)
                }
                onUpdateData.value = layout
            }

            override fun onError(error: ICResponseCode?) {
                finishRequest(false)
                onUpdateData.value = layout
            }
        })
    }

    private fun getRelatedPage(layout: ICLayout) {
        pageInteraction.getRelatedPage(layout.request.url!!, object : ICNewApiListener<ICResponse<ICListResponse<ICRelatedPage>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICRelatedPage>>) {
                finishRequest(true)
                if (!obj.data?.rows.isNullOrEmpty()) {
                    RelationshipHelper.isFollowPage(obj.data?.rows!!)
                    layout.data = obj.data?.rows
                }
                onUpdateData.value = layout
            }

            override fun onError(error: ICResponseCode?) {
                finishRequest(false)
                onUpdateData.value = layout
            }
        })
    }

    private fun getHighLightProducts(layout: ICLayout, pageID: Long?) {
        pageInteraction.getHighlightProducts(layout.request.url!!, object : ICNewApiListener<ICResponse<ICListResponse<ICProductTrend>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICProductTrend>>) {
                finishRequest(true)

                val url = APIConstants.socialHost + APIConstants.Social.GET_OWNER_PRODUCT_SOCIAL.replace("{ownerId}", pageID.toString())
                val params = hashMapOf<String, Any>()
                if (pageID != null)
                    params["page_id"] = pageID
                params["empty_image"] = 0

                if (!obj.data!!.rows.isNullOrEmpty()) {
                    layout.data = RelatedProductModel(ICViewTypes.HIGLIGHT_PRODUCTS_PAGE, url, params, ICheckApplication.getString(R.string.san_pham_tieu_bieu), obj.data!!.rows)
                }
                onUpdateData.value = layout
            }

            override fun onError(error: ICResponseCode?) {
                finishRequest(false)
                onUpdateData.value = layout
            }
        })
    }

    private fun getCategoriesProduct(layout: ICLayout, pageID: Long) {
        pageInteraction.getCategoriesProduct(layout.request.url!!, object : ICNewApiListener<ICResponse<ICListResponse<ICCategoriesProduct>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCategoriesProduct>>) {
                finishRequest(true)

                val url = APIConstants.socialHost + APIConstants.Product.GET_RELATED_PRODUCT_SOCIAL.replace("{id}", pageID.toString())
                val params = hashMapOf<String, Any>()
                params["empty_product"] = 0

                val icHome = ICListHomeItem(layout.id.toString())
                if (!obj.data?.rows.isNullOrEmpty()) {
                    for (item in obj.data!!.rows) {
                        icHome.listLayout.add((ICLayout(layout.id, layout.key, layout.request, layout.custom, null, layout.viewType).apply {
                            data = RelatedProductModel(ICViewTypes.CATEGORIES_PRODUCTS_PAGE, url, params, item.name, item.products.toMutableList())
                        }))
                    }
                }

                onUpdateListData.value = icHome
            }

            override fun onError(error: ICResponseCode?) {
                finishRequest(false)
                onUpdateData.value = layout
            }
        })
    }

    fun getOrUpdateAds(layout: ICLayout? = null) {
        if (Constant.getlistAdsNew().isEmpty() && layout != null) {
            adsRepository.dispose()
            adsRepository.getAds(object : ICNewApiListener<ICResponse<ICListResponse<ICAdsNew>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICAdsNew>>) {
                    finishRequest(true)

                    if (!obj.data?.rows.isNullOrEmpty()) {
                        for (i in obj.data!!.rows.size - 1 downTo 0) {
                            if (obj.data!!.rows[i].data.isNullOrEmpty()) {
                                obj.data!!.rows.removeAt(i)
                            }
                        }
                        Constant.getlistAdsNew().clear()
                        Constant.getlistAdsNew().addAll(obj.data!!.rows)
                    }

                    onUpdateAds.postValue(true)
                }

                override fun onError(error: ICResponseCode?) {
                    finishRequest(false)
                    onUpdateAds.postValue(true)
                }
            })
        } else {
//            finishRequest(false)
            onUpdateAds.postValue(true)
        }
    }

    fun getListPosts() {
        pageInteraction.getListPostOfPage(pathGetPost, offsetPost, object : ICNewApiListener<ICResponse<ICListResponse<ICPost>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICPost>>) {

                val posts = mutableListOf<ICLayout>()
                for (i in obj.data?.rows ?: mutableListOf()) {
                    posts.add(ICLayout("", "", ICRequest(), null, null, ICViewTypes.LIST_POST_TYPE, i))
                }
                onUpdatePost.postValue(posts)
            }

            override fun onError(error: ICResponseCode?) {
                onUpdatePost.postValue(mutableListOf())
            }
        })
    }

    @Synchronized
    private fun finishRequest(isSuccess: Boolean) {
        totalRequest--

        if (!isSuccess) {
            errorRequest--
        }

        if (errorRequest == totalRequest) {
            onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai)))
        }
    }

    fun getListReportFormPage() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onShowMessage.postValue(ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        onUpdateState.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        pageInteraction.getListReportFormPage(object : ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICReportForm>>) {
                onUpdateState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (!obj.data?.rows.isNullOrEmpty()) {
                    listReportForm.postValue(obj.data?.rows)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onUpdateState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onShowMessage.postValue(error?.message
                        ?: ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun sendReportPage(listReason: MutableList<Int>, message: String, listMessage: MutableList<String>) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onShowMessage.postValue(ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        onUpdateState.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        pageInteraction.sendReportPage(pageID, listReason, message, object : ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICReportForm>>) {

                onUpdateState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.statusCode == "200") {
                    val listData = mutableListOf<ICReportForm>()
                    if (message.isNotEmpty()) {
                        listMessage.add(message)
                    }
                    if (!listMessage.isNullOrEmpty()) {
                        for (i in 0 until listMessage.size) {
                            listData.add(ICReportForm(null, listMessage[i]))
                        }
                    }
                    listReportSuccess.postValue(listData)
                } else {
                    onShowMessage.postValue(ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICResponseCode?) {
                onUpdateState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onShowMessage.postValue(error?.message
                        ?: ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun followPage(id: Long?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onShowMessage.postValue(ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (id != null) {
            pageInteraction.followPage(id, object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    pageOverview?.isFollow = true
                    isFollowPage = true
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.FOLLOW_PAGE, id))
                }

                override fun onError(error: ICResponseCode?) {
                    onShowMessage.postValue(error?.message
                            ?: ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            })
        }
    }

    fun unFollowPage(id: Long?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onShowMessage.postValue(ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))

            return
        }

        if (id != null) {
            pageInteraction.unFollowPage(id, object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    pageOverview?.isFollow = false
                    isFollowPage = false
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UNFOLLOW_PAGE, id))
                }

                override fun onError(error: ICResponseCode?) {
                    onShowMessage.postValue(error?.message
                            ?: ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            })
        }
    }

    fun unSubcribePage(id: Long) {
        pageInteraction.unSubcribePage(id, object : ICNewApiListener<ICResponse<Boolean>> {
            override fun onSuccess(obj: ICResponse<Boolean>) {
                if (obj.statusCode == "200") {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_SUBCRIBE_STATUS, false))
                }
            }

            override fun onError(error: ICResponseCode?) {
                onShowMessage.postValue(error?.message
                        ?: ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun reSubcribePage(id: Long) {
        pageInteraction.reSubcribePage(id, object : ICNewApiListener<ICResponse<Boolean>> {
            override fun onSuccess(obj: ICResponse<Boolean>) {
                if (obj.statusCode == "200") {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_SUBCRIBE_STATUS, true))
                }
            }

            override fun onError(error: ICResponseCode?) {
                onShowMessage.postValue(error?.message
                        ?: ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun updateCartCount() {
        cartHelper.updateCountCart()
    }

    fun uploadImage(key: Int, file: File) {
        if (NetworkHelper.isNotConnected(ICheckApplication.currentActivity())) {
            onShowMessage.postValue(ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        onUpdateState.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        ImageHelper.uploadMedia(file, object : ICApiListener<UploadResponse> {
            override fun onSuccess(obj: UploadResponse) {
                editImagePage(key, obj.src)
            }

            override fun onError(error: ICBaseResponse?) {
                onUpdateState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onShowMessage.postValue(error?.message
                        ?: ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    private fun editImagePage(key: Int, image: String) {
        val update = if (key == ICViewTypes.HEADER_INFOR_PAGE) {
            "avatar"
        } else {
            "cover"
        }

        pageInteraction.updatePage(pageID, update, image, object : ICNewApiListener<ICResponse<MutableList<Int>>> {
            override fun onSuccess(obj: ICResponse<MutableList<Int>>) {
                onUpdateState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onUpdatePageSuccess.postValue(image)
            }

            override fun onError(error: ICResponseCode?) {
                onUpdateState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onUpdatePageError.postValue(key)
            }
        })
    }

    fun getHigtlightProducts() {
        val url = APIConstants.socialHost + APIConstants.Page.GET_HIGHLIGHT_PRODUCTS.replace("{id}", pageID.toString())
        val params = hashMapOf<String, Any>()
        params["empty_image"] = 0

        pageInteraction.getHighlightProducts(pageID, object : ICNewApiListener<ICResponse<ICListResponse<ICProductTrend>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICProductTrend>>) {
                if (!obj.data!!.rows.isNullOrEmpty()) {
                    addHorizontal.postValue(RelatedProductModel(ICViewTypes.HIGLIGHT_PRODUCTS_PAGE, url, params, ICheckApplication.getString(R.string.san_pham_noi_bat), obj.data!!.rows))
                } else {
                    checkError()
                }
            }

            override fun onError(error: ICResponseCode?) {
                checkError()
            }
        })
    }

    fun getCategoriesProduct() {
        pageInteraction.getCategoriesProduct(pageID, object : ICNewApiListener<ICResponse<ICListResponse<ICCategoriesProduct>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCategoriesProduct>>) {
                if (!obj.data?.rows.isNullOrEmpty()) {
                    val listData = mutableListOf<RelatedProductModel>()

                    for (item in obj.data!!.rows) {
                        val url = APIConstants.socialHost + APIConstants.Page.GET_PRODUCTS_CATEGORY_PAGE.replace("{id}", pageID.toString())
                        val params = hashMapOf<String, Any>()
                        if (item.id != null) {
                            params["categoryId"] = item.id!!
                        }
                        listData.add(RelatedProductModel(ICViewTypes.CATEGORIES_PRODUCTS_PAGE, url, params, item.name, item.products.toMutableList()))
                    }
                    addVertical.postValue(listData)
                } else {
                    checkError()
                }
            }

            override fun onError(error: ICResponseCode?) {
                checkError()
            }
        })
    }

    fun deletePost(id: Long) {
        if (NetworkHelper.isNotConnected(ICheckApplication.currentActivity())) {
            onShowMessage.postValue(ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        onUpdateState.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        postInteractor.deletePost(id, object : ICNewApiListener<ICResponse<ICCommentPost>> {
            override fun onSuccess(obj: ICResponse<ICCommentPost>) {
                onUpdateState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onDeletePost.postValue(id)
            }

            override fun onError(error: ICResponseCode?) {
                onUpdateState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onShowMessage.postValue(ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun checkError() {
        countError++
        if (countError == 2) {
            onErrorProduct.postValue(ICError(R.drawable.ic_group_120dp, ICheckApplication.getInstance().getString(R.string.trang_chua_co_san_pham_nao)))
        }
    }

    fun disposeApi() {
        pageInteraction.dispose()
        campaignInteraction.dispose()
        cartHelper.dispose()
    }
}