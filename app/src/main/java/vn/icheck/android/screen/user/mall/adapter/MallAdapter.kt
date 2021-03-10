package vn.icheck.android.screen.user.mall.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.banner.BannerHolder
import vn.icheck.android.component.banner.SlideBannerHolder
import vn.icheck.android.component.collection.horizontal.CollectionListProductHorizontalHolder
import vn.icheck.android.component.collection.survey.CollectionSurveyHolder
import vn.icheck.android.component.collection.survey.CollectionSurveySuccessHolder
import vn.icheck.android.component.collection.survey.ISurveyListener
import vn.icheck.android.component.collection.vertical.CollectionListProductVerticalHolder
import vn.icheck.android.network.models.ICAds
import vn.icheck.android.screen.user.campaign.calback.IBannerListener
import vn.icheck.android.screen.user.campaign.calback.IMessageListener
import vn.icheck.android.screen.user.campaign.holder.base.LongMessageHolder
import vn.icheck.android.screen.user.mall.ICMall
import vn.icheck.android.screen.user.mall.holder.MallBusinessHolder

class MallAdapter(
        private val bannerListener: IBannerListener,
        private val messageListener: IMessageListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listData = mutableListOf<ICMall>()

    private var iconMessage = R.drawable.ic_error_request
    private var errorMessage: String? = null

    private var isAddTopBanner = 0
    private var isAddBusiness = 0

    fun addTopBanner(obj: ICMall) {
        errorMessage = null
        isAddTopBanner = 1

        listData.add(0, obj)
        notifyItemInserted(0)
    }

    fun addBusiness(obj: ICMall) {
        errorMessage = null
        isAddBusiness = 1

        listData.add(isAddTopBanner, obj)
        notifyItemInserted(isAddTopBanner)
    }

    fun addCampaign(list: List<ICMall>) {
        errorMessage = null

        val pos = listData.size + 1
        listData.addAll(list)
        notifyItemRangeInserted(pos, listData.size)
    }

    fun hideBannerSurvey(surveyID: Long) {
        for (i in (listData.size - 1) downTo 2) {
            if (listData[i].type == ICViewTypes.ADS_SLIDE_BANNER_TYPE) {
                for (j in (listData[i].listAds ?: mutableListOf()).indices) {
                    if (listData[i].listAds?.get(j)?.id == surveyID) {
                        listData[i].listAds?.removeAt(j)
                        if (listData[i].listAds?.size == 0) {
                            listData.removeAt(i)
                            notifyItemRemoved(i)
                            notifyItemRangeChanged(i - 1, itemCount)
                        } else {
                            notifyItemChanged(i)
                        }
                        return
                    }
                }
            } else if (listData[i].ads?.id == surveyID) {
                listData.removeAt(i)
                notifyItemRemoved(i)
                notifyItemRangeChanged(i - 1, itemCount)
            }
        }
    }

    fun updateCollection(ads: ICAds) {
        for (i in (listData.size - 1) downTo 0) {
            if (listData[i].ads?.id == ads.id) {
                listData[i].ads = ads
                notifyItemChanged(i)
                return
            }
        }
    }

    fun setError(icon: Int, error: String) {
        listData.clear()
        isAddTopBanner = 0
        isAddBusiness = 0

        iconMessage = icon
        errorMessage = error

        notifyDataSetChanged()
    }

    fun resetData() {
        errorMessage = null
        isAddTopBanner = 0
        isAddBusiness = 0

        listData.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNotEmpty()) {
            if (listData[position].type == ICViewTypes.ADS_DIRECT_SURVEY_TYPE) {
                if (listData[position].ads?.survey != null) {
                    ICViewTypes.ADS_DIRECT_SURVEY_TYPE
                } else {
                    ICViewTypes.ADS_DIRECT_SURVEY_SUCCESS_TYPE
                }
            } else {
                listData[position].type
            }
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICViewTypes.ADS_LIST_PRODUCT_VERTICAL -> {
                CollectionListProductVerticalHolder(parent)
            }
            ICViewTypes.ADS_LIST_PRODUCT_HORIZONTAL -> {
                CollectionListProductHorizontalHolder(parent)
            }
            ICViewTypes.ADS_BANNER_TYPE -> {
                BannerHolder(parent)
            }
            ICViewTypes.ADS_SLIDE_BANNER_TYPE -> {
                SlideBannerHolder(parent)
            }
            ICViewTypes.BUSINESS_TYPE -> {
                MallBusinessHolder(parent)
            }
            ICViewTypes.ADS_DIRECT_SURVEY_TYPE -> {
                CollectionSurveyHolder(parent)
            }
            ICViewTypes.ADS_DIRECT_SURVEY_SUCCESS_TYPE -> {
                CollectionSurveySuccessHolder(parent)
            }
            else -> {
                LongMessageHolder(parent)
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CollectionListProductVerticalHolder -> {
                listData[position].ads?.let {
                    holder.bind(it)
                }
            }
            is CollectionListProductHorizontalHolder -> {
                listData[position].ads?.let {
                    holder.bind(it)
                }
            }
            is BannerHolder -> {
                listData[position].ads?.let {
                    holder.bind(it, bannerListener)
                }
            }
            is SlideBannerHolder -> {
                listData[position].listAds?.let {
                    holder.bind(it, bannerListener)
                }
            }
            is MallBusinessHolder -> {
                listData[position].business?.let {
                    holder.bind(it)
                }
            }
            is CollectionSurveyHolder -> {
                listData[position].ads?.survey?.let {
                    holder.bind(it, object : ISurveyListener {
                        override fun onHideSurvey(adsID: Long) {
                            for (i in (listData.size - 1) downTo 0) {
                                if (listData[i].ads?.id == adsID) {
                                    listData.removeAt(i)
                                    notifyItemRemoved(i)
                                    notifyItemRangeChanged(i - 1, listData.size)
                                    return
                                }
                            }
                        }

                        override fun onAnsweredSurvey(adsID: Long) {
                            for (i in (listData.size - 1) downTo 0) {
                                if (listData[i].ads?.id == adsID) {
                                    listData[i].ads?.survey = null
                                    notifyItemChanged(i)
                                    return
                                }
                            }
                        }
                    })
                }
            }
            is LongMessageHolder -> {
                holder.bind(iconMessage, errorMessage, R.string.thu_lai)

                holder.setListener(View.OnClickListener {
                    messageListener.onMessageClicked()
                })
            }
        }
    }
}