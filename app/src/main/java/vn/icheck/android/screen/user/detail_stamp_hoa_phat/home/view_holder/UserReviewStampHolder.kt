package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.ctsp_customer_rv_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICCriteria
import vn.icheck.android.network.models.v1.ICImage
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.EditReviewImgV1Adapter
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.criteria.CriteriaAdapter
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.criteria.CriteriaChild
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back.SlideHeaderStampHoaPhatListener
import vn.icheck.android.util.ick.rText

class UserReviewStampHolder(parent: ViewGroup,val headerImagelistener: SlideHeaderStampHoaPhatListener) : BaseViewHolder<ICCriteria>(LayoutInflater.from(parent.context).inflate(R.layout.ctsp_customer_rv_holder, parent, false)) {

    private val listImg = mutableListOf<ICImage>()
    val listCriteriaChild = mutableListOf<CriteriaChild>()

    override fun bind(obj: ICCriteria) {
        listCriteriaChild.clear()
        listImg.clear()
        itemView.tv_your_score.rText(R.string.x_tuyet_voi, obj.customerEvaluation?.averagePoint!! * 2)
        itemView.customer_rating.rating = obj.customerEvaluation?.averagePoint!!
        if (obj.customerEvaluation?.imageThumbs!!.isNotEmpty()) {
            for (i in obj.customerEvaluation?.imageThumbs!!.indices) {
                listImg.add(ICImage(obj.customerEvaluation?.imageThumbs!![i].thumbnail))
            }
            val imgAdapter = EditReviewImgV1Adapter(listImg)
            itemView.findViewById<RecyclerView>(R.id.rcv_image).layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            itemView.findViewById<RecyclerView>(R.id.rcv_image).adapter = imgAdapter
        }

        val criteriaAdapter = CriteriaAdapter(listCriteriaChild, 1)
        obj.productCriteriaSet?.let {
            for (item in it.indices) {
                try {
                    if (it.get(item).criteria.id == obj.customerEvaluation?.customerCriteria!![item].criteria_id
                            && obj.customerEvaluation?.customerCriteria!![item] != null) {
                        listCriteriaChild.add(CriteriaChild(
                                it.get(item),
                                obj.customerEvaluation?.customerCriteria!!
                                        .get(item).point.toFloat(),
                                true
                        ))
                    } else {
                        listCriteriaChild.add(CriteriaChild(
                                it.get(item),
                                null,
                                true
                        ))
                    }
                } catch (e: Exception) {
                }
            }
            itemView.findViewById<RecyclerView>(R.id.rcv_customer).layoutManager = LinearLayoutManager(itemView.context)
            itemView.findViewById<RecyclerView>(R.id.rcv_customer).adapter = criteriaAdapter
            itemView.findViewById<TextView>(R.id.tv_xem_chi_tiet).setOnClickListener {
                itemView.findViewById<ViewGroup>(R.id.container_detail_review).visibility = View.VISIBLE
                itemView.findViewById<TextView>(R.id.tv_xem_chi_tiet).visibility = View.INVISIBLE
            }
            itemView.findViewById<TextView>(R.id.tv_collapse_drv).setOnClickListener {
                itemView.findViewById<ViewGroup>(R.id.container_detail_review).visibility = View.GONE
                itemView.findViewById<TextView>(R.id.tv_xem_chi_tiet).visibility = View.VISIBLE
            }
        }
        itemView.findViewById<TextView>(R.id.tv_msg_content).text = obj.customerEvaluation?.message
//            view.tv_xct.setOnClickListener {
//                ProductDetailActivity.INSTANCE?.showAllReviews()
//            }
        itemView.findViewById<Button>(R.id.btn_edit).setOnClickListener {
            obj.let { objCriteria ->
                headerImagelistener.editReview(objCriteria)
            }
        }
    }

}