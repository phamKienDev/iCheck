package vn.icheck.android.component.tendency.holder

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.RelationshipHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICPageTrend
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class TopTendencyBusinessHolder(parent: ViewGroup) : BaseViewHolder<ICPageTrend>(ViewHelper.createItemBusinessTopTendency(parent.context)) {
    override fun bind(obj: ICPageTrend) {
        val parent = itemView as LinearLayout
        val img = parent.getChildAt(0) as AppCompatImageView
        val constraintLayout = parent.getChildAt(1) as ConstraintLayout
        val imgAvatar = constraintLayout.getChildAt(0) as CircleImageView
        val tvName = constraintLayout.getChildAt(1) as AppCompatTextView
        val tvFollow = constraintLayout.getChildAt(2) as AppCompatTextView
        val btnFollow = constraintLayout.getChildAt(3) as AppCompatTextView

        WidgetUtils.loadImageUrlRounded(img, obj.cover, SizeHelper.size4)
        WidgetUtils.loadImageUrl(imgAvatar, obj.avatar)

        checkFollow(btnFollow, obj.isFollow)

        if (!obj.name.isNullOrEmpty()) {
            tvName.text = obj.name
        } else {
            tvName.text = itemView.context.getString(R.string.dang_cap_nhat)
        }

        if (obj.follower != null && obj.follower != 0) {
            if (obj.follower!! > 1000) {
                tvFollow.text = "${((obj.follower)!! / 1000f).toString().replace(".0", "")}k người theo dõi"
            } else {
                tvFollow.text = "${obj.follower} người theo dõi"
            }
        }

        btnFollow.setOnClickListener {
            initListener(btnFollow, obj)
        }

        itemView.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                ActivityUtils.startActivity<PageDetailActivity, Long>(activity, Constant.DATA_1, obj.id)
            }
        }

//        when (type) {
//            "Doanh nghiệp" -> {
//                WidgetUtils.loadImageUrlRounded(img, obj.cover, SizeHelper.size4)
//                WidgetUtils.loadImageUrl(imgAvatar, obj.avatar)
//
//                checkFollow(btnFollow, obj.isFollow)
//
//                if (!obj.name.isNullOrEmpty()) {
//                    tvName.text = obj.name
//                } else {
//                    tvName.text = itemView.context.getString(R.string.dang_cap_nhat)
//                }
//
//                if (obj.follower != null && obj.follower != 0) {
//                    if (obj.follower!! > 1000) {
//                        tvFollow.text = "${((obj.follower)!! / 1000f).toString().replace(".0", "")}k người theo dõi"
//                    } else {
//                        tvFollow.text = "${obj.follower} người theo dõi"
//                    }
//                }
//
//                itemView.setOnClickListener {
//                    ICheckApplication.currentActivity()?.let { act ->
//                        ActivityUtils.startActivity<PageDetailActivity, Long>(act, Constant.DATA_1, 1)
//                    }
//                }
//
//                btnFollow.setOnClickListener {
//                    initListener(btnFollow, obj)
//                }
//            }
//            "Chuyên gia" -> {
//
//            }
//        }
    }

    private fun checkFollow(tvFollow: AppCompatTextView, isFollow: Boolean) {
        // Text follow
        tvFollow.run {
            if (isFollow) {
                text = "Đang theo dõi"
                background = ContextCompat.getDrawable(context, R.drawable.bg_gray_f0_corners_4)
                setTextColor(ContextCompat.getColor(itemView.context, R.color.colorSecondText))
            } else {
                text = "Theo dõi"
                background = ContextCompat.getDrawable(context, R.drawable.bg_corners_4_light_blue_solid)
                setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            }
        }
    }

    private fun followPage(tvFollow: AppCompatTextView, pageID: Long, isFollow: Boolean, obj: ICPageTrend) {
        RelationshipHelper.postFollowPage(pageID, object : RelationshipHelper.ClickFollowPage {
            override fun onClickFollowPage() {
                obj.isFollow = !isFollow
                checkFollow(tvFollow, !isFollow)
            }
        })
    }

    fun initListener(tvFollow: AppCompatTextView, obj: ICPageTrend) {
        if (obj.isFollow) {
            ICheckApplication.currentActivity()?.let { activity ->
                DialogHelper.showConfirm(activity,
                        activity.getString(R.string.bo_theo_doi_trang),
                        activity.getString(R.string.ban_chac_chan_bo_theo_doi_trang_xxx_chu, obj.name),
                        object : ConfirmDialogListener {
                            override fun onDisagree() {}

                            override fun onAgree() {
                                followPage(tvFollow, obj.id, obj.isFollow, obj)
                            }
                        })
            }
        } else {
            followPage(tvFollow, obj.id, obj.isFollow, obj)
        }
    }
}