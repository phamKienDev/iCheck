package vn.icheck.android.screen.user.detail_stamp_v6_1.verified_phone

import android.annotation.SuppressLint
import android.content.Intent
import kotlinx.android.synthetic.main.activity_verified_phone.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.dialog.notify.notification.NotificationDialog
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICVariantProductStampV6_1
import vn.icheck.android.network.models.detail_stamp_v6_1.ICVerifiedPhone
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.DetailStampActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.UpdateInformationFirstActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.verified_phone.presenter.VerifiedPhonePresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.verified_phone.view.IVerifiedPhoneView

class VerifiedPhoneActivity : BaseActivity<VerifiedPhonePresenter>(), IVerifiedPhoneView {

    override val getLayoutID: Int
        get() = R.layout.activity_verified_phone

    override val getPresenter: VerifiedPhonePresenter
        get() = VerifiedPhonePresenter(this)

    private var mIdDistributor: Long? = null
    private var mProductCode: String? = null
    private var mSerial: String? = null
    private var mIdProduct: Long? = null
    private var mIdVariant: ICVariantProductStampV6_1.ICVariant.ICObjectVariant? = null
    private var mCodeStamp: String? = null

    @SuppressLint("SetTextI18n")
    override fun onInitView() {
        DetailStampActivity.listActivities.add(this)
        presenter.onGetDataIntent(intent)

        if (DetailStampActivity.isVietNamLanguage == false){
            txtTitle.text = "Activated Phone Number"
            tvMessTitleVerifyPhone.text = "Enter the actived phone number for this product before updating new information"
            tvSubTel.text = "Phone Number"
            edtPhoneNumber.hint = "Enter your phone number"
            btnUpdate.text = "Update"
        } else {
            txtTitle.text = "Số điện thoại kích hoạt"
            tvMessTitleVerifyPhone.text = "Nhập số điện thoại đã kích hoạt bảo hành trước đây\ncho sản phẩm này trước khi cập nhận tông tin mới"
            tvSubTel.text = "Số điện thoại (*)"
            edtPhoneNumber.hint = "Nhập số điện thoại"
            btnUpdate.text = "Cập nhật"
        }

        listener()
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnUpdate.setOnClickListener {
            presenter.onValidPhoneNumber(edtPhoneNumber.text.toString())
        }
    }

    override fun onErrorPhone(errorMessage: String) {
        showShortError(errorMessage)
    }

    override fun onErrorIntent() {
        if (DetailStampActivity.isVietNamLanguage == false) {
            DialogHelper.showNotification(this, "Occurred. Please try again", false, object : NotificationDialogListener {
                override fun onDone() {
                    onBackPressed()
                }
            })
        } else {
            DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                override fun onDone() {
                    onBackPressed()
                }
            })
        }
    }

    override fun onGetDataIntentSuccess(idDistributor: Long, productCode: String?, serial: String?, productId: Long?, variantId: ICVariantProductStampV6_1.ICVariant.ICObjectVariant?, codeStamp: String?) {
        mIdDistributor = idDistributor
        mProductCode = productCode
        mSerial = serial
        mIdProduct = productId
        mIdVariant = variantId
        mCodeStamp = codeStamp
    }

    override fun onVerifiedPhoneSuccess(data: ICVerifiedPhone.Data) {
        if (data.is_true == true) {
            hideSoftKeyboard()
            val intent = Intent(this, UpdateInformationFirstActivity::class.java)
            intent.putExtra(Constant.DATA_1, 1)
            intent.putExtra(Constant.DATA_2, mIdDistributor)
            intent.putExtra(Constant.DATA_3, edtPhoneNumber.text.toString())
            intent.putExtra(Constant.DATA_4, mProductCode)
            intent.putExtra(Constant.DATA_5, mSerial)
            intent.putExtra(Constant.DATA_6, mIdProduct)
            intent.putExtra(Constant.DATA_7, mIdVariant)
            intent.putExtra(Constant.DATA_8, mCodeStamp)
            startActivity(intent)
        } else {
            if (DetailStampActivity.isVietNamLanguage == false) {
                val obj = object : NotificationDialog(this, null, "The entered phone number does not match with the activated phone number of the stamp. Please try again!", "OK", false) {
                    override fun onDone() {
                        dismiss()
                    }
                }
                obj.show()
                obj.hideTitle()
            } else {
                val obj = object : NotificationDialog(this, null, getString(R.string.so_dien_thoai_da_nhap_ko_khop), "OK", false) {
                    override fun onDone() {
                        dismiss()
                    }
                }
                obj.show()
                obj.hideTitle()
            }
        }
    }

    override fun onVerifiedPhoneFail() {
        if (DetailStampActivity.isVietNamLanguage == false) {
            showShortError("Occurred. Please try again")
        } else {
            showShortError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        }
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)
        showShortError(errorMessage)
    }

    override fun onDestroy() {
        super.onDestroy()
        DetailStampActivity.listActivities.remove(this)
    }
}
