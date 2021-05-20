package vn.icheck.android.component.space

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper

class SpacingHolder(parent: ViewGroup): BaseViewHolder<Int>(createView(parent.context)) {

    override fun bind(obj: Int) {
        (itemView).run {
            layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, obj)
        }
    }

    companion object {
        private fun createView(context: Context) : View {
            return View(context).also {
                it.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.size10)
                it.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackgroundGra1y))
            }
        }
    }
}