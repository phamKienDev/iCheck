package vn.icheck.android.screen.user.wall

import vn.icheck.android.component.ICViewModel

data class ICWallModel(val data: Any, val type: Int, val mTag: String = ""): ICViewModel {
    override fun getTag(): String {
        return mTag
    }

    override fun getViewType(): Int {
        return type
    }
}