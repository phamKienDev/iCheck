package vn.icheck.android.screen.user.createqrcode.createwifI

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_create_wifi_qr_code.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.screen.user.createqrcode.base.fragment.BaseCreateQrCodeFragment
import vn.icheck.android.screen.user.createqrcode.success.CreateQrCodeSuccessActivity
import vn.icheck.android.screen.user.createqrcode.createwifI.presenter.CreateWifiQrCodePresenter
import vn.icheck.android.screen.user.createqrcode.createwifI.view.ICreateWifiQrCodeView

/**
 * Created by VuLCL on 10/8/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateWifiQrCodeFragment : BaseCreateQrCodeFragment<CreateWifiQrCodePresenter>(), ICreateWifiQrCodeView {

    override val getLayoutID: Int
        get() = R.layout.fragment_create_wifi_qr_code

    override val getPresenter: CreateWifiQrCodePresenter
        get() = CreateWifiQrCodePresenter(this)

    private val requestNew = 1

    override fun onInitView() {
        initToolbar()
        setupView()
        initSecurityView()
        initListener()
        focusView(edtName)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.chia_se_wifi)

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupView() {
        btnCreate.background = ViewHelper.btnPrimaryCorners4(requireContext())
    }

    private fun initSecurityView() {
        val adapter = ArrayAdapter<String>(requireContext(), R.layout.item_sp_scurity_type, R.id.tvTitle, resources.getStringArray(R.array.wifi_security_type))
        adapter.setDropDownViewResource(R.layout.item_sp_dropdown)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, p3: Long) {
                edtType.setText(if (position == 0) {
                    null
                } else {
                    spinner.selectedItem as String
                })
            }
        }
    }

    private fun initListener() {
        btnCreate.setOnClickListener {
            val securityType = if (spinner.selectedItemPosition == 0) {
                null
            } else {
                spinner.selectedItem as String
            }

            if (securityType.isNullOrEmpty()){
                showShortError("Bạn cần phải chọn kiểu bảo mật")
                return@setOnClickListener
            }

            presenter.validData(edtName.text.toString().trim(), edtPassword.text.toString().trim(), securityType)
        }
    }

    override fun onInValidNameSuccess() {
        tvMessageName.visibility = View.GONE
        edtName.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtName.context)
    }

    override fun onInValidName(error: String) {
        tvMessageName.visibility = View.VISIBLE
        tvMessageName.text = error
        edtName.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_stroke_red_4)
        edtName.requestFocus()
    }

    override fun onInValidPasswordSuccess() {
        tvMessagePassword.visibility = View.GONE
        edtPassword.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtPassword.context)
    }

    override fun onInValidPassword(error: String) {
        tvMessagePassword.visibility = View.VISIBLE
        tvMessagePassword.text = error
        edtPassword.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_corner_stroke_red_4)
        edtPassword.requestFocus()
    }

    override fun onValidSuccess(text: String) {
        tvMessageName.visibility = View.GONE
        tvMessagePassword.visibility = View.GONE

        edtName.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtName.context)
        edtPassword.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtName.context)

        KeyboardUtils.hideSoftInput(edtName)

        val intent = Intent(requireContext(), CreateQrCodeSuccessActivity::class.java)
        intent.putExtra(Constant.DATA_1, text)
        startActivityForResult(intent, requestNew)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestNew) {
            if (resultCode == Activity.RESULT_OK) {
                activity?.onBackPressed()
            }
        }
    }
}