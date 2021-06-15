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
import vn.icheck.android.util.ick.rHintText
import vn.icheck.android.util.ick.rText

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
            txtTitle rText R.string.activated_phone_number
            tvMessTitleVerifyPhone rText R.string.enter_the_actived_phone_number_for_this_product_before_updating_new_information
            tvSubTel rText R.string.phone_number
            edtPhoneNumber rHintText R.string.enter_your_phone_number
            btnUpdate rText R.string.update
        } else {
            txtTitle rText R.string.so_dien_thoai_da_kich_hoat
            tvMessTitleVerifyPhone rText R.string.nhap_so_dien_thoai_da_kich_hoat_bao_hanh_truoc_day
            tvSubTel rText R.string.so_dien_thoai_bat_buoc
            edtPhoneNumber rHintText R.string.nhap_so_dien_thoai
            btnUpdate rText R.string.cap_nhat
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
            DialogHelper.showNotification(this, rText(R.string.occurred_please_try_again), false, object : NotificationDialogListener {
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
                val obj = object : NotificationDialog(this, null, rText(R.string.the_entered_phone_number_does_not_match_with_the_activated_phone_number_of_the_stamp_please_try_again), rText(R.string.ok), false) {
                    override fun onDone() {
                        dismiss()
                    }
                }
                obj.show()
                obj.hideTitle()
            } else {
                val obj = object : NotificationDialog(this, null, getString(R.string.so_dien_thoai_da_nhap_ko_khop), rText(R.string.ok), false) {
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
            showShortError(rText(R.string.occurred_please_try_again))
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
