package vn.icheck.android.screen.user.createqrcode.createcontact

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_create_contact_qr_code.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragmentMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.ContactHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.screen.dialog.PermissionDialog
import vn.icheck.android.screen.user.createqrcode.createcontact.presenter.CreateContactQrCodePresenter
import vn.icheck.android.screen.user.createqrcode.createcontact.view.ICreateContactQrCodeView
import vn.icheck.android.screen.user.createqrcode.success.CreateQrCodeSuccessActivity
import vn.icheck.android.util.KeyboardUtils

/**
 * Created by VuLCL on 10/5/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CreateContactQrCodeFragment : BaseFragmentMVVM(), ICreateContactQrCodeView {
    private val requestContact = 1
    private val permissionContact = 1
    private val requestNew = 2

    private val presenter = CreateContactQrCodePresenter(this@CreateContactQrCodeFragment)

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_create_contact_qr_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitView()
    }

    fun onInitView() {
        initToolbar()
        setupView()
        initListener()
        focusView(edtPhone)
    }

    private fun initToolbar() {
        txtTitle.setText(R.string.chia_se_thong_tin_cua_ban)

        imgBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupView() {
        btnCreate.background = ViewHelper.btnPrimaryCorners4(requireContext())
        edtPhone.setCompoundDrawablesWithIntrinsicBounds(null,null, ViewHelper.fillDrawableColor(R.drawable.ic_phonebook_24dp,requireContext()),null)
    }

    private fun initListener() {
        edtPhone.setOnTouchListener { _, motionEvent ->
            val drawableEnd = 2

            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= (edtPhone.right - edtPhone.compoundDrawables[drawableEnd].bounds.width())) {
                    PermissionDialog.checkPermission(context, PermissionDialog.CONTACT, object : PermissionDialog.PermissionListener {
                        override fun onPermissionAllowed() {
                            ContactHelper.pickContact(this@CreateContactQrCodeFragment, requestContact)
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

        btnCreate.setOnClickListener {
            presenter.validData(edtPhone.text.toString().trim(), edtFirstName.text.toString().trim(),
                    edtMiddleName.text.toString().trim(), edtLastName.text.toString().trim(),
                    edtEmail.text.toString().trim(), edtAddress.text.toString().trim(),
                    edtNote.text.toString().trim())
        }
    }

    override fun onInvalidPhoneSuccess() {
        tvMessagePhone.visibility = View.GONE
        edtPhone.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtPhone.context)
    }

    override fun onInvalidPhone(error: String) {
        tvMessagePhone.visibility = View.VISIBLE
        tvMessagePhone.text = error
        edtPhone.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
        edtPhone.requestFocus()
    }

    override fun onInvalidFirstNameSuccess() {
        tvMessageFirstName.visibility = View.GONE
        edtFirstName.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtFirstName.context)
    }

    override fun onInvalidFirstName(error: String) {
        tvMessageFirstName.visibility = View.VISIBLE
        tvMessageFirstName.text = error
        edtFirstName.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
        edtFirstName.requestFocus()
    }

    override fun onInvalidLastNameSuccess() {
        tvMessageLastName.visibility = View.GONE
        edtLastName.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtLastName.context)
    }

    override fun onInvalidLastName(error: String) {
        tvMessageLastName.visibility = View.VISIBLE
        tvMessageLastName.text = error
        edtLastName.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
        edtLastName.requestFocus()
    }

    override fun onInvalidEmail(error: String) {
        tvMessageEmail.visibility = View.VISIBLE
        tvMessageEmail.text = error
        edtEmail.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
        edtEmail.requestFocus()
    }

    override fun onInvalidAddress(error: String) {
        tvMessageAddress.visibility = View.VISIBLE
        tvMessageAddress.text = error
        edtAddress.background = ViewHelper.bgTransparentStrokeAccentRed0_5Corners4(requireContext())
        edtAddress.requestFocus()
    }

    override fun onValidSuccess(text: String) {
        tvMessagePhone.visibility = View.GONE
        tvMessageLastName.visibility = View.GONE
        tvMessageEmail.visibility = View.GONE
        tvMessageAddress.visibility = View.GONE
        edtPhone.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtPhone.context)
        edtLastName.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtPhone.context)
        edtEmail.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtPhone.context)
        edtAddress.background = ViewHelper.bgWhiteStrokeLineColor0_5Corners4(edtPhone.context)

        KeyboardUtils.hideSoftInput(edtPhone)

        val intent = Intent(requireContext(), CreateQrCodeSuccessActivity::class.java)
        intent.putExtra(Constant.DATA_1, text)
        startActivityForResult(intent, requestNew)
    }

    override fun showError(errorMessage: String) {
        requireContext().showLongErrorToast(errorMessage)
    }

    override val mContext: Context?
        get() = requireContext()

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
                val listInfo = ContactHelper.getDetailPhone(activity, data)
                if (!listInfo.isNullOrEmpty()) {
                    edtPhone.setText(listInfo[0])
                    edtLastName.setText(listInfo[1])
                    edtMiddleName.setText(listInfo[2])
                    edtFirstName.setText(listInfo[3])
                    edtEmail.setText(listInfo[4])
                    edtAddress.setText(listInfo[5])
                    edtNote.setText(listInfo[6])
                }
            }
        }

        if (requestCode == requestNew) {
            if (resultCode == Activity.RESULT_OK) {
                activity?.onBackPressed()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        if (event.type == ICMessageEvent.Type.BACK) {
            activity?.onBackPressed()
        }
    }
}