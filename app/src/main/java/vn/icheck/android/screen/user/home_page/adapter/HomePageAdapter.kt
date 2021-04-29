package vn.icheck.android.screen.user.home_page.adapter

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.component.ads.campaign.AdsCampaignHolder
import vn.icheck.android.component.ads.news.AdsNewHolder
import vn.icheck.android.component.ads.page.AdsPageHolder
import vn.icheck.android.component.ads.product.AdsProductHolder
import vn.icheck.android.component.banner.SlideBannerV2Holder
import vn.icheck.android.component.collection.horizontal.CollectionHorizontalHolder
import vn.icheck.android.component.collection.survey.CollectionSurveyHolder
import vn.icheck.android.component.collection.survey.CollectionSurveySuccessHolder
import vn.icheck.android.component.collection.survey.ISurveyListener
import vn.icheck.android.component.collection.vertical.CollectionVerticalHolder
import vn.icheck.android.component.experience_new_products.ExperienceNewProductsViewHolder
import vn.icheck.android.component.experience_new_products.ICExperienceNewProducts
import vn.icheck.android.component.experience_new_products.TrendingProductHolder
import vn.icheck.android.component.flash_sale.FlashSaleViewHolder
import vn.icheck.android.component.news.NewsHolder
import vn.icheck.android.component.product_for_you.ICProductForYouMedia
import vn.icheck.android.component.product_for_you.ProductForYouViewHolder
import vn.icheck.android.component.product_need_review.ProductNeedReviewHolder
import vn.icheck.android.component.shopping_catalog.ShoppingCatalogHolder
import vn.icheck.android.component.tendency.ICTopTrend
import vn.icheck.android.component.tendency.TopTendencyViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemCheckUpdateBinding
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.helper.getAdsType
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.product_need_review.ICProductNeedReview
import vn.icheck.android.network.models.pvcombank.ICListCardPVBank
import vn.icheck.android.screen.user.campaign.calback.IBannerV2Listener
import vn.icheck.android.screen.user.campaign.calback.IMessageListener
import vn.icheck.android.screen.user.campaign.calback.IProductNeedReviewListener
import vn.icheck.android.screen.user.campaign.holder.base.ShortMessageHolder
import vn.icheck.android.screen.user.home_page.callback.IHomePageView
import vn.icheck.android.screen.user.home_page.holder.primaryfunction.HomeFunctionHolder
import vn.icheck.android.screen.user.home_page.model.ICListHomeItem
import vn.icheck.android.util.ick.openAppInGooglePlay

class HomePageAdapter(
        private val bannerV2Listener: IBannerV2Listener,
        private val messageListener: IMessageListener,
        private val productReviewListener: IProductNeedReviewListener,
        private val homePageListener: IHomePageView
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICLayout>()

    private var timeFlashSale: Long = 0L

    private var iconMessage = R.drawable.ic_error_request
    private var errorMessage: String? = null

    private val isExistTheme = SettingManager.themeSetting?.theme != null

    fun updatePVCombank(obj: ICListCardPVBank?) {
        for (i in listData.indices) {
            if (listData[i].viewType == ICViewTypes.HOME_PRIMARY_FUNC) {
                if (listData[i].data != null) {
                    (listData[i].data as MutableList<Any?>).apply {
                        if (size > 1) {
                            removeLast()
                        }
                        add(obj)
                        notifyDataSetChanged()
                    }
                }
                return
            }
        }
    }

    @Synchronized
    fun addItem(obj: ICLayout) {
        listData.add(obj)
        if (errorMessage == null) {
            notifyItemInserted(listData.size - 1)
        } else {
            errorMessage = null
            notifyDataSetChanged()
        }
    }

    fun selectCategory(id: Long) {
        val pos = listData.indexOfFirst {
            it.viewType == ICViewTypes.EXPERIENCE_NEW_PRODUCT
        }
        if (pos != -1) {
            val data = listData[pos].data
            if (data is ICExperienceNewProducts) {
                data.listCategory?.forEach {
                    it.isSelected = it.id == id
                }
            }
        }
    }

    @Synchronized
    fun addItem(list: MutableList<ICLayout>) {
        listData.addAll(list)
        if (errorMessage == null) {
            notifyItemRangeInserted(listData.size - list.size, listData.size - 1)
        } else {
            errorMessage = null
            notifyDataSetChanged()
        }
    }

    @Synchronized
    fun updateItem(obj: ICLayout) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].id == obj.id) {
                if (obj.data != null) {
                    listData[i].data = obj.data

                    if (listData[i].viewType == ICViewTypes.FLASH_SALE_TYPE) {
                        val endTime = TimeHelper.simpleDateFormatSv.parse((listData[i].data as ICFlashSale).endTime).time
                        val currentTime = System.currentTimeMillis()
                        timeFlashSale = if ((endTime - currentTime) > (24 * 60 * 60000)) {
                            24 * 60 * 60000
                        } else {
                            endTime - currentTime
                        }
                    }

                    notifyItemChanged(i)
                } else {
                    listData.removeAt(i)
                    notifyItemRemoved(i)
                }
                return
            }
        }
    }

    @Synchronized
    fun updateItem(obj: ICListHomeItem) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].id == obj.widgetID) {
                if (obj.listLayout.isNotEmpty()) {
                    for (j in obj.listLayout.size - 1 downTo 0) {
                        listData.add(i + 1, obj.listLayout[j])
                        notifyItemInserted(i + 1)
                    }
                } else {
                    listData.removeAt(i)
                    notifyItemRemoved(i)
                }
                return
            }
        }
    }

    fun removeAllView() {
        listData.clear()
        notifyDataSetChanged()
    }

    fun setError(icon: Int, error: String) {
        iconMessage = icon
        errorMessage = error

        listData.clear()
        notifyDataSetChanged()
    }

    fun updateAds() {
        val listAds = mutableListOf<ICAdsNew>()
        listAds.addAll(Constant.getlistAdsNew())
        listAds.shuffle()

        // check theo id truoc
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].subType == ICViewTypes.ADS_TYPE && !listData[i].entityIdList.isNullOrEmpty()) {
                for (j in listAds.size - 1 downTo 0) {
                    if (listData[i].entityIdList?.find { it == listAds[j].id } != null) {
                        if (listData[i].data == null) {
                            listData[i].data = listAds[j]
                            listData[i].viewType = listAds[j].objectType.getAdsType()
                            listAds.removeAt(j)
                            notifyItemChanged(i)
                        }
                    }
                }
            }
        }

        // random
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].subType == ICViewTypes.ADS_TYPE && listData[i].entityIdList.isNullOrEmpty()) {
                if (listAds.isNotEmpty()) {
                    listData[i].data = listAds[0]
                    listAds.removeAt(0)
                    notifyItemChanged(i)
                }
            }
        }
    }

    fun hideSurvey(surveyID: Long) {
        for (home in (listData.size - 1) downTo 0) {
            if (listData[home].viewType == ICViewTypes.ADS_SLIDE_BANNER_TYPE) {
                for (banner in (listData[home].data as MutableList<ICBanner>)) {
                    if (banner.id == surveyID) {
                        (listData[home].data as MutableList<ICBanner>).remove(banner)

                        if ((listData[home].data as MutableList<ICBanner>).size == 0) {
                            listData.removeAt(home)
                            notifyItemRemoved(home)
                        }
                    }
                }
            }
        }
    }

    fun removeReviewProduct(idReview: Long) {
        for (home in (listData.size - 1) downTo 0) {
            if (listData[home].viewType == ICViewTypes.PRODUCT_NEED_REVIEW) {
                val listItr = (listData[home].data as MutableList<ICProductNeedReview>).iterator()
                while (listItr.hasNext()) {
                    if (listItr.next().id == idReview) {
                        listItr.remove()
                        notifyItemChanged(home)
                    }
                }
                if ((listData[home].data as MutableList<ICProductNeedReview>).size == 0) {
                    listData.removeAt(home)
                    notifyItemRemoved(home)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size + if (errorMessage != null) {
            1
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < listData.size) {
            if (listData[position].data != null) {
                if (listData[position].viewType != ICViewTypes.ADS_DIRECT_SURVEY_TYPE) {
                    listData[position].viewType
                } else {
                    if (listData[position].data != null) {
                        ICViewTypes.ADS_DIRECT_SURVEY_TYPE
                    } else {
                        ICViewTypes.ADS_DIRECT_SURVEY_SUCCESS_TYPE
                    }
                }
            } else {
                super.getItemViewType(position)
            }
        } else {
            if (errorMessage.isNullOrEmpty()) {
                super.getItemViewType(position)
            } else {
                ICViewTypes.MESSAGE_TYPE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICViewTypes.HOME_PRIMARY_FUNC -> {
                HomeFunctionHolder(parent, isExistTheme, homePageListener)
            }
            ICViewTypes.CHECK_UPDATE_TYPE -> {
                CheckUpdateHolder(parent)
            }
            ICViewTypes.LIST_NEWS_TYPE -> {
                NewsHolder(parent)
            }
            ICViewTypes.ADS_BANNER -> {
                SlideBannerV2Holder(parent)
            }
            ICViewTypes.ADS_NEWS -> {
                AdsNewHolder(parent)
            }
            ICViewTypes.ADS_CAMPAIGN -> {
                AdsCampaignHolder(parent)
            }
            ICViewTypes.ADS_PRODUCT -> {
                AdsProductHolder(parent)
            }
            ICViewTypes.ADS_PAGE -> {
                AdsPageHolder(parent)
            }
            ICViewTypes.ADS_LIST_PRODUCT_HORIZONTAL -> {
                CollectionHorizontalHolder(parent)
            }
            ICViewTypes.ADS_LIST_PRODUCT_VERTICAL -> {
                CollectionVerticalHolder(parent)
            }
//            ICViewTypes.ADS_DIRECT_SURVEY_TYPE -> {
//                CollectionSurveyHolder(parent)
//            }
//            ICViewTypes.ADS_DIRECT_SURVEY_SUCCESS_TYPE -> {
//                CollectionSurveySuccessHolder(parent)
//            }
            ICViewTypes.EXPERIENCE_NEW_PRODUCT -> {
//                ExperienceNewProductsViewHolder(parent)
                TrendingProductHolder(parent)
            }
            ICViewTypes.MESSAGE_TYPE -> {
                ShortMessageHolder(parent)
            }
            ICViewTypes.PRODUCT_FOR_YOU_TYPE -> {
                ProductForYouViewHolder(parent)
            }
            ICViewTypes.FLASH_SALE_TYPE -> {
                FlashSaleViewHolder(parent)
            }
            ICViewTypes.PRODUCT_NEED_REVIEW -> {
                ProductNeedReviewHolder(parent, productReviewListener)
            }
            ICViewTypes.TREND -> {
                TopTendencyViewHolder(parent)
            }
            ICViewTypes.MALL_CATALOG -> {
                ShoppingCatalogHolder(parent)
            }
            else -> {
                NullHolder.create(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HomeFunctionHolder -> {
                holder.bind(listData[position].data as MutableList<Any?>)
            }
            is CheckUpdateHolder -> {
                holder.bind()
            }
            is SlideBannerV2Holder -> {
                holder.bind(listData[position].data as ICAdsNew, bannerV2Listener)
            }
            is NewsHolder -> {
                holder.bind(listData[position].data as MutableList<ICNews>)
            }
            is AdsNewHolder -> {
                holder.bind(listData[position].data as ICAdsNew)
            }
            is AdsCampaignHolder -> {
                holder.bind(listData[position].data as ICAdsNew)
            }
            is AdsPageHolder -> {
                holder.bind(listData[position].data as ICAdsNew)
            }
            is AdsProductHolder -> {
                holder.bind(listData[position].data as ICAdsNew)
            }
            is CollectionHorizontalHolder -> {
                holder.bind(listData[position].data as ICCollection)
            }
            is CollectionVerticalHolder -> {
                holder.bind(listData[position].data as ICCollection)
            }
            is CollectionSurveyHolder -> {
                holder.bind(listData[position].data as ICSurvey, object : ISurveyListener {
                    override fun onHideSurvey(adsID: Long) {
                        for (i in (listData.size - 1) downTo 0) {
                            if ((listData[i].data as ICAds?)?.id == adsID) {
                                listData.removeAt(i)
                                notifyItemRemoved(i)
                                notifyItemRangeChanged(i - 1, listData.size)
                                return
                            }
                        }
                    }

                    override fun onAnsweredSurvey(adsID: Long) {
                        for (i in (listData.size - 1) downTo 0) {
                            if ((listData[i].data as ICAds?)?.id == adsID) {
                                (listData[i].data as ICAds?)?.survey = null
                                notifyItemChanged(i)
                                return
                            }
                        }
                    }
                })
            }
            is ExperienceNewProductsViewHolder -> {
                holder.bind(listData[position].data as ICExperienceNewProducts, object : ExperienceNewProductsViewHolder.ExperienceNewProductListener {
                    override fun onRemoveView() {
                        listData.removeAt(position)
                        notifyItemRemoved(position)
                    }
                })
            }
            is TrendingProductHolder -> {
                holder.bind(listData[position].data as ICExperienceNewProducts)
            }
            is ProductForYouViewHolder -> {
                holder.bind(listData[position].data as MutableList<ICProductForYouMedia>)
            }
            is FlashSaleViewHolder -> {
                if (holder.timer != null) {
                    holder.timer!!.cancel()
                }

                holder.timer = object : CountDownTimer(timeFlashSale, 1000) {
                    override fun onFinish() {
                        holder.timer?.cancel()
                    }

                    override fun onTick(p0: Long) {
                        timeFlashSale = p0
                        holder.updateCountDownText(p0)
                    }
                }.start()
                holder.bind((listData[position].data as ICFlashSale))
            }
            is ProductNeedReviewHolder -> {
                holder.bind(listData[position].data as MutableList<ICProductNeedReview>)
            }
            is TopTendencyViewHolder -> {
                holder.bind(listData[position].data as ICTopTrend)
            }
            is ShoppingCatalogHolder -> {
                holder.bind(listData[position].data as MutableList<ICShoppingCatalog>)
            }
            is ShortMessageHolder -> {
                holder.itemView.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

                if (errorMessage.isNullOrEmpty()) {
                    holder.bind(iconMessage, "")
                } else {
                    holder.bind(iconMessage, errorMessage!!)
                }

                holder.setListener(View.OnClickListener {
                    messageListener.onMessageClicked()
                })
            }
        }
    }

    inner class CheckUpdateHolder(parent: ViewGroup, val binding: ItemCheckUpdateBinding = ItemCheckUpdateBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.btnUpdate.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    activity.openAppInGooglePlay()
                }
            }
        }
    }
}