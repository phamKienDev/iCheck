package vn.icheck.android.component.experience_new_products

import vn.icheck.android.network.models.ICExperienceCategory
import vn.icheck.android.network.models.ICProduct

data class ICExperienceNewProducts(
        var listCategory: MutableList<ICExperienceCategory>? = null,
        var listProduct: MutableList<ICProduct>? = null,
        var selectedPos: Int = 0,
        var errorMessage: String = ""
)