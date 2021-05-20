package vn.icheck.android.screen.user.createqrcode.createtext

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_create_text_qr_code.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.screen.user.createqrcode.base.fragment.BaseCreateQrCodeFragment
import vn.icheck.android.screen.user.createqrcode.base.presenter.BaseCreateQrCodePresenter
import vn.icheck.android.screen.user.createqrcode.base.view.IBaseCreateQrCodeView
import vn.icheck.android.screen.user.createqrcode.success.CreateQrCodeSuccessActivity

/**
 * Created by VuLCL on 10/5/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateTextQrCodeFragment : BaseCreateQrCodeFragment<BaseCreateQrCodePresenter>(), IBaseCreateQrCodeView {

    override val getLayoutID: Int
        get() = R.layout.fragment_create_text_qr_code

    override val getPresenter: BaseCreateQrCodePresenter
        get() = BaseCreateQrCodePresenter(this)

    private val requestAddNew = 1

    override fun onInitView() {
        initToolbar()
        setupView()
        initListener()
        focusView(edtContent)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.chia_se_van_ban)

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupView() {
        btnCreate.background = ViewHelper.btnPrimaryCorners4(requireContext())
    }

    private fun initListener() {
        btnCreate.setOnClickListener {
            presenter.validText(edtContent.text.toString().trim())
        }
    }

    override fun onValidSuccess(text: String) {
        tvMessage.visibility = View.GONE
        edtContent.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtContent.context)
        KeyboardUtils.hideSoftInput(edtContent)
        val intent = Intent(requireContext(),CreateQrCodeSuccessActivity::class.java)
        intent.putExtra(Constant.DATA_1,text)
        startActivityForResult(intent,requestAddNew)
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)
        tvMessage.visibility = View.VISIBLE
        tvMessage.text = errorMessage
        edtContent.background = ContextCompat.getDrawable(requireContext(),R.drawable.bg_corner_stroke_red_4)
        edtContent.requestFocus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == requestAddNew){
                activity?.onBackPressed()
            }
        }
    }
}