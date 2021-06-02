package vn.icheck.android.screen.user.listnotification.viewholder.pagerelated

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.models.ICNotificationPage

class NotificationPageComponent(parent: ViewGroup) : BaseViewHolder<MutableList<ICNotificationPage>>(createView(parent.context)) {
    private val friendRequestAdapter = NotificationPageAdapter()

    override fun bind(obj: MutableList<ICNotificationPage>) {
        // Layout parent
        (itemView as ViewGroup).apply {

            // List
            (getChildAt(1) as RecyclerView).apply {
                adapter = friendRequestAdapter
                friendRequestAdapter.setData(obj)

                val horizontalDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                horizontalDecoration.setDrawable(ShapeDrawable().apply {
                    paint.setColor(ContextCompat.getColor(context, R.color.colorBackgroundGray))
                    intrinsicHeight = SizeHelper.size1
                })
                addItemDecoration(horizontalDecoration)
            }
        }
    }

    companion object {

        private fun createView(context: Context): LinearLayout {
            return LinearLayout(context).also { layoutParent ->
                layoutParent.layoutParams = ViewHelper.createLayoutParams(0, SizeHelper.size5, 0, SizeHelper.size5)
                layoutParent.orientation = LinearLayout.VERTICAL
                layoutParent.setBackgroundColor(Constant.getAppBackgroundWhiteColor(layoutParent.context))

                // Text title
                layoutParent.addView(ViewHelper.createText(context,
                        ViewHelper.createLayoutParams(SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, 0),
                        null,
                        ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                        vn.icheck.android.ichecklibs.Constant.getSecondaryColor(context),
                        18f
                ).also {
                    it.setPadding(0, 0, SizeHelper.size12, 0)
                    it.setText(R.string.goi_y_page_theo_doi)
                })

                // List
                layoutParent.addView(RecyclerView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        topMargin = SizeHelper.size10
                    }
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    setPadding(SizeHelper.size5, 0, SizeHelper.size5, 0)
                    clipToPadding = false
                })
            }
        }
    }
}