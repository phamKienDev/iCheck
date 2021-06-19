package vn.icheck.android.screen.account.verifyfacebookphone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_verify_facebook_phone.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.screen.account.registerfacebookphone.RegisterFacebookPhoneActivity
import vn.icheck.android.screen.account.verifyfacebookphone.presenter.VerifyFacebookPhonePresenter
import vn.icheck.android.screen.account.verifyfacebookphone.view.IVerifyFacebookPhoneView
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

/**
 * Created by VuLCL on 9/18/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class VerifyFacebookPhoneActivity : BaseActivityMVVM(), IVerifyFacebookPhoneView {

    private val presenter = VerifyFacebookPhonePresenter(this@VerifyFacebookPhoneActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_verify_facebook_phone)

        onInitView()
    }

    fun onInitView() {
        setupToolbar()
        initListener()
        presenter.getData(intent)
        KeyboardUtils.showSoftInput(edtPhone)
    }

    private fun initListener() {
        btnConfirm.setOnClickListener {
            presenter.validPhone(edtPhone.text.toString().trim())
        }
    }

    private fun setupToolbar() {
        txtTitle.setText(R.string.cap_nhat_so_dien_thoai)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onErrorPhone(errorMessage: String) {
        layoutInputPhone.error = errorMessage
    }

    override fun onSetUserInfo(name: String?, avatar: String?) {
        tvName.text = name
        WidgetUtils.loadImageUrl(imgAvatar, avatar, R.drawable.ic_circle_avatar_default, R.drawable.ic_circle_avatar_default)
    }

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onShowLoading(isShow: Boolean) {
        vn.icheck.android.ichecklibs.DialogHelper.showLoading(this@VerifyFacebookPhoneActivity, isShow)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onRegisterFacebookPhone(facebookToken: String?) {
        val intent = Intent(this, RegisterFacebookPhoneActivity::class.java)
        intent.putExtra(Constant.DATA_1, edtPhone.text.toString().trim())
        intent.putExtra(Constant.DATA_2, facebookToken)
        ActivityUtils.startActivityAndFinish(this, intent)
    }

    override fun showError(errorMessage: String) {

        showShortError(errorMessage)
    }

    override val mContext: Context
        get() = this@VerifyFacebookPhoneActivity
}