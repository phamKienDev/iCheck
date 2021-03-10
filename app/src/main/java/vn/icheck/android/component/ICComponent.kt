package vn.icheck.android.component

import vn.icheck.android.component.product_for_you.ICProductForYouMedia
import vn.icheck.android.network.models.*

open class ICComponent(
        var type: Int = 0,
        var widgetID: String = "",
        var title: String? = null,
        var url: String? = null,
        var params: HashMap<String, Any>? = null,
        var product: ICProduct? = null,
        var listProduct: MutableList<ICProduct>? = null,
        var ads: ICAds? = null,
        var listAds: MutableList<ICAds>? = null,
        var reviews: ICProductReviews.ReviewsRow? = null,
        var criteria: ICCriteria? = null,
        var listCategory: MutableList<ICExperienceCategory>? = null,
        var listProductForYou: MutableList<ICProductForYouMedia>? = null,
        var flashSale: ICFlashSale? = null,
        var selectedPos: Int = 0,
        var errorMessage: String = ""
)