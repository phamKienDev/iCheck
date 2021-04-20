package vn.icheck.android.component

import vn.icheck.android.network.models.ICClientSetting

class BottomModel(val list: MutableList<ICClientSetting>) : ICViewModel {
    override fun getTag(): String {
        return ICViewTags.CONTACT_ICHECK
    }

    override fun getViewType(): Int {
        return ICViewTypes.TYPE_BOTTOM
    }
}