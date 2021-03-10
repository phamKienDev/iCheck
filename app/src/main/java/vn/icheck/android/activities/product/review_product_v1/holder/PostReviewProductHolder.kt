package vn.icheck.android.screen.user.review_product.holder

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.item_post_your_review_product.view.*
import vn.icheck.android.activities.product.review_product_v1.ReviewProductV1Activity
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICCriteria
import vn.icheck.android.screen.account.home.AccountActivity
import vn.icheck.android.activities.product.review_product_v1.adapter.PostImageCriteriaAdapter
import vn.icheck.android.activities.product.review_product_v1.view.IReviewProductView
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.criteria.CriteriaAdapter
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.criteria.CriteriaChild
import vn.icheck.android.util.kotlin.ToastUtils
import java.io.File

class PostReviewProductHolder(view: View, val listener: IReviewProductView) : BaseViewHolder<ICCriteria>(view) {
    val listImage = mutableListOf<File>()
    val imgAdapter = PostImageCriteriaAdapter(listener)

    override fun bind(obj: ICCriteria) {
        val listCriteriaChild = mutableListOf<CriteriaChild>()
        for (item in obj.productCriteriaSet!!) {
            listCriteriaChild.add(CriteriaChild(
                    item,
                    null,
                    false
            ))
        }
        val criteriaAdapter = CriteriaAdapter(listCriteriaChild, 1)
        criteriaAdapter.setAction(object : CriteriaAdapter.CriteriaAction {
            override fun onClickCriteria(listCriteria: List<CriteriaChild>) {
                listener.onProductReviewStartInsider()
            }
        })
        itemView.rcv_criteria.adapter = criteriaAdapter
        itemView.rcv_criteria.layoutManager = LinearLayoutManager(listener.mContext)

        itemView.img_camera_review.setOnClickListener {
            listener.pickPhotoCriteria()
            ReviewProductV1Activity.sendImageCriteria = true
        }

        //image
        itemView.rcv_image_criteria.adapter = imgAdapter
        itemView.rcv_image_criteria.layoutManager = LinearLayoutManager(listener.mContext, LinearLayoutManager.HORIZONTAL, false)

        //post review

        itemView.btn_post_review.setOnClickListener {
            var validate = true
            obj.productCriteriaSet?.let {
                val criteria = mutableListOf<HashMap<String, Any>>()
                for (i in it.indices) {
                    val item = hashMapOf<String, Any>()
                    item["criteria_id"] = it[i].criteria.id
                    if (listCriteriaChild[i].point != null && listCriteriaChild[i].point != 0F) {
                        item["point"] = listCriteriaChild[i].point!!
                    } else {
                        validate = false
                    }
                    criteria.add(item)
                }
                if (validate) {
                    if (SessionManager.isUserLogged) {
                        listener.onPostProductReview(itemView.edt_comment_review.text.toString(), criteria)
                    } else {
                        ReviewProductV1Activity.message = itemView.edt_comment_review.text.toString()
                        ReviewProductV1Activity.listYourCriteria = criteria
                        AccountActivity.start(listener.mContext)
                    }
                } else {
                    ToastUtils.showShortError(listener.mContext, "Vui lòng điển đầy đủ tiêu chí")
                }

            }
        }
    }

    fun createListImage(listImg: MutableList<File>) {
        listImage.clear()
        listImage.addAll(listImg)
        imgAdapter.setData(listImg)
    }

    fun deleteItemImage(pos: Int) {
        imgAdapter.deleteItem(pos)
    }
}