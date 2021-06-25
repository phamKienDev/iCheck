package vn.icheck.android.screen.user.review_product.holder

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.item_your_review_product_v1.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICCriteria
import vn.icheck.android.base.adapter.HorizontalImageAdapter
import vn.icheck.android.activities.product.review_product_v1.view.IReviewProductView
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.criteria.CriteriaAdapter
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.criteria.CriteriaChild
import vn.icheck.android.util.text.ReviewPointText

class YourReviewHolder(view: View, val listener: IReviewProductView) : BaseViewHolder<ICCriteria>(view) {
    override fun bind(obj: ICCriteria) {
        itemView.btn_edit.background=ViewHelper.bgSecondaryCorners40(itemView.context)
        obj.customerEvaluation?.let {
            itemView.tv_your_score.apply {
                text = context.getString(R.string.your_score, it.averagePoint * 2,
                    ReviewPointText.getText(it.averagePoint))
            }
            itemView.customer_rating.rating = it.averagePoint

            val listImg = mutableListOf<String>()
            if (!it.imageThumbs.isNullOrEmpty()) {

                for (i in it.imageThumbs.indices) {
                    listImg.add(it.imageThumbs[i].thumbnail!!)
                }
                val imageAdapter = HorizontalImageAdapter()
                itemView.rcv_image.adapter = imageAdapter
                itemView.rcv_image.layoutManager = LinearLayoutManager(listener.mContext,
                    LinearLayoutManager.HORIZONTAL, false)
                imageAdapter.setData(listImg)
            }
            val listCriteriaChild = mutableListOf<CriteriaChild>()
            obj.productCriteriaSet?.let { productCriteriaSet->
                for (item in productCriteriaSet.indices) {

                    if (productCriteriaSet[item].criteria.id == it.customerCriteria[item].criteria_id) {
                        listCriteriaChild.add(CriteriaChild(
                            productCriteriaSet[item],
                            it.customerCriteria[item].point.toFloat(),
                            true
                        ))
                    } else {
                        listCriteriaChild.add(CriteriaChild(
                            productCriteriaSet[item],
                            null,
                            true
                        ))
                    }
                }
            }
            val criteriaAdapter = CriteriaAdapter(listCriteriaChild, 1)
            itemView.rcv_customer.adapter = criteriaAdapter
            itemView.rcv_customer.layoutManager = LinearLayoutManager(listener.mContext)

            itemView.tv_msg_content.text = it.message
        }

        itemView.tv_xem_chi_tiet.setOnClickListener {
            itemView.container_detail_review.visibility = View.VISIBLE
            itemView.tv_xem_chi_tiet.visibility = View.GONE
        }
        itemView.tv_collapse_drv.setOnClickListener {
            itemView.container_detail_review.visibility = View.GONE
            itemView.tv_xem_chi_tiet.visibility = View.VISIBLE
        }

        itemView.btn_edit.setOnClickListener {
            listener.onClickEditReview(obj)
        }
    }
}