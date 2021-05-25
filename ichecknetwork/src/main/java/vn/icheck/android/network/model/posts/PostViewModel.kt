package vn.icheck.android.network.model.posts

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.model.icklogin.IckUserInfoResponse
import vn.icheck.android.network.models.ICPost

class PostViewModel(val postData: ICPost):ICViewModel {
    override fun getTag(): String {
       return ""
    }

    override fun getViewType(): Int {
        return ICViewTypes.ITEM_USER_POST
    }
}