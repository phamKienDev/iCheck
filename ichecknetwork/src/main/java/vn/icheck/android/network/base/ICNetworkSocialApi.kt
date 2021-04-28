package vn.icheck.android.network.base

import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*
import vn.icheck.android.network.model.kyc.KycResponse
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.history.*
import vn.icheck.android.network.models.product.detail.ICProductVariant
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.network.models.product_detail.IckProductDetailLayoutModel
import vn.icheck.android.network.models.pvcombank.*
import vn.icheck.android.network.models.wall.ICUserFollowWall
import vn.icheck.android.network.models.wall.ICUserPublicInfor

interface ICNetworkSocialApi {

    @POST
    fun registerDevice(@Url url: String, @Body hashMap: HashMap<String, Any>): Observable<ICResponseCode>

    /*
    * Base
    * */
    @POST
    fun postRequest(@Url url: String, @Body body: HashMap<String, Any> = hashMapOf()): Observable<ICResponseCode>
    /*
    * End base
    * */

    /*
    * Product Detail
    */
    @GET
    fun checkBookmark(@Url url: String): Observable<ICResponse<ICBookmark>>

    @GET
    fun getProductDetail(@Url url: String): Observable<ICResponse<ICProductDetail>>

    @GET
    fun getProductDetail(@Url url: String, @Query("layout") layout: String): Observable<ICLayoutData<JsonObject>>

    @POST
    fun getProductDetail(@Url url: String, @Body requestBody: HashMap<String, Any?>): Observable<ICLayoutData<JsonObject>>

    @GET
    fun getReviewSummary(@Url url: String): Observable<ICResponse<ICReviewSummary>>

    @GET
    fun getContribution(@Url url: String): Observable<ICResponse<ICProductContribution>>

    @GET
    fun getTransparency(@Url url: String): Observable<ICResponse<ICTransparency>>

    @GET
    fun getListPage(@Url url: String): Observable<ICResponse<ICListResponse<ICPage>>>


    @GET
    fun getListShopVariant(@Url url: String, @QueryMap queries: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICShopVariantV2>>>

    @GET
    fun getMyReview(@Url url: String,@QueryMap hashMap: HashMap<String, Any>): Observable<ICResponse<ICProductMyReview>>

    @POST
    fun registerBuyProduct(@Url url: String,@Body hashMap: HashMap<String, Any>): Observable<ICResponse<JsonObject>>

    @GET
    fun getListProductInformation(@Url url: String): Observable<ICResponse<ICListResponse<ICProductInformations>>>

    @GET
    fun getListProductReview(@Url url: String): Observable<ICResponse<ICListResponse<ICPost>>>

    @GET
    fun getListProductQuestion(@Url url: String): Observable<ICResponse<ICListResponse<ICProductQuestion>>>

    @GET
    fun getListProductTrend(@Url url: String): Observable<ICResponse<ICListResponse<ICProductTrend>>>


    @GET(APIConstants.Product.PRODUCT_DETAIL_BY_ID)
    suspend fun detailProductByID(@Path("id") id: Long): IckProductDetailLayoutModel

    @GET
    suspend fun getReviewSummary2(@Url link: String?): ICResponse<ICReviewSummary>

    @GET
    suspend fun getShopVariant(@Url link: String?): ICResponse<ICProductVariant>

    @GET
    suspend fun getMyReview2(@Url link: String?): ICResponse<ICProductMyReview>

    @GET
    suspend fun getReview(@Url link: String?): ICResponse<ICListResponse<ICPost>>

    @GET(APIConstants.Social.GET_TREND_PRODUCTS)
    fun getWidgetTrendProducts(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICProductTrend>>>

    @GET(APIConstants.Social.GET_TREND_PAGES)
    fun getWidgetTrendPages(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICPageTrend>>>

    @GET(APIConstants.Social.GET_TREND_EXPERTS)
    fun getWidgetExpertsTrend(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICPageTrend>>>

    @GET
    fun getProductShareLink(@Url url: String): Observable<ICResponse<String>>

    @GET
    suspend fun getProductsECommerce(@Url url: String): ICResponse<ICListResponse<ICProductECommerce>>

    @GET
    fun getProductsECommerceV2(@Url url: String): Observable<ICResponse<ICListResponse<ICProductECommerce>>>

    /*
    * End Product Detail
    */

    /*
    * Comment
    * */
    @GET(APIConstants.Social.GET_LIST_PRODUCT_QUESTIONS)
    fun getListProductQuestions(@Path("id") id: Long, @QueryMap queries: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICProductQuestion>>>

    @GET(APIConstants.Social.GET_LIST_PRODUCT_ANSWERS)
    fun getListProductAnswer(@Path("id") id: Long, @QueryMap queries: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICProductQuestion>>>

    @POST
    fun postProductQuestion(@Url url: String, @Body params: HashMap<String, Any>): Observable<ICResponse<ICProductQuestion>>

    @POST
    fun postProductAnswer(@Url url: String, @Body params: HashMap<String, Any>): Observable<ICResponse<ICProductQuestion>>

    @HTTP(method = "DELETE", hasBody = true)
    fun deleteComment(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICResponseCode>

    @PUT
    fun updateComment(@Url url: String, @Body params: HashMap<String, Any>): Observable<ICResponseCode>

    @POST
    fun likeComment(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICResponseCode>
    /*
    * End Comment
    * */

    /*
    * Wall
    * */
    @GET(APIConstants.Social.GET_USER_PUBLIC_INFORMATION)
    fun getUserPublicInfor(): Observable<ICResponse<ICListResponse<ICUserPublicInfor>>>

    @PUT(APIConstants.Social.GET_USER_PUBLIC_INFORMATION)
    fun updateInforPublic(@Body body: HashMap<String, Any>): Observable<ICResponseCode>

    @GET(APIConstants.Social.GET_USER_FOLLOW_ME)
    fun getListUserFollow(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICUserFollowWall>>>

    @GET(APIConstants.Social.GET_USER_WATCHING)
    fun getListUserWatching(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICUserFollowWall>>>

    @GET(APIConstants.Social.GET_FRIEND_OF_USER)
    fun getListFriendOfUser(@Path("id") id: Long, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICUserFollowWall>>>

    @PUT(APIConstants.Social.UNFRIEND_OR_ADDFRIEND)
    fun putUnOrAddFriend(@Body body: HashMap<String, Any>): Observable<ICResponse<Boolean>>

    @PUT(APIConstants.Social.UNFRIEND)
    fun putUnFriend(@Body body: HashMap<String, Any>): Observable<ICResponse<Boolean>>

    @PUT(APIConstants.Social.FOLLOW_OR_UNFOLLOW)
    fun followOrUnFollow(@Body body: HashMap<String, Any>): Observable<ICResponse<Boolean>>

    @GET(APIConstants.Social.GET_LIST_REPORT_USER)
    fun getListReportUser(): Observable<ICResponse<ICListResponse<ICReportForm>>>

    @POST(APIConstants.Social.REPORT_USER)
    fun postReportUser(@Body body: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICReportForm>>>

    @PUT
    fun postKyc(@Url url: String, @Body body: ICPostKyc): Observable<ICResponse<String>>

    @GET
    fun getUserKyc(@Url url: String): Observable<ICResponse<ListResponse<KycResponse>>>

    /*
    * End Wall
    * */

    /*
    * Post
    * */
    @POST
    fun createPost(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICResponse<ICPost>>

    @PUT
    fun updatePost(@Url url: String, @Body body: ICReqUpdatePost): Observable<ICResponse<ICPost>>
//    fun updatePost(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICResponse<ICPost>>

    @GET
    fun getPostDetail(@Url url: String): Observable<ICResponse<ICPost>>

    @GET
    suspend fun getPostDetailV2(@Url url: String): ICResponse<ICPost>


    @GET
    fun getPostPrivacy(@Url url: String, @QueryMap params: HashMap<String, Long>): Observable<ICResponse<ICListResponse<ICPrivacy>>>
    /*
    * End Post
    * */

    /*
    * Page
    * */
    @GET
    fun getLayoutPage(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICLayoutData<JsonObject>>

    @GET
    fun getCategoriesProduct(@Url url: String): Observable<ICResponse<ICListResponse<ICCategoriesProduct>>>

    @GET
    fun getImageAssetsPage(@Url url: String): Observable<ICResponse<ICListResponse<ICMediaPage>>>

    @GET
    fun getRelatedPage(@Url url: String): Observable<ICResponse<ICListResponse<ICRelatedPage>>>

    @GET
    fun getHighlightProducts(@Url url: String): Observable<ICResponse<ICListResponse<ICProductTrend>>>

    @GET
    fun getBrandPage(@Url url: String): Observable<ICResponse<ICListResponse<ICPageTrend>>>

    @POST
    fun followPage(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICResponse<Boolean>>

    @PUT
    fun unFollowPage(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICResponse<Boolean>>

    @GET(APIConstants.Page.GET_FRIEND_NOFOLLOW_PAGE)
    fun getFriendNofollowPage(@Path("pageId") pageId: Long, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICFriendNofollowPage>>

    @POST(APIConstants.Page.POST_FOLLOW_PAGE_INVITATION)
    fun postFollowPageInvitation(@Body body: HashMap<String, Any>): Observable<ICResponse<Boolean>>

    @POST(APIConstants.Page.UNSUBCRIBE_PAGE)
    fun unSubcribePage(@Body body: HashMap<String, Any>): Observable<ICResponse<Boolean>>

    @PUT(APIConstants.Page.UPDATE_PAGE)
    fun updatePage(@Path("id") id: Long, @Body body: HashMap<String, Any>): Observable<ICResponse<MutableList<Int>>>

    @POST(APIConstants.Page.RESUBCRIBE_PAGE)
    fun reSubcribePage(@Body body: HashMap<String, Any>): Observable<ICResponse<Boolean>>

    @GET(APIConstants.Page.MY_OWNER_PAGE)
    fun getMyOwnerPage(@QueryMap body: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICPage>>>

    @GET(APIConstants.Page.MY_OWNER_PAGE)
    suspend fun getMyOwnerPageV2(@QueryMap body: HashMap<String, Any>): ICResponse<ICListResponse<ICPage>>

    @GET(APIConstants.Page.MY_FOLLOW_PAGE)
    fun getMyFollowPage(@QueryMap body: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICPage>>>

    @GET(APIConstants.Page.MY_FOLLOW_PAGE)
    suspend fun getMyFollowPageV2(@QueryMap body: HashMap<String, Any>): ICResponse<ICListResponse<ICPage>>

    @GET(APIConstants.Page.BUTTON_CUSTOMIZE)
    fun getButtonCustomize(@Path("id") pageId: Long, @QueryMap body: HashMap<String, Any>): Observable<ICResponse<MutableList<ICButtonOfPage>>>

    @PUT(APIConstants.Page.BUTTON_CUSTOMIZE)
    fun updateButtonCustomize(@Path("id") pageId: Long, @Body body: HashMap<String, Any>): Observable<ICResponse<MutableList<ICButtonOfPage>>>

    @POST(APIConstants.Page.PAGE_USER_MANAGER)
    fun getPageUserManager(id: Long): Observable<ICResponse<ICListResponse<ICPageUserManager>>>

    @POST(APIConstants.Page.SKIP_INVITE_USER)
    fun skipInivteUser(@Body body: HashMap<String, Any>): Observable<ICResponse<Boolean>>

    /*
    * End page
    * */

    /*
    * Ads
    * */
    @GET
    fun getAds(@Url url: String): Observable<ICResponse<ICListResponse<ICAdsNew>>>

    @GET
    fun getAdsNew(@Url url: String): Observable<ICResponse<ICListResponse<ICAdsNew>>>

    @GET(APIConstants.User.RANK_OF_USER)
    fun getRankOfUser(): Observable<ICResponse<ICRankOfUser>>

    @GET(APIConstants.Product.MESSAGE_VERIRY)
    fun getSettingMessageSocial(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICClientSetting>>
    /*
    * End Ads
    * */

    /*
    * Campaign
    */
    @GET(APIConstants.Campaign.LIST_ICON_GRID)
    fun getListIconCampaign(@Path("id") idCampaign: String): Observable<ICResponse<ICListResponse<ICGridBoxShake>>>

    @PUT(APIConstants.Campaign.UNBOX_SHAKE_GIFT)
    fun openShakeGift(@Body body: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICOpenShakeGift>>>
    /*
    * End Campaign
    */

    /*
    * PVCombank
    */
    @GET
    suspend fun checkHasCard(@Url url: String): ICResponse<Boolean>

    @GET(APIConstants.PVCombank.FORM_AUTH)
    fun getLinkFormAuth(): Observable<ICResponse<ICAuthenPVCard>>

    @GET(APIConstants.PVCombank.FORM_AUTH)
    suspend fun getLinkFormAuthV2(): ICResponse<ICAuthenPVCard>

    @GET(APIConstants.PVCombank.INFO_CARD)
    fun getInfoCard(@Path("cardId") cardId: String): Observable<ICResponse<ICInfoPVCard>>

    @GET(APIConstants.PVCombank.LIST_CARD)
    fun getListCardPVComBank(@Query("fullName") fullName: String): Observable<ICResponse<ICListResponse<ICListCardPVBank>>>

    @GET
    suspend fun getListCardPVComBankV2(@Url url: String, @Query("fullName") fullName: String): ICResponse<ICListResponse<ICListCardPVBank>>

    @GET
    suspend fun getKyc(@Url url: String): ICResponse<ICKyc>

    @POST(APIConstants.PVCombank.LOCK_CARD)
    fun lockCard(@Path("cardId") cardId: String): Observable<ICResponse<ICLockCard>>

    @POST(APIConstants.PVCombank.UNLOCK_CARD)
    fun unlockCard(@Path("cardId") cardId: String): Observable<ICResponse<ICLockCard>>

    @POST(APIConstants.PVCombank.VERIFY_OTP_UNLOCK_CARD)
    fun verifyOtpUnlockCard(@Body body: HashMap<String, Any>, @Path("reqId") requestId: String): Observable<ICResponse<ICInfoPVCard>>

    @GET(APIConstants.PVCombank.TRANSACTION_CARD)
    fun getTransactionCard(@Path("cardId") cardId: String): Observable<ICResponse<ICTransactionPVCard>>

    @POST(APIConstants.PVCombank.SET_DEFAULT_CARD)
    fun setDefaultCard(@Path("cardId") cardId: String): Observable<ICResponse<Boolean>>

    @GET(APIConstants.PVCombank.GET_FULL_CARD)
    fun getFullCard(@Path("cardId") cardId: String): Observable<ICResponse<ICLockCard>>
    /*
    * End PVCombank
    */

    /*
   * Location
   * */
    @GET
    fun searchLocation(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICPoints>>

    @GET
    fun getLocationDetail(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICPointDetail>>
    /*
    * End Location
    * */

    /*
    * History
    */
    @GET(APIConstants.History.GET_LIST_BIG_CORP)
    fun getListBigCorp(): Observable<ICResponse<ICListResponse<ICBigCorp>>>

    @POST(APIConstants.History.GET_LIST_SCAN_HISTORY)
    fun getListScanHistory(@QueryMap params: HashMap<String, Any>,@Body body: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICItemHistory>>>

    @GET(APIConstants.History.GET_LIST_STORE_SELL)
    fun getListStoreSell(@Path("id") id: Long,@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICStoreNear>>>

    @GET(APIConstants.History.GET_CART_COUNT)
    fun getCartCount(): Observable<ICResponse<Int>>

    @GET(APIConstants.History.GET_LIST_FILTER_TYPE)
    fun getFilterHistory(): Observable<ICResponse<ICListResponse<ICTypeHistory>>>

    @GET(APIConstants.History.GET_SUGGEST_STORE)
    fun getSuggestStoreHistory(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICSuggestStoreHistory>>>

    @GET(APIConstants.History.GET_PRODUCT_OF_SHOP)
    fun getProductOfShopHistory(@Path("id") id: Long,@QueryMap params: HashMap<String, Any>): Observable<ICResponse<ICListResponse<ICProductOfShopHistory>>>

    @GET(APIConstants.History.GET_ROUTES_SHOP)
    fun getRouteShop(@QueryMap params: HashMap<String, Any>): Observable<ICResponse<MutableList<ICRoutesShop>>>

    @GET(APIConstants.History.GET_STORE_NEAR)
    fun getStoreNearHistory(@Path("id") id: Long): Observable<ICResponse<ICListResponse<ICStoreNear>>>
    /*
    * End History
    */
    @GET
    suspend fun addProductToCart(@Url url: String, @Body body: HashMap<String, Any> = hashMapOf()) : ICResponse<*>
}