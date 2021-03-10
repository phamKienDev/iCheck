package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder

import android.view.LayoutInflater
import android.view.ViewGroup
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.activities.product.review_product_v1.ReviewProductV1Activity
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.v1.ICBarcodeProductV1

class NoReviewsStampHolder(parent: ViewGroup) : BaseViewHolder<ICBarcodeProductV1>(LayoutInflater.from(parent.context).inflate(R.layout.ctsp_noreview_holder, parent, false)) {

    override fun bind(obj: ICBarcodeProductV1) {
        itemView.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                ReviewProductV1Activity.showReviews(obj.barcode, obj.id, activity)
            }
        }
    }
}