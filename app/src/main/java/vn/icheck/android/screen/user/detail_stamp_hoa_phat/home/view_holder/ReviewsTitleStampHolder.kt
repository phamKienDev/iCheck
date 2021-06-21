package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.ctsp_dgsph_holder.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.activities.product.review_product_v1.ReviewProductV1Activity
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.util.ick.rText

class ReviewsTitleStampHolder(parent: ViewGroup) : BaseViewHolder<ICBarcodeProductV1>(LayoutInflater.from(parent.context).inflate(R.layout.ctsp_dgsph_holder, parent, false)) {

    override fun bind(obj: ICBarcodeProductV1) {
        itemView.tv_dgsp.rText(R.string.danh_gia_san_pham_x, obj.countReviews)

        itemView.tv_xtcrv.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                ReviewProductV1Activity.showReviews(obj.barcode, obj.id, activity)
            }
        }
    }
}