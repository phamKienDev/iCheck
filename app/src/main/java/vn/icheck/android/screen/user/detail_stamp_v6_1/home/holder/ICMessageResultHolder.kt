package vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.databinding.ItemMessageResultBinding
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.util.beGone
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.network.models.ICWidgetData

class ICMessageResultHolder(parent: ViewGroup, val binding: ItemMessageResultBinding = ItemMessageResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)): BaseViewHolder<ICWidgetData>(binding.root) {

    override fun bind(obj: ICWidgetData) {
        when (obj.success) {
            0 -> {
                binding.container.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorAccentRed))
                binding.tvTitle.apply {
                    beVisible()
                    setText(R.string.canh_bao_uppercase)
                }
                binding.tvContent.apply {
                    setTextColor(getColor(R.color.white))
                    text = obj.text
                }
                binding.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_result_error, 0)
            }
            1 -> {
                binding.container.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.green2))
                binding.tvTitle.apply {
                    beVisible()
                    setText(R.string.san_pham_chinh_hang_uppercase)
                }
                binding.tvContent.apply {
                    setTextColor(getColor(R.color.white))
                    text = obj.text
                }
                binding.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_message_result_success, 0)
            }
            else -> {
                binding.container.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorBackgroundGray))
                binding.tvTitle.beGone()
                binding.tvContent.apply {
                    setTextColor(Constant.getSecondTextColor(itemView.context))
                    text = obj.text
                }
                binding.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }
}