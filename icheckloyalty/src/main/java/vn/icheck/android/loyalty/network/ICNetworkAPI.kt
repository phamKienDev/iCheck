package vn.icheck.android.loyalty.network

import io.reactivex.Observable
import retrofit2.http.*
import vn.icheck.android.loyalty.model.*

interface ICNetworkAPI {
    /**
     * Địa chỉ
     */
    @GET
    fun listProvince(@Url url: String, @QueryMap params: HashMap<String, Int>): Observable<ICKListResponse<ICProvince>>

    @GET
    fun getListDistrict(@Url url: String, @QueryMap query: HashMap<String, Int>): Observable<ICKListResponse<ICDistrict>>

    @GET
    fun getListWard(@Url url: String, @QueryMap query: HashMap<String, Int>): Observable<ICKListResponse<ICWard>>

    /**
     * End Địa chỉ
     */

    @GET
    fun getCampaign(@Url url: String, @Query("target") barcode: String): Observable<ICKResponse<ICKLoyalty>>

    @POST
    fun postRefuseGift(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICKResponse<ICKWinner>>

    @GET
    fun getDetailGiftWinner(@Url url: String): Observable<ICKResponse<ICKGift>>

    @GET
    fun getListGameLoyalty(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICKResponse<ICKListResponse<ICKGame>>>

    @POST
    fun postReceiveGift(@Url url: String, @Body params: HashMap<String, Any>): Observable<ICKResponse<ICKReceiveGift>>

    @POST
    fun postGameGift(@Url url: String, @Body params: HashMap<String, Any>): Observable<ICKResponse<DataReceiveGameResp>>

    @GET
    fun getGiftDetail(@Url url: String): Observable<ICKResponse<ICKLoyalty>>

    @PATCH
    fun confirmGiftLoyalty(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICKResponse<ICKWinner>>

    /**
     * Vòng quay may mắn
     */
    @GET
    suspend fun getListGame(@Url url: String): GameListRep

    @GET
    suspend fun getGameDetail(@Url url: String): LuckyWheelInfoRep

    @POST
    suspend fun customerPlayGame(@Url url: String, @Body body: HashMap<String, Any?>): PlayGameResp

    @GET
    suspend fun getListWinner(@Url url: String, @Query("limit") limit: Int, @Query("offset") offset: Int): ListWinnerResp

    @POST
    suspend fun receiveGame(@Url url: String, @Body requestBody: HashMap<String, Any?>): ReceiveGameResp

    @GET
    fun getTheWinnerLoyalty(@Url url: String, @QueryMap queries: HashMap<String, Any>): Observable<ICKResponse<ICKListResponse<ICKCampaign>>>

    @POST
    fun receiveGameV2(@Url url: String, @Body requestBody: HashMap<String, Any>): Observable<ReceiveGameResp>

    @GET
    fun getListCodeUsed(@Url url: String, @QueryMap queries: HashMap<String, Any>): Observable<ICKResponse<ICKListResponse<ICKItemReward>>>

    @GET
    fun getListOfGiftsReceived(@Url url: String, @QueryMap queries: HashMap<String, Any>): Observable<ICKResponse<ICKListResponse<ICKRewardGameVQMMLoyalty>>>
    /**
     * End Vòng quay may mắn
     */

    /**
     * Tích điểm đổi quà
     */
    @GET
    fun getPointUser(@Url url: String): Observable<ICKResponse<ICKPointUser>>

    @GET
    fun getListRedemptionHistory(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICKResponse<ICKListResponse<ICKBoxGifts>>>

    @POST
    fun postNhapMaTichDiem(@Url url: String, @Body params: HashMap<String, Any>): Observable<ICKResponse<ICKAccumulatePoint>>

    @GET
    fun getListOfGiftsReceivedLoyalty(@Url url: String, @QueryMap queries: HashMap<String, Any>): Observable<ICKResponse<ICKListResponse<ICKRewardGameLoyalty>>>

    @GET
    fun getWinnerPoint(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICKResponse<ICKListResponse<ICKPointUser>>>

    @POST
    fun postExchangeGift(@Url url: String, @Body params: HashMap<String, Any>): Observable<ICKResponse<ICKBoxGifts>>

    @GET
    fun getPointHistoryAll(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICKResponse<ICKListResponse<ICKPointHistory>>>
    /**
     * End Tích điểm đổi quà
     */

    /**
     * Tích điểm dài hạn
     */
    @GET
    fun getTopUpService(@Url url: String): Observable<ICKResponse<TopupServiceResponse>>

    @GET
    fun getRedemptionHistoryLongTime(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICKResponse<ICKListResponse<ICKRedemptionHistory>>>

    @POST
    fun exchangeGift(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICKResponse<ICKRedemptionHistory>>

    @PATCH
    fun exchangeCardGiftVQMM(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICKResponse<ICKRedemptionHistory>>

    @GET
    fun getLongTermProgramList(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICKResponse<ICKListResponse<ICKLongTermProgram>>>

    @GET
    fun getTransactionHistory(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICKResponse<ICKListResponse<ICKTransactionHistory>>>

    @GET
    fun getHeaderHomePage(@Url url: String): Observable<ICKResponse<ICKLongTermProgram>>

    @GET
    fun getCampaignOfBusiness(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICKResponse<ICKListResponse<ICKCampaignOfBusiness>>>

    @GET
    fun getCampaignDetailLongTime(@Url url: String): Observable<ICKResponse<ICKCampaignOfBusiness>>

    @GET
    fun getAccumulationHistory(@Url url: String, @QueryMap params: HashMap<String, Any>): Observable<ICKResponse<ICKListResponse<ICKPointHistory>>>

    @GET
    fun getDetailGift(@Url url: String): Observable<ICKResponse<ICKRedemptionHistory>>

    @GET
    fun getDetailGiftStoreLongTime(@Url url: String): Observable<ICKResponse<ICKRedemptionHistory>>

    @GET("loyalty/loyalty/joined-network/{id}/transaction-history")
    suspend fun getTransactionHistory(@Path("id") id: Long, @Query("offset") offset: Int, @Query("limit") limit: Int, @QueryMap hashMap: HashMap<String, Any>): ICKResponse<ICKListResponse<TransactionHistoryResponse>>

    @GET("loyalty/loyalty/network/{id}/gifts")
    suspend fun getLoyaltyGiftShop(@Path("id") id: Long): ICKResponse<ICKListResponse<LoyaltyGiftItem>>

    @GET("loyalty/loyalty/network/{id}/gifts")
    suspend fun getLoyaltyGiftShop(@Path("id") id: Long, @Query("offset") offset: Int, @Query("limit") limit: Int): ICKResponse<ICKListResponse<LoyaltyGiftItem>>

    @GET("loyalty/loyalty/network/{id}/gifts")
    suspend fun getLoyaltyGiftShop(@Path("id") id: Long, @Query("offset") offset: Int, @Query("limit") limit: Int, @Query("gift_type") giftType: String): ICKResponse<ICKListResponse<LoyaltyGiftItem>>

    @GET("loyalty/loyalty/joined-network/{id}/transaction-history")
    suspend fun getLoyaltyTransactionHistory(@Path("id") id: Long, @Query("offset") offset: Int, @Query("limit") limit: Int): ICKResponse<ICKListResponse<TransactionHistoryResponse>>

    /**
     * Voucher
     */

    @POST
    fun scanVoucher(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICKResponse<ICKScanVoucher>>

    @POST
    fun usedVoucher(@Url url: String, @Body body: HashMap<String, Any>): Observable<ICKResponse<Boolean>>
}