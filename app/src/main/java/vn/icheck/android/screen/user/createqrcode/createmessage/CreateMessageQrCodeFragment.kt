package vn.icheck.android.screen.user.createqrcode.createmessage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_create_message_qr_code.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.screen.user.createqrcode.base.fragment.BaseCreateQrCodeFragment
import vn.icheck.android.screen.user.createqrcode.createmessage.presenter.CreateMessageQrCodePresenter
import vn.icheck.android.screen.user.createqrcode.createmessage.view.ICreateMessageQrCodeView
import vn.icheck.android.screen.user.createqrcode.success.CreateQrCodeSuccessActivity
import vn.icheck.android.helper.ContactHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.dialog.PermissionDialog

/**
 * Created by VuLCL on 10/5/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateMessageQrCodeFragment : BaseCreateQrCodeFragment<CreateMessageQrCodePresenter>(), ICreateMessageQrCodeView {
    private val requestContact = 1
    private val permissionContact = 1

    override val getLayoutID: Int
        get() = R.layout.fragment_create_message_qr_code

    override val getPresenter: CreateMessageQrCodePresenter
        get() = CreateMessageQrCodePresenter(this)

    private val requestNew = 2

    override fun onInitView() {
        initToolbar()
        setupView()
        initListener()
        focusView(edtPhone)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.chia_se_tin_nhan)

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupView() {
        btnCreate.background = ViewHelper.btnPrimaryCorners4(requireContext())
    }

    private fun initListener() {
        btnCreate.setOnClickListener {
            presenter.validData(edtPhone.text.toString().trim(), edtContent.text.toString().trim())
        }

        val drawableEnd = 2

        edtPhone.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= (edtPhone.right - edtPhone.compoundDrawables[drawableEnd].bounds.width())) {
                    PermissionDialog.checkPermission(context, PermissionDialog.CONTACT, object : PermissionDialog.PermissionListener {
                        override fun onPermissionAllowed() {
                            ContactHelper.pickContact(this@CreateMessageQrCodeFragment, requestContact)
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

    override fun onInvalidPhoneSuccess() {
        tvMessage.visibility = View.GONE
        edtPhone.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtPhone.context)
    }

    override fun onInvalidPhone(error: String) {
        tvMessage.visibility = View.VISIBLE
        tvMessage.text = error
        edtPhone.background = ContextCompat.getDrawable(requireContext(),R.drawable.bg_corner_stroke_red_4)
        edtPhone.requestFocus()
    }

    override fun onInvalidContentSuccess() {
        tvMessageContent.visibility = View.GONE
        edtContent.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtContent.context)
    }

    override fun onInvalidContent(error: String) {
        tvMessageContent.visibility = View.VISIBLE
        tvMessageContent.text = error
        edtContent.background = ContextCompat.getDrawable(requireContext(),R.drawable.bg_corner_stroke_red_4)
        edtContent.requestFocus()
    }

    override fun onValidSuccess(text: String) {
        tvMessage.visibility = View.GONE
        tvMessageContent.visibility = View.GONE
        edtPhone.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtPhone.context)
        edtContent.background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(edtPhone.context)
        KeyboardUtils.hideSoftInput(edtPhone)

        val intent = Intent(requireContext(),CreateQrCodeSuccessActivity::class.java)
        intent.putExtra(Constant.DATA_1,text)
        startActivityForResult(intent,requestNew)
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
                edtPhone.setText(ContactHelper.getPhone(activity, data))
            }
        }

        if (requestCode == requestNew) {
            if (resultCode == Activity.RESULT_OK) {
                activity?.onBackPressed()
            }
        }
    }
}