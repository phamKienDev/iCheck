package vn.icheck.android.network.models.history

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.ICDistrict
import vn.icheck.android.network.models.ICLocation
import vn.icheck.android.network.models.ICShop
import java.io.Serializable

data class ICStoreNear(
        @Expose var id: Long? = null,
        @Expose var avatar: String? = null,
        @Expose var name: String? = null,
        @Expose var location: ICLocation? = null,
        @Expose var phone: String? = null,
        @Expose var fax: String? = null,
        @Expose var price: Long? = null,
        @Expose var specialPrice: Long? = null,
        @Expose var district: ICDistrict? = null,
        @Expose var city: ICDistrict? = null,
        @Expose var address: String? = null,
        @Expose var distance: Long? = null
) : Serializable
