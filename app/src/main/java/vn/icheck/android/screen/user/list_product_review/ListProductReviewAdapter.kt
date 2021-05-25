package vn.icheck.android.screen.user.list_product_review

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.product_review.header_list_review.HeaderListReviewHolder
import vn.icheck.android.component.product_review.header_list_review.HeaderListReviewModel
import vn.icheck.android.component.product_review.count_review.CountReviewHolder
import vn.icheck.android.component.product_review.count_review.CountReviewModel
import vn.icheck.android.component.product_review.list_review.ItemListReviewHolder
import vn.icheck.android.component.product_review.list_review.ItemListReviewModel
import vn.icheck.android.component.product_review.my_review.IMyReviewListener
import vn.icheck.android.component.product_review.my_review.MyReviewHolder
import vn.icheck.android.component.product_review.my_review.MyReviewModel
import vn.icheck.android.component.product_review.submit_review.ISubmitReviewListener
import vn.icheck.android.component.product_review.submit_review.SubmitReviewHolder
import vn.icheck.android.component.product_review.submit_review.SubmitReviewModel
import vn.icheck.android.network.models.ICPost

class ListProductReviewAdapter(var callback: IRecyclerViewCallback, private val submitReviewListener: ISubmitReviewListener, private val myReviewListener: IMyReviewListener) : RecyclerViewCustomAdapter<ICViewModel>(callback) {
    var isReviewSummary = 0
    private var refeshTextReview=true

    fun setRefeshTextReview(refesh:Boolean){
        refeshTextReview=refesh
    }

    fun updateItemReview(post: ICPost) {
        for (i in 0 until listData.size) {
            if (listData[i].getViewType() == ICViewTypes.ITEM_REVIEWS_TYPE) {
                if ((listData[i] as ItemListReviewModel).data.id == post.id) {
                    (listData[i] as ItemListReviewModel).data = post
                    notifyItemChanged(i)
                }
            }
        }
    }

    fun addReviewInfo(obj: ICViewModel) {
        if (!listData.isNullOrEmpty()) {
            if (obj.getViewType() == ICViewTypes.HEADER_REVIEW_TYPE) {
                isReviewSummary = 1
                listData.add(0, obj)
            } else {
                listData.add(isReviewSummary, obj)
            }
        } else {
            if (obj.getViewType() == ICViewTypes.HEADER_REVIEW_TYPE) {
                isReviewSummary = 1
            }
            listData.add(obj)
        }
        notifyDataSetChanged()
    }

    fun updateMyReview(obj: ICViewModel) {
        var positionSubmit = -1
        var positionMyReview = -1
        for (i in 0 until listData.size) {
            if (listData[i].getViewType() == ICViewTypes.SUBMIT_REVIEW_TYPE) {
                positionSubmit = i
            }
            if (listData[i].getViewType() == ICViewTypes.MY_REVIEW_TYPE) {
                positionMyReview = i
            }
        }

        if (positionSubmit != -1) {
            listData[positionSubmit] = obj
        } else if (positionMyReview != -1) {
            listData[positionMyReview] = obj
        }
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderListReviewHolder -> {
                holder.bind(listData[position] as HeaderListReviewModel)
            }

            is SubmitReviewHolder -> {
                holder.bind(listData[position] as SubmitReviewModel,refeshTextReview).apply { refeshTextReview=false }
            }

            is MyReviewHolder -> {
                holder.bind(listData[position] as MyReviewModel)
            }

            is CountReviewHolder -> {
                holder.bind(listData[position] as CountReviewModel, false)
            }
            is ItemListReviewHolder -> {
                holder.bind(listData[position] as ItemListReviewModel)
            }
            else -> {
                super.onBindViewHolder(holder, position)
            }
        }

    }

    override fun getItemType(position: Int): Int {
        return listData[position].getViewType()
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICViewTypes.HEADER_REVIEW_TYPE -> HeaderListReviewHolder(parent)
            ICViewTypes.SUBMIT_REVIEW_TYPE -> SubmitReviewHolder(parent, null, submitReviewListener)
            ICViewTypes.MY_REVIEW_TYPE -> MyReviewHolder(parent, myReviewListener)
            ICViewTypes.COUNT_REVIEW_TYPE -> CountReviewHolder(parent)
            else -> ItemListReviewHolder(parent.context)
        }
    }
}