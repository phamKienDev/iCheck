package vn.icheck.android.component.product.enterprise

import java.io.Serializable

data class ICSocialNetworkModel(
        var title: String? = null,
        var image: Int? = null,
        var social: String? = null
) : Serializable