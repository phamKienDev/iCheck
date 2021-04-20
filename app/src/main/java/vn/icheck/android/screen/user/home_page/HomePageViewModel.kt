package vn.icheck.android.screen.user.home_page

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.experience_new_products.ICExperienceNewProducts
import vn.icheck.android.component.product_for_you.ICProductForYouMedia
import vn.icheck.android.component.tendency.ICTopTrend
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.model.reminders.ReminderResponse
import vn.icheck.android.network.api.ICKApi
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.ads.AdsRepository
import vn.icheck.android.network.feature.campaign.CampainsInteractor
import vn.icheck.android.network.feature.mall.MallInteractor
import vn.icheck.android.network.feature.news.NewsInteractor
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.feature.pvcombank.PVcomBankRepository
import vn.icheck.android.network.feature.setting.SettingRepository
import vn.icheck.android.network.feature.utility.UtilityRepository
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.product_need_review.ICProductNeedReview
import vn.icheck.android.network.models.pvcombank.ICListCardPVBank
import vn.icheck.android.screen.user.home_page.model.ICListHomeItem
import vn.icheck.android.util.ick.logError
import kotlin.collections.set

class HomePageViewModel @ViewModelInject constructor(@Assisted val savedStateHandle: SavedStateHandle, val ickApi: ICKApi) : BaseViewModel() {
    private val productInteraction = ProductInteractor()
    private val campaignInteraction = CampainsInteractor()
    private val newsInteraction = NewsInteractor()
    private val mallInteractor = MallInteractor()
    private val adsInteractor = AdsRepository()
    private val functionInteractor = UtilityRepository()
    private val settingInteraction = SettingRepository()
    private val pvcombankRepository = PVcomBankRepository()

    private val cartHelper = CartHelper()

    val onShowPopup = MutableLiveData<ICAds>()
    val onError = MutableLiveData<ICError>()

    val onAddData = MutableLiveData<ICLayout>()
    val onUpdateData = MutableLiveData<ICLayout>()
    val onUpdateListData = MutableLiveData<ICListHomeItem>()
    val onUpdatePVCombank = MutableLiveData<ICListCardPVBank?>()
    val onUpdateAds = MutableLiveData<Boolean>()

    private var totalRequest = 0
    private var errorRequest = 0

    var cartCount = 0

    private var _reminder: ICResponse<ICListResponse<ReminderResponse>>? = null

    fun getHomeLayout() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        adsInteractor.dispose()
        functionInteractor.dispose()
        productInteraction.dispose()
        campaignInteraction.dispose()
        newsInteraction.dispose()
        mallInteractor.dispose()

        totalRequest = 0
        errorRequest = 0

        adsInteractor.getLayout("home", object : ICNewApiListener<ICLayoutData<ICNone>> {
            override fun onSuccess(obj: ICLayoutData<ICNone>) {
                if (!obj.layout.isNullOrEmpty()) {
                    for (i in 0 until obj.layout?.size!!) {
                        val item = obj.layout!![i]

                        when (obj.layout!![i].id) {
                            "home-features-1" -> {
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    val primary = ICLayout(viewType = ICViewTypes.HOME_PRIMARY_FUNC, id = item.id, request = item.request, key = "", custom = item.custom)
                                    onAddData.value = primary
                                    getFunc(primary, item.request.url!!)
                                }
                            }
                            "ads-1" -> {
                                totalRequest++
                                onAddData.value = item.apply {
                                    subType = ICViewTypes.ADS_TYPE
                                }
                                getAds(false)
                            }
                            "news-1" -> {
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    onAddData.value = item.apply { viewType = ICViewTypes.LIST_NEWS_TYPE }
                                    getNews(item, item.request.url!!)
                                }
                            }
                            "ads-survey-1" -> {
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    onAddData.value = item.apply { viewType = ICViewTypes.ADS_DIRECT_SURVEY_TYPE }
                                    getAdsSurvey(item, item.request.url!!)
                                }
                            }
                            "ads-coll-1" -> {
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    onAddData.value = item.apply { viewType = ICViewTypes.COLLECTION_TYPE }
                                    getCollection(item, item.request.url!!)
                                }
                            }
                            "flash-sale-1" -> {
                                // flashSale
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    onAddData.value = item.apply { viewType = ICViewTypes.FLASH_SALE_TYPE }
                                    loadFlashSale(item, item.request.url!!)
                                }
                            }
                            "campaigns-1" -> {
                                // campaigns
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    onAddData.value = item.apply { viewType = ICViewTypes.CAMPAIGNS }
                                    getWidgetCampaigns(item, item.request.url!!)
                                }
                            }
                            "mall-1" -> {
                                // Danh Mục Mua Sắm
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    onAddData.value = item.apply { viewType = ICViewTypes.MALL_CATALOG }
                                    getMallCatalog(item, item.request.url!!)
                                }
                            }
                            "new-pro-1" -> {
                                // newProduct
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    onAddData.value = item.apply { viewType = ICViewTypes.EXPERIENCE_NEW_PRODUCT }
                                    getExperienceNewProducts(item, item.request.url!!)
                                }
                            }
                            "pro-1" -> {
                                // suggestionProduct
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    onAddData.value = item.apply { viewType = ICViewTypes.PRODUCT_FOR_YOU_TYPE }
                                    loadProductForYou(item, item.request.url!!)
                                }
                            }
                            "trend-1" -> {
                                // trendProducts
                                totalRequest++
                                val listCategory = mutableListOf<ICExperienceCategory>()
                                listCategory.add(ICExperienceCategory(0, "Sản phẩm", true))
                                listCategory.add(ICExperienceCategory(0, "Doanh nghiệp", false))
                                listCategory.add(ICExperienceCategory(0, "Chuyên gia", false))
                                onAddData.value = item.apply {
                                    viewType = ICViewTypes.TREND
                                    data = ICTopTrend(listCategory)
                                }
                                finishRequest(true)
                            }
                            "sugg-review-1" -> {
                                // Product Need Review
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    onAddData.value = item.apply { viewType = ICViewTypes.PRODUCT_NEED_REVIEW }
                                    getProductNeedReview(item, item.request.url!!)
                                }
                            }
                            "app-sugg-update-1" -> {
                                if (SettingManager.configUpdateApp?.isSuggested == true) {
                                    onAddData.value = item.apply {
                                        viewType = ICViewTypes.CHECK_UPDATE_TYPE
                                        data = true
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai), null, null))
            }
        })
    }

    @Synchronized
    private fun finishRequest(isSuccess: Boolean) {
        if (!isSuccess) {
            errorRequest++
        }

        if (errorRequest == totalRequest) {
            onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai), null, null))
        }
    }

    private fun getFunc(layout: ICLayout, url: String) {
        functionInteractor.getHomeFunc(url, object : ICNewApiListener<ICResponse<ICTheme>> {
            override fun onSuccess(obj: ICResponse<ICTheme>) {
                if (!obj.data?.secondary_functions.isNullOrEmpty()) {
                    layout.data = mutableListOf(obj.data)
                }
                onUpdateData.value = layout
                getPVCombank()
            }

            override fun onError(error: ICResponseCode?) {
                finishRequest(false)
                onUpdateData.value = layout
            }
        })
    }

    fun getPVCombank() {
//        var response: ICResponse<ICListResponse<ICListCardPVBank>>? = null
//
//        if (SessionManager.isUserLogged && SettingManager.getSessionPvcombank.isNotEmpty()) {
//            viewModelScope.launch {
//                response = try {
//                    withTimeoutOrNull(5000) { pvcombankRepository.getMyListCards() }
//                } catch (e: Exception) {
//                    null
//                }
//
//                if (response?.data?.rows?.firstOrNull() == null) {
//                    SettingManager.setSessionIdPvcombank("")
//                }
//
//                onUpdatePVCombank.value = response?.data?.rows?.firstOrNull()
//            }
//        } else {
//            onUpdatePVCombank.value = response?.data?.rows?.firstOrNull()
//        }
    }

    fun getAds(isOnResume: Boolean) {
        if (Constant.getlistAdsNew().isEmpty()) {
            adsInteractor.getAds(object : ICNewApiListener<ICResponse<ICListResponse<ICAdsNew>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICAdsNew>>) {
                    if (!isOnResume) finishRequest(true)

                    if (!obj.data?.rows.isNullOrEmpty()) {
                        for (i in obj.data!!.rows.size - 1 downTo 0) {
                            if (obj.data!!.rows[i].data.isNullOrEmpty()) {
                                obj.data!!.rows.removeAt(i)
                            }
                        }
                        Constant.getlistAdsNew().clear()
                        Constant.setListAdsNew(obj.data!!.rows)
                    }

                    onUpdateAds.value = true
                }

                override fun onError(error: ICResponseCode?) {
                    if (!isOnResume) finishRequest(false)
                    onUpdateAds.value = true
                }
            })
        } else {
            if (!isOnResume) finishRequest(false)
            onUpdateAds.value = true
        }
    }

    private fun getNews(layout: ICLayout, path: String) {
        newsInteraction.getListNewsSocial(path, object : ICNewApiListener<ICResponse<ICListResponse<ICNews>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICNews>>) {
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

    private fun getAdsSurvey(layout: ICLayout, path: String) {
        adsInteractor.getSurvey(path, object : ICNewApiListener<ICResponse<ICListResponse<ICSurvey>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICSurvey>>) {
                finishRequest(true)

                val icHome = ICListHomeItem(layout.id.toString())
                if (!obj.data?.rows.isNullOrEmpty()) {
                    for (item in obj.data!!.rows) {
                        icHome.listLayout.add(ICLayout(layout.id, layout.key, layout.request, layout.custom, null, ICViewTypes.ADS_DIRECT_SURVEY_TYPE, item))
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

    private fun getCollection(layout: ICLayout, path: String) {
        adsInteractor.getCollection(path, object : ICNewApiListener<ICResponse<ICListResponse<ICCollection>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCollection>>) {
                finishRequest(true)

                val icHome = ICListHomeItem(layout.id.toString())
                if (!obj.data?.rows.isNullOrEmpty()) {
                    for (item in obj.data!!.rows) {
                        icHome.listLayout.add(ICLayout(layout.id, layout.key, layout.request, layout.custom, null, ICViewTypes.ADS_LIST_PRODUCT_VERTICAL, item))
                    }
                }

                onUpdateListData.value = icHome
            }

            override fun onError(error: ICResponseCode?) {
                finishRequest(false)
                // Truyền data = null để xóa item null trong listData ở adapter
                onUpdateData.value = layout
            }
        })
    }

    private fun getProductNeedReview(layout: ICLayout, path: String) {
        productInteraction.getProductNeedReview(path, object : ICNewApiListener<ICResponse<ICListResponse<ICProductNeedReview>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICProductNeedReview>>) {
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

    private fun getWidgetCampaigns(layout: ICLayout, path: String) {
        campaignInteraction.getListCampaign(path, object : ICNewApiListener<ICResponse<ICListResponse<ICCampaign>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCampaign>>) {
                finishRequest(true)
                if (!obj.data?.rows.isNullOrEmpty()) {
                    layout.data = obj.data?.rows
                }
                onUpdateData.value = layout
            }

            override fun onError(error: ICResponseCode?) {
                finishRequest(false)
                onUpdateData.postValue(layout)
            }
        })
    }

    private fun getExperienceNewProducts(layout: ICLayout, path: String) {
        productInteraction.getExperienceCategory(path, object : ICNewApiListener<ICResponse<ICListResponse<ICExperienceCategory>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICExperienceCategory>>) {
                finishRequest(true)
                if (!obj.data?.rows.isNullOrEmpty()) {
                    obj.data?.rows!![0].isSelected = true
                    Observable.fromIterable(obj.data?.rows).flatMap {
                        return@flatMap productInteraction.getCategoryProducts("$path\\${it.id}\\products")
                    }.toList().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe({
                        val arr = mutableListOf<ICProduct>()
                        for (item in it) {
                            arr.addAll(item.data?.rows ?: arrayListOf())
                        }
                        layout.data = ICExperienceNewProducts(obj.data?.rows, arr)
                        onUpdateData.postValue(layout)
                    }, {

                    })

//                    layout.data = ICExperienceNewProducts(obj.data?.rows)
                }
//                onUpdateData.postValue(layout)
            }

            override fun onError(error: ICResponseCode?) {
                finishRequest(false)
                onUpdateData.postValue(layout)
            }
        })
    }


    private fun loadProductForYou(layout: ICLayout, path: String) {
        productInteraction.getProductForYou(path, object : ICNewApiListener<ICResponse<ICListResponse<ICProductForYou>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICProductForYou>>) {
                finishRequest(true)

                val listData = mutableListOf<ICProductForYouMedia>()
                for (i in obj.data!!.rows.indices) {
                    listData.add(ICProductForYouMedia(obj.data!!.rows[i]).also {
                        it.checkTypeMedia(ICheckApplication.getInstance().applicationContext)
                    })
                }

                if (listData.isNotEmpty()) {
                    layout.data = listData
                }

                onUpdateData.value = layout
            }

            override fun onError(error: ICResponseCode?) {
                finishRequest(false)
                onUpdateData.value = layout
            }
        })
    }

    private fun loadFlashSale(layout: ICLayout, path: String) {
        productInteraction.getFlashSale(path, object : ICNewApiListener<ICResponse<ICFlashSale>> {
            override fun onSuccess(obj: ICResponse<ICFlashSale>) {
                finishRequest(true)
                layout.data = obj.data
                onUpdateData.value = layout
            }

            override fun onError(error: ICResponseCode?) {
                finishRequest(false)
                onUpdateData.value = layout
            }
        })
    }

    private fun getMallCatalog(layout: ICLayout, path: String) {
        mallInteractor.getMallCatalog(path, object : ICNewApiListener<ICResponse<ICListResponse<ICShoppingCatalog>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICShoppingCatalog>>) {
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

    fun getHomePopup() {
        AdsRepository().getAds("home_popup", null, 1, null, object : ICApiListener<ICListResponse<ICAds>> {
            override fun onSuccess(obj: ICListResponse<ICAds>) {
                if (obj.rows.isNotEmpty()) {
                    onShowPopup.postValue(obj.rows[0])
                }
            }

            override fun onError(error: ICBaseResponse?) {
            }
        })
    }

    fun getCoin(): LiveData<Result<ICResponse<ICSummary>?>> {
        settingInteraction.dispose()
        return request { settingInteraction.getCoin() }
    }

//    fun getCoin() {
//        // Lấy về Coin hiện tại
//        settingInteraction.dispose()
//        settingInteraction.getCoinOfMe(object : ICNewApiListener<ICResponse<ICSummary>> {
//            override fun onSuccess(obj: ICResponse<ICSummary>) {
//                SessionManager.setCoin(obj.data?.availableBalance ?: 0)
//                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COIN_AND_RANK))
//            }
//
//            override fun onError(error: ICResponseCode?) {
//            }
//        })
//
//        updateCountCart()
//    }

    fun updateCountCart() {
        // Lấy về số lượng sản phẩm trong giỏ hàng
        if (SessionManager.isUserLogged) {
            cartHelper.updateCountCart()
        } else {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COUNT_CART, null))
        }
    }

    fun getCartCount(): LiveData<ICResponse<Int>> {
        return liveData {
            try {
                val res = ickApi.getCartCount()
                emit(res)
            } catch (e: Exception) {
                logError(e)
            }
        }
    }

    fun getListReminders(): List<ReminderResponse> {
        return _reminder?.data?.rows ?: arrayListOf()
    }

    private fun getReminderAt(pos: Int): ReminderResponse? {
        if (!_reminder?.data?.rows.isNullOrEmpty()) {
            if (pos < _reminder!!.data!!.rows.size) {
                return _reminder!!.data!!.rows[pos]
            }
        }

        return null
    }

    fun getReminders(): LiveData<ICResponse<ICListResponse<ReminderResponse>>?> {
        return liveData {
            try {
                _reminder = ickApi.getReminders()
            } catch (e: Exception) {
                logError(e)
            } finally {
                emit(_reminder)
            }
        }
    }

    fun getRemindersCount(): Int {
        return _reminder?.data?.rows?.size ?: 0
    }

    fun deleteReminder(pos: Int): LiveData<ICResponse<*>> {
        return liveData {
            val request = hashMapOf<String, Any?>()
            val reminder = getReminderAt(pos)
            request["type"] = reminder?.type
            val res = ickApi.deleteReminder(request)
            try {
                if (res.statusCode == "200") {
                    _reminder?.data?.rows?.removeAt(pos)
                }
            } catch (e: Exception) {

            } finally {
                emit(res)
            }

        }
    }

    fun disposeApi() {
        productInteraction.dispose()
        campaignInteraction.dispose()
        newsInteraction.dispose()
        mallInteractor.dispose()
        adsInteractor.dispose()
        settingInteraction.dispose()
        cartHelper.dispose()
        pvcombankRepository.dispose()
    }
}