package vn.icheck.android.screen.user.wall

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes

data class ICWallModel(val data: Any, val type: Int, val mTag: String = ""): ICViewModel {
    override fun getTag(): String {
        return mTag
    }

    override fun getViewType(): Int {
        return type
    }
}