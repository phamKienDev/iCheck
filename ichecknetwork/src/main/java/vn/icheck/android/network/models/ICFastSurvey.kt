package vn.icheck.android.network.models

data class ICFastSurvey(
        val shop: ICShop?,
        val content: String?,
        val product1: ICProduct?,
        val product2: ICProduct?,
        val btnLeft: String?,
        val btnRight: String?,
        val isRating: Boolean?,
        val product1Rating: Int?,
        val product2Rating: Int?
)