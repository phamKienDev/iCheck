package vn.icheck.android.screen.user.suggest_page

import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_suggest_follow_friend.view.*
import kotlinx.android.synthetic.main.item_suggest_item_page.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewCustomAdapter
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.models.ICSuggestPage
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.network.models.feed.ICAvatarOfFriend
import vn.icheck.android.screen.user.list_facebook_friend.ListFacebookFriendActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils


class SuggestPageAdapter(val callback: IRecyclerViewCallback) : RecyclerViewCustomAdapter<Any>(callback) {
    var listSelected = mutableListOf<ICSuggestPage>()
    val itemPageType = 3
    val friendsType = 4
    val titleType = 5

    fun addFriend(obj: ICListResponse<ICUser>) {
        if (listData.isNotEmpty()) {
            listData.add(0, obj)
        } else {
            listData.add(obj)
        }
        notifyDataSetChanged()
    }

    override fun getItemType(position: Int): Int {
        return if (listData[position] is ICSuggestPage) {
            itemPageType
        } else if (listData[position] is String) {
            titleType
        } else {
            friendsType
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (holder is ItemPageHolder) {
            holder.bind(listData[position] as ICSuggestPage)
        }

        if (holder is FriendHolder) {
            holder.bind(listData[position] as ICListResponse<ICUser>)
        }

    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            friendsType -> FriendHolder(parent)
            titleType -> TitleHolder(parent)
            else -> ItemPageHolder(parent)
        }
    }

    inner class FriendHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_suggest_follow_friend, parent, false)) {
        fun bind(obj: ICListResponse<ICUser>) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val listFriend = mutableListOf<String>()
                for (friend in obj.rows) {
                    listFriend.add(friend.avatar ?: "")
                }
                val icUser = ICAvatarOfFriend(listFriend, obj.count)
                itemView.img_list_avatar.bind(icUser, null, true, R.drawable.ic_avatar_default_84dp)
            }

            if (obj.count > 0) {
                itemView.tv_number.beVisible()
                itemView.tv_number.text = itemView.context.getString(R.string.d_ban_be_dang_su_dung_icheck, obj.count)
            } else {
                itemView.tv_number.beGone()
            }

            itemView.img_list_avatar.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    ActivityUtils.startActivity<ListFacebookFriendActivity>(activity)
                }
            }
        }
    }

    inner class TitleHolder(parent: ViewGroup) : RecyclerView.ViewHolder(createTitleView(parent))

    fun createTitleView(parent: ViewGroup): AppCompatTextView {
        return ViewHelper.createText(parent.context,
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(SizeHelper.size30, SizeHelper.size6, SizeHelper.size30, SizeHelper.size12)
                },
                null, ViewHelper.createTypeface(parent.context, R.font.barlow_medium),
                ColorManager.getSecondTextColor(parent.context), 14f).also {
            it.gravity = Gravity.CENTER
            it.includeFontPadding = false
            it.text = parent.context.getString(R.string.goi_y_page)
        }
    }


    inner class ItemPageHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_suggest_item_page, parent, false)) {
        fun bind(obj: ICSuggestPage) {
            WidgetUtils.loadImageUrl(itemView.img_logo_page, obj.avatar, R.drawable.ic_business_v2, R.drawable.ic_business_v2)

            itemView.img_verfified.visibility = if (obj.isVerify) {
                View.VISIBLE
            } else {
                View.GONE
            }

            itemView.tv_name_page.text = obj.name
                    ?: itemView.context.getString(R.string.dang_cap_nhat)

            itemView.tv_number_follow.text = if (obj.followCount > 0) {
                itemView.context.getString(R.string.s_nguoi_dang_theo_doi, TextHelper.formatMoneyPhay(obj.followCount.toLong()))
            } else {
                itemView.context.getString(R.string.chua_co_nguoi_theo_doi)

            }

            itemView.tv_follow.apply {
                background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(context)
                setOnClickListener {
                    if (!obj.selected) {
                        itemView.tv_follow.setTextColor(ColorManager.getSecondTextColor(itemView.context))
                        itemView.tv_follow.text = itemView.context.getString(R.string.dang_theo_doi)
                        itemView.tv_follow.background = vn.icheck.android.ichecklibs.ViewHelper.bgGrayCorners4(itemView.context)
                        listSelected.add(obj)
                    }
                }
            }
        }
    }

}