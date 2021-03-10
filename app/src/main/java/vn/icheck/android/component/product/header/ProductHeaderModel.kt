package vn.icheck.android.component.product.header

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICBarcodeProductV2
import vn.icheck.android.network.models.ICCriteria
import vn.icheck.android.network.models.ICProductReviews

class ProductHeaderModel(
        var icBarcodeProductV2: ICBarcodeProductV2,
        var icCriteria:ICCriteria?,
        var icProductReviews:ICProductReviews?,
        val headerClickListener: HeaderClickListener
        ):ICViewModel {

    var vt = ICViewTypes.HEADER_TYPE

    override fun getTag(): String {
        return ICViewTags.HEADER_COMPONENT
    }

    override fun getViewType(): Int {
        return vt
    }
}

interface HeaderClickListener{
    /**
     * Dong gop thong tin san pham
     */
    fun contribute()
    /**
     * Show all images
     */
    fun showGallery(productHeaderModel: ProductHeaderModel)
    /**
     * Share product
     */
    fun share(productHeaderModel: ProductHeaderModel)
    /**
     * Show all review
     */
    fun showAllReview(productHeaderModel: ProductHeaderModel)

}