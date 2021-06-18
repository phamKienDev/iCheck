package vn.icheck.android.activities.product.review_product_v1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICProductReviews
import vn.icheck.android.base.holder.LoadingHolder
import vn.icheck.android.activities.product.review_product_v1.holder.ListReviewHolder
import vn.icheck.android.activities.product.review_product_v1.holder.PostReviewProductHolder
import vn.icheck.android.screen.user.review_product.holder.YourReviewHolder
import vn.icheck.android.screen.user.review_product.model.ICReviewProduct
import vn.icheck.android.activities.product.review_product_v1.view.IReviewProductView

class ReviewProductAdapter(val listener: IReviewProductView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICReviewProduct>()
    var messageError: String? = null
    var iconError: Int? = null

    private var isLoading = false
    private var isLoadmore = false

    private var isAddReview = false

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    fun disLoadmore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }


    fun addYourCriteria(item: ICReviewProduct) {
        if (isAddReview) {
            listData.add(0, item)
            notifyItemInserted(0)
            notifyItemRangeChanged(0, listData.size)
        } else {
            listData.add(item)
            notifyDataSetChanged()
        }
    }

    fun resetData() {
        messageError = null
        iconError = null
        isAddReview = false
        listData.clear()
        notifyDataSetChanged()
    }

    fun addAllReview(list: MutableList<ICReviewProduct>) {
        isLoadmore = list.size >= APIConstants.LIMIT
        isLoading = false
        isAddReview = true

        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListComment(list: MutableList<ICProductReviews.Comments>, reviewPos: Int) {
        (listData[reviewPos].reviews?.comments as MutableList).addAll(list)
    }

    fun addItemCommet(obj: ICProductReviews.Comments, reviewPos: Int) {
        if (listData[reviewPos].reviews?.comments.isNullOrEmpty()) {
            listData[reviewPos].reviews?.comments = mutableListOf()
            (listData[reviewPos].reviews?.comments as MutableList).add(obj)
        } else {
            (listData[reviewPos].reviews?.comments as MutableList).add(0, obj)
        }

        var count = listData[reviewPos].reviews?.commentCounts
        if (count == null) {
            count = 1
            listData[reviewPos].reviews?.commentCounts = count
        } else {
            count += 1
            listData[reviewPos].reviews?.commentCounts = count
        }
    }

    fun setError(errorMessage: String, iconError: Int) {
        messageError = errorMessage
        this.iconError = iconError
        isLoadmore = false

        listData.clear()
        notifyDataSetChanged()
    }

    fun sizeData(): Int = listData.size

    override fun getItemCount(): Int {
        return if (messageError != null) {
            1
        } else {
            if (isLoadmore) {
                listData.size + 1
            } else {
                listData.size
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageError == null) {
            if (position < listData.size) {
                listData[position].type
            } else {
                Constant.TYPE_LOAD_MORE
            }
        } else {
            Constant.MESSAGE_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Constant.TYPE_POST_YOUR_REVIEW -> {
                PostReviewProductHolder(layoutInflater.inflate(R.layout.item_post_your_review_product, parent, false), listener)
            }

            Constant.TYPE_YOUR_REVIEW -> {
                YourReviewHolder(layoutInflater.inflate(R.layout.item_your_review_product_v1, parent, false), listener)
            }
            Constant.TYPE_ALL_REVIEW -> {
                ListReviewHolder(layoutInflater.inflate(R.layout.item_list_review_product_v1, parent, false), listener)
            }
            Constant.TYPE_HEADER_REVIEW -> {
                HeaderReviewHolder(layoutInflater.inflate(R.layout.item_header_list_review_product, parent, false))
            }
            Constant.TYPE_LOAD_MORE -> {
                LoadingHolder(parent)
            }
            else -> {
                MessageHolder(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostReviewProductHolder -> {
                listData[position].criteria?.let {
                    holder.bind(it)
                }
            }
            is YourReviewHolder -> {
                listData[position].criteria?.let {
                    holder.bind(it)
                }
            }
            is ListReviewHolder -> {
                listData[position].reviews?.let {
                    holder.bind(it)
                }
            }
            is LoadingHolder -> {
                holder.itemView.setBackgroundColor(vn.icheck.android.ichecklibs.Constant.getAppBackgroundWhiteColor(holder.itemView.context))
                if (!isLoading) {
                    isLoading = true
                    listener.onLoadmore()
                }
            }
            is MessageHolder -> {
                holder.bind(iconError!!, messageError!!, R.string.thu_lai)
                holder.listener(View.OnClickListener {
                    listener.onClickTryAgain()
                })
            }
            is HeaderReviewHolder->{
                holder.itemView.setBackgroundColor(vn.icheck.android.ichecklibs.Constant.getAppBackgroundWhiteColor(holder.itemView.context))
            }

        }
    }

    inner class HeaderReviewHolder(view: View) : RecyclerView.ViewHolder(view)
}