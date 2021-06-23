package vn.icheck.android.component.commentpost

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.dialog_comment_post_option.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICCommentPost
import vn.icheck.android.network.models.ICProductQuestion
import vn.icheck.android.util.kotlin.ToastUtils

abstract class CommentPostOptionDialog(val context: Context) : BaseBottomSheetDialog(context, R.layout.dialog_comment_post_option, true) {

    fun show( obj: ICCommentPost) {
        if (!obj.isReply) {
            dialog.tvAnswer.visibility = View.GONE
        }

        if (obj.user?.id == SessionManager.session.user?.id) {
            if (obj.content.isNullOrEmpty()) {
                dialog.tvEdit.visibility = View.GONE
            }
        } else {
            dialog.tvEdit.visibility = View.GONE
            dialog.tvDelete.visibility = View.GONE
        }

        if (obj.content.isNullOrEmpty()) {
            dialog.tvCopy.visibility = View.GONE
            dialog.tvEdit.visibility = View.GONE
        }

        dialog.tvAnswer.setOnClickListener {
            dialog.dismiss()
            onAnswer()
        }

        dialog.tvEdit.setOnClickListener {
            dialog.dismiss()
            onEdit()
        }

        dialog.tvDelete.setOnClickListener {
            dialog.dismiss()
            onDelete()
        }

        dialog.tvCopy.setOnClickListener {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("simple text", obj.content)
            clipboard.setPrimaryClip(clip)
            dialog.dismiss()
            ICheckApplication.currentActivity()?.let {
                DialogHelper.showDialogSuccessBlack(it,it.getString(R.string.sao_chep_thanh_cong))
            }
        }

        dialog.show()
    }

    fun show(obj: ICProductQuestion) {
        if (!obj.isReply) {
            dialog.tvAnswer.visibility = View.GONE
        }

        if (obj.user?.id == SessionManager.session.user?.id) {
            if (obj.content.isNullOrEmpty()) {
                dialog.tvEdit.visibility = View.GONE
            }
        } else {
            dialog.tvEdit.visibility = View.GONE
            dialog.tvDelete.visibility = View.GONE
        }

        if (obj.content.isNullOrEmpty()) {
            dialog.tvCopy.visibility = View.GONE
        }

        dialog.tvAnswer.setOnClickListener {
            dialog.dismiss()
            onAnswer()
        }

        dialog.tvEdit.setOnClickListener {
            dialog.dismiss()
            onEdit()
        }

        dialog.tvDelete.setOnClickListener {
            dialog.dismiss()
            onDelete()
        }

        dialog.tvCopy.setOnClickListener {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("simple text", obj.content)
            clipboard.setPrimaryClip(clip)
            ToastUtils.showLongSuccess(context, R.string.da_copy_vao_bo_nho_dem)
            dialog.dismiss()
        }

        dialog.show()
    }

    protected abstract fun onAnswer()
    protected abstract fun onEdit()
    protected abstract fun onDelete()
}