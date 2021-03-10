package vn.icheck.android.component.product.`null`

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.error_request.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder

class ErrorProductHolder(parent: ViewGroup) : BaseViewHolder<ErrorProductModel>(LayoutInflater.from(parent.context).inflate(R.layout.error_request,parent,false)) {

    override fun bind(obj: ErrorProductModel) {
        itemView.imgError.setImageResource(obj.error)
        itemView.tvMessageError.setText(obj.message)
    }
}