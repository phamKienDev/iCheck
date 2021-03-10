package vn.icheck.android.screen.user.createqrcode.cretephone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_create_phone_qr_code.*
import kotlinx.android.synthetic.main.fragment_create_phone_qr_code.btnCreate
import kotlinx.android.synthetic.main.fragment_create_phone_qr_code.edtContent
import kotlinx.android.synthetic.main.fragment_create_phone_qr_code.imgBack
import kotlinx.android.synthetic.main.fragment_create_phone_qr_code.tvMessage
import kotlinx.android.synthetic.main.fragment_create_phone_qr_code.txtTitle
import kotlinx.android.synthetic.main.fragment_create_text_qr_code.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.screen.user.createqrcode.base.fragment.BaseCreateQrCodeFragment
import vn.icheck.android.screen.user.createqrcode.base.presenter.BaseCreateQrCodePresenter
import vn.icheck.android.screen.user.createqrcode.base.view.IBaseCreateQrCodeView
import vn.icheck.android.screen.user.createqrcode.success.CreateQrCodeSuccessActivity
import vn.icheck.android.helper.ContactHelper
import vn.icheck.android.screen.dialog.PermissionDialog

/**
 * Created by VuLCL on 10/5/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreatePhoneQrCodeFragment : BaseCreateQrCodeFragment<BaseCreateQrCodePresenter>(), IBaseCreateQrCodeView {
    private val requestContact = 1
    private val permissionContact = 1

    override val getLayoutID: Int
        get() = R.layout.fragment_create_phone_qr_code

    override val getPresenter: BaseCreateQrCodePresenter
        get() = BaseCreateQrCodePresenter(this)

    private val requestNew = 2

    override fun onInitView() {
        initToolbar()
        initListener()
        focusView(edtContent)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.chia_se_so_dien_thoai)

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
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
        edtContent.background = ContextCompat.getDrawable(requireContext(),R.drawable.bg_corner_gray_solid_white)
        KeyboardUtils.hideSoftInput(edtContent)

        val intent = Intent(requireContext(),CreateQrCodeSuccessActivity::class.java)
        intent.putExtra(Constant.DATA_1,text)
        startActivityForResult(intent,requestNew)
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)
        tvMessage.visibility = View.VISIBLE
        tvMessage.text = errorMessage
        edtContent.background = ContextCompat.getDrawable(requireContext(),R.drawable.bg_corner_stroke_red_4)
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

        if (requestCode == requestNew){
            if (resultCode == Activity.RESULT_OK){
                activity?.onBackPressed()
            }
        }
    }
}