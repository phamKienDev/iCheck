package vn.icheck.android.network.api

import com.google.gson.JsonElement
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*
import vn.icheck.android.model.IckLayoutResponse
import vn.icheck.android.model.bookmark.BookmarkHistoryResponse
import vn.icheck.android.model.cart.CartResponse
import vn.icheck.android.model.cart.PurchasedOrderResponse
import vn.icheck.android.model.detail_order.DetailOrderResponse
import vn.icheck.android.network.models.campaign.CampaignResponse
import vn.icheck.android.network.models.campaign.CampaignRewardResponse
import vn.icheck.android.model.category.CategoryAttributesResponse
import vn.icheck.android.model.category.IckCategoryResponse
import vn.icheck.android.model.firebase.LoginDeviceResponse
import vn.icheck.android.model.icklogin.ConfirmOtpResponse
import vn.icheck.android.model.icklogin.RequestOtpResponse
import vn.icheck.android.model.icklogin.IckLoginFacebookResponse
import vn.icheck.android.model.icklogin.IckLoginResponse
import vn.icheck.android.model.icklogin.IckUserInfoResponse
import vn.icheck.android.model.location.CityResponse
import vn.icheck.android.model.loyalty.OpenGiftHistoryResponse
import vn.icheck.android.model.loyalty.ReceivedGiftResponse
import vn.icheck.android.model.loyalty.ShipAddressResponse
import vn.icheck.android.model.page.PageProductResponse
import vn.icheck.android.model.posts.PostResponse
import vn.icheck.android.model.privacy.UserPrivacyResponse
import vn.icheck.android.model.reminders.ReminderResponse
import vn.icheck.android.model.reports.ReportUserCategoryResponse
import vn.icheck.android.model.wall.LayoutResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.models.wall.IcFriendResponse
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ListTypeResponse
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.campaign.DetailRewardResponse
import vn.icheck.android.network.models.firebase.FirebaseToken

interface ICKApi {

    @GET("/login/facebook")
    suspend fun loginFacebook(@Query("token") token: String): IckLoginFacebookResponse

    /** Login get token **/
    @POST("login")
    suspend fun loginICKAcc(@Body body: HashMap<String, Any>): IckLoginResponse

    /** Get user info **/
    @GET("/social/api/users/me")
    suspend fun getUserInfo(): IckUserInfoResponse

    @GET("/social/api/users/profile/{id}")
    suspend fun getProfile(@Path("id") id: Long): IckUserInfoResponse

    @GET("login/otp")
    suspend fun requestLoginOtp(@Query("phoneNumber") phoneNumber: String): RequestOtpResponse

    @POST("/login/otp/confirm")
    suspend fun loginOtp(@Body body: HashMap<String, Any?>): ConfirmOtpResponse

    @GET("/api/noauth/user/password/forgot/request")
    suspend fun requestForgotPw(@Query("phone") phoneNumber: String): RequestOtpResponse

    @POST("api/noauth/user/register/request")
    suspend fun requestRegisterOtp(@Body requestBody: HashMap<String, Any>): RequestOtpResponse

    @GET("/api/noauth/user/register/resent-otp")
    suspend fun resentOtpRegister(@Query("token") token: String?): RequestOtpResponse

    @POST("/api/noauth/user/password/forgot/confirm")
    suspend fun confirmOtpForgotPw(@Body body: HashMap<String, Any?>): ConfirmOtpResponse

    @POST("/api/noauth/user/register/confirm")
    suspend fun confirmOtpRegister(@Body body: HashMap<String, Any?>): ConfirmOtpResponse

    @POST("/api/noauth/user/password/forgot/change")
    suspend fun changePassword(@Body requestBody: HashMap<String, Any?>): RequestOtpResponse

    @POST("/api/user/password")
    suspend fun updatePassword(@Body requestBody: HashMap<String, Any>): RequestOtpResponse

    @PUT("/api/user/updated/password")
    suspend fun updateFirstPassword(@Body requestBody: HashMap<String, Any>): ICResponse<*>

    @GET("/social/api/locations/cities")
    suspend fun getCities(): CityResponse

    @GET("/social/api/locations/districts")
    suspend fun getDistricts(@Query("cityId") cityId: Int?): CityResponse

    @GET("/social/api/locations/wards")
    suspend fun getWards(@Query("districtId") districtId: Int?): CityResponse

    @PUT("/social/api/users/profile")
    suspend fun updateUser(@Body requestBody: HashMap<String, Any?>): RequestOtpResponse

    @GET
    suspend fun getUserProfile(@Url url: String): IckUserInfoResponse

    @GET
    suspend fun getUserFriends(@Url url: String): ResponseBody

    @GET("/social/api/users/{id}/friends")
    suspend fun getUserFriendList(@Path("id") id: Long, @Query("limit") limit: Int, @Query("offset") offset: Int): IcFriendResponse

    @GET(APIConstants.Social.LIST_FRIEND_INVITATION)
    suspend fun getFriendsRequest(@Query("offset") offset: Int, @Query("limit") limit: Int): ICResponse<ICListResponse<ICSearchUser>>

    @GET
    suspend fun getUserFriendList(@Url url: String): ResponseBody

    @GET("/social/api/privacy/user-social")
    suspend fun getUserPrivacy(): UserPrivacyResponse

    @PUT("/social/api/privacy/user-social")
    suspend fun putUserPrivacy(@Body requestBody: HashMap<String, Any>): ResponseBody

    @GET("/social/api/users/{id}/posts")
    suspend fun getUserPosts(@Path("id") id: Long, @Query("limit") limit: Int, @Query("offset") offset: Int): PostResponse

    @GET("/social/api/pages/{id}/highlight-products")
    suspend fun getPageHighlightProducts(@Path("id") pageId: Long, @Query("limit") limit: Int, @Query("offset") offset: Int): PageProductResponse

    @GET("/social/api/loyalty/campaign")
    suspend fun getCampaign(@Query("limit") limit: Int, @Query("offset") offset: Int): CampaignResponse

    @POST("login/anonymous")
    suspend fun loginAnonymous(@Body requestBody: HashMap<String, Any?>):ResponseBody

    @GET("/api/logout")
    suspend fun logout(): ResponseBody

    @POST(APIConstants.Social.UNREGISTER_DEVICE)
    suspend fun logoutDevice(@Body requestBody: HashMap<String, Any?>): ResponseBody

    @POST("/social/api/products/contribution")
    suspend fun contributeProduct(@Body requestBody: HashMap<String, Any?>): ICContribute

    @GET("/social/api/categories/children")
    suspend fun getCategories(@Query("limit") limit: Int, @Query("offset") offset: Int, @Query("level") level: Int): IckCategoryResponse

    @GET("/social/api/categories/children")
    suspend fun getCategories(@Query("limit") limit: Int, @Query("offset") offset: Int, @Query("filterString") filterString: String, @Query("level") level: Int): IckCategoryResponse

    @GET("/social/api/categories/children")
    suspend fun getChildCategories(@Query("parentId") parentId: Long, @Query("limit") limit: Int, @Query("offset") offset: Int, @Query("level") level: Int): IckCategoryResponse

    @GET("/social/api/categories/children")
    suspend fun getChildCategories( @Query("limit") limit: Int, @Query("offset") offset: Int, @Query("level") level: Int): IckCategoryResponse

    @GET("/social/api/categories/children")
    suspend fun getChildCategories( @Query("limit") limit: Int, @Query("offset") offset: Int): IckCategoryResponse

    @GET("/social/api/categories/children")
    suspend fun getChildCategories( @Query("limit") limit: Int, @Query("offset") offset: Int, @Query("filterString") filterString: String): IckCategoryResponse

    @GET("/social/api/categories/children")
    suspend fun getChildCategories(@Query("parentId") parentId: Long, @Query("limit") limit: Int, @Query("offset") offset: Int): IckCategoryResponse

    @GET("/social/api/categories/children")
    suspend fun getChildCategories(@Query("parentId") parentId: Long, @Query("limit") limit: Int, @Query("offset") offset: Int, @Query("filterString") filterString: String): IckCategoryResponse

    @GET("/social/api/categories/{id}/attribute-contribution")
    suspend fun getCategoryAttributes(@Path("id") id: Long): CategoryAttributesResponse

    @GET("/social/api/products/{id}/my-contribution")
    suspend fun getMyContribution(@Path("id") productId: Long): ResponseBody

    @GET("/social/api/products/{id}/my-contribution")
    suspend fun getMyContribution(@Path("id") barcode: String?): ResponseBody

    @GET("/social/api/products/{id}/contribution")
    suspend fun getContribution(@Path("id") productId: Long): ResponseBody

    @GET("/social/api/categories")
    suspend fun getCategoryById(@Query("ids") id: Long): IckCategoryResponse

    @PUT("/social/api/products/contributions/{id}")
    suspend fun updateContribution(@Path("id") id: Long?, @Body requestBody: HashMap<String, Any?>): ICContribute?

    @PUT("/social/api/products/contributions/{barcode}")
    suspend fun updateContribution(@Path("barcode") barcode: String?, @Body requestBody: HashMap<String, Any?>): ICContribute

    @GET("/social/api/layouts")
    suspend fun getLayout(@Query("layout") layout: String, @Query("layoutEntityId") query: String): LayoutResponse

    @GET
    suspend fun getPrivateInfo(@Url url: String?): IckUserInfoResponse

    @GET
    suspend fun getPublicInfo(@Url url: String?): IckUserInfoResponse

    @GET
    suspend fun getPublicFriendList(@Url url: String?): IcFriendResponse

    @GET
    suspend fun getFriendInvitation(@Url url: String?): ICResponse<ICListResponse<ICSearchUser>>

    @GET
    suspend fun getListFriendSuggestion(@Url url: String?): ICResponse<ICListResponse<ICUser>>

    @GET
    suspend fun getUserPosts(@Url url: String?): PostResponse

    @GET("/social/api/report/user")
    suspend fun getReportUserCategory(): ReportUserCategoryResponse

    @POST("/social/api/report/user")
    suspend fun postReportUser(@Body requestBody: HashMap<String, Any?>): ResponseBody

    @HTTP(method = "DELETE", hasBody = true)
    suspend fun deletePost(@Url url: String, @Body body: HashMap<String, Any>): ICResponse<ICCommentPost>

    @PUT("/social/api/relationships/friend-invitation")
    suspend fun addFriend(@Body requestBody: HashMap<String, Any?>): ResponseBody

    @POST("/api/noauth/user/register/facebook")
    suspend fun requestRegisterFacebook(@Body requestBody: HashMap<String, Any?>): RequestOtpResponse

    @GET("/social/api/loyalty/campaign/{id}/reward")
    suspend fun getCampaignRewards(@Path("id") id: String): CampaignRewardResponse

    @GET("/social/api/loyalty/campaign/{id}/open-his")
    suspend fun getOpenedGiftHistory(@Path("id") campaignId: String?): OpenGiftHistoryResponse

    @GET("/social/api/loyalty/reward/item")
    suspend fun getRewardItems(@Query("limit") limit: Int, @Query("offset") offset: Int): ReceivedGiftResponse

    @GET("/social/api/loyalty/campaign/{id}/user-reward-his")
    suspend fun getRewardItems(@Path("id") id: String?, @Query("limit") limit: Int, @Query("offset") offset: Int): ReceivedGiftResponse

    @GET("/social/api/loyalty/reward/item/{id}")
    suspend fun getDetailReward(@Path("id") id: String?): DetailRewardResponse

    @POST(APIConstants.Social.REGISTER_DEVICE)
    suspend fun loginDevice(@Body requestBody: HashMap<String, Any?>): LoginDeviceResponse

    @PUT("/social/api/relationships/un-friend")
    suspend fun unFriendUser(@Body requestBody: HashMap<String, Any?>): ICResponse<*>

    @PUT("/social/api/relationships/follow-user")
    suspend fun unFollowUser(@Body requestBody: HashMap<String, Any?>): ICResponse<*>

    @PUT("/social/api/loyalty/reward/item/cancel")
    suspend fun refuseGift(@Body requestBody: HashMap<String, Any?>): ICResponse<*>

    @GET("/social/api/reminder")
    suspend fun getReminders(): ICResponse<ICListResponse<ReminderResponse>>

    @HTTP(method = "DELETE", path = "/social/api/notifications/reminders/{id}", hasBody = true)
    suspend fun deleteReminder(@Path("id") id: Long, @Body requestBody: HashMap<String, Any?>): ICResponse<*>

    @POST("social/api/reminder/delete")
    suspend fun deleteReminder(@Body requestBody: HashMap<String, Any?>): ICResponse<*>

    @GET("/social/api/cms/order/address")
    suspend fun getAddress(): ICResponse<ICListResponse<ShipAddressResponse>>

    @POST("/social/api/cms/order/address")
    suspend fun createAddress(@Body requestBody: HashMap<String, Any?>): ICResponse<*>

    @PUT("/social/api/cms/order/address/{id}")
    suspend fun updateAddress(@Body requestBody: HashMap<String, Any?>, @Path("id") addressId: Long): ICResponse<*>

    @POST("social/api/loyalty/reward/item/ship")
    suspend fun confirmShipGift(@Body requestBody: HashMap<String, Any?>): ICResponse<*>

    @POST("social/api/cms/order/purchase")
    suspend fun checkoutOrder(@Body requestBody: HashMap<String, Any?>): ICResponse<PurchasedOrderResponse>

    @GET("social/api/cms/order/delivery-charges")
    suspend fun getFee(@Query("type") type:String = "icheck"): ICResponse<JsonElement>

    @GET("/social/api/notifications")
    suspend fun getNotification(@Query("notificationType") notificationType: Int = 4, @Query("offset") offset: Int, @Query("limit") limit: Int = 10): ICResponse<ICListResponse<ICNotification>>

    @GET("social/api/cms/order/cart")
    suspend fun getCart(): ICResponse<List<CartResponse>>

    @GET("social/api/loyalty/reward/item/get-card/{id}")
    suspend fun getCard(@Path("id") id: String): ICResponse<*>

    @POST("social/api/cms/order/cart/up-quantity")
    suspend fun addProductIntoCart(@Body requestBody: HashMap<String, Any?>): ICResponse<*>

    @POST("social/api/cms/order/cart/down-quantity")
    suspend fun moveProductOutOfCart(@Body requestBody: HashMap<String, Any?>): ICResponse<*>

    @POST("social/api/cms/order/cart/delete-item-cart")
    suspend fun deleteItemCart(@Body requestBody: HashMap<String, Any?>): ICResponse<*>

    @POST("social/api/cms/order/purchase")
    suspend fun purchase(@Body requestBody: HashMap<String, Any?>): ICResponse<PurchasedOrderResponse>

    @GET("/social/api/cms/order/{id}")
    suspend fun getDetailOrder(@Path("id") orderId: Long): ICResponse<DetailOrderResponse>

    @PUT("social/api/cms/order/{id}/status")
    suspend fun updateOrderStatus(@Path("id") orderId: Long, @Body requestBody: HashMap<String, Any?>): ICResponse<*>

    @GET("social/api/cms/order/cart/count")
    suspend fun getCartCount(): ICResponse<Int>

    @GET("social/api/users/bookmarks")
    suspend fun getBookMarkHistory(@Query("offset") offset: Int, @Query("limit") limit: Int = 10):ListTypeResponse<BookmarkHistoryResponse>

    @GET("social/api/users/bookmarks")
    suspend fun getBookMarkHistory(@Query("offset") offset: Int, @Query("filterString") filterString: String, @Query("limit") limit: Int = 10):ListTypeResponse<BookmarkHistoryResponse>

    @POST("social/api/products/{id}/bookmark")
    suspend fun bookmarkProduct(@Path("id") productId:Long, @Body requestBody: HashMap<String, Any?> = hashMapOf()):ICResponse<*>

    @POST("social/api/products/{id}/bookmark/delete")
    suspend fun deleteBookMarkProduct(@Path("id") productId:Long, @Body requestBody: HashMap<String, Any?> = hashMapOf()):ICResponse<*>

    @GET("social/api/posts/{id}")
    suspend fun getDetailPost(@Path("id") postId:Long):ICResponse<ICPost>

    @POST("social/api/relationships/phone-contacts")
    suspend fun syncContacts(@Body requestBody: HashMap<String, Any?>):ICResponse<*>

    @PUT("social/api/loyalty/reward/item/get-card/{id}")
    suspend fun getMobileCard(@Path("id") campaignId: String?, @Body requestBody: HashMap<String, Any?>):ICResponse<*>

    @GET("api/user/link-facebook")
    suspend fun linkFacebook(@Query("token") facebookToken:String?):ICResponse<*>

    @PUT("/api/user/firebase/verify")
    suspend fun updateFirebaseToken(@Body requestBody: HashMap<String, Any?>):FirebaseToken

    @PUT("social/api/loyalty/reward/item/mark-used/{id}")
    suspend fun updateUsingState(@Body requestBody: HashMap<String, Any?>, @Path("id") id:String?):ICResponse<*>
}