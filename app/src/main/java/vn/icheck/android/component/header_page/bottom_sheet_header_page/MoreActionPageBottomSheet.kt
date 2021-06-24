package vn.icheck.android.component.header_page.bottom_sheet_header_page

import android.content.Context
import kotlinx.android.synthetic.main.dialog_more_action_page.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.network.models.ICPageOverview

abstract class MoreActionPageBottomSheet(context: Context, val obj: ICPageOverview) : BaseBottomSheetDialog(context, R.layout.dialog_more_action_page, true) {

    fun show() {
        if (obj.isFollow) {
            dialog.tvFollow.apply {
                text = context.getString(R.string.bo_theo_doi_trang_nay)
            }
            dialog.tvSubUnfollow?.apply {
                text = context.getString(R.string.khi_bo_theo_doi_cac_bai_viet_cua_trang_nay_se_khong_hien_thi_voi_ban_nua, obj.name)
            }
        } else {
            dialog.tvFollow.apply {
                text = context.getString(R.string.theo_doi_trang_nay)
            }
            dialog.tvSubUnfollow.apply {
                text = context.getString(R.string.khi_theo_doi_cac_bai_viet_cua_trang_se_hien_day_du_voi_ban, obj.name)
            }
        }

        if (!obj.unsubscribeNotice) {
            dialog.tv_notify.apply {
                text = context.getString(R.string.bat_thong_bao_cho_trang_nay)
            }
            dialog.tvNotificationOn.apply {
                text = context.getString(R.string.ban_se_nhan_duoc_thong_bao_khi_trang_dang_noi_dung_moi, obj.name)
            }
            dialog.imgNotification.setImageResource(R.drawable.ic_turn_on_notification_40px)
        } else {
            dialog.tv_notify.apply {
                text = context.getString(R.string.tat_thong_bao_cho_trang_nay)
            }
            dialog.tvNotificationOn.apply {
                text = context.getString(R.string.ban_se_khong_nhan_duoc_thong_bao_khi_s_dang_noi_dung_moi, obj.name)
            }
            dialog.imgNotification.setImageResource(R.drawable.ic_turn_off_notification_40dp)
        }

        dialog.layoutUnfollow.setOnClickListener {
            dialog.dismiss()
            onClickUnfollow()
        }

        dialog.layoutStateNotification.setOnClickListener {
            dialog.dismiss()
            onClickStateNotification()
        }

        dialog.layoutReport.setOnClickListener {
            dialog.dismiss()
            onClickReportPage()
        }

        dialog.show()
    }

    protected abstract fun onClickUnfollow()
    protected abstract fun onClickStateNotification()
    protected abstract fun onClickReportPage()
}