package vn.icheck.android.network.base

import android.annotation.SuppressLint
import android.app.Application
import vn.icheck.android.network.BuildConfig
import vn.icheck.android.network.R
import java.lang.reflect.InvocationTargetException

object APIConstants {

    const val LIMIT = 10

    @JvmField
    var LATITUDE = 0.0

    @JvmField
    var LONGITUDE = 0.0

    const val PATH = "social/api"
    const val PATH_CDN = "icheck-social-cdn/"

    const val POST = "POST"


    val LOYALTY_HOST: String
        get() {
//            return if (BuildConfig.FLAVOR.contentEquals("dev")) "https://api.dev.icheck.vn/api/business/" else "https://api.icheck.com.vn/api/business/"
            return if (BuildConfig.FLAVOR.contentEquals("dev")) "https://api.dev.icheck.vn/api/business/" else "https://api-social.icheck.com.vn/api/business/"
        }

    external fun STAMPDETAIL(): String
    external fun STAMPMOREPRODUCTVERIFIEDDISTRIBUTOR(): String
    external fun STAMPHISTORYGUARANTEE(): String
    external fun STAMPNOTEHISTORYGUARANTEE(): String
    external fun STAMPMOREPRODUCTVERIFIEDVENDOR(): String
    external fun STAMPVERIFIEDNUMBERGUARANTEE(): String
    external fun STAMPDETAILCUSTOMERGUARANTEE(): String
    external fun ADDRESSDETAILCITY(): String
    external fun STAMPGETNAMEDISTRICTSGUARANTEE(): String
    external fun STAMPUPDATEINFORMATIONCUSTOMERGUARANTEE(): String
    external fun STAMPGETSHOPVARIANT(): String
    external fun STAMPGETCONFIGERROR(): String
    external fun PRODUCTINFO(): String
    external fun VARIANTPRODUCT(): String
    external fun GETFIELDLISTGUARANTEE(): String
    external fun USERSENDOTPCONFIRMPHONESTAMP(): String
    external fun USERCONFIRMPHONESTAMP(): String
    external fun ADDRESSDISTRICTS(): String
    external fun ADDRESSCITIES(): String
    external fun SCAN(): String
    external fun CRITERIADETAIL(): String
    external fun PRODUCTLISTQUESTION(): String
    external fun CRITERIALISTREVIEW(): String
    external fun PRODUCTLIST(): String
    external fun PRODUCTLISTANSWER(): String
    external fun CARTADD(): String
    external fun CRITERIAVOTEREVIEW(): String
    external fun CRITERIALISTCOMMENT(): String
    external fun SHARELINK(): String
    external fun CRITERIAREVIEWPRODUCT(): String
    external fun UPLOADIMAGEV1(): String
    external fun CRITERIALISTPRODUCTCOMMENT(): String
    external fun PRODUCTDETAIL(): String



    object Social {
        const val LIST_POST = "social/api/pages/{id}/posts"
        const val GET_LIST_PRODUCT_QUESTIONS = "social/api/products/{id}/questions"
        const val GET_LIST_PRODUCT_ANSWERS = "social/api/questions/{id}/comments"
        const val COMMENT_DETAIL = "social/api/comments/{id}"
        const val QUESTION_DETAIL = "social/api/questions/{id}"
        const val LIKE_QUESTION = "social/api/questions/{id}/expressive"
        const val LIKE_ANSWER = "social/api/comments/{id}/expressive"
        const val POST_PRODUCT_QUESTION = "social/api/products/{id}/question"
        const val GET_FLASH_SALE = "social/api/home/flash-sale"
        const val GET_EXPERIENCE_CATEGORY = "social/api/home/new-product/categories"
        const val GET_PRODUCT_SUGGESTIONS = "social/api/home/suggestions"
        const val PRODUCT_NEED_REVIEW = "social/api/home/suggest-reviews"
        const val GET_TREND_PRODUCTS = "social/api/home/trend/products"
        const val GET_TREND_PAGES = "social/api/home/trend/pages"
        const val GET_TREND_EXPERTS = "social/api/home/trend/experts"
        const val GET_USER_PUBLIC_INFORMATION = "social/api/privacy/user-public-info"
        const val GET_USER_FOLLOW_ME = "social/api/users/user-following-me"
        const val GET_USER_WATCHING = "social/api/users/me-following-user"
        const val GET_FRIEND_OF_USER = "social/api/users/{id}/friends"
        const val GET_MUTUAL_FRIEND = "social/api/users/profile/{id}/general-friends"
        const val UNFRIEND_OR_ADDFRIEND = "social/api/relationships/friend-invitation"
        const val UNFRIEND = "social/api/relationships/un-friend"
        const val FOLLOW_OR_UNFOLLOW = "social/api/relationships/follow-user"
        const val GET_LIST_REPORT_USER = "social/api/report/user"
        const val REPORT_USER = "social/api/report/user"
        const val LIST_FRIEND_SUGGESTION = "social/api/relationships/friend-suggestion"
        const val LIST_NOTIFICATIONS = "social/api/notifications"
        const val LIST_FRIEND_INVITATION = "social/api/users/friend-invitations"
        const val UPDATE_FRIEND_INVITATION = "social/api/relationships/friend-invitation"
        const val GET_POST_PRIVACY = "social/api/privacy/post"
        const val CREATE_POST = "social/api/posts"
        const val UPDATE_POST = "social/api/posts/{id}"
        const val GET_OWNER_PRODUCT_SOCIAL = "social/api/products/owner/{ownerId}"
        const val FOLLOW_PAGE = "social/api/relationships/follow-page"
        const val UN_FOLLOW_PAGE = "social/api/relationships/un-follow-page"
        const val LIST_MISSION = "social/api/loyalty/campaign/{id}/mission-list"
        const val LIST_MISSION_ACTIVE = "social/api/loyalty/campaign/{id}/mission-list-active"
        const val PIN_POST = "social/api/posts/pin"
        const val REGISTER_DEVICE = "social/api/users/login-device"
        const val UNREGISTER_DEVICE = "social/api/users/logout-device"
        const val PRODUCT_SHARE_LINK = "social/api/products/{id}/share-link"
    }

    object Layout {
        const val LAYOUTS = "social/api/layouts"
        const val PAGES = "social/api/pages/{id}"
    }

    object Settings {
        const val SETTING = "settings/client"
        const val SETTING_SOCIAL = "$PATH/cms/system-settings"
        const val NOTIFY_SETTING = "$PATH/notifications/turnoff"
        const val CONFIG_UPDATE_APP = "$PATH/cms/config-update-app"
    }

    object Auth {
        const val LOGIN = "users/login"
        const val LOGIN_DEVICE = "login/anonymous"
        const val REFRESH_TOKEN = "users/refresh-token"
        const val LOGIN_FACEBOOK = "facebook/login"
    }

    object User {
        const val ME = "users/me"
        const val CHANGE_PASSWORD = "users/me/change-password"
        const val ADDRESSES = "users/me/addresses"
        const val DETAIL_ADDRESS = "users/me/addresses/{id}"
        const val SEND_OTP_RESET_PASSWORD = "users/send-otp"
        const val RESET_PASSWORD = "users/reset-password"
        const val SEND_OTP_CONFIRM_PHONE = "users/send-otp-confirm-phone"
        const val CONFIRM_NEW_PHONE = "users/user-confirm-phone"
        const val CONFIRM_PHONE = "users/confirm-phone"
        const val REGISTER = "users/register"
        const val CHECK_CREDENTIALS = "users/check-credentials"
        const val UPDATE = "c"
        const val PROFILE = "users/{id}/profile"
        const val LIST_FRIENDS = "${PATH}/users/{id}/friends"
        const val RANK_OF_USER = "${PATH}/users/rank"
        const val VNSHOP_LINK = "users/vnshop-link"
        const val MY_ID = "social/api/users/my-id"
        const val ICOIN = "${PATH}/users/balance"
        const val USER_QUERY = "social/api/users/query"
        const val POST_FAVOURITE_TOPIC = "social/api/favourite-topics/user"
    }

    object Follow {
        const val FOLLOWERS = "followers"
        const val FOLLOWINGS = "followings"
    }

    object Address {
        const val CITIES = "cities"
        const val CITIES_V2 = "social/api/locations/cities"
        const val CITY = "cities/{cityId}"
        const val DISTRICTS = "districts"
        const val DISTRICT = "districts/{districtId}"
        const val WARDS = "wards"
        const val WARD = "wards/{wardId}"
    }

    object Product {
        const val LIST = "products"
        const val SCAN = "${PATH}/products/scan"
        const val PRODUCT_DETAIL_BY_BARCODE = "${PATH}/products/barcode/{barcode}"
        const val PRODUCT_DETAIL_BY_ID = "${PATH}/products/{id}"
        const val HISTRORIES = "histories/products"
        const val DELETE_PRODUCT_BARCODE = "histories/products/{id}"
        const val HISTRORIES_SHOP = "histories/products/shops"
        const val PRODUCT_REPORT = "${PATH}/report/product"
        const val MESSAGE_VERIRY = "${PATH}/cms/system-setting-messages/verify_message"
        const val FORM_REPORT_CONTRIBUTE = "${PATH}/products/contributions/{id}/vote"
        const val GET_NEWS_PRODUCT_CATEGORY = "${PATH}/home/new-product/categories/{categoryId}/products"
        const val GET_RELATED_PRODUCT_SOCIAL = "social/api/products/{id}/related-products"
        const val GET_LIST_CONTRIBUTOR_PRODUCT = "social/api/products/{barcode}/contributions"
        const val GET_LIST_REPORT_CONTRIBUTE = "${PATH}/products/contributions/report-form"
        const val GET_INFORMATION_PRODUCT = "${PATH}/products/{id}/information/{code}"
        const val GET_LIST_QUESTIONS = "products/{barcode}/questions"
        const val POST_TRANSPARENCY = "${PATH}/products/{productId}/transparency"
        const val POST_REVIEW = "${PATH}/products/{id}/review"
        const val COMMENT_QUESTION = "social/api/questions/{id}/comment"
        const val POST_LIKE_QUESTION = "social/api/questions/{id}/expressive"
        const val GET_BOOK_MARK = "products/user-bookmarks/{userId}"
        const val GET_LIST_REVIEW = "social/api/products/{id}/reviews"
        const val GET_LIST_REVIEW_NOT_ME = "social/api/products/{id}/reviews/not-me"
        const val GET_REVIEW_SUMMARY = "social/api/products/{id}/review-summary"
        const val GET_LIST_COMMENT_REVIEW = "social/api/reviews/{id}/comments"
        const val GET_LIST_CHILD_COMMENT = "social/api/comments/{id}/replies"
        const val POST_COMMENT_REVIEW = "social/api/reviews/{id}/comment"
        const val TURN_OFF_NOTIFY = "social/api/notifications/unsubscribe"
        const val POST_COMMENT_REPLY = "social/api/comments/{id}/reply"
        const val DELETE_COMMENT = "social/api/comments/{id}"
        const val DELETE_POST = "social/api/posts/{id}"
        const val GET_MY_REVIEW = "social/api/products/{id}/my-review"
        const val GET_DETAIL_REVIEW = "social/api/reviews/{id}"
        const val GET_CRITERIA = "products/{id}/criteria"
        const val POST_LIKE_REVIEW = "social/api/reviews/{id}/expressive"
        const val POST_VOTE_CONTRIBUTOR = "social/api/products/contributions/{id}/vote"
        const val GET_PRIVACY_POST = "social/api/privacy/post"
        const val PUT_PRIVACY_POST = "social/api/privacy/post/{postId}"
        const val LINK_PRODUCT = "social/api/products/{id}/share-link"
        const val CHECK_BOOKMARK = "social/api/products/{id}/bookmark/bookmark-check"
        const val ADD_BOOKMARK = "social/api/products/{id}/bookmark"
        const val DELETE_BOOKMARK = "social/api/products/{id}/bookmark/delete"
        const val LIST_DISTRIBUTORS = "social/api/products/{id}/distributors"
        const val REGISTER_BUY_PRODUCT = "social/api/cms/order/purchase-reserved"
    }

    object Category {
        const val LIST = "categories"
    }

    object Review {
        const val LIST_USEFUL = "reviews/useful"
        const val UN_NOTICATION = "/notifications/unsubscribe"
        const val RE_NOTICATION = "/notifications/resubscribe"

    }

    internal object Additives {
        const val LIST = "additives"
        const val DETAIL = "additives/{id}"
    }

    object Business {
        const val LIST = "pages"
        const val DETAIL = "pages/{id}"
        const val LIST_CATEGORY = "pages/{id}/categories"
        const val GET_MAP_PAGE = "bookmarks/pages/batch-delete"
        const val GET_LIST_USER_FOLLOW_PAGE = "${PATH}/pages/{id}/query-follower"
    }

    object Ads {
        const val ITEM = "ads"
        const val IGNORE = "ads/{id}/hide" //POST device_id
        const val ADS_SOCIAL = "ads_1.json" //POST device_id
        const val ADS_DEV = "ads_2.json"
    }

    object Notify {
        const val MARK_READ = "social/api/notifications/mark-read"
        const val DELETE = "social/api/notifications/{id}"
        const val UNSUBCRIBE = "social/api/notifications/unsubscribe"
        const val RESUBCRIBE = "social/api/notifications/resubscribe"
    }

    object News {
        const val LIST = "articles"
        const val LIST_CATEGORY = "${PATH}/cms/article-categories"
        const val DETAIL = "articles/{id}"
        const val DETAIL_SOCIAL = "${PATH}/cms/articles/{id}"
        const val LIST_SOCIAL = "social/api/cms/articles"
    }

    object Shop {
        const val DETAIL = "shops/{id}"
        const val LIST_VARIANT = "shops/variants"
        const val LIST_PRODUCT = "shop-products"
        const val SCAN_BUY = "scan/{barcode}/buy"
        const val REVIEW = "criteria/review"
        const val LIST_CRITERIA = "criteria/object/{id}/type/shop"
    }

    object Cart {
        const val CART_ITEMS = "${PATH}/carts/items"
        const val ADD_CART_ITEM = "${PATH}carts/add-to-cart"
    }

    object Checkout {
        const val CHECKOUTS = "checkouts"
        const val COMPLETE = "checkouts/complete"
    }

    object Order {
        const val LIST = "${PATH}/cms/order"
        const val LIST_REPORT_ERROR = "${PATH}/cms/order/report-error"
        const val PUT_REPORT_ERROR = "${PATH}/cms/order/{id}/error"
        const val DETAIL = "orders/{id}"
        const val PAY = "orders/{id}/pay"
        const val COMPLETE = "orders/{id}/complete"
        const val UPDATE_STATUS = "${PATH}/cms/order/{id}/status"
    }

    object Location {
        const val SEARCH = "social/api/locations/autocomplete-address"
        const val DETAIL = "social/api/locations/address-to-geo"
    }

    object Chat {
        const val SYNC_CONTACTS = "users/me/sync-contacts"
        const val GET_USER = "accounts/{id}"
        const val GET_FOLLOWING = "followings"
    }


    object Campaign {
        const val LIST_CAMPAIGN = "social/api/loyalty/campaign"
        const val CAMPAIGN_ONBOARDING = "social/api/loyalty/campaign/{id}/onboarding"
        const val INFO_CAMPAIGN = "social/api/loyalty/campaign/{id}/info"
        const val UNBOX_SHAKE_GIFT = "${PATH}/loyalty/reward/box/open"
        const val SUMMARY = "user/loyalty/summary"
        const val REFUSE_GIFT = "loyalty/reward/item/cancel"
        const val MISSION_DETAIL = "${PATH}/loyalty/mission/{id}"
        const val DETAIL_REWARD = "loyalty/reward/item/{id}"
        const val GET_CAMPAIGN_REWARD = "${PATH}/loyalty/campaign/{id}/reward"
        const val GET_WINNER_CAMPAIGN = "social/api/loyalty/campaign/{id}/user-has-reward"
        const val GET_TOP_WINNER_CAMPAIGN = "social/api/loyalty/campaign/{id}/top-reward"
        const val GET_LIST_REWARD_ITEM = "${PATH}/users/reward-item"
        const val GET_LIST_MY_GIFT_BOX = "loyalty/reward/box"
        const val LIST_ICON_GRID = "${PATH}/loyalty/campaign/{id}/icon"
    }

    object Popup {
        const val GET_POPUP_BY_SCREEN = "social/api/popup-ads/find-by-target"
    }

    object Stamp {
        const val DETAIL_STAMP = "scan"
        const val MORE_PRODUCT_VERIFIED_DISTRIBUTOR = "products/distributor/{distributorId}"
        const val MORE_PRODUCT_VERIFIED_VENDOR = "products/vendor/{vendorId}"
        const val HISTORY_GUARANTEE = "guarantee/history/{serial}"
        const val VERIFIED_NUMBER_GUARANTEE = "validate/{serial}/{phone}"
        const val DETAIL_CUSTOMER_GUARANTEE = "customers/{distributor_id}/{phone}"
        const val GET_NAME_CITY_GUARANTEE = "cities/{cities_id}"
        const val GET_NAME_DISTRICTS_GUARANTEE = "districts/{districts_id}"
        const val UPDATE_INFORMATION_CUSTOMER_GUARANTEE = "update/serial/{serial}"
        const val GET_SHOP_VARIANT = "shops/variants"
        const val GET_CONFIG_ERROR = "config"
        const val SCAN_CHECK_STAMP = "stamps/check"
        const val GET_ID_PAGE_SOCIAL = "$PATH/pages/query"
    }

    object StampV6 {
        const val DETAIL_STAMP_QRM = "stamps/scan-qrm"
        const val DETAIL_STAMP_QRI = "stamps/scan"
        const val DETAIL_PRODUCT_BY_SKU = "scan/{barcode}"
        const val GET_HISTORY_GUARANTEE_V6 = "logs/guarantee/{qrm}"
        const val GET_LIST_STORE_V6 = "g_store/stamp/{id}"
        const val UPDATE_INFORMATION_CUSTOMER_GUARANTEE_V6 = "stamps/customer"
    }

    object StampV5 {
        const val DETAIL_STAMP = "stamps/scan/v5"
    }

    object GiftStore {
        const val GET_LIST_GIFT_STORE = "gift-exchange"
        const val GET_DETAIL_GIFT_STORE = "gift-exchange/detail"
        const val GET_LIST_PRODUCT_OF_STORE = "${PATH}/shop/shop-items"
        const val GET_PRODUCT_DETAIL_OF_STORE = "shop-items/{id}"
        const val ADD_TO_CART = "${PATH}/cms/order/cart/up-quantity"
    }

    object Topup {
        const val GET_LIST_TOPUP_SERVICE = "topups/services"
        const val GET_LIST_TOPUP_SERVICE_V2 = "social/api/topup/provider"
        const val BUY_TOPUP_SERVICE = "topups/exec-topup"
        const val GET_LIST_HISTORY_BUY_TOPUP = "topups"
        const val GET_LIST_HISTORY_BUY_TOPUP_V2 = "social/api/topup/history-topup"
        const val GET_LIST_HISTORY_LOADED_TOPUP_V2 = "topup/history-loaded-card"
        const val TICK_USE_TOPUP = "topups/{id}/used"
        const val TICK_USE_TOPUP_V2 = "social/api/topup/card/used"
        const val LIST_PAYMENT_TYPE = "social/api/topup/pay-type"
        const val BUY_CARD = "social/api/topup/pay-buy-card"
        const val RECHARGE_CARD = "social/api/topup/pay-top-up"
        const val VN_PAY = "social/api/topup/url-vnpay"
        const val DETAIL_CARD = "social/api/topup/detail"
    }

    object Facebook {
        const val MAPPING = "facebook/mapping"
    }

    object Point {
        const val COINT_HISTORY = "${PATH}/users/wallet-history"
        const val RANK_HISTORY = "${PATH}/users/rank-history"
    }

    object Image {
        const val UPLOAD = "upload/stream"
    }

    object ProductQuestions {
        const val GET_PRODUCT_QUESTIONS = "product-questions"
        const val GET_LIST_ANSWER_BY_QUESTION = "product-questions/{questionId}/answers"
        const val CREATE_QUESTION = "product-questions"
        const val CREATE_ANSWER = "product-questions/{questionId}/answers"
    }

    object Suggest {
        const val GET_SUGGEST_PAGE = "${PATH}/pages/topics"
        const val GET_LIST_TOPIC = "${PATH}/favourite-topics"
    }

    object Page {
        const val GET_BRAND = "pages/{id}/brands"
        const val GET_HIGHLIGHT_PRODUCTS = "social/api/pages/{id}/highlight-products"
        const val GET_CATEGORIES_PRODUCTS = "social/api/pages/{id}/product-by-category"
        const val GET_PRODUCTS_CATEGORY_PAGE = "social/api/pages/{id}/query"
        const val IMAGEE_ASSET = "${PATH}/pages/{id}/image-assets"
        const val GET_LIST_REPORT_PAGE = "${PATH}/report/page"
        const val SHARE_LINK = "${PATH}/posts/{id}/share-link"
        const val LIST_POST = "${PATH}/pages/{id}/posts"
        const val REPORT_POST = "${PATH}/report/post"
        const val RELATIONSHIP_CURRENT_USER = "${PATH}/relationships/information"
        const val GET_FRIEND_NOFOLLOW_PAGE = "social/api/users/friend-nofollow-page/{pageId}"
        const val POST_FOLLOW_PAGE_INVITATION = "social/api/relationships/follow-page-invitation"
        const val UNSUBCRIBE_PAGE = "${PATH}/notifications/unsubscribe"
        const val RESUBCRIBE_PAGE = "${PATH}/notifications/resubscribe"
        const val PAGE_USER_MANAGER = "${PATH}/pages/query"
        const val MY_OWNER_PAGE = "${PATH}/pages/page-owner"
        const val MY_FOLLOW_PAGE = "${PATH}/pages/page-follower"
        const val UPDATE_PAGE = "${PATH}/pages/{id}"
        const val BUTTON_CUSTOMIZE = "${PATH}/pages/{id}/button-customizes"
        const val SKIP_INVITE_USER = "${PATH}/pages/ignore-invite"
    }

    object Relationship {
        const val FOLLOW_USER = "${PATH}/relationships/follow-user"
        const val ME_FOLLOW_USER = "relationships/me-following-user"
        const val REMOVE_FRIEND_SUGGESTION = "social/api/relationships/remove-friend-suggestion"
        const val FOLLOW_PAGE = "${PATH}/relationships/follow-page"
        const val UN_FOLLOW_PAGE = "${PATH}/relationships/un-follow-page"
    }

    object GiftCampaign {
        const val GET_GIFT_RECEIVED = "loyalty/reward/item"
    }

    object Search {
        const val GET_PRODUCT = "social/api/products/search"
        const val GET_REVIEW = "social/api/reviews/search"
        const val GET_PAGE = "social/api/pages/query"
        const val GET_SHOP = "social/api/pages/shop/query"
        const val GET_CATEGORY_PARENT = "social/api/categories/level"
        const val GET_CATEGORY_CHILD = "social/api/categories/children"
        const val GET_POPULAR_SEARCH = "social/api/search/keywords"
        const val GET_AUTO_SEARCH = "social/api/search/word/{word}"
        const val GET_RECENT_SEARCH = "social/api/search/recent-search"
    }

    object Post {
        const val GET_POST_DETAIL = "social/api/posts/{id}"
        const val GET_LIST_COMMENTS_OF_POST = "social/api/posts/{id}/comments"
        const val POST_LIKE_COMMENT = "social/api/comments/{id}/expressive"
        const val POST_COMMENT = "social/api/posts/{id}/comment"
        const val LIKE_POST = "social/api/posts/{id}/expressive"
    }

    external fun allUtilities(): String

    object PVCombank {
        const val PATH_PVCBANK = "pvcombank/api/cards"
        const val HAS_CARD = "${PATH_PVCBANK}/has-card"
        const val FORM_AUTH = "${PATH_PVCBANK}/oauth/auth"
        const val INFO_CARD = "${PATH_PVCBANK}/{cardId}/info"
        const val TRANSACTION_CARD = "${PATH_PVCBANK}/{cardId}/transactions"
        const val LIST_CARD = PATH_PVCBANK
        const val LOCK_CARD = "${PATH_PVCBANK}/{cardId}/lock"
        const val UNLOCK_CARD = "${PATH_PVCBANK}/{cardId}/unlock"
        const val VERIFY_OTP_UNLOCK_CARD = "${PATH_PVCBANK}/{reqId}/confirmation"
        const val SET_DEFAULT_CARD = "${PATH_PVCBANK}/{cardId}/default"
        const val GET_FULL_CARD = "${PATH_PVCBANK}/{cardId}/fullcard"
        const val GET_KYC = "${PATH_PVCBANK}/kyc"
    }

    object History {
        const val GET_LIST_BIG_CORP = "${PATH}/shop/big-corp"
        const val GET_LIST_SCAN_HISTORY = "${PATH}/history-action/scan/query"
        const val GET_LIST_STORE_SELL = "${PATH}/history-action/product/{id}/store-sell"
        const val GET_CART_COUNT = "${PATH}/cms/order/cart/count"
        const val GET_LIST_FILTER_TYPE = "${PATH}/history-action/filter"
        const val GET_SUGGEST_STORE = "${PATH}/history-action/suggest-store"
        const val GET_PRODUCT_OF_SHOP = "${PATH}/history-action/store/{id}/products"
        const val GET_ROUTES_SHOP = "${PATH}/locations/route"
        const val GET_STORE_NEAR = "${PATH}/history-action/product/{id}/search-store-near"
    }

    val tripiTokenProduct: String
        get() {
            return if (BuildConfig.FLAVOR.contentEquals("dev")) tripiTokenDev() else tripiTokenProd()
        }

    init {
        System.loadLibrary("native-lib")
    }

    val socialHost: String
        get() {
            return if (BuildConfig.FLAVOR.contentEquals("dev")) socialHostDev() else socialHostProd()
        }

    val defaultHost: String
        get() {
            return if (BuildConfig.FLAVOR.contentEquals("dev")) defaultHostDev() else defaultHostProd()
        }

    val DETAIL_STAMP_HOST: String
        get() {
            return if (BuildConfig.FLAVOR.contentEquals("dev")) detailStampHostDev() else detailStampHostProd()
        }

    val adsSocialHost: String
        get() {
            return if (BuildConfig.FLAVOR.contentEquals("dev")) adsSocialHostDev() else adsSocialHostProd()
        }

    val insiderPartnerName: String
        get() {
            return if (BuildConfig.FLAVOR.contentEquals("dev")) insiderPartnerNameDev() else insiderPartnerNameProd()
        }

    val trackingUrlTeko: String
        get() {
            return if (BuildConfig.FLAVOR.contentEquals("dev")) trackingTekoUrlDev() else trackingTekoUrlProd()
        }

    val trackingAppId: String
        get() {
            return if (BuildConfig.FLAVOR.contentEquals("dev")) trackingAppIdDev() else trackingAppIdProd()
        }

    private external fun socialHostDev(): String
    private external fun socialHostProd(): String
    private external fun defaultHostDev(): String
    private external fun defaultHostProd(): String
    private external fun detailStampHostDev(): String
    private external fun detailStampHostProd(): String
    private external fun adsSocialHostDev(): String
    external fun adsSocialHostProd(): String
    external fun adsSocialHostOnly(): String
    external fun adsOriginSocialHostOnly(): String
    external fun uploadFileHost(): String
    external fun uploadFileHostV1(): String
    external fun detailStampV6Host(): String
    external fun scanditLicenseKey(): String
    external fun insiderPartnerNameDev(): String
    external fun insiderPartnerNameProd(): String
    external fun tripiTokenDev(): String
    external fun tripiTokenProd(): String
    external fun trackingTekoUrlDev(): String
    external fun trackingTekoUrlProd(): String
    external fun trackingAppIdProd(): String
    external fun trackingAppIdDev(): String
    external fun themeSetting(): String
    external fun productsECommerce(): String

    fun checkErrorString(code: String, default: String?): String {
        return when (code) {
            "500" -> {
                getApplicationByReflect().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
            }
            "U100" -> {
                getApplicationByReflect().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
            }
            "400" -> {
                getApplicationByReflect().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
            }
            "U102" -> {
                getApplicationByReflect().applicationContext.getString(R.string.token_khong_hop_le_hoac_het_han)
            }
            "U103" -> {
                getApplicationByReflect().applicationContext.getString(R.string.invalid_user_id_on_header)
            }
            "U104" -> {
                getApplicationByReflect().applicationContext.getString(R.string.invalid_user_id_on_header)
            }
            "U105" -> {
                getApplicationByReflect().applicationContext.getString(R.string.this_future_is_disable)
            }
            "U3000" -> {
                getApplicationByReflect().applicationContext.getString(R.string.tai_khoan_da_ton_tai)
            }
            "U3001" -> {
                getApplicationByReflect().applicationContext.getString(R.string.ma_otp_khong_chinh_xac)
            }
            "U3002" -> {
                getApplicationByReflect().applicationContext.getString(R.string.vuot_qua_so_lan_nhap_sai_otp)
            }
            "U3003" -> {
                getApplicationByReflect().applicationContext.getString(R.string.tai_khoan_khong_hop_le)
            }
            "U3004" -> {
                getApplicationByReflect().applicationContext.getString(R.string.khong_tim_thay_tai_khoan)
            }
            "U3005" -> {
                getApplicationByReflect().applicationContext.getString(R.string.mat_khau_khong_chinh_xac)
            }
            "U3006" -> {
                getApplicationByReflect().applicationContext.getString(R.string.mat_khau_khong_chinh_xac)
            }
            "U3007" -> {
                getApplicationByReflect().applicationContext.getString(R.string.sdt_sai_dinh_dang)
            }
            "U3009" -> {
                getApplicationByReflect().applicationContext.getString(R.string.ma_otp_khong_chinh_xac)
            }
            "U3010" -> {
                getApplicationByReflect().applicationContext.getString(R.string.tai_khoan_hoac_mk_khong_chinh_xac)
            }
            "U3011" -> {
                getApplicationByReflect().applicationContext.getString(R.string.tai_khoan_bi_khoa)
            }
            "U3012" -> {
                getApplicationByReflect().applicationContext.getString(R.string.tai_khoan_bi_khoa_tu_social)
            }
            "U3013" -> {
                getApplicationByReflect().applicationContext.getString(R.string.token_facebook_khong_hop_le)
            }
            "U3014" -> {
                getApplicationByReflect().applicationContext.getString(R.string.dang_nhap_lan_dau_bang_fb_vui_long_xac_thuc_sdt)
            }
            "U3015" -> {
                getApplicationByReflect().applicationContext.getString(R.string.mat_khau_khong_dung_dinh_dang)
            }
            "U3016" -> {
                getApplicationByReflect().applicationContext.getString(R.string.tai_khoan_fb_da_dang_ky_he_thong)
            }
            "U3017" -> {
                getApplicationByReflect().applicationContext.getString(R.string.register_request_has_existed)
            }
            "U3018" -> {
                getApplicationByReflect().applicationContext.getString(R.string.da_ton_tai_yeu_cau_quen_mat_khau)
            }
            "U3019" -> {
                getApplicationByReflect().applicationContext.getString(R.string.tai_khoan_da_duoc_xac_thuc_sdt)
            }
            "U3020" -> {
                getApplicationByReflect().applicationContext.getString(R.string.da_ton_tai_yeu_cau_nay)
            }
            "U3031" -> {
                getApplicationByReflect().applicationContext.getString(R.string.sdt_da_duoc_dang_ky)
            }
            "U3032" -> {
                getApplicationByReflect().applicationContext.getString(R.string.client_id_existed)
            }
            "U3033" -> {
                getApplicationByReflect().applicationContext.getString(R.string.client_id_not_existed)
            }
            "U3034" -> {
                getApplicationByReflect().applicationContext.getString(R.string.permission_denied)
            }
            "U3035" -> {
                getApplicationByReflect().applicationContext.getString(R.string.client_id_and_user_not_matching)
            }
            "U3036" -> {
                getApplicationByReflect().applicationContext.getString(R.string.tai_khoan_da_duoc_xac_thuc_email)
            }
            "U3037" -> {
                getApplicationByReflect().applicationContext.getString(R.string.email_nay_da_duoc_xac_thuc_o_tai_khoan_khac)
            }
            "U3038" -> {
                getApplicationByReflect().applicationContext.getString(R.string.token_khong_hop_le)
            }
            "U3041" -> {
                getApplicationByReflect().applicationContext.getString(R.string.email_khong_dung_dinh_dang)
            }
            "U3042" -> {
                getApplicationByReflect().applicationContext.getString(R.string.tai_khoan_da_duoc_cai_dat_mat_khau)
            }
            "U3043" -> {
                getApplicationByReflect().applicationContext.getString(R.string.tai_khoan_chua_duoc_dang_ki_su_dung_voi_ung_dung_nay)
            }
            "U4000" -> {
                getApplicationByReflect().applicationContext.getString(R.string.sms_format_not_found)
            }
            "U4001" -> {
                getApplicationByReflect().applicationContext.getString(R.string.phone_number_max_sms)
            }
            "U4002" -> {
                getApplicationByReflect().applicationContext.getString(R.string.the_minimum_time_between_message_is_not_reached)
            }
            "U4003" -> {
                getApplicationByReflect().applicationContext.getString(R.string.vuot_qua_so_lan_gui_otp)
            }
            "U4004" -> {
                getApplicationByReflect().applicationContext.getString(R.string.co_loi_khi_gui_otp)
            }
            "U4005" -> {
                getApplicationByReflect().applicationContext.getString(R.string.co_loi_khi_gui_email)
            }
            "U5001" -> {
                getApplicationByReflect().applicationContext.getString(R.string.application_or_client_secret_invalid)
            }
            "U5002" -> {
                getApplicationByReflect().applicationContext.getString(R.string.application_permission_denied)
            }
            else -> {
                default ?: ""
            }
        }
    }

    private fun getApplicationByReflect(): Application {
        try {
            @SuppressLint("PrivateApi") val activityThread = Class.forName("android.app.ActivityThread")
            val thread = activityThread.getMethod("currentActivityThread").invoke(null)
            val app = activityThread.getMethod("getApplication").invoke(thread)
                    ?: throw NullPointerException("u should init first")
            return app as Application
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        throw NullPointerException("u should init first")
    }
}