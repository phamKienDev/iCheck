package vn.icheck.android.screen.user.createqrcode.createemail

import android.app.Activity
import android.content.Intent
import android.view.View
import kotlinx.android.synthetic.main.fragment_create_email_qr_code.*
import kotlinx.android.synthetic.main.fragment_create_email_qr_code.btnCreate
import kotlinx.android.synthetic.main.fragment_create_email_qr_code.edtContent
import kotlinx.android.synthetic.main.fragment_create_email_qr_code.imgBack
import kotlinx.android.synthetic.main.fragment_create_email_qr_code.txtTitle
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.screen.user.createqrcode.base.fragment.BaseCreateQrCodeFragment
import vn.icheck.android.screen.user.createqrcode.createemail.presenter.CreateEmailQrCodePresenter
import vn.icheck.android.screen.user.createqrcode.createemail.view.ICreateEmailQrCodeView
import vn.icheck.android.screen.user.createqrcode.success.CreateQrCodeSuccessActivity

/**
 * Created by VuLCL on 10/5/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateEmailQrCodeFragment : BaseCreateQrCodeFragment<CreateEmailQrCodePresenter>(), ICreateEmailQrCodeView {

    override val getLayoutID: Int
        get() = R.layout.fragment_create_email_qr_code

    override val getPresenter: CreateEmailQrCodePresenter
        get() = CreateEmailQrCodePresenter(this)

    private val requestNew = 1

    override fun onInitView() {
        initToolbar()
        setupView()
        initListener()
        focusView(edtEmail)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.chia_se_email)

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupView() {
        btnCreate.background = ViewHelper.btnPrimaryCorners4(requireContext())
    }

    private fun initListener() {
        btnCreate.setOnClickListener {
            presenter.validData(edtEmail.text.toString().trim(), edtTitle.text.toString().trim(),
                    edtContent.text.toString().trim())
        }
    }

    override fun onInvalidEmail(error: String) {
        tvMessageEmail.visibility = View.VISIBLE
        tvMessageEmail.text = error
        edtEmail.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
        edtEmail.requestFocus()
    }

    override fun onInvalidTitle(error: String) {
        tvMessageTitle.visibility = View.VISIBLE
        tvMessageTitle.text = error
        edtTitle.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
        edtTitle.requestFocus()
    }

    override fun onInvalidContent(error: String) {
        tvMessageContent.visibility = View.VISIBLE
        tvMessageContent.text = error
        edtContent.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
        edtContent.requestFocus()
    }

    override fun onValidSuccess(text: String) {
        tvMessageEmail.visibility = View.GONE
        tvMessageTitle.visibility = View.GONE
        tvMessageContent.visibility = View.GONE
        edtEmail.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtEmail.context)
        edtTitle.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtEmail.context)
        edtTitle.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtEmail.context)
        KeyboardUtils.hideSoftInput(edtContent)

        val intent = Intent(requireContext(),CreateQrCodeSuccessActivity::class.java)
        intent.putExtra(Constant.DATA_1,text)
        startActivityForResult(intent,requestNew)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == requestNew){
                activity?.onBackPressed()
            }
        }
    }
}