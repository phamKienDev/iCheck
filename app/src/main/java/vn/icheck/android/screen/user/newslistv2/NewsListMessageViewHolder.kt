package vn.icheck.android.screen.user.newslistv2

import android.view.View
import kotlinx.android.synthetic.main.item_message.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder


class NewsListMessageViewHolder(view: View) : BaseViewHolder<String>(view) {
    override fun bind(obj: String) {
        if (obj.isEmpty()) {
            itemView.imgIcon.setImageResource(R.drawable.ic_default_gift)
            itemView.txtMessage.setText(R.string.khong_co_tin_tuc_nao)
        } else {
            when (obj) {
                itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai) -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_network)
                    itemView.txtMessage.text = obj
                }
                itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai) -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_request)
                    itemView.txtMessage.text = obj
                }
                else -> {
                    itemView.txtMessage.text = obj
                }
            }
        }
    }
}