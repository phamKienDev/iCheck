package vn.icheck.android.component.space

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes

class SpacingModel : ICViewModel {
    override fun getTag(): String {
        return ICViewTags.SPACING_COMPONENT
    }

    override fun getViewType(): Int {
        return ICViewTypes.SPACING_TYPE
    }

}