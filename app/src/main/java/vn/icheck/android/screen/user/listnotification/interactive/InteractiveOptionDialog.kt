package vn.icheck.android.screen.user.listnotification.interactive

import android.content.Context
import android.text.Html
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_interactive_option.*
import kotlinx.android.synthetic.main.item_interactive.view.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.kotlin.WidgetUtils

abstract class InteractiveOptionDialog(context: Context) : BaseBottomSheetDialog(context, R.layout.dialog_interactive_option, true) {

    fun show(obj: ICNotification, type: String? = null) {
        WidgetUtils.loadImageUrl(dialog.imgAvatar, obj.sourceUser?.firstOrNull()?.avatar, R.drawable.ic_user_svg)

        dialog.tvRank.apply {
            if (obj.sourceUser?.firstOrNull()?.entity == "USER") {
                beVisible()
                when (obj.sourceUser?.firstOrNull()?.rank?.level) {
                    Constant.USER_LEVEL_SILVER -> {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_avatar_rank_silver_16dp, 0)
                    }
                    Constant.USER_LEVEL_GOLD -> {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_avatar_rank_gold_16dp, 0)
                    }
                    Constant.USER_LEVEL_DIAMOND -> {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_avatar_rank_diamond_16dp, 0)
                    }
                    else -> {
                        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_avatar_rank_standard_16dp, 0)
                    }
                }
            } else {
                beGone()
            }

        }

        val name = obj.sourceUser?.firstOrNull()?.name
        dialog.tvTitle.text = obj.message ?: obj.description
        when (obj.action) {
            "INVITE_FOLLOW", "ADD_PRODUCT", "INVITE_FRIEND", "post_follow_added" -> {
                dialog.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notification_follow_20dp, 0)
            }
            "LIKE_REVIEW", "post_like_added","LIKE" -> {
                dialog.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notification_like_20dp, 0)
            }
            "COMMENT_REVIEW", "COMMENT_REVIEW_MANY", "post_comment_added","COMMENT" -> {
                dialog.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notification_comment_20dp, 0)
            }
            else -> {
                dialog.tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notification_follow_20dp, 0)
            }
        }

        dialog.layoutContent.apply {
            getChildAt(0).apply {
                if (obj.status == 1 || obj.isReaded == true) {
                    visibility = View.GONE
                }

                setOnClickListener {
                    dialog.dismiss()
                    onRead()
                }
            }

            getChildAt(1).apply {
                if (obj.isTurnOff == true) {
                    if (this is TextView) {
                        this rText R.string.bat_thong_bao_cho_doi_tuong_nay
                        this.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_turn_on_notification_40dp,0,0,0)
                    }
                } else {
                    if (this is TextView) {
                        this rText R.string.tat_thong_bao_cho_doi_tuong_nay
                        this.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_turn_off_notification_40dp,0,0,0)
                    }
                }
                setOnClickListener {
                    dialog.dismiss()
                    onTurnOffNotification()
                }
            }

            getChildAt(2).setOnClickListener {
                dialog.dismiss()
                onRemove()
            }
        }

        dialog.show()
    }

    protected abstract fun onRead()
    protected abstract fun onTurnOffNotification()
    protected abstract fun onRemove()
}