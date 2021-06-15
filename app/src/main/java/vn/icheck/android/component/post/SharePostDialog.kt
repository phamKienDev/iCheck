package vn.icheck.android.component.post

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.dialog_post_share.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.*
import vn.icheck.android.screen.user.share_post_of_page.bottom_sheet_option_page.OptionPageShare
import vn.icheck.android.screen.user.share_post_of_page.SharePostActivity
import vn.icheck.android.screen.user.share_post_of_page.bottom_sheet_chat_share.ShareViaChat
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.kotlin.ToastUtils

class SharePostDialog(context: Context) : BaseBottomSheetDialog(context, R.layout.dialog_post_share, true) {
    private val interaction = PageRepository()

    fun show(obj: ICPost, user: ICUser) {
        dialog.layoutShareMyPage.setOnClickListener {
            val intent = Intent(dialog.context, SharePostActivity::class.java)
            intent.putExtra(Constant.DATA_1, obj)
            intent.putExtra(Constant.DATA_2, user)
            dialog.context.startActivity(intent)
            dialog.dismiss()
        }

        dialog.layoutSharePage.setOnClickListener {
            sharePageUserManager(obj,user)
        }

        dialog.layoutShareChat.setOnClickListener {
            ICheckApplication.currentActivity().let { activity ->
                if (activity is FragmentActivity) {
                    ShareViaChat(obj).apply {
                        show(activity.supportFragmentManager, tag)
                    }
                    dialog.dismiss()
                }
            }
        }

        dialog.layoutShareMore.setOnClickListener {
            shareMore(obj)
        }

        dialog.show()
    }

    private fun sharePageUserManager(post: ICPost,user: ICUser) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                ToastUtils.showLongError(activity, activity rText R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            interaction.getPageUserManager(user.id, object : ICNewApiListener<ICResponse<ICListResponse<ICPageUserManager>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICPageUserManager>>) {
                    DialogHelper.closeLoading(activity)
                    if (!obj.data?.rows.isNullOrEmpty()) {
                        dialog.dismiss()
                        OptionPageShare(dialog.context).show(obj.data?.rows!!, user,post)
                    } else {
                        ToastUtils.showLongError(activity, activity rText R.string.ban_chua_co_page_nao)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    ToastUtils.showLongError(activity, error?.message ?: activity rText R.string.co_loi_xay_ra_vui_long_thu_lai)
                }
            })
        }
    }

    private fun shareMore(obj: ICPost) {
        ICheckApplication.currentActivity()?.let { activity ->
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                ToastUtils.showLongError(activity, activity rText R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }

            DialogHelper.showLoading(activity)

            interaction.getShareLinkOfPost(obj.id, object : ICNewApiListener<ICResponse<String>> {
                override fun onSuccess(obj: ICResponse<String>) {
                    DialogHelper.closeLoading(activity)

                    dialog.dismiss()
                    obj.data?.let { shareLink ->
                        val share = Intent.createChooser(Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareLink)
                            putExtra(Intent.EXTRA_TITLE, activity rText R.string.chia_se)
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }, null)
                        activity.startActivity(share)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    DialogHelper.closeLoading(activity)
                    val message = error?.message ?: activity rText R.string.co_loi_xay_ra_vui_long_thu_lai
                    ToastUtils.showLongError(activity, message)
                }
            })
        }
    }
}