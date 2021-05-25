package vn.icheck.android.screen.user.detail_stamp_v6_1.otp_information_guarantee

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ActivityVerifyOtpguaranteeBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICUpdateCustomerGuarantee
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.otp_information_guarantee.presenter.VerifyOTPGuaranteePresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.otp_information_guarantee.view.IVerifyOTPGuaranteeView
import vn.icheck.android.util.KeyboardUtils

class VerifyOTPGuaranteeActivity : BaseActivityMVVM(), IVerifyOTPGuaranteeView {
    private lateinit var binding: ActivityVerifyOtpguaranteeBinding
    private val presenter = VerifyOTPGuaranteePresenter(this)

    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpguaranteeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        presenter.getDataByIntent(intent)
    }

    private fun setupToolbar() {
        binding.layoutToolbar.txtTitle.setText(R.string.xac_thuc_so_dien_thoai)

        binding.layoutToolbar.imgBack.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupListener() {
        binding.edtOtp.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.txtOtp.visibility = if (binding.edtOtp.text.isNullOrEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        binding.txtStatus.setOnClickListener {
            if (StampDetailActivity.isVietNamLanguage == false) {
                if (binding.txtStatus.text.toString() == "Resend code") {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.txtStatus.text = "Sending code"

                    presenter.sendOtpConfirmPhone()
                }
            } else {
                if (binding.txtStatus.text.toString() == "Gửi lại mã") {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.txtStatus.text = "Đang gửi mã"

                    presenter.sendOtpConfirmPhone()
                }
            }
        }

        binding.btnConfirm.setOnClickListener {
            presenter.confirmOtp(binding.edtOtp.text.toString())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showTextTime(isShow: Boolean) {
        if (StampDetailActivity.isVietNamLanguage == false) {
            if (isShow) {
                binding.txtTime.visibility = View.VISIBLE
                binding.txtTime.text = getString(R.string.gui_lai_ma_xxx_s, "60")

                binding.layoutStatus.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.INVISIBLE
            } else {
                binding.txtTime.visibility = View.INVISIBLE

                binding.layoutStatus.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
                binding.txtStatus.text = "Resend code"
            }
        } else {
            if (isShow) {
                binding.txtTime.visibility = View.VISIBLE
                binding.txtTime.text = getString(R.string.gui_lai_ma_xxx_s, "60")

                binding.layoutStatus.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.INVISIBLE
            } else {
                binding.txtTime.visibility = View.INVISIBLE

                binding.layoutStatus.visibility = View.VISIBLE
                binding.progressBar.visibility = View.INVISIBLE
                binding.txtStatus.setText(R.string.gui_lai_ma)
            }
        }
    }

    override fun onGetDataError() {
        DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    override fun onGetDataIntentSuccess(obj: ICUpdateCustomerGuarantee) {
        binding.tvTitle.text = Html.fromHtml(getString(R.string.login_ma_xac_thuc_otp_da_duoc_gui_toi, obj.phone ?: getString(R.string.dang_cap_nhat)))

        KeyboardUtils.showSoftInput(binding.edtOtp)
//        binding.txtContent.text = Html.fromHtml(getString(R.string.otp_content_xxx, obj.phone))

        setupListener()
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
                this@VerifyOTPGuaranteeActivity.let {
                    binding.txtTime?.text = it.getString(R.string.gui_lai_ma_xxx_s, (millisecond / 1000).toString())
                }
            }
        }

        timer?.start()
    }

    override fun updateInformationCusomterGuaranteeSuccess(user: ICUpdateCustomerGuarantee) {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(Constant.DATA_1, user)
        })

        if (StampDetailActivity.isVietNamLanguage == false) {
            showShortSuccess("Successfully updated")
        } else {
            showShortSuccess("Cập nhật thông tin thành công")
        }
        onBackPressed()
    }

    override fun updateInformationCusomterGuaranteeFail() {
        if (StampDetailActivity.isVietNamLanguage == false) {
            showShortError("Occurred. Please try again")
        } else {
            showShortError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        }
    }

    override fun onErrorOtp(errorMessage: String) {
        binding.layoutInputOtp.error = errorMessage
        binding.edtOtp.setSelection(binding.edtOtp.text.toString().length)
    }

    override fun showError(errorMessage: String) {
        showShortError(errorMessage)
    }

    override val mContext: Context
        get() = this

    override fun onShowLoading(isShow: Boolean) {
        if (isShow) {
            DialogHelper.showLoading(this)
        } else {
            DialogHelper.closeLoading(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        timer?.cancel()
        timer = null
    }
}
