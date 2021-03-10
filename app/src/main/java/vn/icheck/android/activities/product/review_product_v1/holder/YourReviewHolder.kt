package vn.icheck.android.screen.user.review_product.holder

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.item_your_review_product_v1.view.*
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICCriteria
import vn.icheck.android.base.adapter.HorizontalImageAdapter
import vn.icheck.android.activities.product.review_product_v1.view.IReviewProductView
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.criteria.CriteriaAdapter
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.criteria.CriteriaChild
import vn.icheck.android.util.text.ReviewPointText

class YourReviewHolder(view: View, val listener: IReviewProductView) : BaseViewHolder<ICCriteria>(view) {
    override fun bind(obj: ICCriteria) {
        itemView.tv_your_score.text = String.format("%.1f %s", obj.customerEvaluation!!.averagePoint * 2,
                ReviewPointText.getText(obj.customerEvaluation!!.averagePoint))
        itemView.customer_rating.rating = obj.customerEvaluation!!.averagePoint

        val listImg = mutableListOf<String>()
        if (!obj.customerEvaluation!!.imageThumbs.isNullOrEmpty()) {

            for (i in obj.customerEvaluation!!.imageThumbs.indices) {
                listImg.add(obj.customerEvaluation!!.imageThumbs[i].thumbnail!!)
            }
            val imageAdapter = HorizontalImageAdapter()
            itemView.rcv_image.adapter = imageAdapter
            itemView.rcv_image.layoutManager = LinearLayoutManager(listener.mContext,
                    LinearLayoutManager.HORIZONTAL, false)
            imageAdapter.setData(listImg)
        }
        val listCriteriaChild = mutableListOf<CriteriaChild>()
        for (item in obj.productCriteriaSet!!.indices) {

            if (obj.productCriteriaSet!![item].criteria.id == obj.customerEvaluation!!.customerCriteria[item].criteria_id
                    && obj.customerEvaluation!!.customerCriteria[item]!=null) {
                listCriteriaChild.add(CriteriaChild(
                        obj.productCriteriaSet!!.get(item),
                        obj.customerEvaluation!!.customerCriteria.get(item).point.toFloat(),
                        true
                ))
            } else {
                listCriteriaChild.add(CriteriaChild(
                        obj.productCriteriaSet!!.get(item),
                        null,
                        true
                ))
            }
        }
        val criteriaAdapter = CriteriaAdapter(listCriteriaChild, 1)
        itemView.rcv_customer.adapter = criteriaAdapter
        itemView.rcv_customer.layoutManager = LinearLayoutManager(listener.mContext)

        itemView.tv_msg_content.text = obj.customerEvaluation!!.message

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