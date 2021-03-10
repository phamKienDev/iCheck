package vn.icheck.android.network.models.v1

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import vn.icheck.android.network.models.ICCategory
import java.io.Serializable

data class ICBarcodeProductV1(
        @SerializedName("name")
        val name: String,
        @SerializedName("image")
        val image: String,
        @SerializedName("price")
        val price: Long,
        @SerializedName("barcode")
        val barcode: String,
        @SerializedName("attachments")
        val attachments: List<Attachments>,
        @SerializedName("rating")
        val rating: Float,
        @SerializedName("review_count")
        val reviewCount: Long,
        @SerializedName("verified")
        val verified: Boolean,
        @SerializedName("vendors")
        val vendor: List<Vendor>?,
        @SerializedName("owner")
        val owner: Vendor?,
        @SerializedName("unverified_owner")
        val unverifiedOwner: UnverifiedOwner?,
        @SerializedName("id")
        val id: Long,
        @SerializedName("informations")
        val informations: List<Information>?,
        @SerializedName("distributors")
        val distributors: List<Distributors>?,
        @SerializedName("certificates")
        val certificates: List<Certificate>,
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
        val categories: List<ICCategory>,
        val popup: List<PopupItem>?,
        var countReviews: Int = 0,
        var productRate: Float = 0f
) : Serializable {

    data class UnverifiedOwner(
            @Expose
            val id: Long,
            val name: String?,
            val phone: String?,
            val email: String?,
            val tax: String?,
            val address: String?,
            @SerializedName("product_id")
            val productId: Long
    ) : Serializable

    data class PopupItem(
            val id: String,
            val type: Int,
            val banner: String?,
            @SerializedName("htmlContent")
            val htmlContent: String?,
            val attach: PopupAttach?
    ) : Serializable

    class PopupAttach(
            val type: Int,
            val data: PopupData?
    )

    class PopupData(
            val productId: Int,
            val barcode: String
    )

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
            @SerializedName("type")
            val type: String,
            @SerializedName("thumbnails")
            val thumbnails: Thumbnails
    ) : Serializable

    data class Thumbnails(
            @SerializedName("original")
            val original: String,
            @SerializedName("thumbnail")
            val thumbnail: String,
            val small: String?
    ) : Serializable

    data class Vendor(
            @SerializedName("is_verified")
            val isVerified: Boolean,
            @SerializedName("page")
            val vendorPage: VendorPage,
            @SerializedName("id")
            val id: Long,
            @SerializedName("country")
            val country: Country,
            val title: DistributorsTitle?
    ) : Serializable

    data class VendorPage(
            @SerializedName("name")
            val name: String?,
            @SerializedName("verified")
            val verified: Boolean,
            @SerializedName("tax")
            val tax: String?,
            @SerializedName("website")
            val website: String?,
            @SerializedName("address")
            val address: String?,
            @SerializedName("phone")
            val phone: String?,
            @SerializedName("email")
            val email: String?,
            @SerializedName("country")
            val country: Country,
            @SerializedName("id")
            val id: Long
    ) : Serializable

    data class Information(
            @SerializedName("title")
            val title: String,
            @SerializedName("content")
            val content: String,
            @SerializedName("type_id")
            val typeId: Int
    ) : Serializable

    data class Distributors(
            @SerializedName("id")
            val id: Long,
            @SerializedName("is_verified")
            val isVerified: Boolean,
            @SerializedName("title")
            val title: DistributorsTitle?,
            @SerializedName("page")
            val page: VendorPage,
            val role: String
    ) : Serializable

    data class DistributorsTitle(
            @SerializedName("id")
            val id: Long,
            @SerializedName("title")
            val title: String?
    ) : Serializable

    data class Certificate(
            @SerializedName("id")
            val id: Long,
            @SerializedName("thumbnails")
            val thumbnails: Thumbnails
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