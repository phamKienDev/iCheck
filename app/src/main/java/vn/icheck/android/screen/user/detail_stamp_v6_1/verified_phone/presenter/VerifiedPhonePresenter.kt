package vn.icheck.android.screen.user.detail_stamp_v6_1.verified_phone.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.ValidHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampInteractor
import vn.icheck.android.network.models.detail_stamp_v6_1.ICVariantProductStampV6_1
import vn.icheck.android.network.models.detail_stamp_v6_1.ICVerifiedPhone
import vn.icheck.android.screen.user.detail_stamp_v6_1.verified_phone.view.IVerifiedPhoneView

/**
 * Created by PhongLH on 12/14/2019.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class VerifiedPhonePresenter(val view: IVerifiedPhoneView) : BaseActivityPresenter(view) {

    private val interactor = DetailStampInteractor()

    private var serial: String? = null

    fun onGetDataIntent(intent: Intent) {
        val mSerial = try {
            intent.getStringExtra(Constant.DATA_1)
        } catch (e: Exception) {
            ""
        }

        val idDistributor = try {
            intent.getLongExtra(Constant.DATA_2, 0L)
        } catch (e: Exception) {
            0L
        }

        val productCode = try {
            intent.getStringExtra(Constant.DATA_3)
        } catch (e: Exception) {
            ""
        }

        val productId = try {
            intent.getLongExtra(Constant.DATA_4, 0L)
        } catch (e: Exception) {
            0L
        }

        val objVariant = try {
            intent.getSerializableExtra(Constant.DATA_5) as ICVariantProductStampV6_1.ICVariant.ICObjectVariant
        } catch (e: Exception) {
            null
        }

        val codeStamp = try {
            intent.getStringExtra(Constant.DATA_8)
        } catch (e: Exception) {
            null
        }

        serial = mSerial?.replace("Serial: ", "")?.replace(" ", "")

        if (serial.isNullOrEmpty()) {
            view.onErrorIntent()
        }

        view.onGetDataIntentSuccess(idDistributor, productCode, serial, productId, objVariant, codeStamp)

    }

    fun onValidPhoneNumber(phone: String) {
        var isValidSuccess = true

        val validPhone = ValidHelper.validPhoneNumber(view.mContext, phone)

        if (validPhone != null) {
            isValidSuccess = false
            view.onErrorPhone(validPhone)
        }

        if (isValidSuccess) {
            if (NetworkHelper.isNotConnected(view.mContext)) {
                showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                return
            }
            onVerifiedPhoneNumber(phone)
        }
    }

    private fun onVerifiedPhoneNumber(phone: String) {
        view.onShowLoading(true)

        interactor.onVerifiedPhoneNumber(serial!!, phone, object : ICApiListener<ICVerifiedPhone> {
            override fun onSuccess(obj: ICVerifiedPhone) {
                view.onShowLoading(false)
                if (obj.data != null) {
                    view.onVerifiedPhoneSuccess(obj.data!!)
                } else {
                    view.onVerifiedPhoneFail()
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onShowLoading(false)
                error?.message?.let {
                    showError(it)
                }
            }
        })
    }
}