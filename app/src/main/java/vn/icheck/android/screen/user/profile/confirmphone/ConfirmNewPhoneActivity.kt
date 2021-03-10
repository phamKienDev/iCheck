package vn.icheck.android.screen.user.profile.confirmphone

import android.app.Activity
import android.os.CountDownTimer
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.fragment_register_user_otp.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.screen.user.profile.confirmphone.presenter.ConfirmNewPhonePresenter
import vn.icheck.android.screen.user.profile.confirmphone.view.IConfirmNewPhoneView
import vn.icheck.android.util.KeyboardUtils

/**
 * Created by VuLCL on 11/7/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ConfirmNewPhoneActivity : BaseActivity<ConfirmNewPhonePresenter>(), IConfirmNewPhoneView {
    private var timer: CountDownTimer? = null

    override val getLayoutID: Int
        get() = R.layout.fragment_register_user_otp

    override val getPresenter: ConfirmNewPhonePresenter
        get() = ConfirmNewPhonePresenter(this)

    override fun onInitView() {
        initToolbar()
        presenter.getData(intent)
    }

    private fun initToolbar() {
        txtTitle.text = getString(R.string.nhap_ma_xac_nhan)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initListener() {
        edtOtp.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                txtOtp.visibility = if (edtOtp.text.isNullOrEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        txtStatus.setOnClickListener {
            if (txtStatus.text.toString() == getString(R.string.gui_lai_ma)) {
                progressBar.visibility = View.VISIBLE
                txtStatus.setText(R.string.dang_gui_ma)

                presenter.sendOtpConfirmPhone()
            }
        }

        btnConfirm.setOnClickListener {
            presenter.confirmOtp(edtOtp.text.toString())
        }
    }

    private fun showTextTime(isShow: Boolean) {
        if (isShow) {
            txtTime.visibility = View.VISIBLE
            txtTime.text = getString(R.string.khong_nhan_duoc_ma_xac_nhan_gui_lai_sau_xxx_giay, "60")

            layoutStatus.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
        } else {
            txtTime.visibility = View.INVISIBLE

            layoutStatus.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
            txtStatus.setText(R.string.gui_lai_ma)
        }
    }

    override fun onGetDataError() {
        DialogHelper.showNotification(this@ConfirmNewPhoneActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    override fun onGetDataSuccess(phone: String) {
        KeyboardUtils.showSoftInput(edtOtp)
        txtContent.text = Html.fromHtml(getString(R.string.otp_content_xxx, phone))
        initListener()
    }

    override fun onCountDownOtp() {
        showTextTime(true)

        timer?.cancel()
        timer = null

        timer = object : CountDownTimer(60000, 1000) {
            override fun onFinish() {
                showTextTime(false)
            }

            override fun onTick(millisecond: Long) {
                txtTime?.text = getString(R.string.khong_nhan_duoc_ma_xac_nhan_gui_lai_sau_xxx_giay, (millisecond / 1000).toString())
            }
        }

        timer?.start()
    }

    override fun onErrorOtp(errorMessage: String) {
        layoutInputOtp.error = errorMessage
        edtOtp.setSelection(edtOtp.text.toString().length)
    }

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onUpdateSuccess() {
        setResult(Activity.RESULT_OK)
        onBackPressed()
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)

        showShortError(errorMessage)
    }

    override fun onDestroy() {
        super.onDestroy()

        timer?.cancel()
        timer = null
    }
}