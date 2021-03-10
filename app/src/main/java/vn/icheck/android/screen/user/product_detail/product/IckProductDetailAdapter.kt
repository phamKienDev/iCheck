package vn.icheck.android.screen.user.product_detail.product

import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.BottomModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.component.ads.campaign.AdsCampaignHolder
import vn.icheck.android.component.ads.news.AdsNewHolder
import vn.icheck.android.component.ads.page.AdsPageHolder
import vn.icheck.android.component.ads.product.AdsProductHolder
import vn.icheck.android.component.image_video_slider.ICImageVideoSlider
import vn.icheck.android.component.image_video_slider.ICImageVideoSliderModel
import vn.icheck.android.component.infomation_contribution.ContributionHolder
import vn.icheck.android.component.infomation_contribution.ContributrionModel
import vn.icheck.android.component.infomation_contribution.NoContributionHolder
import vn.icheck.android.component.my_contribution.MyContributionHolder
import vn.icheck.android.component.my_contribution.MyContributionViewModel
import vn.icheck.android.component.noimage.NoImageHolder
import vn.icheck.android.component.product.horizontal_product.ListProductHorizontalHolder
import vn.icheck.android.component.product.ProductDetailListener
import vn.icheck.android.component.product.bottominfo.BottomInfoHolder
import vn.icheck.android.component.product.certifications.CertificationsHolder
import vn.icheck.android.component.product.certifications.CertificationsModel
import vn.icheck.android.component.product.emty_qa.EmptyQAModel
import vn.icheck.android.component.product.emty_qa.ProductEmptyQaHolder
import vn.icheck.android.component.product.enterprise.EnterpriseComponentV2
import vn.icheck.android.component.product.enterprise.EnterpriseModelV2
import vn.icheck.android.component.product.header.HeaderInforProductComponent
import vn.icheck.android.component.product.header.ProductHeaderModelV2
import vn.icheck.android.component.product.infor.ProductInformationHolder
import vn.icheck.android.component.product.infor_contribution.EmptyContributionEnterpriseHolder
import vn.icheck.android.component.product.infor_contribution.InformationContributionModel
import vn.icheck.android.component.product.mbtt.MbttModel
import vn.icheck.android.component.product.mbtt.MbttHolder
import vn.icheck.android.component.product.notverified.ProductNotVerifiedHolder
import vn.icheck.android.component.product.notverified.ProductNotVerifiedModel
import vn.icheck.android.component.product.npp.DistributorHolder
import vn.icheck.android.component.product.npp.DistributorModel
import vn.icheck.android.component.product.related_product.RelatedProductHolder
import vn.icheck.android.component.product.related_product.RelatedProductModel
import vn.icheck.android.component.product.reviewsummary.ReviewSummaryHolder
import vn.icheck.android.component.product.vendor.VendorHolder
import vn.icheck.android.component.product.vendor.VendorModel
import vn.icheck.android.component.product.verified.ProductVerifiedHolder
import vn.icheck.android.component.product_list_review.ProductListReviewHolder
import vn.icheck.android.component.product_list_review.ProductListReviewModel
import vn.icheck.android.component.product_question_answer.ProductQuestionHolder
import vn.icheck.android.component.product_question_answer.ProductQuestionModel
import vn.icheck.android.component.product_review.my_review.IMyReviewListener
import vn.icheck.android.component.product_review.my_review.MyReviewHolder
import vn.icheck.android.component.product_review.my_review.MyReviewModel
import vn.icheck.android.component.product_review.submit_review.ISubmitReviewListener
import vn.icheck.android.component.product_review.submit_review.SubmitReviewHolder
import vn.icheck.android.component.product_review.submit_review.SubmitReviewModel
import vn.icheck.android.component.shopvariant.product_detail.ProductDetailShopHolder
import vn.icheck.android.component.shopvariant.product_detail.ShopProductModel
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.getAdsType
import vn.icheck.android.loyalty.helper.CampaignLoyaltyHelper
import vn.icheck.android.loyalty.holder.LoyaltyViewHolder
import vn.icheck.android.loyalty.model.ICKLoyalty
import vn.icheck.android.network.models.*
import vn.icheck.android.screen.user.home_page.home.model.ICListHomeItem
import vn.icheck.android.screen.user.product_detail.product.model.IckReviewSummaryModel
import vn.icheck.android.util.KeyboardUtils

class IckProductDetailAdapter(listener: IRecyclerViewCallback, private val productListener: ProductDetailListener, private val submitReviewListener: ISubmitReviewListener, private val listenerLoyalty: CampaignLoyaltyHelper.IRemoveHolderInputLoyaltyListener, private val listenerLogin: CampaignLoyaltyHelper.ILoginListener, private val listenerMyReview: IMyReviewListener) : RecyclerViewCustomAdapter<ICLayout>(listener) {
    private var sharedPool: RecyclerView.RecycledViewPool? = null

//    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
//        super.onAttachedToRecyclerView(recyclerView)
//        sharedPool = recyclerView.recycledViewPool
//    }

    fun addHolderInput(obj: ICLayout) {
        if (listData.isNullOrEmpty()) {
            listData.add(obj)
        } else {
            listData.add(0, obj)
        }
        notifyDataSetChanged()
    }

    fun removeHolderInput() {
        listData.removeAt(0)
        notifyItemRemoved(0)
    }

    fun removeContributionEnterprise() {
        for (i in 0 until listData.size) {
            if (listData[i].viewType == ICViewTypes.EMPTY_CONTRIBUTION_INTERPRISE_TYPE) {
                listData.removeAt(i)
                notifyDataSetChanged()
                return
            }
        }
    }

    @Synchronized
    fun addLayout(obj: ICLayout) {
        if (listData.isNotEmpty()) {
            listData.add(obj)
            notifyItemInserted(listData.size - 1)
        } else {
            listData.add(obj)
            notifyDataSetChanged()
        }
    }

    @Synchronized
    fun updateLayout(obj: ICLayout) {
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
    fun updateLayout(obj: ICListHomeItem) {
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
                        if (listData[i].data==null) {
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

    fun updateQuestion(obj: ICLayout) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].viewType == ICViewTypes.QUESTIONS_ANSWER_TYPE || listData[i].viewType == ICViewTypes.EMPTY_QA_TYPE) {
                if (listData[i].data != null) {
                    listData[i].viewType = obj.viewType
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

    fun updateReview(obj: ICLayout) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].viewType == ICViewTypes.MY_REVIEW_TYPE || listData[i].viewType == ICViewTypes.SUBMIT_REVIEW_TYPE) {
                if (listData[i].data != null) {
                    listData[i].viewType = obj.viewType
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

    fun updateTransparency(obj: ICTransparency) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].viewType == ICViewTypes.TRANSPARENCY_TYPE) {
                if (listData[i].data != null) {
                    (listData[i].data as MbttModel).icTransparency = obj
                    notifyItemChanged(i)
                } else {
                    listData.removeAt(i)
                    notifyItemRemoved(i)
                }
                return
            }
        }
    }

    fun updateBookMark(isBookMark: Boolean) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].viewType == ICViewTypes.HEADER_TYPE) {
                (listData[i].data as ProductHeaderModelV2).icBarcodeProduct.isBookMark = isBookMark
                notifyItemChanged(i)
                return
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is SubmitReviewHolder) {
            KeyboardUtils.hideSoftInput(holder.itemView)
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
            ICViewTypes.IMAGE_VIDEO_SLIDER -> ICImageVideoSlider.createAttachments(parent, sharedPool)
            ICViewTypes.HEADER_TYPE -> HeaderInforProductComponent.create(parent, productListener)
            ICViewTypes.REVIEW_SUMMARY_TYPE -> ReviewSummaryHolder(parent)
            ICViewTypes.SHOP_VARIANT_TYPE -> ProductDetailShopHolder(parent)
            ICViewTypes.TRANSPARENCY_TYPE -> MbttHolder.create(parent)
            ICViewTypes.ENTERPRISE_TYPE -> EnterpriseComponentV2(parent, sharedPool)
            ICViewTypes.EMPTY_CONTRIBUTION_INTERPRISE_TYPE -> EmptyContributionEnterpriseHolder(parent, productListener)
            ICViewTypes.NOT_VERIFIED_TYPE -> ProductNotVerifiedHolder(parent)
            ICViewTypes.VENDOR_TYPE -> VendorHolder(parent, sharedPool)
            ICViewTypes.DISTRIBUTOR -> DistributorHolder(parent, sharedPool)
            ICViewTypes.LIST_REVIEWS_TYPE -> ProductListReviewHolder(parent, sharedPool)
            ICViewTypes.SUBMIT_REVIEW_TYPE -> SubmitReviewHolder(parent, sharedPool, submitReviewListener)
            ICViewTypes.MY_REVIEW_TYPE -> MyReviewHolder(parent, listenerMyReview)
            ICViewTypes.QUESTIONS_ANSWER_TYPE -> ProductQuestionHolder(parent, sharedPool, productListener)
            ICViewTypes.EMPTY_QA_TYPE -> ProductEmptyQaHolder(parent)
            ICViewTypes.DESCRIPTION_TYPE -> ProductInformationHolder(parent)
            ICViewTypes.CONTRIBUTE_USER -> ContributionHolder(parent)
            ICViewTypes.EMPTY_CONTRIBUTE_USER -> NoContributionHolder(parent)
            ICViewTypes.RELATED_PRODUCT_TYPE -> RelatedProductHolder(parent, sharedPool, Color.TRANSPARENT)
            ICViewTypes.CHUNG_CHI_TYPE -> CertificationsHolder(parent, sharedPool)
            ICViewTypes.VERIFIED_TYPE -> ProductVerifiedHolder(parent)
            ICViewTypes.OWNER_PRODUCT_TYPE -> ListProductHorizontalHolder(parent, sharedPool, Color.TRANSPARENT)
            ICViewTypes.TYPE_BOTTOM -> BottomInfoHolder(parent, productListener)
            ICViewTypes.ADS_NEWS -> AdsNewHolder(parent)
            ICViewTypes.ADS_PAGE -> AdsPageHolder(parent)
            ICViewTypes.ADS_CAMPAIGN -> AdsCampaignHolder(parent)
            ICViewTypes.ADS_PRODUCT -> AdsProductHolder(parent)
            ICViewTypes.MY_CONTRIBUTION -> MyContributionHolder.create(parent)
            ICViewTypes.HOLDER_NO_IMAGE -> NoImageHolder.create(parent)
            ICViewTypes.LOYALTY_HOLDER_TYPE -> LoyaltyViewHolder(parent)
            else -> NullHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ICViewTypes.IMAGE_VIDEO_SLIDER -> (holder as ICImageVideoSlider).bind(listData[position].data as ICImageVideoSliderModel)
            ICViewTypes.HEADER_TYPE -> (holder as HeaderInforProductComponent).bind(listData[position].data as ProductHeaderModelV2)
            ICViewTypes.REVIEW_SUMMARY_TYPE -> (holder as ReviewSummaryHolder).bind(listData[position].data as IckReviewSummaryModel)
            ICViewTypes.SHOP_VARIANT_TYPE -> (holder as ProductDetailShopHolder).bind(listData[position].data as ShopProductModel)
            ICViewTypes.TRANSPARENCY_TYPE -> (holder as MbttHolder).bind(listData[position].data as MbttModel)
            ICViewTypes.ENTERPRISE_TYPE -> (holder as EnterpriseComponentV2).bind(listData[position].data as EnterpriseModelV2)
            ICViewTypes.EMPTY_CONTRIBUTION_INTERPRISE_TYPE -> (holder as EmptyContributionEnterpriseHolder).bind(listData[position].data as InformationContributionModel)
            ICViewTypes.VERIFIED_TYPE -> (holder as ProductVerifiedHolder).bind(listData[position].data as ICClientSetting)
            ICViewTypes.NOT_VERIFIED_TYPE -> (holder as ProductNotVerifiedHolder).bind(listData[position].data as ProductNotVerifiedModel)
            ICViewTypes.VENDOR_TYPE -> (holder as VendorHolder).bind(listData[position].data as VendorModel)
            ICViewTypes.DISTRIBUTOR -> (holder as DistributorHolder).bind(listData[position].data as DistributorModel, IckProductDetailActivity.urlDistributor)
            ICViewTypes.LIST_REVIEWS_TYPE -> (holder as ProductListReviewHolder).bind(listData[position].data as ProductListReviewModel)
            ICViewTypes.SUBMIT_REVIEW_TYPE -> (holder as SubmitReviewHolder).bind(listData[position].data as SubmitReviewModel)
            ICViewTypes.MY_REVIEW_TYPE -> (holder as MyReviewHolder).bind(listData[position].data as MyReviewModel)
            ICViewTypes.QUESTIONS_ANSWER_TYPE -> (holder as ProductQuestionHolder).bind(listData[position].data as ProductQuestionModel)
            ICViewTypes.EMPTY_QA_TYPE -> (holder as ProductEmptyQaHolder).bind(listData[position].data as EmptyQAModel)
            ICViewTypes.DESCRIPTION_TYPE -> (holder as ProductInformationHolder).bind(listData[position].data as ICProductInformations)
            ICViewTypes.CONTRIBUTE_USER -> (holder as ContributionHolder).bind(listData[position].data as ContributrionModel)
            ICViewTypes.EMPTY_CONTRIBUTE_USER -> (holder as NoContributionHolder).bind(listData[position].data as ContributrionModel)
            ICViewTypes.RELATED_PRODUCT_TYPE -> (holder as RelatedProductHolder).bind(listData[position].data as RelatedProductModel)
            ICViewTypes.CHUNG_CHI_TYPE -> (holder as CertificationsHolder).bind(listData[position].data as CertificationsModel)
            ICViewTypes.OWNER_PRODUCT_TYPE -> (holder as ListProductHorizontalHolder).bind(listData[position].data as RelatedProductModel)
            ICViewTypes.TYPE_BOTTOM -> (holder as BottomInfoHolder).bind(listData[position].data as BottomModel)
            ICViewTypes.ADS_NEWS -> (holder as AdsNewHolder).bind(listData[position].data as ICAdsNew)
            ICViewTypes.ADS_PAGE -> (holder as AdsPageHolder).bind(listData[position].data as ICAdsNew)
            ICViewTypes.ADS_CAMPAIGN -> (holder as AdsCampaignHolder).bind(listData[position].data as ICAdsNew)
            ICViewTypes.ADS_PRODUCT -> (holder as AdsProductHolder).bind(listData[position].data as ICAdsNew)
            ICViewTypes.MY_CONTRIBUTION -> (holder as MyContributionHolder).bind(listData[position].data as MyContributionViewModel)
            ICViewTypes.LOYALTY_HOLDER_TYPE -> ICheckApplication.currentActivity()?.let { activity ->
                if (activity is IckProductDetailActivity) {
                    (holder as LoyaltyViewHolder).bind(listData[position].data as ICKLoyalty, IckProductDetailActivity.barcode, activity, listenerLoyalty, listenerLogin)
                }
            }
            else -> super.onBindViewHolder(holder, position)
        }
    }
}