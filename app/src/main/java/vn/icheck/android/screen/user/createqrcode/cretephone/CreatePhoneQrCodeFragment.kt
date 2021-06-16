package vn.icheck.android.screen.user.createqrcode.cretephone

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_create_phone_qr_code.*
import kotlinx.android.synthetic.main.fragment_create_phone_qr_code.btnCreate
import kotlinx.android.synthetic.main.fragment_create_phone_qr_code.edtContent
import kotlinx.android.synthetic.main.fragment_create_phone_qr_code.imgBack
import kotlinx.android.synthetic.main.fragment_create_phone_qr_code.tvMessage
import kotlinx.android.synthetic.main.fragment_create_phone_qr_code.txtTitle
import kotlinx.android.synthetic.main.fragment_create_text_qr_code.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ContactHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.dialog.PermissionDialog
import vn.icheck.android.screen.user.createqrcode.base.presenter.BaseCreateQrCodePresenter
import vn.icheck.android.screen.user.createqrcode.base.view.IBaseCreateQrCodeView
import vn.icheck.android.screen.user.createqrcode.success.CreateQrCodeSuccessActivity
import vn.icheck.android.util.KeyboardUtils

/**
 * Created by VuLCL on 10/5/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreatePhoneQrCodeFragment : BaseFragmentMVVM(), IBaseCreateQrCodeView {
    private val requestContact = 1
    private val permissionContact = 1

    private val requestNew = 2

    val presenter = BaseCreateQrCodePresenter(this@CreatePhoneQrCodeFragment)

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_phone_qr_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitView()
    }

    fun onInitView() {
        initToolbar()
        setupView()
        initListener()
        focusView(edtContent)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.chia_se_so_dien_thoai)

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupView() {
        btnCreate.background = ViewHelper.btnPrimaryCorners4(requireContext())
    }

    private fun initListener() {
        btnCreate.setOnClickListener {
            presenter.validPhone(edtContent.text.toString())
        }

        val drawableEnd = 2

        edtContent.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= (edtContent.right - edtContent.compoundDrawables[drawableEnd].bounds.width())) {
                    PermissionDialog.checkPermission(context, PermissionDialog.CONTACT, object : PermissionDialog.PermissionListener {
                        override fun onPermissionAllowed() {
                            ContactHelper.pickContact(this@CreatePhoneQrCodeFragment, requestContact)
                        }

                        override fun onRequestPermission() {
                            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), permissionContact)
                        }

                        override fun onPermissionNotAllow() {
                            showShortError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
                        }
                    })
                    return@setOnTouchListener true
                }
            }

            return@setOnTouchListener false
        }
    }

    override fun onValidSuccess(text: String) {
        tvMessage.visibility = View.GONE
        edtContent.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(requireContext())
        KeyboardUtils.hideSoftInput(edtContent)

        val intent = Intent(requireContext(), CreateQrCodeSuccessActivity::class.java)
        intent.putExtra(Constant.DATA_1, text)
        startActivityForResult(intent, requestNew)
    }

    override fun showError(errorMessage: String) {
        tvMessage.visibility = View.VISIBLE
        tvMessage.text = errorMessage
        edtContent.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
        edtContent.requestFocus()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionContact) {
            if (PermissionHelper.checkResult(grantResults)) {
                ContactHelper.pickContact(this, requestContact)
            } else {
                showLongError(R.string.khong_the_thuc_hien_tac_vu_vi_ban_chua_cap_quyen)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestContact) {
            if (resultCode == Activity.RESULT_OK) run {
                edtContent.setText(ContactHelper.getPhone(activity, data))
            }
        }

        if (requestCode == requestNew) {
            if (resultCode == Activity.RESULT_OK) {
                activity?.onBackPressed()
            }
        }
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