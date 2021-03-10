package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICBarcodeProductV2(
        @SerializedName("name")
        val name: String,
        @SerializedName("image")
        val image: String,
        @SerializedName("price")
        val price: Long,
        @SerializedName("barcode")
        var barcode: String,
        @SerializedName("attachments")
        val attachments: List<Attachments>,
        @SerializedName("rating")
        val rating: Float,
        @SerializedName("review_count")
        val reviewCount: Long,
        @SerializedName("verified")
        val verified: Boolean,
        @SerializedName("vendor")
        val vendor: Vendor?,
        @SerializedName("owner")
        val owner: Vendor?,
        @SerializedName("unverified_owner")
        val unverifiedOwner:UnverifiedOwner?,
        @SerializedName("id")
        val id: Long,
        @SerializedName("informations")
        val informations: List<Information>?,
        @SerializedName("distributors")
        val distributors: MutableList<Distributors>?,
        @SerializedName("certificates")
        val certificates: List<ICCertificate>,
        @SerializedName("type")
        val type: String?,
        @SerializedName("question_count")
        val questionCount: Long,
        @SerializedName("status")
        val status: Int,
        @SerializedName("user_bookmark")
        var userBookmark: UserBookmark?,
        @SerializedName("contribution")
        val contributions: Contributions?,
        @SerializedName("hide_fields")
        val hideFields: List<String>?,
        @SerializedName("disable_contribution")
        val disableContribution: Boolean?,
        @SerializedName("manager")
        val manager: Manager?,
        @SerializedName("alert_message")
        val alertMessage: AlertMessage?,
        @SerializedName("categories")
        val categories:List<ICCategory>
) : Serializable {

    data class UnverifiedOwner(
            @Expose
            val id:Long,
            val name:String?,
            val phone:String?,
            val email:String?,
            val tax:String?,
            val address:String?,
            @SerializedName("product_id")
            val productId:Long
    ):Serializable

    data class Contributions(
            val id: Long,
            var upvotes: Long,
            var downvotes: Long,
            @SerializedName("user")
            val user: ContributeUser,
            @SerializedName("user_vote_type")
            val userVote: Int?

    ) : Serializable

    data class ContributeUser(
            val id: Long,
            val name: String,
            @SerializedName("avatar_thumbnails")
            val thumbnails: Thumbnails
    ) : Serializable

    data class Manager(
            val id: Long,
            val name: String,
            @SerializedName("avatar_thumbnails")
            val avatarThumbnails: Thumbnails
    ) : Serializable

    data class Attachments(
            @SerializedName("thumbnails")
            val thumbnails: Thumbnails
    ) : Serializable

    data class Thumbnails(
            @SerializedName("original")
            val original: String,
            @SerializedName("thumbnail")
            val thumbnail: String,
            val small:String?,
            @SerializedName("medium")
            val medium:String
    ) : Serializable

    data class Vendor(
            @SerializedName("is_verified")
            val isVerified: Boolean,
            @SerializedName("page")
            val vendorPage: ICVendorPage,
            @SerializedName("id")
            val id: Long,
            @SerializedName("country")
            val country: Country
    ) : Serializable

    data class Information(
            @SerializedName("title")
            val title: String,
            @SerializedName("content")
            val content: String,
            @SerializedName("type_id")
            val typeId:Int
    ) : Serializable

    data class Distributors(
            @SerializedName("id")
            val id: Long,
            @SerializedName("is_verified")
            val isVerified: Boolean,
            @SerializedName("title")
            val title: DistributorsTitle?,
            @SerializedName("page")
            val page: ICVendorPage,
            val role: String
    ) : Serializable

    data class DistributorsTitle(
            @SerializedName("id")
            val id: Long,
            @SerializedName("title")
            val title: String?
    ) : Serializable

    data class Country(
            @SerializedName("id")
            val id: Long,
            @SerializedName("name")
            val name: String,
            @SerializedName("code")
            val code: String
    ) : Serializable

    data class UserBookmark(
            val id: Long
    ) : Serializable

    data class AlertMessage(
            @SerializedName("id") val id: Long,
            @SerializedName("title") val title: String,
            @SerializedName("body") val body: String,
            @SerializedName("description") val description: String,
            @SerializedName("created_at") val created_at: String,
            @SerializedName("updated_at") val updated_at: String
    )
}