package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.holder.ListStampECommerceHolder
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.network.models.ICCriteria
import vn.icheck.android.network.models.ICProductReviews
import vn.icheck.android.network.models.detail_stamp_v6_1.ICDetailStampV6_1
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectInfo
import vn.icheck.android.network.models.ICProductLink
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.network.models.v1.ICRelatedProductV1
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back.*
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.model.ICQAStamp
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.model.ICStampItem
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.model.ICViewType
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder.*

class DetailStampHoaPhatAdapter(private val headerImagelistener: SlideHeaderStampHoaPhatListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICStampItem>()

    @Synchronized
    fun addData(list: MutableList<ICStampItem>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    @Synchronized
    fun updateData(it: ICStampItem) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].type == it.type) {
                if (it.data != null) {
                    listData[i].data = it.data
                    notifyItemChanged(i)
                } else {
                    listData.removeAt(i)
                    notifyItemRemoved(i)
                }
                return
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData[position].data != null) {
            listData[position].type
        } else {
            when (listData[position].type) {
                ICViewType.METHOD_STAMP -> {
                    ICViewType.METHOD_STAMP
                }
                ICViewType.NO_QUESTION_STAMP -> {
                    ICViewType.NO_QUESTION_STAMP
                }
                else -> {
                    super.getItemViewType(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICViewType.IMAGE_HEADER_STAMP -> {
                ImageHeaderStampHolder(parent, headerImagelistener)
            }
            ICViewType.INFOR_HEADER_STAMP -> {
                InforHeaderStampHolder(parent, headerImagelistener)
            }
            ICViewType.ACCURACY_STAMP -> {
                AccuracyStampHolder(parent)
            }
            ICViewType.PRODUCT_ECCOMMERCE_TYPE -> {
                ListStampECommerceHolder(parent)
            }
            ICViewType.METHOD_STAMP -> {
                MethodStampHolder(parent,headerImagelistener)
            }
            ICViewType.INFORMATION_PRODUCT_STAMP -> {
                InformationProductStampHolder(parent, headerImagelistener)
            }
            ICViewType.STEP_BUILD_PRODUCT_STAMP -> {
                StepBuildProductStampHolder(parent)
            }
            ICViewType.CERTIFICATE_STAMP -> {
                CeritificateStampHolder(parent, headerImagelistener)
            }
            ICViewType.DNSH_STAMP -> {
                DnshStampHolder(parent, headerImagelistener)
            }
            ICViewType.USER_REVIEW_STAMP -> {
                UserReviewStampHolder(parent,headerImagelistener)
            }
            ICViewType.USER_NO_REVIEW_STAMP -> {
                UserNoReviewStampHolder(parent)
            }
            ICViewType.REVIEWS_TITLE -> ReviewsTitleStampHolder(parent)
            ICViewType.REVIEWS_CONTENT -> ReviewsContentStampHolder(parent)
            ICViewType.REVIEWS_COMMENT -> ReviewsCommentStampHolder(parent)
            ICViewType.REVIEWS_VIEW_MORE -> ReviewsMoreCommentHolder(parent)
            ICViewType.NO_REVIEWS -> NoReviewsStampHolder(parent)
            ICViewType.NO_QUESTION_STAMP -> {
                NoQuestionStampHolder(parent,headerImagelistener)
            }
            ICViewType.QUESTION_ANSWERS_STAMP -> {
                QuestionAnswerStampHolder(parent,headerImagelistener)
            }
            ICViewType.RELATED_PRODUCT_STAMP -> {
                ProductRelatedStampHolder(parent,headerImagelistener)
            }
            else -> NullHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageHeaderStampHolder -> {
                holder.bind(listData[position].data as ICBarcodeProductV1)
            }
            is InforHeaderStampHolder -> {
                holder.bind(listData[position].data as ICBarcodeProductV1)
            }
            is AccuracyStampHolder -> {
                holder.bind(listData[position].data as ICDetailStampV6_1.ICObjectDetailStamp)
            }
            is ListStampECommerceHolder -> {
                holder.bind(listData[position].data as MutableList<ICProductLink>)
            }
            is StepBuildProductStampHolder -> {
                holder.bind(listData[position].data as MutableList<ICObjectInfo>)
            }
            is InformationProductStampHolder -> {
                holder.bind(listData[position].data as ICBarcodeProductV1.Information)
            }
            is CeritificateStampHolder -> {
                holder.bind(listData[position].data as MutableList<ICBarcodeProductV1.Certificate>)
            }
            is DnshStampHolder -> {
                holder.bind(listData[position].data as ICBarcodeProductV1.Vendor)
            }
            is UserReviewStampHolder -> {
                holder.bind(listData[position].data as ICCriteria)
            }
            is UserNoReviewStampHolder -> {
                holder.bind(listData[position].data as ICCriteria)
            }
            is ReviewsTitleStampHolder -> {
                holder.bind(listData[position].data as ICBarcodeProductV1)
            }
            is ReviewsContentStampHolder -> {
                holder.bind(listData[position].data as ICProductReviews.ReviewsRow)
            }
            is ReviewsCommentStampHolder -> {
                holder.bind(listData[position].data as ICProductReviews.Comments)
            }
            is ReviewsMoreCommentHolder -> {
                holder.bind(listData[position].data as ICProductReviews.ReviewsRow, object : ItemClickListener<MutableList<ICStampItem>> {
                    override fun onItemClick(position: Int, item: MutableList<ICStampItem>?) {
                        listData.removeAt(position)
                        listData.removeAt(position - 1)
                        listData.addAll(position - 1, item!!)
                        notifyDataSetChanged()
                    }
                })
            }
            is NoReviewsStampHolder -> {
                holder.bind(listData[position].data as ICBarcodeProductV1)
            }
            is QuestionAnswerStampHolder -> {
                holder.bind(listData[position].data as ICQAStamp)
            }
            is ProductRelatedStampHolder -> {
                holder.bind(listData[position].data as MutableList<ICRelatedProductV1.RelatedProductRow>)
            }
        }
    }
}