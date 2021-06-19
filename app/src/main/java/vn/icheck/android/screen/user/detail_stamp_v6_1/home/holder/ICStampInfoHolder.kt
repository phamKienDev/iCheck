package vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.ichecklibs.R
import vn.icheck.android.ichecklibs.SizeHelper
import vn.icheck.android.ichecklibs.view.TextHeader

class ICStampInfoHolder(parent: ViewGroup) : BaseViewHolder<String>(
        TextHeader(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT).apply {
                topMargin = SizeHelper.size10
            }
            setPadding(SizeHelper.size12, SizeHelper.size16, SizeHelper.size12, SizeHelper.size16)
            setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            gravity = Gravity.CENTER
            setBackgroundColor(Color.WHITE)
        }
) {

    override fun bind(obj: String) {
        (itemView as AppCompatTextView).text = itemView.context.getString(vn.icheck.android.R.string.serial_v2_xxx, obj)
    }
}