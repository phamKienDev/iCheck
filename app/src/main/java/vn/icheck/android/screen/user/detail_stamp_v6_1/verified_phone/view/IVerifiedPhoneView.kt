package vn.icheck.android.screen.user.detail_stamp_v6_1.verified_phone.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6_1.ICVariantProductStampV6_1
import vn.icheck.android.network.models.detail_stamp_v6_1.ICVerifiedPhone

/**
 * Created by PhongLH on 12/14/2019.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
interface IVerifiedPhoneView : BaseActivityView {
    fun onErrorPhone(errorMessage: String)
    fun onErrorIntent()
    fun onVerifiedPhoneSuccess(data: ICVerifiedPhone.Data)
    fun onVerifiedPhoneFail()
    fun onGetDataIntentSuccess(idDistributor: Long, productCode: String?, serial: String?, productId: Long?, objVariant: ICVariantProductStampV6_1.ICVariant.ICObjectVariant?, codeStamp: String?)
}