package vn.icheck.android.activities.product.review_product_v1.holder

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list_review_product_v1.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICProductReviews
import vn.icheck.android.base.adapter.HorizontalImageAdapter
import vn.icheck.android.activities.product.review_product_v1.adapter.CommentReviewAdapter
import vn.icheck.android.activities.product.review_product_v1.view.IReviewProductView
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.criteria.CriteriaAdapter
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.criteria.CriteriaChild
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.ReviewsTimeUtils

class ListReviewHolder(val view: View, val listener: IReviewProductView) : BaseViewHolder<ICProductReviews.ReviewsRow>(view) {
    var useful = 0
    var unuseful = 0
    var commentAdapter: CommentReviewAdapter? = null

    fun setTextUseful(number: Long): String {
        return if (number > 0) {
            view.context.getString(R.string.huu_ich_x, number)
        } else {
            view.context.getString(R.string.huu_ich)
        }
    }

    fun setTextUnUseful(number: Long): String {
        return if (number > 0) {
            view.context.getString(R.string.khong_huu_ich_x, number)
        } else {
            view.context.getString(R.string.khong_huu_ich)
        }
    }

    override fun bind(obj: ICProductReviews.ReviewsRow) {
        if (obj.owner == null) {
            WidgetUtils.loadImageUrl(itemView.one_user, "", R.drawable.user_placeholder)
            itemView.tv_user_1_name.text =""
        } else {
            WidgetUtils.loadImageUrl(itemView.one_user, obj.owner.avatarThumb?.small, R.drawable.user_placeholder)
            itemView.tv_user_1_name.text = obj.owner.name
        }

        itemView.tv_user_1_comment.text = obj.message
        itemView.user_1_rating.rating = obj.averagePoint
        itemView.tv_1_time.text = ReviewsTimeUtils(obj).getTime()

        //useful/ unUseful
        itemView.tv_1_useful.text = setTextUseful(obj.useful)
        itemView.tv_1_unuseful.text = setTextUnUseful(obj.unuseful)

        obj.actionUseful?.let {
            if ("useful" == it) {
                view.tv_1_useful.text = setTextUseful(obj.useful)
                view.tv_1_useful.setTextColor(Color.parseColor("#3C5A99"))
                view.tv_1_useful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_useful_fc_24px, 0, 0, 0)
                useful = 1
            } else if ("unuseful" == it) {
                view.tv_1_unuseful.text = setTextUnUseful(obj.unuseful)
                view.tv_1_unuseful.setTextColor(Color.parseColor("#3C5A99"))
                view.tv_1_unuseful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unuseful_fc_24px, 0, 0, 0)
                unuseful = 1
            }
        }
        val showRate = obj.message != null && obj.message.isNotEmpty() && obj.customerId != SessionManager.session.user?.id
        if (showRate) {
            itemView.gv_useful.visibility = View.VISIBLE
        } else {
            itemView.gv_useful.visibility = View.GONE
        }
        itemView.tv_1_useful.setOnClickListener {
            if (useful == 0) {
                obj.useful++
                view.tv_1_useful.text = setTextUseful(obj.useful)
                view.tv_1_useful.setTextColor(Color.parseColor("#3C5A99"))
                view.tv_1_useful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_useful_fc_24px, 0, 0, 0)
                useful = 1
                if (unuseful == 1) {
                    obj.unuseful--
                    view.tv_1_unuseful.text = setTextUnUseful(obj.unuseful)
                    view.tv_1_unuseful.setTextColor(Color.parseColor("#828282"))
                    view.tv_1_unuseful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unuseful_unfc_24px, 0, 0, 0)
                    unuseful = 0
                }
                listener.onVoteReviews(obj.id, "useful")
            } else {
                obj.useful--
                view.tv_1_useful.text = setTextUseful(obj.useful)
                view.tv_1_useful.setTextColor(Color.parseColor("#828282"))
                view.tv_1_useful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_useful_unfc_24px, 0, 0, 0)
                useful = 0
                listener.onVoteReviews(obj.id, "")
            }
        }

        itemView.tv_user_1_name.setOnClickListener {
            listener.onShowDetailUser(obj.customerId, "user")
        }
        itemView.one_user.setOnClickListener {
            listener.onShowDetailUser(obj.customerId, "user")
        }

        itemView.tv_1_unuseful.setOnClickListener {
            if (unuseful == 0) {
                obj.unuseful++
                view.tv_1_unuseful.text = setTextUnUseful(obj.unuseful)
                view.tv_1_unuseful.setTextColor(Color.parseColor("#3C5A99"))
                view.tv_1_unuseful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unuseful_fc_24px, 0, 0, 0)
                unuseful = 1
                if (useful == 1) {
                    obj.useful--
                    view.tv_1_useful.text = setTextUseful(obj.useful)
                    view.tv_1_useful.setTextColor(Color.parseColor("#828282"))
                    view.tv_1_useful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_useful_unfc_24px, 0, 0, 0)
                    useful = 0
                }
                listener.onVoteReviews(obj.id, "unuseful")
            } else {
                obj.unuseful--
                view.tv_1_unuseful.text = setTextUnUseful(obj.unuseful)
                view.tv_1_unuseful.setTextColor(Color.parseColor("#828282"))
                view.tv_1_unuseful.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unuseful_unfc_24px, 0, 0, 0)
                unuseful = 0
                listener.onVoteReviews(obj.id, "")
            }
        }

        //rating

        if (!obj.customerCriteria.isNullOrEmpty()) {

            val listCriteriaChild = mutableListOf<CriteriaChild>()
            for (item in obj.customerCriteria.indices) {
                listCriteriaChild.add(CriteriaChild(
                        obj.customerCriteria.get(item),
                        obj.customerCriteria.get(item).point?.toFloat(),
                        true
                ))
            }
            val criteriaAdapter = CriteriaAdapter(listCriteriaChild, 1)
            itemView.rcv_rating.adapter = criteriaAdapter
            itemView.rcv_rating.isNestedScrollingEnabled = false
            itemView.rcv_rating.layoutManager = LinearLayoutManager(view.context)
        }

        itemView.tv_1_detail.setOnClickListener {
            if (itemView.rcv_rating.visibility == View.GONE) {
                itemView.rcv_rating.visibility = View.VISIBLE
                itemView.detail.visibility = View.GONE
                itemView.coplapse.visibility = View.VISIBLE
            } else {
                itemView.findViewById<RecyclerView>(R.id.rcv_rating).visibility = View.GONE
                itemView.detail.visibility = View.VISIBLE
                itemView.coplapse.visibility = View.GONE
            }
        }

        //image
        val imageAdapter = HorizontalImageAdapter()
        itemView.rcv_image_review.adapter = imageAdapter
        imageAdapter.clearData()
        itemView.rcv_image_review.layoutManager = LinearLayoutManager(listener.mContext,
                LinearLayoutManager.HORIZONTAL, false)
        val listImg = mutableListOf<String>()
        if (!obj.imageThumbs.isNullOrEmpty()) {
            listImg.clear()
            for (i in obj.imageThumbs.indices) {
                listImg.add(obj.imageThumbs[i].thumbnail!!)
            }
            imageAdapter.setData(listImg)
        }

        //comment
        itemView.rcv_comment_review.layoutManager = LinearLayoutManager(view.context)
        commentAdapter = CommentReviewAdapter(adapterPosition, listener)
        commentAdapter?.clearData()
        itemView.rcv_comment_review.adapter = commentAdapter

        if (!obj.comments.isNullOrEmpty()) {
            commentAdapter?.setData(obj.comments as MutableList<ICProductReviews.Comments>, obj.commentCounts)
            commentAdapter?.setCommentID(obj.id)
        }

        itemView.img_make_commend.setOnClickListener {
            obj.owner.name?.let { it1 ->
                listener.onClickCreateComment(adapterPosition, it1, obj.id)
            }
        }

    }

    fun addListComment(list: MutableList<ICProductReviews.Comments>) {
        commentAdapter?.addList(list)
    }

    fun insertComment(obj: ICProductReviews.Comments) {
        commentAdapter?.addComment(obj)
    }

    fun hideShowMoreComment(positionShowComment:Int){
        val holder = itemView.rcv_comment_review.findViewHolderForAdapterPosition(positionShowComment)
        if (holder != null && holder is CommentReviewAdapter.CommentHolder) {
            holder.hideShowMoreComment(positionShowComment)
        }
    }

}