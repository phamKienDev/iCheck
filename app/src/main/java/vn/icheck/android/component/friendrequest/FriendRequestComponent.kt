package vn.icheck.android.component.friendrequest

import  android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.models.ICSearchUser
import vn.icheck.android.screen.user.listnotification.friendrequest.ListFriendRequestActivity
import vn.icheck.android.util.kotlin.ActivityUtils

class FriendRequestComponent(parent: ViewGroup): BaseViewHolder<ICListResponse<ICSearchUser>>(createView(parent.context)) {
    private val friendRequestAdapter = FriendRequestAdapter()

    override fun bind(obj: ICListResponse<ICSearchUser>) {
        // Layout parent
        (itemView as ViewGroup).apply {

            // Layout title
            (getChildAt(0) as ViewGroup).apply {

                // Text title
                (getChildAt(0) as AppCompatTextView).apply {
                    text = context.getString(R.string.loi_moi_ket_ban_s, "(${obj.count})")
                }

                // Text view more
                getChildAt(1).setOnClickListener {
                    ICheckApplication.currentActivity()?.let { activity ->
                        ActivityUtils.startActivity<ListFriendRequestActivity>(activity)
                    }
                }
            }

            // List
            (getChildAt(1) as RecyclerView).apply {
                adapter = friendRequestAdapter
                friendRequestAdapter.setData(obj.rows)

                val horizontalDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                horizontalDecoration.setDrawable(ShapeDrawable().apply {
                    paint.color = ContextCompat.getColor(context, vn.icheck.android.ichecklibs.R.color.grayF0)
                    intrinsicHeight = SizeHelper.size1
                })
                addItemDecoration(horizontalDecoration)
            }
        }
    }

    companion object {

        private fun createView(context: Context) : LinearLayout {
            return LinearLayout(context).also {layoutParent ->
                layoutParent.layoutParams = ViewHelper.createLayoutParams(0, SizeHelper.size10, 0, 0)
                layoutParent.orientation = LinearLayout.VERTICAL
                layoutParent.setBackgroundColor(vn.icheck.android.ichecklibs.ColorManager.getAppBackgroundWhiteColor(layoutParent.context))

                // ALayout title
                layoutParent.addView(LinearLayout(context).also {layoutTitle ->
                    layoutTitle.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, 0)
                    layoutTitle.orientation = LinearLayout.HORIZONTAL
                    layoutTitle.gravity = Gravity.CENTER_VERTICAL

                    val secondaryColor = vn.icheck.android.ichecklibs.ColorManager.getSecondaryColor(context)

                    layoutTitle.addView(ViewHelper.createText(context,
                            ViewHelper.createLayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f),
                            null,
                            ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                            secondaryColor,
                            18f
                    ).also {
                        it.setPadding(0, 0, SizeHelper.size12, 0)
                    })

                    layoutTitle.addView(ViewHelper.createText(context,
                            ViewHelper.createLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT),
                            ViewHelper.outValue.resourceId,
                            ViewHelper.createTypeface(context, R.font.barlow_semi_bold),
                            secondaryColor,
                            14f
                    ).also {
                        it.setPadding(0, SizeHelper.size6, 0, SizeHelper.size6)
                        it.setText(R.string.xem_tat_ca)
                    })
                })

                // List
                layoutParent.addView(RecyclerView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    layoutManager = LinearLayoutManager(context)
                })
            }
        }
    }
}