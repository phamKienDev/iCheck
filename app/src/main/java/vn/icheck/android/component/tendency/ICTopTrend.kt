package vn.icheck.android.component.tendency

import vn.icheck.android.network.models.ICExperienceCategory
import vn.icheck.android.network.models.ICPageTrend
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.network.models.ICProductTrend

data class ICTopTrend(
        var listCategory: MutableList<ICExperienceCategory>? = null,
        var listProduct: MutableList<ICProductTrend>? = null,
        var listPageTrend: MutableList<ICPageTrend>? = null,
        var selectedPos: Int = 0,
        var errorMessage: String = ""
)