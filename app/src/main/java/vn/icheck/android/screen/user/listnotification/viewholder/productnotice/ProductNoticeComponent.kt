package vn.icheck.android.screen.user.listnotification.viewholder.productnotice

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.screen.user.listnotification.productnotice.ListProductNoticeActivity
import vn.icheck.android.util.kotlin.ActivityUtils

class ProductNoticeComponent(parent: ViewGroup) : BaseViewHolder<MutableList<ICNotification>>(createView(parent.context)) {
    private val friendRequestAdapter = ProductNoticeAdapter()

    private var listener: View.OnClickListener? = null

    override fun bind(obj: MutableList<ICNotification>) {
        // Layout parent
        (itemView as ViewGroup).apply {

            // Layout title
            (getChildAt(0) as ViewGroup).apply {

                // Text title
                (getChildAt(0) as AppCompatTextView).apply {
                    text = context.getString(R.string.san_pham_moi)
                }

                // Text view more
                getChildAt(1).setOnClickListener {
                    ICheckApplication.currentActivity()?.let { activity ->
                        ActivityUtils.startActivity<ListProductNoticeActivity>(activity)
                    }
                }
            }

            // List
            (getChildAt(1) as RecyclerView).apply {
                adapter = friendRequestAdapter
                friendRequestAdapter.setData(obj)

                friendRequestAdapter.setOnRemoveListener(View.OnClickListener {
                    listener?.onClick(null)
                })
            }
        }
    }

    fun setOnRemoveListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    companion object {

        private fun createView(context: Context): LinearLayout {
            return LinearLayout(context).also { layoutParent ->
                layoutParent.layoutParams = ViewHelper.createLayoutParams(0, SizeHelper.size5, 0, SizeHelper.size5)
                layoutParent.orientation = LinearLayout.VERTICAL
                layoutParent.setBackgroundColor(Color.WHITE)

                // ALayout title
                layoutParent.addView(LinearLayout(context).also { layoutTitle ->
                    layoutTitle.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, 0)
                    layoutTitle.orientation = LinearLayout.HORIZONTAL
                    layoutTitle.gravity = Gravity.CENTER_VERTICAL

                    layoutTitle.addView(ViewHelper.createText(context,
                            ViewHelper.createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f),
                            null,
                            ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                            ContextCompat.getColor(context, R.color.blue),
                            18f
                    ).also {
                        it.setPadding(0, 0, SizeHelper.size12, 0)
                    })

                    layoutTitle.addView(ViewHelper.createText(context,
                            ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT),
                            ViewHelper.outValue.resourceId,
                            ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                            ContextCompat.getColor(context, R.color.blue),
                            14f
                    ).also {
                        it.setPadding(0, SizeHelper.size6, 0, SizeHelper.size6)
                        it.setText(R.string.xem_tat_ca)
                    })
                })

                // List
                layoutParent.addView(RecyclerView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        topMargin = SizeHelper.size10
                    }
                    layoutManager = LinearLayoutManager(context)
                })
            }
        }
    }
}