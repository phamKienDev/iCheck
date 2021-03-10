package vn.icheck.android.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.icheck.android.R
import vn.icheck.android.network.models.ICClientSetting

class BottomModel(val list: MutableList<ICClientSetting>) : ICViewModel {
    override fun getTag(): String {
        return ICViewTags.CONTACT_ICHECK
    }

    override fun getViewType(): Int {
        return ICViewTypes.TYPE_BOTTOM
    }
}