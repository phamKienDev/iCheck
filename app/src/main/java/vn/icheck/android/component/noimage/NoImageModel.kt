package vn.icheck.android.component.noimage

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes

class NoImageModel:ICViewModel {

    override fun getTag() = ""

    override fun getViewType() = ICViewTypes.HOLDER_NO_IMAGE
}