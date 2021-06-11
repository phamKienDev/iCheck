package vn.icheck.android.network.base

import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.ICProductReviews.Comments
import vn.icheck.android.network.models.ICProductReviews.ReviewsRow
import vn.icheck.android.network.models.bookmark.ICBookmarkPage
import vn.icheck.android.network.models.bookmark.ICBookmarkProduct
import vn.icheck.android.network.models.bookmark.ICBookmarkShop
import vn.icheck.android.network.models.campaign.ICGiftOfCampaign
import vn.icheck.android.network.models.chat.DetailSticker
import vn.icheck.android.network.models.chat.StickerWrapper
import vn.icheck.android.network.models.detail_stamp_v6.ICDetailStampV6
import vn.icheck.android.network.models.detail_stamp_v6.ICListHistoryGuaranteeV6
import vn.icheck.android.network.models.detail_stamp_v6.ICStoreStampV6
import vn.icheck.android.network.models.detail_stamp_v6.IC_RESP_UpdateCustomerGuaranteeV6
import vn.icheck.android.network.models.detail_stamp_v6_1.*
import vn.icheck.android.network.models.point_user.*
import vn.icheck.android.network.models.product.detail.ICProductVariant
import vn.icheck.android.network.models.product.report.ICReportContribute
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.network.models.product_need_review.ICProductNeedReview
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone
import vn.icheck.android.network.models.recharge_phone.IC_RESP_Buy_Recharge_Phone
import vn.icheck.android.network.models.recharge_phone.IC_RESP_HistoryBuyTopup
import vn.icheck.android.network.models.stamp_hoa_phat.ICGetIdPageSocial
import vn.icheck.android.network.models.topup.TopupServices
import vn.icheck.android.network.models.upload.UploadResponse
import vn.icheck.android.network.models.v1.*

interface ICNetworkAPI {
    //    /*
//     * Account Kit
//     * */
//    // REGISTER ACCOUNT KIT
//    @POST(APIConstants.AccountKit.REGISTER)
//    Observable<ICNone> registerAccountKit(@Body ICRegisterAccountKit body);
//
//    // RESET PASSWORD
//    @POST(APIConstants.AccountKit.RESET_PASSWORD)
//    Observable<ICNone> resetPasswordAccountKit(@Body ICResetPassword body);
//    /*
//     * End Account Kit
//     * */
    /*
     * User
     * */
    @GET
    fun getProductDetal(@Url url: String): Observable<ICBarcodeProductV1>

    @GET
    fun getListProductQuestions(@Url url: String, @QueryMap queries: HashMap<String, Any?>): Observable<ICListResponse<ICQuestionRow>>

    @GET
    fun getListAnswersByQuestion(@Url url: String, @QueryMap queries: HashMap<String, Any?>): Observable<ICListResponse<ICQuestionsAnswers>>

    @POST
    fun createQuestions(@Url url: String, @Body body: ICReqProductQuestion): Single<ICQuestionRow>

    @POST
    fun createAnswer(@Url url: String, @Body body: ICReqProductAnswer): Single<ICQuestionsAnswers>

    @POST(APIConstants.User.SEND_OTP_RESET_PASSWORD)
    fun sendOtpResetPassword(@Body body: HashMap<String, String>): Observable<ICStatus>

    @POST(APIConstants.User.RESET_PASSWORD)
    fun resetPassword(@Body body: HashMap<String, String>): Observable<ICID>

    @POST(APIConstants.User.CHECK_CREDENTIALS)
    fun checkCredentials(@Body body: HashMap<String, String>): Observable<ICStatus>

    @POST
    fun uploadImageV1(@Url url: String, @Body body: RequestBody): Call<UploadResponse>

    @POST
    fun sendOtpConfirmPhone(@Url url: String, @Body body: HashMap<String, String>): Observable<ICStatus>

    @POST(APIConstants.User.CONFIRM_NEW_PHONE)
    fun confirmNewPhone(@Body body: HashMap<String, String>): Observable<ICUser>

    @POST
    fun confirmPhone(@Url url: String, @Body body: HashMap<String, String>): Observable<ICStatus>

    @POST(APIConstants.User.REGISTER)
    fun registerUser(@Body body: ICReqRegisterUser): Observable<ICNone>

    @PUT(APIConstants.User.UPDATE)
    fun updateUser(@Path("id") id: Long, @Body body: ICReqUpdateUser): Observable<ICUser>

    @POST(APIConstants.User.CHANGE_PASSWORD)
    fun changePassword(@Body body: HashMap<String, String>): Observable<ICUser>

    @get:GET(APIConstants.User.ME)
    val userMe: Observable<ICUser>

    @GET(APIConstants.User.PROFILE)
    fun getUserProfile(@Path("id") id: Long): Observable<ICUser>

    @get:GET(APIConstants.User.ADDRESSES)
    val userAddress: Observable<ICListResponse<ICAddress>>

    @POST(APIConstants.User.ADDRESSES)
    fun createUserAddress(@Body body: ICAddress): Observable<ICAddress>

    @POST(APIConstants.User.DETAIL_ADDRESS)
    fun getUserAddressDetail(@Path("id") id: Long): Call<ICAddress>

    @DELETE(APIConstants.User.DETAIL_ADDRESS)
    fun deleteUserAddress(@Path("id") id: Long): Call<ICRespID>

    @POST(APIConstants.User.VNSHOP_LINK)
    fun getVnShopLink(@Body body: HashMap<String, String>): Observable<ICLink>

    @GET
    fun getListFriendRequest(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICSearchUser>>>

    @PUT(APIConstants.Relationship.FOLLOW_USER)
    fun followUser(@Body body: HashMap<String, Any>): Observable<ICResponse<Boolean>>

    @GET(APIConstants.Relationship.ME_FOLLOW_USER)
    fun getMeFollowUser(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICNotification>>>

    @GET
    fun getListFriendSuggestion(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICUser>>>

    @POST(APIConstants.Relationship.REMOVE_FRIEND_SUGGESTION)
    fun removeFriendSuggestion(@Body params: HashMap<String, Any?>): Observable<ICResponse<List<ICUser>>>

    @POST(APIConstants.User.USER_QUERY)
    suspend fun searchUsers(@Body params: HashMap<String, Any>): ICResponse<ICListResponse<ICSearchUser>>

    @POST
    fun postNotification(@Url url: String, @Body params: HashMap<String, Any>): Observable<ICResponse<Boolean>>

    /*
     * End User
     * */

    /*
     * Auth
     * */
// LOGIN ACCOUNT KIT
    @FormUrlEncoded
    @POST(APIConstants.Auth.LOGIN)
    fun loginUser(@FieldMap fields: HashMap<String, String>): Observable<ICSessionData>

    @POST
    fun loginDevice(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICResponse<ICSessionData>>

    @POST
    suspend fun loginAnonymous(@Url url: String, @Body body: HashMap<String, Any>): ICResponse<ICSessionData>

    // LOGIN FACEBOOK
    @POST(APIConstants.Auth.LOGIN_FACEBOOK)
    fun loginFacebook(@Body body: HashMap<String, String>): Observable<ICSessionData>

    // REFRESH TOKEN
    @FormUrlEncoded
    @POST(APIConstants.Auth.REFRESH_TOKEN)
    fun refreshToken(@FieldMap fields: HashMap<String, Any>): Observable<ICSessionData>

    // LOGIN FACEBOOK
//    @FormUrlEncoded
//    @POST(APIConstants.Auth.REGISTER)
//    fun loginFacebook(@FieldMap fields: HashMap<String, Any>): Observable<ICSessionData>

    /*
     * End Auth
     * */
/*
     * Address
     * */
    @GET(APIConstants.Address.CITIES)
    fun listProvince(@QueryMap params: HashMap<String, Int>): Observable<ICListResponse<ICProvince>>

    @GET(APIConstants.Address.CITIES_V2)
    fun listProvinceV2(@QueryMap params: HashMap<String, Int>): Observable<ICResponse<ICListResponse<ICProvince>>>

    @GET(APIConstants.Address.CITY)
    fun getProvince(@Path("cityId") cityId: Int): Observable<ICProvince>

    @GET(APIConstants.Address.DISTRICTS)
    fun getListDistrict(@QueryMap query: HashMap<String, Int>): Observable<ICListResponse<ICDistrict>>

    @GET(APIConstants.Address.DISTRICT)
    fun getDistrict(@Path("districtId") districtId: Int): Observable<ICDistrict>

    @GET(APIConstants.Address.WARDS)
    fun getListWard(@QueryMap query: HashMap<String, Int>): Observable<ICListResponse<ICWard>>

    @GET(APIConstants.Address.WARD)
    fun getWard(@Path("wardId") wardId: Int): Observable<ICWard>
    /*
     * End Address
     * */

    /*
     * Ads
     * */
// GET BANNER
    @GET(APIConstants.Ads.ITEM)
    fun getHomeTopBanner(@QueryMap queries: HashMap<String, Any>): Observable<ICListResponse<ICAds>>

    @GET
    fun getListSlideBanner(@Url url: String): Observable<ICResponse<ICListResponse<ICAdsNew>>>

    // GET LIST ADS
    @GET(APIConstants.Ads.ITEM)
    fun getListAds(@QueryMap queries: HashMap<String, Any>): Call<ICListResponse<ICAds>>

    // GET LIST ADS
    @GET(APIConstants.Ads.ITEM)
    fun getAds(@QueryMap queries: HashMap<String, Any>): Observable<ICListResponse<ICAds>>

    @GET(APIConstants.Ads.ITEM)
    suspend fun getAdsSuspend(@QueryMap queries: HashMap<String, Any>): ICListResponse<ICAds>

    // GET LIST ADS
    @POST(APIConstants.Ads.IGNORE)
    fun hideAds(@Path("id") id: Long, @Body body: HashMap<String, String>): Observable<Response<Void>>

    // ANSWER ADS
    @POST
    fun answerAds(@Url url: String, @Body body: RequestBody): Observable<Response<Void>>

    @GET
    fun getAdsSurvey(@Url url: String): Observable<ICResponse<ICListResponse<ICSurvey>>>

    @GET
    fun getAdsCollection(@Url url: String): Observable<ICResponse<ICListResponse<ICCollection>>>
    /*
     * End Ads
     * */

    /*
     * Product
     * */
    @GET(APIConstants.Product.LIST)
    fun getListProduct(@QueryMap queries: HashMap<String, Any>): Observable<ICListResponse<ICProduct>>

    @GET
    fun getListProduct(@Url url: String, @QueryMap queries: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICProductTrend>>>

    @GET
    @Headers("X-Authenticated-Userid:1")
    fun getCampaign(@Url url: String, @Query("target") barcode: String): Observable<ICResponse<ICLoyalty>>

    /*
     * End Product
     * */
/*
     * News
     * */
// GET LIST NEWS
    @GET(APIConstants.News.LIST)
    fun getListNews(@QueryMap queries: HashMap<String, Any>): Observable<ICListResponse<ICNews>>

    // GET LIST NEWS CATEGORY
    @GET(APIConstants.News.LIST_CATEGORY)
    fun getListNewsCategory(): Observable<ICResponse<ICListResponse<ICArticleCategory>>>

    @GET(APIConstants.News.LIST_SOCIAL)
    fun getListNewsSocial(@QueryMap queries: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICNews>>>

    @GET
    fun getListNewsSocial(@Url url: String): Observable<ICResponse<ICListResponse<ICNews>>>

    // GET LIST NEWS
    @GET(APIConstants.News.DETAIL_SOCIAL)
    fun getNewsDetailSocial(@Path("id") id: Long): Observable<ICResponse<ICNews>>

    // GET LIST NEWS
    @GET(APIConstants.News.DETAIL)
    fun getNewsDetail(@Path("id") id: Long): Observable<ICNews>

    /*
     * End News
     * */
/*
     * Review
     * */
    @GET(APIConstants.Review.LIST_USEFUL)
    fun getListReviewsUseful(@QueryMap queries: HashMap<String, Any>): Observable<ICListResponse<ICReview>>

    @GET(APIConstants.Product.GET_LIST_REVIEW)
    fun getListReviewProduct(@Path("id") productId: Long, @QueryMap queries: HashMap<String, Any>?): Observable<ICResponse<ICListResponse<ICPost>>>

    @GET(APIConstants.Product.GET_LIST_REVIEW_NOT_ME)
    fun getListReviewNotMe(@Path("id") productId: Long, @QueryMap queries: HashMap<String, Any>?): Observable<ICResponse<ICListResponse<ICPost>>>

    @GET(APIConstants.Product.GET_REVIEW_SUMMARY)
    fun getReviewSummaryProduct(@Path("id") productId: Long): Observable<ICResponse<ICReviewSummary>>

    @GET(APIConstants.Product.GET_LIST_COMMENT_REVIEW)
    fun getListCommentReview(@Path("id") reviewId: Long, @QueryMap queries: HashMap<String, Any>?): Observable<ICResponse<ICListResponse<ICCommentPost>>>

    @GET(APIConstants.Product.GET_LIST_CHILD_COMMENT)
    fun getListRepliesOfComment(@Path("id") reviewId: Long, @QueryMap queries: HashMap<String, Any>?): Observable<ICResponse<ICListResponse<ICCommentPost>>>

    @POST(APIConstants.Product.POST_COMMENT_REVIEW)
    fun postCommentReviewSocail(@Path("id") reviewId: Long, @Body queries: HashMap<String, Any>?): Observable<ICResponse<ICCommentPost>>

    @POST
    fun postCommentReply(@Url url: String, @Body queries: HashMap<String, Any>?): Observable<ICResponse<ICCommentPost>>

    @POST(APIConstants.Product.POST_LIKE_REVIEW)
    fun postLikeReview(@Path("id") reviewId: Long, @Body body: HashMap<String, Any>): Observable<ICResponse<ICNotification>>

    @GET(APIConstants.Product.GET_MY_REVIEW)
    fun getMyReviewProduct(@Path("id") productId: Long, @QueryMap queries: HashMap<String, Any>?): Observable<ICResponse<ICProductMyReview>>

    @GET(APIConstants.Product.GET_DETAIL_REVIEW)
    fun getDetailReview(@Path("id") reviewId: Long): Observable<ICResponse<ICPost>>

    @GET(APIConstants.Product.GET_CRITERIA)
    fun getCriteriaProduct(@Path("id") productId: Long): Observable<ICListResponse<ICCriteriaReview>>


    @POST(APIConstants.Product.TURN_OFF_NOTIFY)
    fun postTurnOffNotify(@Body queries: HashMap<String, Any>?): Observable<ICResponse<Boolean>>

    @HTTP(method = "DELETE", hasBody = true)
    fun deleteComment(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICResponse<ICCommentPost>>

    @HTTP(method = "DELETE", hasBody = true)
    fun deletePost(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICResponse<ICCommentPost>>

    @GET(APIConstants.Product.GET_PRIVACY_POST)
    fun getPrivacyPost(@Query("postId") id: Long?): Observable<ICResponse<ICListResponse<ICPrivacy>>>

    @PUT(APIConstants.Product.PUT_PRIVACY_POST)
    fun postPrivacyPost(@Path("postId") id: Long, @Body queries: HashMap<String, Any>?): Observable<ICResponse<Boolean>>

    /*
     * End Review
     * */
/*
     * Category
     * */
// GET LIST CATEGORIES
    @GET(APIConstants.Category.LIST)
    fun getListCategories(@QueryMap queries: HashMap<String, Any>): Observable<ICListResponse<ICCategory>>

    /*
     * End Category
     * */

    /*
    * Mall Catalog
    **/
    @GET
    fun getListMallCatalog(@Url url: String): Observable<ICResponse<ICListResponse<ICShoppingCatalog>>>

    /*
         * Business
         * */
// GET LIST BUSINESS
    @GET(APIConstants.Business.LIST)
    fun getListBusiness(@QueryMap queries: HashMap<String, String>): Observable<ICListResponse<ICBusiness>>

    @GET(APIConstants.Business.DETAIL)
    fun getInformationBusiness(@Path("id") id: Long): Observable<ICBusinessHeader>

    @GET(APIConstants.Business.LIST_CATEGORY)
    fun getProductCategory(@Path("id") id: Long): Observable<ICListResponse<ICCategory>>

    @POST("bookmarks/pages")
    fun postBookmarks(@Body body: HashMap<String, Any>): Observable<ICBookmarkPage.Rows>

    @DELETE("bookmarks/pages/{bookmark_id}")
    fun deleteBookmarks(@Path("bookmark_id") bookmark_id: Long): Observable<ICBookmarkPage>

    @get:GET("bookmarks/pages")
    val bookmarks: Observable<ICBookmarkPage>
    /*
     * End Business
     * */


    @GET(APIConstants.Product.HISTRORIES)
    fun getListHistoryProduct(@QueryMap queries: HashMap<String, Any>): Observable<ICListResponse<ICHistory_Product>>

    @GET("bookmarks/products")
    fun getProductBookmark(@QueryMap queries: HashMap<String, Any>): Observable<ICListResponse<ICHistory_Product>>

    @GET(APIConstants.Product.HISTRORIES_SHOP)
    fun getListShop(@QueryMap queries: HashMap<String, Any>): Observable<ICListResponse<ICHistory_Shop>>

    @DELETE(APIConstants.Product.DELETE_PRODUCT_BARCODE)
    fun deleteItemProductBarcode(@Path("history_id") id: Long): Observable<ICDeleteItemProductBarcode>

    @DELETE
    fun deleteItemProduct(@Url url: String): Observable<ResponseBody>

    @HTTP(method = "DELETE", path = APIConstants.Product.DELETE_PRODUCT_BARCODE, hasBody = true)
    fun deleteItemProductBarcodeNoLogin(@Path("history_id") id: Long, @Body body: HashMap<String, Any>): Observable<ICDeleteItemProductBarcode>

    //SYNC CONTACTS
    @POST(APIConstants.Chat.SYNC_CONTACTS)
    fun syncContacts(@Body phoneMap: HashMap<String, List<String>>): Observable<ICSyncContacts>

    // GET USER ID
    @GET(APIConstants.Chat.GET_USER)
    fun getUserDetail(@Path("id") id: Int): Observable<ICUser>

    /*
     * Additives
     * */
    @GET(APIConstants.Additives.LIST)
    fun searchAdditives(@QueryMap queries: HashMap<String, Any>): Observable<ICListResponse<ICAdditives>>

    @GET(APIConstants.Additives.DETAIL)
    fun getAdditivesDetail(@Path("id") id: Long): Observable<ICAdditives>

    /*
     * End Additives
     * */
    @GET(APIConstants.Chat.GET_USER)
    fun getUser(@Path("id") id: Long): Observable<ICUserId>

    @GET(APIConstants.Chat.GET_USER)
    suspend fun getUserById(@Path("id") id: Long): ICUserId

    @GET(APIConstants.Chat.GET_USER)
    fun testUser(@Path("id") id: Long): Observable<ResponseBody>

    // GET FOLLOWER
    @GET(APIConstants.Chat.GET_FOLLOWING)
    fun getFollowing(@Query("object_type") type: String, @Query("account_id") id: Long): Observable<ICFollowing>


    @GET(APIConstants.Chat.GET_FOLLOWING)
    suspend fun getFollow(@Query("object_type") type: String, @Query("account_id") id: Long?): ICFollowing


    /*
    * Image
    * */
    @Headers("multipart: true")
    @Multipart
    @POST(APIConstants.Image.UPLOAD)
    fun uploadImage(@Part body: MultipartBody.Part): Call<ICResponse<UploadResponse>>

    @Headers("multipart: true")
    @Multipart
    @POST(APIConstants.Image.UPLOAD)
    suspend fun uploadImageV2(@Part body: MultipartBody.Part): ICResponse<UploadResponse>

    /*
    * End Image
    * */

    //SEND CHAT MESSAGE
    @POST("messages")
    fun sendMsg(@Body msg: HashMap<String, Any>): Single<ICChatCodeResponse>

    @POST("messages")
    suspend fun sendChat(@Body msg: HashMap<String, Any>): ICChatCodeResponse

    //CREATE GROUP CHAT
    @POST("rooms")
    fun createRoom(@Body body: HashMap<String, Any>): Single<ICChatCodeResponse>

    @POST("rooms")
    suspend fun createRoomChat(@Body body: HashMap<String, Any>): ICChatCodeResponse

    @GET("sticker-packages")
    suspend fun getStickerPackages(): StickerWrapper

    @GET("sticker-packages/{id}/stickers")
    suspend fun getStickers(@Path("id") id: String): DetailSticker

    @GET(APIConstants.Chat.GET_USER)
    suspend fun getUserById(@Path("id") id: Long?): ICUserId

    //INVITE USER TO ROOM
    @POST("rooms/{roomId}/invite")
    fun inviteGroupRoom(@Body body: HashMap<String, Any>, @Path("roomId") roomId: String): Observable<ICChatCodeResponse>

    //KICK USER FROM ROOM
    @POST("rooms/{roomId}/kick")
    fun kickGroupRoom(@Body body: HashMap<String, Any>, @Path("roomId") roomId: String): Observable<ICChatCodeResponse>

    //LEAVE ROOM
    @POST("rooms/{roomId}/leave")
    fun leaveGroupRoom(@Path("roomId") roomId: String): Observable<ICChatCodeResponse>

    //LEAVE ROOM
    @POST("rooms/{roomId}/leave")
    suspend fun leaveGroupRoomSp(@Path("roomId") roomId: String): ICChatCodeResponse

    //READ ROOM
    @POST("rooms/{roomId}/read")
    fun readRoom(@Path("roomId") roomId: String): Single<ResponseBody>

    @POST("rooms/{roomId}/read")
    suspend fun markAsRead(@Path("roomId") roomId: String): ResponseBody

    // Get list mission
    @get:GET("user/loyalty/mission")
    val missionsList: Observable<ICMissionList>

    // Get list mission
    @GET(APIConstants.Social.LIST_MISSION)
    fun getListMission(@Path("id") id: String, @QueryMap queries: HashMap<String, Any?>): Observable<ICResponse<ICListResponse<ICMission>>>

    // Get list mission active
    @GET(APIConstants.Social.LIST_MISSION_ACTIVE)
    fun getListMissionActive(@Path("id") id: String, @QueryMap queries: HashMap<String, Any?>): Observable<ICResponse<ICListResponse<ICMission>>>

    // Get mission detail
    @GET(APIConstants.Campaign.MISSION_DETAIL)
    fun getMissionsDetail(@Path("id") id: String): Observable<ICResponse<ICMissionDetail>>

    /*
     * Facebook
     * */
    @POST(APIConstants.Facebook.MAPPING)
    fun mappingFacebook(@Body body: HashMap<String, Any>): Observable<ICRespMappingFacebook>

    /*
     * End Facebook
     * */
//CAMPAIGN
    @GET(APIConstants.Campaign.LIST_CAMPAIGN)
    fun getListCampaign(@QueryMap queries: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICCampaign>>>

    @GET(APIConstants.Campaign.CAMPAIGN_ONBOARDING)
    fun getCampaignOnboarding(@Path("id") id: String): Observable<ICResponse<ICCampaignOnboarding>>

    @GET(APIConstants.Campaign.INFO_CAMPAIGN)
    fun getInfoCampaign(@Path("id") id: String): Observable<ICResponse<ICCampaign>>

    @POST(APIConstants.Campaign.CAMPAIGN_ONBOARDING)
    fun postCampaignOnboarding(@Path("id") id: String, @Body hashMap: HashMap<String, Any>): Observable<ICResponse<Any>>

    @get:GET(APIConstants.Campaign.SUMMARY)
    val summary: Observable<ICSummary>

    @get:GET("gift-exchange")
    val listGift: Observable<ICListGift>

    @POST("histories/products")
    fun postHistory(@Body body: HashMap<String, Any>): Single<ICPostHistory>

    @PUT(APIConstants.Campaign.REFUSE_GIFT)
    fun refuseGift(@Body body: HashMap<String, Any>): Observable<ICResponseCode>

    /*
     * Setting
     * */
    @get:GET(APIConstants.Settings.SETTING)
    val getClientSetting: Observable<ICClientSetting>

    @POST(APIConstants.Settings.NOTIFY_SETTING)
    fun postNotifySetting(@Body body: HashMap<String, Any>): Observable<ICResponse<ICSetting>>

    @GET(APIConstants.Settings.NOTIFY_SETTING)
    fun getNotifySetting(): Observable<ICResponse<ICSetting>>

    @GET
    suspend fun getThemeSetting(@Url url: String): ICResponse<ICThemeSetting>

    @GET(APIConstants.Settings.SETTING_SOCIAL)
    fun getSystemSetting(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICClientSetting>>>

    @GET
    suspend fun getClientSettingSocial(@Url url: String, @QueryMap params: HashMap<String, Any>): ICResponse<ICListResponse<ICClientSetting>>

    @GET
    suspend fun getConfigUpdateApp(@Url url: String, @QueryMap params: HashMap<String, String>): ICResponse<ICConfigUpdateApp>

    /*
     * End Setting
     * */
/*
     * Shop
     * */
    @GET(APIConstants.Shop.SCAN_BUY)
    fun getListShopVariant(@Path("barcode") barcode: String, @QueryMap params: HashMap<String, Any>): Observable<ICShopVariant>


    @GET(APIConstants.Shop.DETAIL)
    fun getShopDetail(@Path("id") id: Long): Observable<ICShop>

    @GET(APIConstants.Shop.LIST_PRODUCT)
    fun getShopProduct(@QueryMap params: HashMap<String, Any?>): Observable<ICListResponse<ICProduct>>

    @POST("bookmarks/shops")
    fun postFollowShop(@Body body: HashMap<String, Any?>): Observable<ICBookmarkShop.Rows>

    @DELETE("bookmarks/shops/{bookmark_id}")
    fun deleteFollowShop(@Path("bookmark_id") bookmark_id: Long): Observable<ResponseBody>

    @get:GET("bookmarks/shops")
    val follow: Observable<ICBookmarkShop>

    @POST(APIConstants.Shop.REVIEW)
    fun reviewShop(@Body body: ICReqShopReview): Observable<ICRespShopReview>

    @GET(APIConstants.Shop.LIST_CRITERIA)
    fun getShopCriterias(@Path("id") id: Long): Observable<ICListResponse<ICCriteriaShop>>

    /*
     * End shop
     * */
/*
     * Cart
     * */
    @GET
    fun listCart(@Url url: String): Observable<ICRespCart>

    @POST(APIConstants.Cart.ADD_CART_ITEM)
    fun addCart(@Body params: HashMap<String, Any>): Observable<ICRespCart>

    @POST(APIConstants.Cart.ADD_CART_ITEM)
    fun addRxCart(@Body params: HashMap<String, Any>): Single<Response<Void>>

    @POST(APIConstants.Cart.ADD_CART_ITEM)
    suspend fun addProductToCart(@Body params: HashMap<String, Any>): ResponseBody

    /*
     * End Cart
     * */

    /*
     * Stamp V6.1
     * */
    @GET
    fun scanBarcodeByStamp(@Url url: String): Observable<ICBarcodeProductV1>

    @POST
    suspend fun getDetailStampHoaPhat(@Url url: String, @Body body: HashMap<String, Any>): ICDetailStampV6_1

    @POST
    fun getDetailStamp(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICDetailStampV6_1>

    @POST
    suspend fun getStampDetail(@Url url: String, @Body body: JsonObject): ICResponse<ICStampV61>

    @GET
    fun getListNoteHistoryGuarantee(@Url url: String, @QueryMap queries: HashMap<String, Any>): Observable<ICResp_Note_Guarantee>

    //More product verified distributor
    @GET
    fun getListMoreProductVerifiedDistributor(@Url url: String, @QueryMap queries: HashMap<String, Any>): Observable<ICMoreProductVerified>

    @GET
    fun getInformationProduct(@Url url: String): Observable<IC_RESP_InformationProduct>

    @GET
    fun getVariantProduct(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICVariantProductStampV6_1>

    @GET
    suspend fun getProductVariant(@Url url: String, @QueryMap params: HashMap<String, Any>): ICResponse<ICVariantProductStampV6_1>

    @GET
    suspend fun getGuaranteeVariant(@Url url: String): ICResponse<MutableList<ICFieldGuarantee>>

    @GET
    fun getFieldListGuarantee(@Url url: String): Observable<ICResponse<MutableList<ICFieldGuarantee>>>

    //More product verified vendor
    @GET()
    fun getListMoreProductVerifiedVendor(@Url url: String, @QueryMap queries: HashMap<String, Any>): Observable<ICMoreProductVerified>

    //History Guanrantee
    @GET
    fun getListHistoryGuarantee(@Url url: String): Observable<ICHistoryGuarantee>

    @GET
    suspend fun getWarrantyHistory(@Url url: String): ICResponse<MutableList<ICListHistoryGuarantee>>

    //Verified Number Guarantee
    @GET
    fun getVerifiedNumberPhone(@Url url: String): Observable<ICVerifiedPhone>

    //Get Detail Customer Guarantee
    @GET
    fun getDetailCustomerGuarantee(@Url url: String): Observable<ICDetailCustomerGuranteeVerified>

    @GET
    suspend fun getGuaranteeCustomerDetail(@Url url: String): ICResponse<ICGuaranteeCustomerDetail>

    //Get name city
    @GET
    fun getNameCity(@Url url: String): Call<ICNameCity>

    //Get name districts
    @GET
    fun getNameDistrict(@Url url: String): Call<ICNameDistricts>

    //Update information customer guarantee
    @PUT
    fun updateInformationCustomerGuarantee(@Url url: String, @Body body: HashMap<String, Any>): Observable<IC_RESP_UpdateCustomerGuarantee>

    //Get Shop Variant
    @GET
    fun getShopVariant(@Url url: String, @QueryMap params: HashMap<String, Any>): Call<ICListResponse<ICShopVariantStamp>>

    @GET
    fun configErrorStamp(@Url url: String): Call<IC_Config_Error>

    @GET
    suspend fun getStampConfig(@Url url: String): ICResponse<ICStampConfig>

    /*
     * End Stamp V6.1
     * */

    //List Gift Store
    @GET(APIConstants.GiftStore.GET_LIST_GIFT_STORE)
    fun getListGiftStore(@QueryMap params: HashMap<String, Any>): Observable<ICListResponse<ICListGifExchange>>

    //Get Detail Gift Store
    @GET(APIConstants.GiftStore.GET_DETAIL_GIFT_STORE)
    fun getDetailGiftStore(@QueryMap params: HashMap<String, Any>): Observable<ICDetailGiftStore>

    @GET(APIConstants.GiftStore.GET_PRODUCT_DETAIL_OF_STORE)
    fun getDetailStore(@Path("id") id: Long): Observable<ICResponse<ICStoreiCheck>>

    @POST(APIConstants.GiftStore.ADD_TO_CART)
    fun addToCart(@Body body: HashMap<String, Any>): Observable<ICResponse<Int>>

    @GET(APIConstants.GiftStore.GET_LIST_PRODUCT_OF_STORE)
    fun getListStore(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICStoreiCheck>>>

    //List Topup Service
    @GET(APIConstants.Topup.GET_LIST_TOPUP_SERVICE)
    fun getListTopupService(@QueryMap params: HashMap<String, Any>): Observable<ICListResponse<ICRechargePhone>>

    //List Topup Service v2
    @GET(APIConstants.Topup.GET_LIST_TOPUP_SERVICE_V2)
    fun getListTopupServiceV2(): Observable<ICResponse<ICRechargeThePhone>>

    //List Payment Type
    @GET(APIConstants.Topup.LIST_PAYMENT_TYPE)
    fun getListPaymentType(): Observable<ICResponse<ICListResponse<ICRechargePhone>>>

    //Detail Card
    @POST(APIConstants.Topup.DETAIL_CARD)
    fun getDetailCard(@Body body: HashMap<String, Any>): Observable<ICResponse<ICRechargePhone>>

    //VnPay Card
    @POST(APIConstants.Topup.VN_PAY)
    fun vnpayCard(@Body body: HashMap<String, Any>): Observable<ICResponse<ICRechargePhone>>

    //Buy Card
    @POST(APIConstants.Topup.BUY_CARD)
    fun buyCard(@Body body: HashMap<String, Any>): Observable<ICResponse<ICRechargePhone>>

    //Rẹcharge Card
    @POST(APIConstants.Topup.RECHARGE_CARD)
    fun rechargeCard(@Body body: HashMap<String, Any>): Observable<ICResponse<ICRechargePhone>>

    //Buy Topup Service
    @POST(APIConstants.Topup.BUY_TOPUP_SERVICE)
    fun buyTopupService(@Body body: HashMap<String, Any>): Observable<IC_RESP_Buy_Recharge_Phone>

    //History Buy Topup
    @GET(APIConstants.Topup.GET_LIST_HISTORY_BUY_TOPUP)
    fun getHistoryBuyTopup(@QueryMap params: HashMap<String, Any>): Observable<ICListResponse<IC_RESP_HistoryBuyTopup>>

    //History Buy Topup V2
    @GET(APIConstants.Topup.GET_LIST_HISTORY_BUY_TOPUP_V2)
    fun getHistoryBuyTopupV2(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICRechargePhone>>>

    //History Loaded Topup V2
    @GET(APIConstants.Topup.GET_LIST_HISTORY_LOADED_TOPUP_V2)
    fun getHistoryLoadedTopupV2(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICRechargePhone>>>

    //Tick Use Topup
    @POST(APIConstants.Topup.TICK_USE_TOPUP)
    fun onTickUseTopup(@Path("id") id: String): Observable<IC_RESP_HistoryBuyTopup>

    //Tick Use Topup
    @POST(APIConstants.Topup.TICK_USE_TOPUP_V2)
    fun onTickUseTopup(@Body body: HashMap<String, Any>): Observable<ICResponse<ICNone>>

    /*
     * Stamp V6
     * */
    @POST(APIConstants.StampV6.DETAIL_STAMP_QRM)
    fun getDetailStampQRMV6(@Body body: HashMap<String, Any>): Observable<ICDetailStampV6>

    @POST(APIConstants.StampV6.DETAIL_STAMP_QRI)
    fun getDetailStampQRIV6(@Body body: HashMap<String, Any>): Observable<ICDetailStampV6>

    @GET(APIConstants.StampV6.DETAIL_PRODUCT_BY_SKU)
    fun getDetailProductBySku(@Path("barcode") barCode: String): Observable<ICBarcodeProductV1>

    @GET(APIConstants.StampV6.GET_HISTORY_GUARANTEE_V6)
    fun getListHistoryGuaranteeV6(@Path("qrm") qrm: String, @QueryMap params: HashMap<String, Any>): Observable<ICListHistoryGuaranteeV6>

    @GET(APIConstants.StampV6.GET_LIST_STORE_V6)
    fun getListStoreStampV6(@Path("id") id: String): Observable<ICStoreStampV6>

    @POST(APIConstants.StampV6.UPDATE_INFORMATION_CUSTOMER_GUARANTEE_V6)
    fun updateInformationCustomerGuaranteeV6(@Body body: HashMap<String, Any>): Observable<IC_RESP_UpdateCustomerGuaranteeV6>

    /*
     * End Stamp V6
     * */
/*
     * Stamp V5
     * */
    @POST(APIConstants.StampV5.DETAIL_STAMP)
    fun getDetailStampV5(@Body body: HashMap<String, Any>): Observable<ICDetailStampV6>

    /*
     * End Stamp V5
     * */
//    @GET("scan/{barcode}")
//    fun postBarcode(@Path("barcode") barCode: String?): Single<ICBarcodeProduct>

    @GET
    fun getListDistrictStamp(@Url url: String, @QueryMap query: HashMap<String, Int>): Observable<ICDistrictStamp>

    @GET
    fun listProvinceStamp(@Url url: String): Observable<ICProvinceStamp>

    @GET()
    suspend fun scanBarcode(@Url url: String): ICBarcodeProductV1

    @GET("scan/{barcode}")
    suspend fun scanBarcodeChatV2(@Path("barcode") barCode: String?): ICBarcodeProductV1

    @GET
    suspend fun getProductQuestions(@Url url: String, @Query("product_id") productId: Long?, @Query("include") include: String = "answers", @Query("skip_hidden") skipHidden: Boolean = true): ICProductQuestions

    @GET("scan/{barcode}")
    fun scanBarcode1(@Path("barcode") barCode: String?): Observable<ICBarcodeProductV1>

    @GET("products/{barcode}")
    suspend fun productById(@Path("barcode") barCode: Long?): ICBarcodeProductV1

    @GET("scan/{barcode}")
    suspend fun scanBarcodeIsScan(@Path("barcode") barCode: String?, @Query("isScan") isScan: Boolean): ICBarcodeProductV1

    @GET("scan/{barcode}")
    suspend fun testBarcode(@Path("barcode") barCode: String?): ResponseBody


    @GET("criteria/product/{id}")
    suspend fun testCriteria(@Path("id") id: Long?): ResponseBody

    @GET("criteria/product/{id}")
    suspend fun getProductCriteria(@Path("id") id: Long?): ICCriteria

    @GET
    suspend fun getProductCriteria(@Url url: String): ICCriteria

    @GET("criteria/product/{id}")
    fun getProductCriteria1(@Path("id") id: Long?): Observable<ICCriteria>

    @GET
    fun getProductCriteria1(@Url url: String): Observable<ICCriteria>

    @GET("criteria/product/{id}/reviews")
    suspend fun getProductCriteriaReviews(@Path("id") id: Long?): ICProductReviews

    @GET("criteria/product/{id}/reviews")
    fun getProductCriteriaReviews1(@Path("id") id: Long?, @Query("limit") limit: Int, @Query("offset") offset: Int): Observable<ICProductReviews>

    @GET
    fun getProductCriteriaReviews1(@Url url: String, @Query("limit") limit: Int, @Query("offset") offset: Int): Observable<ICProductReviews>

    @GET("criteria/product/{id}/reviews")
    suspend fun testProductCriteriaReviews(@Path("id") id: Long?): ResponseBody

    @GET("criteria/product/{id}/reviews")
    suspend fun getProductReview(@Path("id") id: Long?): ICProductReviews

    @GET
    suspend fun getProductReview(@Url url: String): ICProductReviews

    @GET
    suspend fun getSameOwnerProduct(@Url url: String, @Query("owner_id") ownerId: Long?, @Query("empty_image") emptyImage: Int): ICRelatedProductV1

    @GET
    suspend fun getProductQuestionAnswer(@Url url: String): ICProductQuestions

    @POST(APIConstants.Product.POST_TRANSPARENCY)
    fun postTransparencySocial(@Path("productId") productId: Long, @Body body: HashMap<String, Boolean>): Observable<ICResponse<ICTransparency>>

    /**
     * Bookmark a product
     */
    @POST("bookmarks/products")
    suspend fun bookmarkProduct(@Body body: HashMap<String, Any?>): ICBarcodeProductV1.UserBookmark

    @GET("bookmarks/products")
    suspend fun getBookmarkProduct(@Query("lat") lat: Double, @Query("lon") lon: Double): ICBookmarkProduct

    @GET("bookmarks/products")
    suspend fun getBookmarkProductNoLatLng(): ICBookmarkProduct


    @DELETE("bookmarks/products/{bookmarkId}")
    suspend fun deleteProductBookmark(@Path("bookmarkId") bookmarkId: String): ResponseBody

    @DELETE("bookmarks/shops/{bookmarkId}")
    suspend fun deleteShopBookmark(@Path("bookmarkId") bookmarkId: Long?): ResponseBody

    @DELETE("bookmarks/pages/{bookmarkId}")
    suspend fun deletePageBookmark(@Path("bookmarkId") bookmarkId: Long?): ResponseBody

    /**
     * Get product bookmark list
     */
    @GET("bookmarks/shops")
    fun getBookmarkShops(@Query("limit") limit: Int, @Query("offset") offset: Int): Single<ICBookmarkShop>

    @GET("bookmarks/shops")
    suspend fun getBookmarkShop(): ResponseBody

    @GET("bookmarks/shops")
    fun testBookmarkShops(): Single<ResponseBody>

    @POST(APIConstants.Checkout.CHECKOUTS)
    fun createCheckout(@Body body: ICReqCheckout): Single<ICCheckout>

    @POST(APIConstants.Checkout.COMPLETE)
    fun completeCheckout(@Body body: ICReqCheckout): Single<ICRespCheckoutCart>

    /**
     * Post
     */
    @GET(APIConstants.Post.GET_POST_DETAIL)
    fun getPostDetail(@Path("id") id: Long): Observable<ICResponse<ICPost>>

    @GET(APIConstants.Post.GET_LIST_COMMENTS_OF_POST)
    fun getListCommentsOfPost(@Path("id") id: Long, @QueryMap queries: HashMap<String, Any>?): Observable<ICResponse<ICListResponse<ICCommentPost>>>

    @POST(APIConstants.Post.POST_LIKE_COMMENT)
    fun postLikeComment(@Path("id") commentId: Long, @Body body: HashMap<String, Any>): Observable<ICResponse<ICNotification>>

    @POST
    fun postCommentPost(@Url url: String, @Body queries: HashMap<String, Any>?): Observable<ICResponse<ICCommentPost>>

    /**
     * Get product bookmark list
     */
    @GET("bookmarks/pages")
    suspend fun bookmarkPages(): ICBookmarkPage

    @GET("linker.php")
    fun testShareLink(@Query("id") productId: Long?, @Query("type") type: String): Single<ICShare>

    @GET("criteria/reviews/{review_id}/comment")
    fun getComments(@Path("review_id") reviewId: Long): Single<ICComments>

    @GET
    fun getComments(@Url url: String): Single<ICComments>

    @GET("criteria/reviews/{review_id}/comment")
    fun getComments1(@Path("review_id") reviewId: Long, @Query("limit") limit: Int, @Query("offset") offset: Int): Observable<ICListResponse<Comments>>

    @GET
    fun getComments1(@Url url: String, @Query("limit") limit: Int, @Query("offset") offset: Int): Observable<ICListResponse<Comments>>

    @GET("criteria/reviews/{review_id}/comment")
    suspend fun getCommentReviews(@Path("review_id") reviewId: Long): ResponseBody

    @POST("criteria/customer-review-product/{id}/comment")
    fun postCommentReview(@Path("id") reviewId: Long, @Body message: HashMap<String, Any>): Single<Comments>

    @POST("criteria/customer-review-product/{id}/comment")
    fun postCommentReview1(@Path("id") reviewId: Long, @Body message: HashMap<String, Any?>): Observable<Comments>

    @POST
    fun postCommentReview1(@Url url: String, @Body message: HashMap<String, Any?>): Observable<Comments>

    @POST("criteria/review")
    suspend fun reviewCriteria(@Path("id") reviewId: Long, @Body message: HashMap<String, Any>): ICCriteria

    @POST("criteria/customer-review-product/{id}/comment")
    suspend fun commentReview(@Path("id") reviewId: Long, @Body message: HashMap<String, Any>): ResponseBody

    @POST("criteria/customer-review-product/{id}/action")
    fun postVoteReview(@Path("id") reviewId: Long, @Body body: HashMap<String, Any?>): Single<Comments>

    @POST
    fun postVoteReview(@Url url: String, @Body body: HashMap<String, Any?>): Single<Comments>

    @POST("criteria/customer-review-product")
    fun postProductReview(@Body body: HashMap<String, Any?>): Single<ReviewsRow>

    @POST("criteria/customer-review-product")
    fun postProductReview1(@Body body: HashMap<String, Any?>): Observable<ReviewsRow>

    @POST
    fun postProductReview1(@Url url: String, @Body body: HashMap<String, Any?>): Observable<ReviewsRow>

    @POST("criteria/customer-review-product")
    suspend fun postProductReviews(@Body body: HashMap<String, Any?>): ResponseBody

    // GET LIST ADS
    @GET(APIConstants.Ads.ITEM)
    fun getAdsByPos(@Query("position") pos: String, @Query("tag") tag: String): Single<ResponseBody>

    // GET LIST ADS
    @GET(APIConstants.Ads.ITEM)
    fun testAdsByPos(@Body pos: HashMap<String, Any>): Single<ResponseBody>

    /**
     * Notification
     */
    @GET
    fun getListNotification(@Url url: String, @QueryMap queries: HashMap<String, Any>): Observable<ICLayoutData<ICListResponse<ICNotification>>>

    @GET
    fun getListNotification(@Url url: String): Observable<ICLayoutData<ICListResponse<ICNotification>>>


    @POST(APIConstants.Notify.MARK_READ)
    fun markReadNotification(@Body body: HashMap<String, Any?>): Observable<ICResponse<Boolean>>

    @POST
    fun subcribeNotification(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICResponse<Boolean>>

    @HTTP(method = "DELETE", path = APIConstants.Notify.DELETE)
    fun deleteNotification(@Path("id") id: Long): Observable<ICResponse<Long>>

    @HTTP(method = "DELETE", path = APIConstants.Notify.DELETE, hasBody = true)
    fun deleteNotification(@Path("id") id: Long, @Body body: HashMap<String, Any?>): Observable<ICResponse<Any>>

    /**
     * End
     */

    /*
    * Point
    * */
    @GET(APIConstants.Point.COINT_HISTORY)
    fun getCointHistory(@QueryMap queries: HashMap<String, Any?>): Observable<ICResponse<ICListResponse<ICCoinHistory>>>

    /*
     * End Point
     * */
    @GET("topups/services")
    fun getTopupsServices(@Query("type") type: String): Single<TopupServices>

    @GET("topups/services")
    fun getListBuyCardService(@Query("type") type: String): Observable<ICListResponse<ICRechargePhone>>

    @POST("topups/buy-epin")
    fun postBuyEpin(@Body requestBody: HashMap<String, Any>): Single<ICBuyEpin>

    @POST("topups/buy-epin")
    fun testBuyEpin(@Body requestBody: HashMap<String, Any>): Single<ResponseBody>

    // Get distributor title
    @GET("product-information-types")
    fun testInformationTypes(): Single<ICInformationTitles>

    // Create product
    @PUT("products/{productId}")
    fun updateProduct(@Body requestBody: HashMap<String, Any>, @Path("productId") productId: String): Single<ResponseBody>

    @POST("contributions")
    fun postContribution(@Body productBody: HashMap<String, Any>): Single<ResponseBody>

    @POST("contributions")
    suspend fun postContributionKtx(@Body productBody: HashMap<String, Any>): Response<Void>

    @POST("contributions")
    suspend fun postContriKtx(@Body productBody: HashMap<String, Any>): ResponseBody

    @POST("contributions/votes")
    suspend fun voteContribution(@Body productBody: HashMap<String, Any>): ResponseBody

    @HTTP(method = "DELETE", path = "contributions/votes", hasBody = true)
    suspend fun deleteVoteContribution(@Body productBody: HashMap<String, Any>): ResponseBody

    // Get spop variants
    @GET("scan/{barcode}/variants")
    fun getProductVariants(@Path("barcode") barcode: String,
                           @Query("lat") lat: Double,
                           @Query("lon") lon: Double): Single<ICProductVariant>

    @GET("scan/{barcode}/variants")
    suspend fun getProductDetailVariants(@Path("barcode") barcode: String,
                                         @Query("lat") lat: Double,
                                         @Query("lon") lon: Double): ICProductVariant

    @GET("scan/{barcode}/variants")
    suspend fun testProductDetailVariants(@Path("barcode") barcode: String,
                                          @Query("lat") lat: Double,
                                          @Query("lon") lon: Double): ResponseBody

    /*
     * Order
     * */
    @GET(APIConstants.Order.LIST)
    fun getListOrders(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICOrderHistoryV2>>>

    @GET(APIConstants.Order.LIST_REPORT_ERROR)
    fun getListReportOrder(): Observable<ICResponse<ICListResponse<ICReportForm>>>

    @PUT(APIConstants.Order.PUT_REPORT_ERROR)
    fun putReportOrder(@Path("id") id: Long, @Body body: HashMap<String, Any>): Observable<ICResponse<Any>>

    @GET(APIConstants.Order.DETAIL)
    fun getOrderDetail(@Path("id") id: Long): Observable<ICOrderDetail>

    @PUT(APIConstants.Order.UPDATE_STATUS)
    fun updateStatusOrder(@Path("id") id: Long, @Body body: HashMap<String, Any>): Observable<ICResponse<ICRespID>>

    @PUT(APIConstants.Order.PAY)
    fun payOrder(@Path("id") id: Long): Observable<ICRespCheckoutCart>

    @PUT(APIConstants.Order.COMPLETE)
    fun completeOrder(@Path("id") id: Long): Observable<ICOrderHistory>
    /*
     * End Order
     * */

    @GET("products/{id}/distributors")
    suspend fun getDistributors(@Path("id") id: Long): ICBookmarkPage

    @GET("products/{productId}/transparency")
    suspend fun getTransparency(@Path("productId") productId: Long?): ICTransparency

    @POST("products/{productId}/transparency")
    suspend fun postTransparency(@Path("productId") productId: Long, @Body body: HashMap<String, Boolean>): ICTransparency

    @POST("image/v1/raw")
    suspend fun postImage(@Body body: RequestBody): UploadResponse

    @POST("histories/products")
    suspend fun postHistoryScan(@Body body: HashMap<String, Any>): ResponseBody

    @POST("stamps/check")
    suspend fun checkValidStamp(@Body body: RequestBody): ICCheckValidStamp

    @GET(APIConstants.Product.GET_BOOK_MARK)
    fun getListProductBookmark(@Path("userId") userId: String, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICProductTrend>>>

    @POST(APIConstants.Product.POST_VOTE_CONTRIBUTOR)
    fun postVoteContributor(@Path("id") id: Long, @Body body: HashMap<String, Any?>): Observable<ICResponse<ICContribute>>

    @GET
    fun getExperienceCategory(@Url url: String): Observable<ICResponse<ICListResponse<ICExperienceCategory>>>

    @GET
    fun getCategoryProducts(@Url url: String): Observable<ICResponse<ICListResponse<ICProduct>>>

    @GET(APIConstants.Product.GET_NEWS_PRODUCT_CATEGORY)
    fun getProductExperienceCategory(@Path("categoryId") categoryId: Long, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICProduct>>>

    @GET
    fun getCampainsHome(@Url url: String): Observable<ICResponse<ICListResponse<ICCampaign>>>

    @GET(APIConstants.User.ICOIN)
    fun getCoinOfMe(): Observable<ICResponse<ICSummary>>

    @GET
    suspend fun getCoin(@Url url: String): ICResponse<ICSummary>

    @GET(APIConstants.Page.RELATIONSHIP_CURRENT_USER)
    fun getRelationshipCurrentUser(): Observable<ICResponse<ICRelationshipsInformation>>

    @GET(APIConstants.Page.RELATIONSHIP_CURRENT_USER)
    suspend fun getRelationshipInformation(): ICResponse<ICRelationshipsInformation>

    @GET(APIConstants.Campaign.GET_CAMPAIGN_REWARD)
    fun getCampaignReward(@Path("id") id: String, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICGiftOfCampaign>>>

    @GET(APIConstants.Campaign.GET_TOP_WINNER_CAMPAIGN)
    fun getTopWinnerCampaign(@Path("id") id: String): Observable<ICResponse<ICListResponse<ICCampaign>>>

    @GET(APIConstants.Campaign.GET_WINNER_CAMPAIGN)
    fun getWinnerCampaign(@Path("id") id: String, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICCampaign>>>

    @GET(APIConstants.Campaign.GET_LIST_REWARD_ITEM)
    fun getRewardItemV2(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICItemReward>>>

    @GET(APIConstants.Campaign.GET_LIST_MY_GIFT_BOX)
    fun getListMyGiftBox(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICItemReward>>>

    @GET
    fun getProductForYou(@Url url: String): Observable<ICResponse<ICListResponse<ICProductForYou>>>

    @GET
    fun getFlashSale(@Url url: String): Observable<ICResponse<ICFlashSale>>

    /*
    * Layout
    * */
    @GET(APIConstants.Layout.LAYOUTS)
    fun getLayoutHome(@Query("layout") layoutName: String): Observable<ICLayoutData<ICNone>>

    @GET(APIConstants.Business.GET_MAP_PAGE)
    fun getMapPageHistory(): Observable<ICResponse<ICPageDetail>>

    @POST(APIConstants.Business.GET_LIST_USER_FOLLOW_PAGE)
    fun getListUserFollowPage(@Path("id") id: Long, @Body queries: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICSearchUser>>>

    /*
    * Product Need Review
    * */
    @GET
    fun getProductNeedReview(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICProductNeedReview>>>

    /*
    * Product Report && Contribute
    * */
    @GET(APIConstants.Product.PRODUCT_REPORT)
    fun getReportProductFrom(): Observable<ICResponse<ICListResponse<ICReportForm>>>

    @POST(APIConstants.Product.PRODUCT_REPORT)
    fun postReportForm(@Body body: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICReportForm>>>


    @POST(APIConstants.Product.FORM_REPORT_CONTRIBUTE)
    fun postReportFormContributor(@Path("id") id: Long, @Body body: HashMap<String, Any>): Observable<ICResponse<ICReportContribute>>


    @POST(APIConstants.Product.FORM_REPORT_CONTRIBUTE)
    fun postReportFormContribute(@Path("id") id: Long, @Body body: HashMap<String, Any?>): Observable<ICResponse<ICReportContribute>>
    /*
    * End Product Report
    * */


    /**
     * Suggest Topic
     */
    @GET(APIConstants.Suggest.GET_LIST_TOPIC)
    fun getListTopic(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICSuggestTopic>>>

    /**
     * Suggest Page
     */
    @POST(APIConstants.Suggest.GET_SUGGEST_PAGE)
    fun getSuggestPage(@Body queries: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICSuggestPage>>>

    @GET(APIConstants.User.LIST_FRIENDS)
    fun getSuggestFriend(@Path("id") id: Long, @QueryMap queries: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICUser>>>

    @GET(APIConstants.Product.GET_RELATED_PRODUCT_SOCIAL)
    fun getRelatedProductSocial(@Path("id") id: Long): Observable<ICResponse<ICListResponse<ICProductTrend>>>

    @GET(APIConstants.Social.GET_OWNER_PRODUCT_SOCIAL)
    fun getOwnerProductSocial(@Path("ownerId") id: Long): Observable<ICResponse<ICListResponse<ICProductTrend>>>

    @GET(APIConstants.Product.GET_LIST_CONTRIBUTOR_PRODUCT)
    fun getListContributor(@Path("barcode") barcode: String, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICContribute>>>

    @GET(APIConstants.Product.GET_LIST_REPORT_CONTRIBUTE)
    fun getListReportContribute(): Observable<ICResponse<ICListResponse<ICReportForm>>>

    @GET(APIConstants.Product.GET_INFORMATION_PRODUCT)
    fun getInformationProduct(@Path("id") informationId: Long, @Path("code") id: String): Observable<ICResponse<ICInformationProduct>>

    @POST(APIConstants.User.POST_FAVOURITE_TOPIC)
    suspend fun postFavouriteTopic(@Body queries: HashMap<String, Any>): ICResponse<Boolean>

    @POST(APIConstants.Social.FOLLOW_PAGE)
    suspend fun postFollowPage(@Body queries: HashMap<String, Any>): ICResponse<Boolean>

    /**
     * Page Detail
     */


    @GET
    fun getBrandPage(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICPageTrend>>>

    @GET(APIConstants.Page.GET_HIGHLIGHT_PRODUCTS)
    fun getHighlightProducts(@Path("id") id: Long, @QueryMap queries: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICProductTrend>>>

    @GET(APIConstants.Page.GET_CATEGORIES_PRODUCTS)
    fun getCategoriesProduct(@Path("id") id: Long): Observable<ICResponse<ICListResponse<ICCategoriesProduct>>>

    @GET(APIConstants.Social.GET_LIST_PRODUCT_QUESTIONS)
    fun getListProductQuestions(@Path("id") id: Long, @QueryMap queries: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICProductQuestion>>>

    @PUT(APIConstants.Product.POST_REVIEW)
    fun postProductReview(@Path("id") id: Long, @Body queries: HashMap<String, Any?>): Observable<ICResponse<ICPost>>

    @POST
    fun postProductReview(@Url url: String, @Body body: HashMap<String, Any?>): Single<ReviewsRow>

    @PUT(APIConstants.Product.POST_REVIEW)
    fun editProductReview(@Path("id") id: Long, @Body queries: HashMap<String, Any?>): Observable<ICResponse<ICPost>>

    @POST(APIConstants.Social.GET_LIST_PRODUCT_QUESTIONS)
    fun postProductQuestion(@Path("id") id: Long, @Body params: HashMap<String, Any>): Observable<ICResponse<ICProductQuestion>>

    @POST(APIConstants.Product.COMMENT_QUESTION)
    fun postAnswer(@Path("id") questionId: Long, @Body params: HashMap<String, Any>): Observable<ICResponse<ICProductAnswerV2>>

    @POST
    fun postLikeQuestion(@Url url: String, @Body queries: HashMap<String, String>): Observable<ICResponse<ICNotification>>

    @GET(APIConstants.User.MY_ID)
    fun getMyID(): Observable<ICResponse<ICMyID>>

    @GET(APIConstants.Page.IMAGEE_ASSET)
    fun getImageAssetsPage(@Path("id") id: Long, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICMediaPage>>>

    @GET
    fun getNotificationPage(@Url url: String): Observable<ICResponse<ICListResponse<ICNotificationPage>>>

    @GET
    fun getNotificationPage(@Url url: String, @Body body: JsonObject): Observable<ICResponse<ICListResponse<ICNotificationPage>>>

    @GET(APIConstants.Page.GET_LIST_REPORT_PAGE)
    fun getListReportPage(): Observable<ICResponse<ICListResponse<ICReportForm>>>

    @POST(APIConstants.Page.GET_LIST_REPORT_PAGE)
    fun postReportPage(@Body body: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICReportForm>>>

    @GET(APIConstants.Page.LIST_POST)
    fun getListPostsOfPage(@Path("id") id: Long, @QueryMap body: HashMap<String, Int>): Observable<ICResponse<ICListResponse<ICPost>>>


    @GET
    fun getListPosts(@Url url: String, @QueryMap body: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICPost>>>

    @POST(APIConstants.Post.LIKE_POST)
    fun likePostOfPage(@Path("id") id: Long?, @Body body: HashMap<String, Any>): Observable<ICResponse<ICPost>>

    @GET(APIConstants.Page.SHARE_LINK)
    fun getShareLinkOfPost(@Path("id") id: Long): Observable<ICResponse<String>>

    @POST(APIConstants.Page.SHARE_LINK)
    fun postShareLinkOfPost(@Path("id") id: Long, @Body body: HashMap<String, Any>): Observable<ICResponse<String>>

    @POST(APIConstants.Social.PIN_POST)
    fun pinPostOfPage(@Body body: HashMap<String, Any>): Observable<ICResponse<ICPost>>

    @GET(APIConstants.Page.REPORT_POST)
    fun getListReportPost(): Observable<ICResponse<ICListResponse<ICReportForm>>>

    @POST(APIConstants.Page.REPORT_POST)
    fun reportPost(@Body body: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICReportForm>>>

    /*
    * Relationship
    * */
    @PUT
    fun updateFriendInvitation(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICResponse<Boolean>>

    @POST(APIConstants.Relationship.FOLLOW_PAGE)
    fun relationshipFollowPage(@Body body: HashMap<String, Any>): Observable<ICResponse<Boolean>>

    @PUT(APIConstants.Relationship.UN_FOLLOW_PAGE)
    fun unFollowPage(@Body body: HashMap<String, Any>): Observable<ICResponse<Boolean>>


    /*
    * Gift history*/
    @GET(APIConstants.GiftCampaign.GET_GIFT_RECEIVED)
    fun getGiftReceived(@QueryMap body: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICGiftReceived>>>

    /*
    * Search*/
    @GET(APIConstants.Search.GET_PRODUCT)
    suspend fun getProductSearch(@QueryMap body: HashMap<String, Any>): ICResponse<ICListResponse<ICProductTrend>>


    @GET(APIConstants.Search.GET_REVIEW)
    suspend fun getReviewSearch(@QueryMap body: HashMap<String, Any>): ICResponse<ICListResponse<ICPost>>

    @POST(APIConstants.Search.GET_PAGE)
    suspend fun getPageSearch(@Body body: HashMap<String, Any>): ICResponse<ICListResponse<ICPageQuery>>

    @GET
    fun getListDistributor(@Url url: String): Observable<ICResponse<ICListResponse<ICPage>>>

    //    @POST(APIConstants.SearchV2.GET_SHOP)
    @POST(APIConstants.Search.GET_PAGE)
    fun getShopSearch(@Body body: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICShopQuery>>>

    @GET(APIConstants.Search.GET_CATEGORY_PARENT)
    fun getCategoryParent(@QueryMap body: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICCategorySearch>>>

    @GET(APIConstants.Search.GET_CATEGORY_CHILD)
    fun getCategoryChildren(@QueryMap body: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICCategorySearch>>>

    @GET(APIConstants.Point.RANK_HISTORY)
    fun getRankHistory(@QueryMap body: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICHistoryPoint>>>

    @GET(APIConstants.Search.GET_POPULAR_SEARCH)
    fun getPopularSearch(): Observable<ICResponse<ICListResponse<ICCategorySearch>>>

    @GET(APIConstants.Search.GET_AUTO_SEARCH)
    fun getAutoSearch(@Path("word") word: String): Observable<ICResponse<ICListResponse<String?>>>

    @GET(APIConstants.Search.GET_RECENT_SEARCH)
    fun getRecentSearch(): Observable<ICResponse<ICListResponse<ICCategorySearch>>>

    @HTTP(method = "DELETE", path = APIConstants.Search.GET_RECENT_SEARCH, hasBody = true)
    fun deleteRecentSearch(@Body body: HashMap<String, Any>): Observable<ICResponse<Boolean>>

    @GET(APIConstants.Campaign.DETAIL_REWARD)
    fun getDetailReward(@Path("id") id: String): Observable<ICResponse<ICItemReward>>

    @POST(APIConstants.Product.LINK_PRODUCT)
    fun getLinkOfProduct(@Path("id") id: Long): Observable<ICResponse<ICNotification>>

    /*
    * PV COMBANK
    * */
    @GET
    fun getAllUtility(@Url url: String): Observable<ICResponse<MutableList<ICTheme>>>

    @GET
    fun getHomeFunc(@Url url: String): Observable<ICResponse<ICTheme>>

    /*
    * Qr Code
    * */

//    @POST("https://qrcode-test-api.icheck.vn/qr-core-aps/scan/check")
//    fun checkScanQrCode(@Body body: HashMap<String, Any>) : Observable<ICResponse<ICQrScan>>

    @POST(APIConstants.Stamp.SCAN_CHECK_STAMP)
    fun checkScanQrCode(@Body body: HashMap<String, Any>): Observable<ICResponse<ICValidStampSocial>>

    @POST(APIConstants.Stamp.GET_ID_PAGE_SOCIAL)
    fun getIdPageSocial(@Body body: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICGetIdPageSocial>>>

    @GET
    fun getCart(@Url url: String): Observable<ICResponse<MutableList<ICCartSocial>>>

    /**
     * Tích điểm đổi quà
     */
    @GET
    fun getPointUser(@Url url: String): Observable<ICResponse<ICKPointUser>>

    @GET
    fun getListRedemptionHistory(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICKBoxGifts>>>

    @POST
    fun postNhapMaTichDiem(@Url url: String, @Body params: HashMap<String, Any>): Observable<ICResponse<ICKAccumulatePoint>>

    @POST
    fun postExchangeGift(@Url url: String, @Body params: HashMap<String, Any>): Observable<ICResponse<ICKBoxGifts>>

    @GET
    fun getGiftHistory(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICItemReward>>>


    @GET
    fun getListOfGiftsReceivedLoyalty(@Url url: String, @QueryMap queries: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICKRewardGameLoyalty>>>

    @GET
    fun getWinnerPoint(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICKPointUser>>>

    @GET
    fun getPointHistoryAll(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICKPointHistory>>>


    /**
     * Popup quảng cáo
     */
    @GET(APIConstants.Popup.GET_POPUP_BY_SCREEN)
    fun getPopupByScreen(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICPopup>>

    @PATCH
    fun clickPopupAds(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICResponse<Any>>
}