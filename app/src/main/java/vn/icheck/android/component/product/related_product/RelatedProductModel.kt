package vn.icheck.android.component.product.related_product

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICProductTrend

data class RelatedProductModel(
        val type: Int,
        val url: String,
        val params: HashMap<String, Any>?,
        val title: String,
        val listProduct: MutableList<ICProductTrend>
) : ICViewModel {

    override fun getTag(): String {
        return if (type == ICViewTypes.RELATED_PRODUCT_TYPE) {
            ICViewTags.RELATED_PRODUCT
        } else {
            ICViewTags.SAME_OWNER_PRODUCT
        }
    }

    override fun getViewType(): Int = type
}