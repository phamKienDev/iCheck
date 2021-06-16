package vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.databinding.ItemWrongStampBinding
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.SizeHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICStampConfig

class ICWrongStampHolder(parent: ViewGroup, val binding: ItemWrongStampBinding =
        ItemWrongStampBinding.inflate(LayoutInflater.from(parent.context), parent, false)) :
        BaseViewHolder<ICStampConfig>(binding.root) {

    override fun bind(obj: ICStampConfig) {
        binding.tvMessage.apply {
            background = ViewHelper.createShapeDrawable(Constant.getAccentRedColor(itemView.context), SizeHelper.size4.toFloat())
            text = context.getString(R.string.canh_bao_uppercase_down_xxx, obj.errorMessage ?: context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        }

        binding.recyclerView.apply {
            adapter = ICStampContactAdapter(obj.contacts ?: mutableListOf())
        }
    }
}