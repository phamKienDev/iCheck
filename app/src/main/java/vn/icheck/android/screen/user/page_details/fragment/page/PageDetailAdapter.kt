package vn.icheck.android.screen.user.page_details.fragment.page

import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.component.ads.campaign.AdsCampaignHolder
import vn.icheck.android.component.ads.news.AdsNewHolder
import vn.icheck.android.component.ads.page.AdsPageHolder
import vn.icheck.android.component.header_page.HeaderInforPageHolder
import vn.icheck.android.component.header_page.bottom_sheet_header_page.IListReportView
import vn.icheck.android.component.image_assets.ImageAssetsHolder
import vn.icheck.android.component.image_video_slider.ICImageVideoSlider
import vn.icheck.android.component.image_video_slider.ICImageVideoSliderModel
import vn.icheck.android.component.invite_follow_page.InviteFollowPageHolder
import vn.icheck.android.component.page_introduction.PageIntroductionHolder
import vn.icheck.android.component.post.IPostListener
import vn.icheck.android.component.post.PostHolder
import vn.icheck.android.component.product.horizontal_product.ListProductHorizontalHolder
import vn.icheck.android.component.product.related_product.RelatedProductHolder
import vn.icheck.android.component.product.related_product.RelatedProductModel
import vn.icheck.android.component.relatedpage.RelatedPageHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.getAdsType
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.*
import vn.icheck.android.base.holder.LongMessageHolder
import vn.icheck.android.screen.user.home_page.model.ICListHomeItem
import vn.icheck.android.screen.user.page_details.fragment.page.widget.brand.WidgetBrandPageHolder
import vn.icheck.android.screen.user.page_details.fragment.page.widget.campaign.WidgetCampaignHolder
import vn.icheck.android.screen.user.page_details.fragment.page.widget.detail.WidgetPageDetailHolder

class PageDetailAdapter(val view: IListReportView, listener: IRecyclerViewCallback, val postListener: IPostListener) : RecyclerViewCustomAdapter<ICLayout>(listener) {
    private var recycledViewPool: RecyclerView.RecycledViewPool? = null
    private var pageType = Constant.PAGE_BRAND_TYPE

    @Synchronized
    fun addItem(obj: ICLayout) {
        listData.add(obj)
        if (message.isEmpty()) {
            notifyItemInserted(listData.size - 1)
        } else {
            message = ""
            notifyDataSetChanged()
        }
    }

    @Synchronized
    fun updateItem(obj: ICLayout) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].id == obj.id) {
                if (obj.data != null) {
                    listData[i].data = obj.data
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

    fun addPost(list: MutableList<ICLayout>, offsetPost: Int) {
        if (offsetPost > 10) {
            addListData(list)
        } else {
            for (i in listData.size - 1 downTo 0) {
                if (listData[i].viewType == ICViewTypes.LIST_POST_TYPE) {
                    checkLoadmore(list)
                    if (list.isNotEmpty()) {
                        listData.addAll(i + 1, list)
                        notifyItemInserted(i + 1)
                    } else {
                        listData.removeAt(i)
                        notifyItemRemoved(i)
                    }
                }
            }
        }
    }

    fun createPost(post: ICPost) {
        val index = listData.indexOfFirst { it.data is ICPost }

        if (index == -1) {
            listData.add(ICLayout("", "", ICRequest(), null, null, ICViewTypes.LIST_POST_TYPE, post))
            notifyDataSetChanged()
        } else {
            listData.add(index, ICLayout("", "", ICRequest(), null, null, ICViewTypes.LIST_POST_TYPE, post))
            notifyItemInserted(index)
        }
    }

    fun pinPost(postId: Long) {
        listData.forEachIndexed { index, layout ->
            if (layout.data is ICPost) {
                if ((layout.data as ICPost).id != postId) {
                    if ((layout.data as ICPost).pinned) {
                        (getListData[index].data as ICPost).pinned = false
                        notifyItemChanged(index)
                    }
                } else {
                    (getListData[index].data as ICPost).pinned = true
                    notifyItemChanged(index)
                }
            }
        }
    }

    fun unPinPost(postId: Long) {
        listData.forEachIndexed { index, layout ->
            if (layout.data is ICPost) {
                if ((layout.data as ICPost).id == postId) {
                    if ((layout.data as ICPost).pinned) {
                        (layout.data as ICPost).pinned = false
                        notifyItemChanged(index)
                    }
                }
            }
        }
    }

    fun skipInviteFollowWidget() {
        val indexInvite = listData.indexOfFirst { it.viewType == ICViewTypes.INVITE_FOLLOW_TYPE }
        if (indexInvite >= 0) {
            listData.removeAt(indexInvite)
            notifyItemRemoved(indexInvite)
        }
    }

    fun updatePost(post: ICPost) {
        val indexPost = listData.indexOfFirst { it.data is ICPost && (it.data as ICPost).id == post.id }
        if (indexPost >= 0) {
            listData[indexPost].data = post
            notifyItemChanged(indexPost)
        }
    }

    fun deletePost(id: Long) {
        val indexPost = listData.indexOfFirst { it.data is ICPost && (it.data as ICPost).id == id }
        if (indexPost >= 0) {
            listData.removeAt(indexPost)
            notifyItemRemoved(indexPost)
        }
    }

    fun updateSubcribeState(isSub: Boolean) {
        val index = listData.indexOfFirst { it.viewType == ICViewTypes.HEADER_INFOR_PAGE }
        if (index != -1) {
            (listData[index].data as ICPageOverview).unsubscribeNotice = isSub
            notifyItemChanged(index)
        }
    }

    override fun getItemType(position: Int): Int {
        return if (listData[position].data != null) {
            listData[position].viewType
        } else {
            ICViewTypes.NULL_HOLDER
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICViewTypes.IMAGE_VIDEO_SLIDER -> ICImageVideoSlider.createAttachments(parent, recycledViewPool, SizeHelper.dpToPx(200))
            ICViewTypes.HEADER_INFOR_PAGE -> HeaderInforPageHolder(parent, view)
            ICViewTypes.WIDGET_DETAIL -> PageIntroductionHolder.create(parent, recycledViewPool)
            ICViewTypes.WIDGET_BRAND -> WidgetBrandPageHolder(parent)
            ICViewTypes.CAMPAIGNS -> WidgetCampaignHolder(parent, recycledViewPool)
            ICViewTypes.HIGLIGHT_PRODUCTS_PAGE -> ListProductHorizontalHolder(parent, recycledViewPool)
            ICViewTypes.CATEGORIES_PRODUCTS_PAGE -> RelatedProductHolder(parent, recycledViewPool)
            ICViewTypes.IMAGE_ASSETS_PAGE -> ImageAssetsHolder(parent, recycledViewPool, view)
            ICViewTypes.RELATED_PAGE_TYPE -> RelatedPageHolder(parent)
            ICViewTypes.ADS_NEWS -> AdsNewHolder(parent)
            ICViewTypes.ADS_PAGE -> AdsPageHolder(parent)
            ICViewTypes.ADS_CAMPAIGN -> AdsCampaignHolder(parent)
            ICViewTypes.MESSAGE_TYPE -> LongMessageHolder(parent)
            ICViewTypes.LIST_POST_TYPE -> PostHolder(parent, postListener)
            ICViewTypes.INVITE_FOLLOW_TYPE -> InviteFollowPageHolder(parent)
            else -> NullHolder(parent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        when (holder) {
            is ICImageVideoSlider -> {
                holder.bind(listData[position].data as ICImageVideoSliderModel)
            }
            is HeaderInforPageHolder -> {
                holder.bind(listData[position].data as ICPageOverview)
            }
            is WidgetPageDetailHolder -> {
                holder.bind(listData[position].data as ICPageDetail)
            }
            is WidgetBrandPageHolder -> {
                holder.bind(listData[position].data as MutableList<ICPageTrend>, pageType)
            }
            is WidgetCampaignHolder -> {
                holder.bind(listData[position].data as MutableList<ICCampaign>)

                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            }
            is ListProductHorizontalHolder -> {
                holder.bind(listData[position].data as RelatedProductModel)
            }
            is RelatedProductHolder -> {
                holder.bind(listData[position].data as RelatedProductModel)
                holder.itemView.setBackgroundColor(0)
            }
            is ImageAssetsHolder -> {
                holder.bind(listData[position].data as ICImageAsset)
            }
            is RelatedPageHolder -> {
                holder.bind(listData[position].data as MutableList<ICRelatedPage>, pageType)
            }
            is PageIntroductionHolder -> {
                holder.bind(listData[position].data as ICPageDetail)
            }
            is AdsPageHolder -> {
                holder.bind(listData[position].data as ICAdsNew)
            }
            is AdsCampaignHolder -> {
                holder.bind(listData[position].data as ICAdsNew)
            }
            is AdsNewHolder -> {
                holder.bind(listData[position].data as ICAdsNew)
            }
            is PostHolder -> {
                holder.bind(listData[position].data as ICPost, SessionManager.session.user)
            }
            is InviteFollowPageHolder -> {
                holder.bind(listData[position].data as ICPageOverview)
            }
        }
    }
}