package vn.icheck.android.screen.account.registerfacebookphone

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import kotlinx.android.synthetic.main.fragment_register_facebook_phone.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.screen.account.registerfacebookphone.presenter.RegisterFacebookPhonePresenter
import vn.icheck.android.screen.account.registerfacebookphone.view.RegisterFacebookPhoneView
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class RegisterFacebookPhoneActivity : BaseActivityMVVM(), RegisterFacebookPhoneView, View.OnClickListener {
    private var timer: CountDownTimer? = null

    private val presenter = RegisterFacebookPhonePresenter(this@RegisterFacebookPhoneActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_register_facebook_phone)

        onInitView()
    }

    fun onInitView() {
        initToolbar()
        onCountDownOtp()
        initListener()

        if (presenter.getData(intent))
            KeyboardUtils.showSoftInput(edtOtp)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.tao_mat_khau)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initListener() {
        WidgetUtils.setClickListener(this, txtTime, imgShowOrHidePassword, imgShowOrHideRePassword, btnConfirm)
    }

    override fun onGetPhoneError() {
        DialogHelper.showNotification(this@RegisterFacebookPhoneActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    override fun onErrorOtp(errorMessage: String) {
        layoutInputOtp.error = errorMessage
    }

    override fun onErrorPassword(errorMessage: String) {
        layoutInputPassword.error = errorMessage
    }

    override fun onErrorRePassword(errorMessage: String) {
        layoutInputRePassword.error = errorMessage
    }

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@RegisterFacebookPhoneActivity, isShow)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onCountDownOtp() {
        edtOtp.text = null

        timer?.cancel()
        timer = null

        timer = object : CountDownTimer(60000, 1000) {
            override fun onFinish() {
                txtTime?.setText(R.string.gui_lai_upcase)
            }

            override fun onTick(millisecond: Long) {
                txtTime?.text = ((millisecond / 1000).toString() + "s")
            }
        }

        timer?.start()
    }

    override fun onRegisterSuccess() {
        showShortSuccess(R.string.lien_ket_facebook_thanh_cong)
        onBackPressed()
    }

    override fun onLoginSuccess() {
        setResult(Activity.RESULT_OK)
        onBackPressed()
    }

    override fun showError(errorMessage: String) {

        showShortError(errorMessage)
    }

    override val mContext: Context
        get() = this@RegisterFacebookPhoneActivity

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txtTime -> {
                if (txtTime.text == getString(R.string.gui_lai_upcase)) {
                    presenter.sendOtp()
                }
            }
            imgShowOrHidePassword.id -> {
                WidgetUtils.showOrHidePassword(edtPassword, imgShowOrHidePassword)
            }
            imgShowOrHideRePassword.id -> {
                WidgetUtils.showOrHidePassword(edtRePassword, imgShowOrHideRePassword)
            }
            btnConfirm.id -> {
                presenter.registerFacebookPhone(edtOtp.text.toString(), edtPassword.text.toString(), edtRePassword.text.toString())
            }
        }
    }
}