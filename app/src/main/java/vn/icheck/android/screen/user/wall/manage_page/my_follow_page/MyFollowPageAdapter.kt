package vn.icheck.android.screen.user.wall.manage_page.my_follow_page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_me_follow_page_holder.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.ICPage
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class MyFollowPageAdapter(val typeHome: Boolean, callback: IRecyclerViewCallback? = null) : RecyclerViewAdapter<ICPage>(callback) {
    var dialog: MyFollowPageDialog? = null

    fun dismissDialog() {
        dialog?.dismiss()
        dialog = null
    }

    fun deleteItem(pageId: Long) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i].id == pageId) {
                listData.removeAt(i)
                notifyItemRemoved(i)
            }
        }
    }

    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (holder is ViewHolder) {
            holder.bind(listData[position])
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICPage>(LayoutInflater.from(parent.context).inflate(R.layout.item_me_follow_page_holder, parent, false)) {
        override fun bind(obj: ICPage) {
            itemView.layoutParams = (itemView.layoutParams as RecyclerView.LayoutParams).apply {
                topMargin = if (typeHome)
                    SizeHelper.size10
                else
                    SizeHelper.size16
            }

            if (obj.isVerify) {
                itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_16px, 0)
            } else {
                itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            if (typeHome) {
                itemView.imgMore.beGone()
            } else {
                itemView.imgMore.beVisible()
            }

            WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.avatar, R.drawable.img_default_business_logo_big, R.drawable.img_default_business_logo_big)
            itemView.tvName.text = obj.name ?: ""
            itemView.tvCountFollow.text = if (obj.followCount ?: 0 > 0) {
                "${TextHelper.formatMoneyPhay(obj.followCount)} Người đang theo dõi"
            } else {
                itemView.context.getString(R.string.chua_co_nguoi_theo_doi)
            }

            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let {
                    ActivityUtils.startActivity<PageDetailActivity, Long>(it, Constant.DATA_1, obj.id!!)
                }
            }

            itemView.imgMore.setOnClickListener {
                obj.id?.let { id ->
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.SHOW_DIALOG_MY_FOLLOW_PAGE, id))
                }
            }
        }
    }
}