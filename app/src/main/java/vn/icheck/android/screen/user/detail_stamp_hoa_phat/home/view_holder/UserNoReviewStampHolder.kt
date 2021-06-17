package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.stamp_product_not_rated_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICCriteria

class UserNoReviewStampHolder (parent:ViewGroup) : BaseViewHolder<ICCriteria>(LayoutInflater.from(parent.context).inflate(R.layout.stamp_product_not_rated_holder, parent, false))  {
    override fun bind(obj: ICCriteria) {
        itemView.btn_nrv_send.background=ViewHelper.bgSecondaryCorners40(itemView.context)
        itemView.edt_nrv_comment.background=ViewHelper.bgTransparentStrokeLineColor1Corners10(itemView.context)
    }
}