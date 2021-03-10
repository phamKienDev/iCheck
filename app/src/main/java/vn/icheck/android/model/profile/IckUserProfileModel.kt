package vn.icheck.android.model.profile

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.model.icklogin.IckUserInfoResponse

class IckUserProfileModel(val profile: IckUserInfoResponse):ICViewModel {

    var id:Long = 0L
    var sendInvitation = false

    override fun getTag(): String {
        return ICViewTags.PROFILE_USER
    }

    override fun getViewType(): Int {
        return ICViewTypes.PROFILE_USER
    }
}