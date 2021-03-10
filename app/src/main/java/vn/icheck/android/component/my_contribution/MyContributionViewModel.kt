package vn.icheck.android.component.my_contribution

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.model.category.CategoryAttributesItem

class MyContributionViewModel(val arrayInfo: ArrayList<CategoryAttributesItem>) : ICViewModel {
    var productId:Long? = null
    override fun getTag(): String {
        return "my_contribution"
    }

    override fun getViewType(): Int {
        return ICViewTypes.MY_CONTRIBUTION
    }
}