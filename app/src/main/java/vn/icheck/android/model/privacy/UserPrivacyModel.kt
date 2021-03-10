package vn.icheck.android.model.privacy

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes

class UserPrivacyModel(val listData:List<PrivacyDataItem?>):ICViewModel {
    override fun getTag(): String {
        return "user_privacy"
    }

    override fun getViewType(): Int {
        return ICViewTypes.ITEM_PRIVACY_DEFAULT
    }
}