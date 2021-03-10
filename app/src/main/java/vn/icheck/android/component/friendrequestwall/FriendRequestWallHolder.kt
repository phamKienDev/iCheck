package vn.icheck.android.component.friendrequestwall

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.view.Gravity
import android.view.View
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
import vn.icheck.android.constant.SHOW_LIST_INVITATION
import vn.icheck.android.constant.USER_WALL_ADD_FRIEND
import vn.icheck.android.constant.USER_WALL_BROADCAST
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.models.ICSearchUser
import vn.icheck.android.screen.user.listnotification.friendrequest.ListFriendRequestActivity
import vn.icheck.android.util.ick.toPx
import vn.icheck.android.util.kotlin.ActivityUtils

class FriendRequestWallHolder(parent: ViewGroup) : BaseViewHolder<ICListResponse<ICSearchUser>>(createView(parent.context)) {
    private val friendRequestAdapter = FriendRequestWallAdapter()

    private var listener: View.OnClickListener? = null

    override fun bind(obj: ICListResponse<ICSearchUser>) {
        // Layout parent
        (itemView as ViewGroup).apply {

            // Layout title
            (getChildAt(0) as ViewGroup).apply {

                // Text title
                (getChildAt(0) as AppCompatTextView).apply {
                    text = context.getString(R.string.loi_moi_ket_ban_xxx, "(${obj.count})")
                }

                // Text view more
                getChildAt(1).setOnClickListener {
//                    ICheckApplication.currentActivity()?.let { activity ->
//                        ActivityUtils.startActivity<ListFriendRequestActivity>(activity)
//                    }
                    it.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                        putExtra(USER_WALL_BROADCAST, SHOW_LIST_INVITATION)
                    })
                }
            }

            // List
            (getChildAt(1) as RecyclerView).apply {
                adapter = friendRequestAdapter
                friendRequestAdapter.setData(obj.rows)

                friendRequestAdapter.setUpdateTitleListener(View.OnClickListener {
                    obj.count--
                    setTitle(obj.count)
                })

                friendRequestAdapter.setOnRemoveListener(View.OnClickListener {
                    listener?.onClick(null)
                })
            }
        }
    }

    fun setOnRemoveListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    private fun setTitle(count: Int) {
        (((itemView as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(0) as AppCompatTextView).text = itemView.context.getString(R.string.loi_moi_ket_ban_xxx, "($count)")
    }

    companion object {

        private fun createView(context: Context): LinearLayout {
            return LinearLayout(context).also { layoutParent ->
                layoutParent.layoutParams = ViewHelper.createLayoutParams(0, 0, 0, 10.toPx())
                layoutParent.orientation = LinearLayout.VERTICAL
                layoutParent.setBackgroundColor(Color.WHITE)

                // ALayout title
                layoutParent.addView(LinearLayout(context).also { layoutTitle ->
                    layoutTitle.layoutParams = ViewHelper.createLayoutParams()
                    layoutTitle.setPadding(SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, SizeHelper.size10)
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
                    setPadding(SizeHelper.size7, 0, SizeHelper.size7, 0)
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        bottomMargin = SizeHelper.size10
                    }
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                })
            }
        }
    }
}