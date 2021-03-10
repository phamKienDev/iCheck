package vn.icheck.android.component.product.noowner

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes

data class NoOwnerModel (
    val listener: NoOWnerListener
) : ICViewModel {

    override fun getTag(): String = ""

    override fun getViewType(): Int = ICViewTypes.NO_OWNER_TYPE

    interface NoOWnerListener {

        fun onContribute()
    }
}