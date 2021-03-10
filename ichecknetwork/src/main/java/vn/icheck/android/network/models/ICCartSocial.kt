package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ICCartSocial : Serializable {

    @Expose
    val shop: ICShopCart? = null

    @Expose
    val products: MutableList<ICCartItem>? = null

    @Expose
    val userId: Long? = null
}

data class ICShopCart(

        @Expose
        val cover: String? = null,

        @Expose
        val name: String? = null,

        @Expose
        val id: Long? = null,

        @Expose
        val avatar: String? = null
) : Serializable

data class ICProductCart(

        @Expose
        val imageUrl: String? = null,

        @Expose
        val name: String? = null,

        @Expose
        val id: Long? = null
) : Serializable

data class ICCartItem(

        @Expose
        val product: ICProductCart? = null,

        @Expose
        val quantity: Int? = null,

        @Expose
        val price: Long? = null,

        @Expose
        val originPrice: Long? = null,

        @Expose
        val productTotal: Long? = null
) : Serializable