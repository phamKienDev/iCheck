package vn.icheck.android.model.page

import vn.icheck.android.component.ICViewModel

class PageProductViewModel(val type:Int, val listProduct:List<ProductItem?>):ICViewModel {

    override fun getTag(): String {
        return ""
    }

    override fun getViewType(): Int  = type
}