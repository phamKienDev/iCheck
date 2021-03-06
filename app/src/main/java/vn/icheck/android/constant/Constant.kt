package vn.icheck.android.constant

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.util.getString
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
    const val DATA_6 = "data_6"
    const val DATA_7 = "data_7"
    const val DATA_8 = "data_8"

    const val ID = "id"
    const val CODE = "code"
    const val BARCODE = "barcode"
    const val MA_VACH = "M?? v???ch"
    const val MA_QR = "M?? QR"

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

    const val ORDER = "order"
    const val CONTRIBUTION = "contribution"

    const val USER = "user"
    const val PAGE = "page"
    const val PRODUCT = "product"
    const val ENTERPRISE = "enterprise"
    const val SHOP = "shop"

    const val PAGE_BRAND_TYPE = 1 // Nh??n h??ng
    const val PAGE_EXPERT_TYPE = 2 // Chuy??n gia
    const val PAGE_ENTERPRISE_TYPE = 3 // Doanh nghi???p
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
    // -1	L???i h??? th???ng
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
    const val PAGE_APPROACH = "page_approach"                   // Ti???p c???n
    const val PAGE_CHANGE_SUBCRIBE = "page_change_subscribe"    // Chuy???n ?????i tham gia
    const val PAGE_CONTACT = "page_contact"                     // Li??n h???
    const val PRODUCT_CHANGE_BUY = "product_change_buy"         // Mua s???n ph???m
    const val PRODUCT_APPROACH = "product_approach"             // Ti???p c???n s???n ph???m

    const val SLIDE = "slide"
    const val ADS_SLIDE_TYPE = 1
    const val GRID = "grid"
    const val ADS_GRID_TYPE = 2
    const val HORIZONTAL = "horizontal"
    const val ADS_HORIZONTAL_TYPE = 3

    /*
    * Slide
    * */
    const val TIME_DELAY_SLIDE_SECOND = 5L
    const val TIME_DELAY_SLIDE_MILLISECOND = 3000L

    /**
     * ??i???u ch???nh l???i thu???c t??nh c???a ???nh ????? ph?? h???p v???i m??n h??nh ??i???n tho???i
     *
     * @param bodyHTML n???i dung html tr?????c khi ch???nh s???a
     * @return n???i dung html ???? ch???nh s???a
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

    fun getVerticalProductBackground(itemType: Int,context: Context): Drawable {
        return when (itemType) {
            1 -> ViewHelper.bgProductItemTopLeft(context)
            2 -> ViewHelper.bgProductItemTopRight(context)
            3 ->  ViewHelper.bgProductItemBottemLeft(context)
            else ->ViewHelper.bgProductItemBottemRight(context)
        }
    }

    fun getMissionInprogressIcon(event: String?): Int {
        return when (event) {
            "LOGIN" -> { // ????ng nh???p
                R.drawable.ic_mission_login_inprogress_24dp
            }
            "SURVEY" -> { // Tham gia survey
                R.drawable.ic_mission_survey_inprogress_36dp
            }
            "REVIEW" -> { // ????nh gia s???n ph???m
                R.drawable.ic_mission_review_inprogress_36dp
            }
            "ORDER" -> { // Mua h??ng
                R.drawable.ic_mission_buy_inprogress_36dp
            }
            "ORDER.REVIEW" -> { // ????nh gi?? ????n h??ng
                R.drawable.ic_mission_review_order_inprogress_24px
            }
            "SCAN.BARCODE" -> { // Qu??t m?? barcode
                R.drawable.ic_mission_barcode_inprogress_24dp
            }
            "SCAN.QRCODE" -> { // Qu??t tem QRCode
                R.drawable.ic_mission_qrcode_inprogress_24dp
            }
            "INVITED" -> { // Gi???i thi???u ng?????i d??ng
                R.drawable.ic_mission_invite_inprogress_24dp
            }
            "UPDATE.PROFILE.EMAIL" -> { // X??c th???c email
                R.drawable.ic_mission_mail_inprogress_24dp
            }
            "UPDATE.PROFILE.PHONE" -> { // X??c th???c s??? ??i???n tho???i
                R.drawable.ic_mission_update_phone_inprogress_24dp
            }
            "UPDATE.PROFILE.FACEBOOK" -> { // Li??n k???t t??i kho???n facebook
                R.drawable.ic_mission_facebook_inprogress_24dp
            }
            "UPDATE.PROFILE" -> { //Ho??n thi???n h??? s?? ng?????i d??ng
                R.drawable.ic_mission_update_information_inprogress_24dp
            }
            else -> {
                R.drawable.ic_mission_verification_inprogress_24dp
            }
        }
    }

    fun getMissionFailedIcon(event: String?): Int {
        return when (event) {
            "LOGIN" -> { // ????ng nh???p
                R.drawable.ic_mission_login_fail_24dp
            }
            "SURVEY" -> { // Tham gia survey
                R.drawable.ic_mission_survey_fail_24dp
            }
            "REVIEW" -> { // ????nh gia s???n ph???m
                R.drawable.ic_mission_review_fail_24dp
            }
            "ORDER" -> { // Mua h??ng
                R.drawable.ic_mission_buy_fail_24dp
            }
            "ORDER.REVIEW" -> { // ????nh gi?? ????n h??ng
                R.drawable.ic_mission_review_order_fail_24dp
            }
            "SCAN.BARCODE" -> { // Qu??t m?? barcode
                R.drawable.ic_mission_barcode_fail_24dp
            }
            "SCAN.QRCODE" -> { // Qu??t tem QRCode
                R.drawable.ic_mission_qrcode_fail_24dp
            }
            "INVITED" -> { // Gi???i thi???u ng?????i d??ng
                R.drawable.ic_mission_invite_fail_24dp
            }
            "UPDATE.PROFILE.EMAIL" -> { // X??c th???c email
                R.drawable.ic_mission_mail_fail_24dp
            }
            "UPDATE.PROFILE.PHONE" -> { // X??c th???c s??? ??i???n tho???i
                R.drawable.ic_mission_update_phone_fail_24dp
            }
            "UPDATE.PROFILE.FACEBOOK" -> { // Li??n k???t t??i kho???n facebook
                R.drawable.ic_mission_facebook_fail_24dp
            }
            "UPDATE.PROFILE" -> { //Ho??n thi???n h??? s?? ng?????i d??ng
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

    fun callPhone(phone: String?) {
        if (!phone.isNullOrEmpty()) {
            ICheckApplication.currentActivity()?.let { activity ->
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone.replace(" ".toRegex(), "")}"))
                ActivityUtils.startActivity(activity, intent)
            }
        }
    }

    fun sendEmail(email: String?) {
        if (!email.isNullOrEmpty()) {
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
    }

    fun openUrl(url: String?) {
        if (!url.isNullOrEmpty()) {
            ICheckApplication.currentActivity()?.let { activity ->
                WebViewActivity.start(activity, url)
            }
        }
    }

    fun getName(lastName: String?, firstName: String?, default: String = getString(R.string.dang_cap_nhat)): String {
        return if (!lastName.isNullOrEmpty() || !firstName.isNullOrEmpty()) {
            "${lastName ?: ""} ${firstName ?: ""}".trim()
        } else {
            default
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