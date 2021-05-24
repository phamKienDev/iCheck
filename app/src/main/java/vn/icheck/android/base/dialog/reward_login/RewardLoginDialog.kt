package vn.icheck.android.base.dialog.reward_login

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_reward_login.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.screen.checktheme.CheckThemeActivity

class RewardLoginDialog : DialogFragment() {

    private var isStartLogin = false

    private var listener: RewardLoginCallback? = null

    companion object {
        var currentFragmentManager: String? = null
        fun show(fragmentManager: FragmentManager, callback: RewardLoginCallback) {
            if (fragmentManager.findFragmentByTag(RewardLoginDialog::class.java.simpleName)?.isAdded == true) {
                currentFragmentManager = null
            } else {
                if (ICheckApplication.currentActivity() !is CheckThemeActivity) {
                    if (currentFragmentManager != fragmentManager.toString()) {
                        RewardLoginDialog().apply {
                            setListener(callback)
                            show(fragmentManager, RewardLoginDialog::class.java.simpleName)
                            currentFragmentManager = fragmentManager.toString()
                        }
                    }
                }
            }
        }
    }

    fun setListener(listener: RewardLoginCallback){
        this.listener = listener
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return inflater.inflate(R.layout.dialog_reward_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentFragmentManager = null
        btnLogin.setOnClickListener {
            isStartLogin = true
            dismiss()
            listener?.onLogin()
        }

        btnRegister.setOnClickListener {
            isStartLogin = true
            dismiss()
            listener?.onRegister()
        }

        tvClose?.setOnClickListener {
            dismiss()
            listener?.onDismiss()
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!isStartLogin)
            listener?.onDismiss()
    }

}