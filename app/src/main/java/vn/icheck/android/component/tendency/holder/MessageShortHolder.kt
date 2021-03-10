package vn.icheck.android.component.tendency.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_message_short.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.util.kotlin.ToastUtils

class MessageShortHolder(parent: ViewGroup, val imgEmpty: Int) : BaseViewHolder<String>(LayoutInflater.from(parent.context).inflate(R.layout.item_message_short, parent, false)) {

    override fun bind(obj: String) {
        if (obj.isEmpty()) {
            itemView.txtMessage.text = "Không có dữ liệu. Vui lòng thử lại!!!"
            itemView.imgError.setImageResource(imgEmpty)
        } else {
            itemView.txtMessage.text = obj

            when (obj) {
                itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai) -> {
                    itemView.imgError.setImageResource(R.drawable.ic_error_network)
                }
                itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai) -> {
                    itemView.imgError.setImageResource(R.drawable.ic_error_request)
                }
            }
        }

        itemView.setOnClickListener {
            ToastUtils.showLongWarning(itemView.context, "onClickTryAgain")
        }
    }
}