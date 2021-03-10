package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICGridBoxShake (
        @Expose var image:String?,
        @Expose var imageUrl:String?,
        @Expose var dynamicImage:String?,
        @Expose var dynamicImageUrl:String?,
        @Expose var id:Int?
) : Serializable