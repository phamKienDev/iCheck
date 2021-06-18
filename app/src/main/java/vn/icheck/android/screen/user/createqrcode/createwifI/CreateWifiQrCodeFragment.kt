package vn.icheck.android.screen.user.createqrcode.createwifI

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_create_wifi_qr_code.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.screen.user.createqrcode.createwifI.presenter.CreateWifiQrCodePresenter
import vn.icheck.android.screen.user.createqrcode.createwifI.view.ICreateWifiQrCodeView
import vn.icheck.android.screen.user.createqrcode.success.CreateQrCodeSuccessActivity
import vn.icheck.android.util.KeyboardUtils

/**
 * Created by VuLCL on 10/8/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateWifiQrCodeFragment : BaseFragmentMVVM(), ICreateWifiQrCodeView {

    val presenter = CreateWifiQrCodePresenter(this@CreateWifiQrCodeFragment)

    private val requestNew = 1

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_wifi_qr_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitView()
    }

    fun onInitView() {
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

         ViewHelper.bgWhiteStrokeLineColor0_5Corners4(requireContext()).apply {
             edtName.background=this
             edtPassword.background=this
             edtType.background=this
         }
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
        edtName.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtName.context)
    }

    override fun onInValidName(error: String) {
        tvMessageName.visibility = View.VISIBLE
        tvMessageName.text = error
        edtName.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
        edtName.requestFocus()
    }

    override fun onInValidPasswordSuccess() {
        tvMessagePassword.visibility = View.GONE
        edtPassword.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtPassword.context)
    }

    override fun onInValidPassword(error: String) {
        tvMessagePassword.visibility = View.VISIBLE
        tvMessagePassword.text = error
        edtPassword.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
        edtPassword.requestFocus()
    }

    override fun onValidSuccess(text: String) {
        tvMessageName.visibility = View.GONE
        tvMessagePassword.visibility = View.GONE

        edtName.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtName.context)
        edtPassword.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtName.context)

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

    override fun showError(errorMessage: String) {
        requireContext().showLongErrorToast(errorMessage)
    }

    override val mContext: Context?
        get() = requireContext()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        if (event.type == ICMessageEvent.Type.BACK) {
            activity?.onBackPressed()
        }
    }
}