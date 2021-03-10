package vn.icheck.android.screen.user.profile.changepassword

import android.view.View
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.screen.user.profile.changepassword.presenter.ChangePasswordPresenter
import vn.icheck.android.screen.user.profile.changepassword.view.ChangePasswordView
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.kotlin.WidgetUtils

/**
 * Created by VuLCL on 11/7/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ChangePasswordActivity : BaseActivity<ChangePasswordPresenter>(), ChangePasswordView, View.OnClickListener {

    override val getLayoutID: Int
        get() = R.layout.activity_change_password

    override val getPresenter: ChangePasswordPresenter
        get() = ChangePasswordPresenter(this)

    override fun onInitView() {
        initToolbar()
        initListener()
        KeyboardUtils.showSoftInput(edtOldPassword)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.thay_doi_mat_khau)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initListener() {
        WidgetUtils.setClickListener(this, imgShowOrHideOldPassword,
                imgShowOrHidePassword, imgShowOrHideRePassword, btnConfirm)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imgShowOrHideOldPassword -> {
                WidgetUtils.showOrHidePassword(edtOldPassword, imgShowOrHideOldPassword)
            }
            R.id.imgShowOrHidePassword -> {
                WidgetUtils.showOrHidePassword(edtPassword, imgShowOrHidePassword)
            }
            R.id.imgShowOrHideRePassword -> {
                WidgetUtils.showOrHidePassword(edtRePassword, imgShowOrHideRePassword)
            }
            R.id.btnConfirm -> {
                presenter.changePassword(edtOldPassword.text.toString(),
                        edtPassword.text.toString(),
                        edtRePassword.text.toString())
            }
        }
    }

    override fun onErrorOldPassword(error: String) {
        layoutInputOldPassword.error = error
    }

    override fun onErrorPassword(error: String) {
        layoutInputPassword.error = error
    }

    override fun onErrorRePassword(error: String) {
        layoutInputRePassword.error = error
    }

    override fun onShowLoading() {
        DialogHelper.showLoading(this)
    }

    override fun onCloseLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onChangePasswordSuccess() {
        showLongSuccess(R.string.doi_mat_khau_thanh_cong)
        onBackPressed()
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)

        showShortError(errorMessage)
    }
}