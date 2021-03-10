package vn.icheck.android.constant

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.WindowManager
import androidx.core.content.ContextCompat.getSystemService
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.models.ICAdsNew
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import java.util.regex.Pattern


object Constant {
    private val listAdsNew = mutableListOf<ICAdsNew>()

    fun setListAdsNew(listAds: MutableList<ICAdsNew>) {
        listAdsNew.clear()
        listAdsNew.addAll(listAds)

        for (ads in listAdsNew) {
            if (ads.objectType.contains("campaign")) {
                val listCampaignItr = ads.data.iterator()
                while (listCampaignItr.hasNext()) {
                    val campaign = listCampaignItr.next()
                    val timeLeft = System.currentTimeMillis() - (TimeHelper.convertDateTimeSvToMillisecond(campaign.endTime)
                            ?: System.currentTimeMillis())

                    if (timeLeft > AlarmManager.INTERVAL_DAY) {
                        listCampaignItr.remove()
                    }
                }
            }
        }
    }

    fun getlistAdsNew(): MutableList<ICAdsNew> {
        return listAdsNew
    }

    const val TYPE_URL = 1
    const val TYPE_DOCUMENT = 2
    const val TYPE_PHONE_NUMBER = 3
    const val TYPE_SMS = 4
    const val TYPE_MAIL = 5
    const val TYPE_COORDINATE = 6
    const val TYPE_CONTACT = 7
    const val TYPE_CALENDAR = 8
    const val TYPE_WIFI = 9
    const val TYPE_UNDEFINED = 0

    const val DATA = "data"
    const val DATA_1 = "data_1"
    const val DATA_2 = "data_2"
    const val DATA_3 = "data_3"
    const val DATA_4 = "data_4"
    const val DATA_5 = "data_5"
    const val DATA_6 = "data_5"
    const val DATA_7 = "data_5"
    const val DATA_8 = "data_5"

    const val ID = "id"
    const val CODE = "code"
    const val BARCODE = "barcode"
    const val MA_VACH = "Mã vạch"
    const val MA_QR = "Mã QR"

    const val CART = "cart"

    const val REGISTER_TYPE = "register_type"
    const val LOGIN_FACEBOOK_TYPE = "login_facebook_type"

    const val BANNER = "banner"
    const val COLLECTION = "collection"
    const val DIRECT_SURVEY = "direct_survey"
    const val BANNER_SURVEY = "banner_survey"
    const val SELECT = "select"
    const val GRID_TYPE = "grid"
    const val URL = "url"
    const val HTML = "html"
    const val DEFAULT_ITEM_COUNT = 10
    const val PAGESIZE = 10

    const val MESSAGE_TYPE = 13

    const val IMAGE = "image"
    const val VIDEO = "video"
    const val STICKER = "sticker"
    const val TITLE = "title"

    const val USER = "user"
    const val PAGE = "page"
    const val ENTERPRISE = "enterprise"
    const val SHOP = "shop"

    const val PAGE_BRAND_TYPE = 1 // Nhãn hàng
    const val PAGE_EXPERT_TYPE = 2 // Chuyên gia
    const val PAGE_ENTERPRISE_TYPE = 3 // Doanh nghiệp
    const val PAGE_SHOP_TYPE = 4 // Shop
    fun getPageType(type: Int): String {
        return when (type) {
            PAGE_BRAND_TYPE -> {
                "brand"
            }
            PAGE_EXPERT_TYPE -> {
                "expert"
            }
            PAGE_ENTERPRISE_TYPE -> {
                "enterprise"
            }
            else -> {
                "shop"
            }
        }
    }

    //Type Post
    const val REVIEW = "review"
    const val QUESTION = "question"

    const val USER_LEVEL_STANDARD = 1
    const val USER_LEVEL_SILVER = 2
    const val USER_LEVEL_GOLD = 3
    const val USER_LEVEL_DIAMOND = 4

    const val NAMTRAM = 500000
    const val HAITRAM = 200000
    const val MOTTRAM = 100000
    const val NAMMUOINGHIN = 50000
    const val HAIMUOINGHIN = 20000
    const val MUOINGHIN = 10000
    const val NAMNGHIN = 5000
    const val HAINGHIN = 2000
    const val MOTNGHIN = 1000
    const val NAMTRAMDONG = 500
    const val HAITRAMDONG = 200
    const val MOTTRAMDONG = 100
    const val NAMMUOI = 50
    const val HAIMUOI = 20
    const val MUOI = 10

    const val FRIEND_REQUEST_AWAIT = 1
    const val FRIEND_REQUEST_ACCEPTED = 2
    const val FRIEND_REQUEST_DENIED = -1

    /**
     * Search
     */
    const val EVERY_ONE = "EVERY_ONE"
    const val EVERYONE = "EVERYONE"
    const val ONLY_ME = "ONLY_ME"
    const val FRIEND = "FRIEND"
    const val ONLY_MY_FRIEND = "ONLY_MY_FRIEND"

    /**
     * PRIVACY
     */
    const val PRODUCT_TYPE = 11
    const val REVIEW_TYPE = 12
    const val PAGE_TYPE = 13
    const val USER_TYPE = 14
    const val SHOP_TYPE = 15

    /*
    Relationships
     */
    const val friendInvitationMeUserIdList = "friendInvitationMeUserIdList"
    const val myFriendInvitationUserIdList = "myFriendInvitationUserIdList"
    const val myFollowingUserIdList = "myFollowingUserIdList"
    const val myFriendIdList = "myFriendIdList"
    const val myFollowingPageIdList = "myFollowingPageIdList"
    const val myOwnerPageIdList = "myOwnerPageIdList"


    /*
    ProductReview
     */
    const val TYPE_POST_YOUR_REVIEW = 1
    const val TYPE_YOUR_REVIEW = 2
    const val TYPE_ALL_REVIEW = 4
    const val TYPE_NOT_CRITERIA = 1
    const val TYPE_CRITERIA = 2
    const val TYPE_HEADER_REVIEW = 3
    const val TYPE_LIST_REVIEWS = 4
    const val TYPE_LOAD_MORE = 9

    /*
    * Error
    * */
    // -1	Lỗi hệ thống
    const val ERROR_SERVER = 1
    const val ERROR_INTERNET = 2
    const val ERROR_UNKNOW = 3
    const val ERROR_EMPTY = 4
    const val ERROR_EMPTY_SEARCH = 5
    const val ERROR_EMPTY_WATCHING = 6
    const val ERROR_EMPTY_FOLLOW = 7
    const val RESULT_EMPTY = 8
    const val SEARCH_MORE = 9

    /*
    * Ads
    * */
    const val PAGE_APPROACH = "page_approach"                   // Tiếp cận
    const val PAGE_CHANGE_SUBCRIBE = "page_change_subscribe"    // Chuyển đổi tham gia
    const val PAGE_CONTACT = "page_contact"                     // Liên hệ
    const val PRODUCT_CHANGE_BUY = "product_change_buy"         // Mua sản phẩm
    const val PRODUCT_APPROACH = "product_approach"             // Tiếp cận sản phẩm

    const val SLIDE = "slide"
    const val ADS_SLIDE_TYPE = 1
    const val GRID = "grid"
    const val ADS_GRID_TYPE = 2
    const val HORIZONTAL = "horizontal"
    const val ADS_HORIZONTAL_TYPE = 3

    /*
    * Slide
    * */
    const val TIME_DELAY_SLIDE_SECOND = 3L
    const val TIME_DELAY_SLIDE_MILLISECOND = 3000L

    /**
     * Điều chỉnh lại thuộc tính của ảnh để phù hợp với màn hình điện thoại
     *
     * @param bodyHTML nội dung html trước khi chỉnh sửa
     * @return nội dung html đã chỉnh sửa
     */
    fun getHtmlData(bodyHTML: String): String {
        val head = "<head><style> content{max-width: 100% !important; width: 100% !important; height: auto !important;} img{max-width: 100% !important; width: 100% !important; height: auto !important;}</style></head>"
        return "<html>$head<body>$bodyHTML</body></html>"
    }

    fun getAdditivesIconFromCode(code: String): Int {
        return when (code) {
            "L" -> R.drawable.ic_sad_36dp
            "K" -> R.drawable.ic_quizzical_36dp
            "? K" -> R.drawable.ic_quizzical_question_36dp
            "J" -> R.drawable.ic_happy_faces_36dp
            "J J" -> R.drawable.ic_double_happy_faces_36dp
            "L L" -> R.drawable.ic_double_sad_36dp
            else -> android.R.color.transparent
        }
    }

    fun getUserLevelNameUpcase(context: Context, level: Int): String {
        return when (level) {
            USER_LEVEL_SILVER -> {
                context.getString(R.string.bac_upcase)
            }
            USER_LEVEL_GOLD -> {
                context.getString(R.string.vang_upcase)
            }
            USER_LEVEL_DIAMOND -> {
                context.getString(R.string.kim_cuong_upcase)
            }
            else -> {
                context.getString(R.string.chuan_upcase)
            }
        }
    }

    fun getUserLevelName(context: Context, level: Int): String {
        return when (level) {
            USER_LEVEL_SILVER -> {
                context.getString(R.string.bac)
            }
            USER_LEVEL_GOLD -> {
                context.getString(R.string.vang)
            }
            USER_LEVEL_DIAMOND -> {
                context.getString(R.string.kim_cuong)
            }
            else -> {
                context.getString(R.string.chuan)
            }
        }
    }

    fun getUserLevelIcon20(level: Int?): Int {
        return when (level) {
            USER_LEVEL_SILVER -> {
                R.drawable.ic_level_silver_20dp
            }
            USER_LEVEL_GOLD -> {
                R.drawable.ic_level_gold_20dp
            }
            USER_LEVEL_DIAMOND -> {
                R.drawable.ic_level_diamond_20dp
            }
            else -> {
                R.drawable.ic_level_standard_20dp
            }
        }
    }

    fun getUserLevelIcon28(level: Int?): Int {
        return when (level) {
            USER_LEVEL_SILVER -> {
                R.drawable.ic_level_silver_28dp
            }
            USER_LEVEL_GOLD -> {
                R.drawable.ic_level_gold_28dp
            }
            USER_LEVEL_DIAMOND -> {
                R.drawable.ic_level_diamond_28dp
            }
            else -> {
                R.drawable.ic_level_standard_28dp
            }
        }
    }

    fun getAvatarLevelIcon16(level: Int?): Int {
        return when (level) {
            USER_LEVEL_SILVER -> {
                R.drawable.ic_avatar_rank_silver_16dp
            }
            USER_LEVEL_GOLD -> {
                R.drawable.ic_avatar_rank_gold_16dp
            }
            USER_LEVEL_DIAMOND -> {
                R.drawable.ic_avatar_rank_diamond_16dp
            }
            else -> {
                R.drawable.ic_avatar_rank_standard_16dp
            }
        }
    }

    fun getVerticalProductBackground(itemType: Int): Int {
        return when (itemType) {
            1 -> R.drawable.bg_product_item_top_left
            2 -> R.drawable.bg_product_item_top_right
            3 -> R.drawable.bg_product_item_bottom_left
            else -> R.drawable.bg_product_item_bottom_right
        }
    }

    fun getMissionInprogressIcon(event: String?): Int {
        return when (event) {
            "LOGIN" -> { // Đăng nhập
                R.drawable.ic_mission_login_inprogress_24dp
            }
            "SURVEY" -> { // Tham gia survey
                R.drawable.ic_mission_survey_inprogress_36dp
            }
            "REVIEW" -> { // Đánh gia sản phẩm
                R.drawable.ic_mission_review_inprogress_36dp
            }
            "ORDER" -> { // Mua hàng
                R.drawable.ic_mission_buy_inprogress_36dp
            }
            "ORDER.REVIEW" -> { // Đánh giá đơn hàng
                R.drawable.ic_mission_review_order_inprogress_24px
            }
            "SCAN.BARCODE" -> { // Quét mã barcode
                R.drawable.ic_mission_barcode_inprogress_24dp
            }
            "SCAN.QRCODE" -> { // Quét tem QRCode
                R.drawable.ic_mission_qrcode_inprogress_24dp
            }
            "INVITED" -> { // Giời thiệu người dùng
                R.drawable.ic_mission_invite_inprogress_24dp
            }
            "UPDATE.PROFILE.EMAIL" -> { // Xác thực email
                R.drawable.ic_mission_mail_inprogress_24dp
            }
            "UPDATE.PROFILE.PHONE" -> { // Xác thực số điện thoại
                R.drawable.ic_mission_update_phone_inprogress_24dp
            }
            "UPDATE.PROFILE.FACEBOOK" -> { // Liên kết tài khoản facebook
                R.drawable.ic_mission_facebook_inprogress_24dp
            }
            "UPDATE.PROFILE" -> { //Hoàn thiện hồ sơ người dùng
                R.drawable.ic_mission_update_information_inprogress_24dp
            }
            else -> {
                R.drawable.ic_mission_verification_inprogress_24dp
            }
        }
    }

    fun getMissionFailedIcon(event: String?): Int {
        return when (event) {
            "LOGIN" -> { // Đăng nhập
                R.drawable.ic_mission_login_fail_24dp
            }
            "SURVEY" -> { // Tham gia survey
                R.drawable.ic_mission_survey_fail_24dp
            }
            "REVIEW" -> { // Đánh gia sản phẩm
                R.drawable.ic_mission_review_fail_24dp
            }
            "ORDER" -> { // Mua hàng
                R.drawable.ic_mission_buy_fail_24dp
            }
            "ORDER.REVIEW" -> { // Đánh giá đơn hàng
                R.drawable.ic_mission_review_order_fail_24dp
            }
            "SCAN.BARCODE" -> { // Quét mã barcode
                R.drawable.ic_mission_barcode_fail_24dp
            }
            "SCAN.QRCODE" -> { // Quét tem QRCode
                R.drawable.ic_mission_qrcode_fail_24dp
            }
            "INVITED" -> { // Giời thiệu người dùng
                R.drawable.ic_mission_invite_fail_24dp
            }
            "UPDATE.PROFILE.EMAIL" -> { // Xác thực email
                R.drawable.ic_mission_mail_fail_24dp
            }
            "UPDATE.PROFILE.PHONE" -> { // Xác thực số điện thoại
                R.drawable.ic_mission_update_phone_fail_24dp
            }
            "UPDATE.PROFILE.FACEBOOK" -> { // Liên kết tài khoản facebook
                R.drawable.ic_mission_facebook_fail_24dp
            }
            "UPDATE.PROFILE" -> { //Hoàn thiện hồ sơ người dùng
                R.drawable.ic_mission_update_information_fail_24dp
            }
            else -> {
                R.drawable.ic_mission_verification_fail_24dp
            }
        }
    }

    fun isMarketingStamps(http: String): Boolean {
        val regex = SettingManager.domainQr.firstOrNull()?.regex ?: "(^|.)(cd.qcheck.vn|mkt.icheck.vn|qr.icheck.vn|qrmkt-scan.dev.icheck.vn)$"
        return Pattern.compile(regex).matcher(Uri.parse(http).host ?: "").matches()
    }


    fun callPhone(phone: String) {
        ICheckApplication.currentActivity()?.let { activity ->
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone}"))
            ActivityUtils.startActivity(activity, intent)
        }
    }

    fun sendEmail(email: String) {
        ICheckApplication.currentActivity()?.let { activity ->
            val mailIntent = Intent(Intent.ACTION_VIEW)
            val data = Uri.parse("mailto:?to=${email}")
            mailIntent.data = data
            try {
                ActivityUtils.startActivity(activity, Intent.createChooser(mailIntent, "Send mail..."))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun openUrl(url: String) {
        ICheckApplication.currentActivity()?.let { activity ->
            WebViewActivity.start(activity, url)
        }
    }

    fun getName(lastName: String?, firstName: String?): String {
        return if (!lastName.isNullOrEmpty()) {
            lastName + if (!firstName.isNullOrEmpty()) " $firstName" else ""
        } else {
            ICheckApplication.getString(R.string.dang_cap_nhat)
        }
    }

    fun formatPhone(phone: String?): String? {
        return if (phone?.length ?: 0 >= 10) {
            StringBuilder(phone!!).insert(phone.length - 3, " ").insert(phone.length - 6, " ").toString()
        } else {
            phone
        }
    }
}