package vn.icheck.android.helper

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.facebook.shimmer.ShimmerFrameLayout
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.accecp_ship_gift.DialogAcceptShipGift
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.callback.IScanBuyPopupListener
import vn.icheck.android.base.dialog.notify.callback.NotEnoughPointListener
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.dialog.notify.comfirm_da_lay_qua.DialogComfirmDaLayQua
import vn.icheck.android.base.dialog.notify.confirm.ConfirmDialog
import vn.icheck.android.base.dialog.notify.lock_card_pvcombank.DialogLockCardBank
import vn.icheck.android.base.dialog.notify.notification.NotificationDialog
import vn.icheck.android.base.dialog.notify.shaking.DialogEmtyBoxGift
import vn.icheck.android.base.dialog.reward_login.RewardLoginDialog
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICMission
import vn.icheck.android.screen.account.home.AccountActivity
import vn.icheck.android.screen.dialog.ScanBuyPopUp
import vn.icheck.android.util.ick.showSimpleErrorToast
import vn.icheck.android.util.ick.showSimpleSuccessToast
import vn.icheck.android.util.kotlin.ActivityUtils

object DialogHelper {

    fun showConfirm(context: Context?, message: String?, listener: ConfirmDialogListener) {
        showConfirm(context, null, message, null, null, true, listener)
    }

    fun showConfirm(context: Context?, title: String?, message: String?, listener: ConfirmDialogListener) {
        showConfirm(context, title, message, null, null, true, listener)
    }

    fun showConfirm(context: Context?, message: String?, isCancelable: Boolean, listener: ConfirmDialogListener) {
        showConfirm(context, null, message, null, null, isCancelable, listener)
    }

    fun showConfirm(context: Context?, title: String?, message: String?, isCancelable: Boolean, listener: ConfirmDialogListener) {
        showConfirm(context, title, message, null, null, isCancelable, listener)
    }

    fun showConfirm(context: Context?, title: String?, message: String?, disagree: String?, agree: String?, isCancelable: Boolean, listener: ConfirmDialogListener) {
        context?.let {
            object : ConfirmDialog(it, title, message, disagree, agree, isCancelable) {
                override fun onDisagree() {
                    listener.onDisagree()
                }

                override fun onAgree() {
                    listener.onAgree()
                }

                override fun onDismiss() {

                }
            }.show()
        }
    }

    fun showConfirm(context: Context?, title: String?, message: String?, disagree: String?, agree: String?, isCancelable: Boolean, colorDisagree: Int?, colorAgree: Int?, listener: ConfirmDialogListener) {
        context?.let {
            object : ConfirmDialog(it, title, message, disagree, agree, isCancelable, colorDisagree, colorAgree) {
                override fun onDisagree() {
                    listener.onDisagree()
                }

                override fun onAgree() {
                    listener.onAgree()
                }

                override fun onDismiss() {

                }
            }.show()
        }
    }

    fun showConfirm(context: Context?, title: String?, message: String?, disagree: String?, agree: String?, colorTitle: Int?, colorMessage: Int?, isCancelable: Boolean, listener: ConfirmDialogListener) {
        context?.let {
            object : ConfirmDialog(it, title, message, disagree, agree, isCancelable, null, null, colorTitle, colorMessage) {
                override fun onDisagree() {
                    listener.onDisagree()
                }

                override fun onAgree() {
                    listener.onAgree()
                }

                override fun onDismiss() {

                }
            }.show()
        }
    }


    fun showConfirm(context: Context?, messageID: Int?, listener: ConfirmDialogListener) {
        showConfirm(context, null, messageID, null, null, true, listener)
    }

    fun showConfirm(context: Context?, messageID: Int?, isCancelable: Boolean, listener: ConfirmDialogListener) {
        showConfirm(context, null, messageID, null, null, isCancelable, listener)
    }

    fun showConfirm(context: Context?, messageID: Int?, disagreeID: Int?, agreeID: Int?, listener: ConfirmDialogListener) {
        showConfirm(context, null, messageID, disagreeID, agreeID, true, listener)
    }

    fun showConfirm(context: Context?, titleID: Int?, messageID: Int?, listener: ConfirmDialogListener) {
        showConfirm(context, titleID, messageID, null, null, true, listener)
    }

    fun showConfirm(context: Context?, titleID: Int?, messageID: Int?, isCancelable: Boolean, listener: ConfirmDialogListener) {
        showConfirm(context, titleID, messageID, null, null, isCancelable, listener)
    }


    fun showConfirm(context: Context?, titleID: Int?, messageID: Int?, disagreeID: Int?, agreeID: Int?, isCancelable: Boolean, listener: ConfirmDialogListener) {
        context?.let {
            val title = if (titleID != null) {
                it.getString(titleID)
            } else {
                null
            }

            val message = if (messageID != null) {
                it.getString(messageID)
            } else {
                null
            }

            val disagree = if (disagreeID != null) {
                it.getString(disagreeID)
            } else {
                null
            }

            val agree = if (agreeID != null) {
                it.getString(agreeID)
            } else {
                null
            }

            object : ConfirmDialog(it, title, message, disagree, agree, isCancelable) {
                override fun onDisagree() {
                    listener.onDisagree()
                }

                override fun onAgree() {
                    listener.onAgree()
                }

                override fun onDismiss() {
                    listener.onDisagree()
                }
            }.show()
        }
    }


    private fun getString(context: Context?, res: Int?): String? {
        return if (res == null) {
            null
        } else {
            context?.getString(res)
        }
    }


    fun showNotification(context: Context?, message: String?) {
        showNotification(context, null, message, null, true, null)
    }

    fun showNotification(context: Context?, message: String?, isCancelable: Boolean) {
        showNotification(context, null, message, null, isCancelable, null)
    }

    fun showNotification(context: Context?, title: String?, message: String?, button: String?) {
        showNotification(context, title, message, button, true, null)
    }

    fun showNotification(context: Context?, title: String?, message: String?, button: String?, isCancelable: Boolean) {
        showNotification(context, title, message, button, isCancelable, null)
    }


    fun showNotification(context: Context?, messageID: Int?) {
        showNotification(context, null, messageID, null, true, null)
    }

    fun showNotification(context: Context?, messageID: Int?, isCancelable: Boolean) {
        showNotification(context, null, messageID, null, isCancelable, null)
    }

    fun showNotification(context: Context?, messageID: Int?, isCancelable: Boolean, listener: NotificationDialogListener?) {
        showNotification(context, null, messageID, null, isCancelable, listener)
    }

    fun showNotification(context: Context?, message: String?, isCancelable: Boolean, listener: NotificationDialogListener?) {
        showNotification(context, null, message, null, isCancelable, listener)
    }

    fun showNotification(context: Context?, titleID: Int?, messageID: Int?) {
        showNotification(context, titleID, messageID, null, true, null)
    }

    fun showNotification(context: Context?, titleID: Int?, messageID: Int?, isCancelable: Boolean) {
        showNotification(context, titleID, messageID, null, isCancelable, null)
    }

    fun showNotification(context: Context?, titleID: Int?, messageID: Int?, isCancelable: Boolean, listener: NotificationDialogListener?) {
        showNotification(context, titleID, messageID, null, isCancelable, listener)
    }

    fun showNotification(context: Context?, titleID: Int?, messageID: Int?, buttonID: Int?) {
        showNotification(context, titleID, messageID, buttonID, true, null)
    }

    fun showNotification(context: Context?, titleID: Int?, messageID: Int?, buttonID: Int?, isCancelable: Boolean) {
        showNotification(context, titleID, messageID, buttonID, isCancelable, null)
    }

    fun showNotification(context: Context?, titleID: Int?, messageID: Int?, buttonID: Int?, isCancelable: Boolean, listener: NotificationDialogListener?) {
        showNotification(context, getString(context, titleID), getString(context, messageID), getString(context, buttonID), isCancelable, listener)
    }

    private fun showNotification(context: Context?, title: String?, message: String?, button: String?, isCancelable: Boolean, listener: NotificationDialogListener?) {
        context?.let {
            object : NotificationDialog(it, title, message, button, isCancelable) {
                override fun onDone() {
                    listener?.onDone()
                }
            }.show()
        }
    }

    fun showDialogLockCardBank(context: Context?, title: Int, message: Int, isCancelable: Boolean, listener: ConfirmDialogListener) {
        context?.let {
            object : DialogLockCardBank(it, title, message, isCancelable) {
                override fun onDisagree() {
                    listener.onDisagree()
                }

                override fun onAgree() {
                    listener.onAgree()
                }
            }.show()
        }
    }

    fun showDialogComfirmDaLayQua(context: Context?, isCancelable: Boolean, listener: NotEnoughPointListener) {
        context?.let {
            object : DialogComfirmDaLayQua(it, isCancelable) {
                override fun onClose() {
                    listener.onClose()
                }

                override fun onGivePoint() {
                    listener.onGiveIcoin()
                }

                override fun onDismiss() {

                }
            }.show()
        }
    }

    fun showPopupScanBuy(context: Context?, isCancelable: Boolean, listener: IScanBuyPopupListener) {
        context?.let {
            object : ScanBuyPopUp(it, isCancelable) {
                override fun onClose() {
                    listener.onClose()
                }

                override fun onClickSeller() {
                    listener.onClickSeller()
                }

                override fun onClickChat() {
                    listener.onClickChat()
                }

                override fun onDismiss() {

                }
            }.show()
        }
    }

    fun showDialogAcceptShipGift(context: Context?, isCancelable: Boolean, listener: NotEnoughPointListener) {
        context?.let {
            object : DialogAcceptShipGift(it, isCancelable) {
                override fun onClose() {
                    listener.onClose()
                }

                override fun onGivePoint() {
                    listener.onGiveIcoin()
                }
            }.show()
        }
    }

    fun showDialogEmtyBoxGift(context: Context?, image: Int, title: String, idCampaign: String, missions: MutableList<ICMission>, isCancelable: Boolean, listener: NotEnoughPointListener) {
        context?.let {
            object : DialogEmtyBoxGift(it, image, title, idCampaign, missions, isCancelable) {
                override fun onClose() {
                    listener.onClose()
                }

                override fun onMoreEvent() {
                    listener.onGiveIcoin()
                }
            }.show()
        }
    }

    fun showLoading(dialog: Dialog) {
        (dialog.window?.decorView as ViewGroup?)?.apply {
            val layout = findViewById<View?>(R.id.dialogLoading)
            if (layout == null) {
                addView(LayoutInflater.from(context).inflate(R.layout.dialog_loading, this, false).apply {
                    id = R.id.dialogLoading
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    isClickable = true
                    isFocusable = true
                })
            }
        }
    }

    fun closeLoading(dialog: Dialog) {
        (dialog.window?.decorView as ViewGroup?)?.apply {
            val layout = findViewById<View?>(R.id.dialogLoading)
            if (layout != null) {
                removeView(layout)
            }
        }
    }

    fun showLoading(fragment: Fragment) {
        if (fragment.isVisible) {
            showLoading(fragment.requireActivity())
        }
    }

    fun closeLoading(fragment: Fragment) {
        if (fragment.isVisible) {
            closeLoading(fragment.requireActivity())
        }
    }

    fun showLoading(activity: Activity) {
        (activity.findViewById<View>(android.R.id.content).rootView as ViewGroup).apply {
            val dialog = findViewById<View?>(R.id.dialogLoading)
            if (dialog == null) {
                addView(LayoutInflater.from(context).inflate(R.layout.dialog_loading, this, false).apply {
                    id = R.id.dialogLoading
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    isClickable = true
                    isFocusable = true
                })
            }
        }
    }

    fun closeLoading(activity: Activity) {
        (activity.findViewById<View>(android.R.id.content).rootView as ViewGroup).apply {
            val dialog = findViewById<View?>(R.id.dialogLoading)
            if (dialog != null) {
                removeView(dialog)
            }
        }
    }

    fun showAddCartSuccess(context: Context, message: String?) {
        object : Dialog(context, R.style.DialogNotification) {}.apply {
            setContentView(R.layout.dialog_special_notification)
            val txtMessage = findViewById<AppCompatTextView>(R.id.tvMessage)

            txtMessage?.text = message
            if (window != null) {
                window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            show()

            Handler().postDelayed({
                dismiss()
            }, 1000)
        }
    }

    fun showDialogSuccessBlack(context: Context, message: String? = null, style: Int? = null, time: Long = 1500) {
        context.showSimpleSuccessToast(message)
    }

    fun showDialogErrorBlack(context: Context, message: String? = null, style: Int? = null, time: Long = 1500) {
        context.showSimpleErrorToast(message)
    }

    fun showShimmer(shimmer: ShimmerFrameLayout) {
        shimmer.startShimmer()
    }

    fun hideShimmer(shimmer: ShimmerFrameLayout, view: View) {
        shimmer.stopShimmer()
        view.visibility = View.VISIBLE
        shimmer.visibility = View.GONE
    }

    fun showLoginPopup(activity: Activity) {
        object : RewardLoginDialog(activity) {
            override fun onLogin() {
                ActivityUtils.startActivity<AccountActivity>(activity)
            }

            override fun onRegister() {
                ActivityUtils.startActivity<AccountActivity>(activity, Constant.DATA_1, Constant.REGISTER_TYPE)
            }

            override fun onDismiss() {
            }
        }.show()
    }
}