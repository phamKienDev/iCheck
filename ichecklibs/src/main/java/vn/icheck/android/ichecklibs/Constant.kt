package vn.icheck.android.ichecklibs

import android.content.Context
import android.content.Intent
import android.net.MailTo

object Constant {

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

    const val PRIMARY_COLOR="primaryColor"
    const val SECONDARY_COLOR="secondaryColor"
    const val ACCENT_BLUE_COLOR="accentBlueColor"
    const val ACCENT_GREEN_COLOR="accentGreenColor"
    const val ACCENT_RED_COLOR="accentRedColor"
    const val ACCENT_CYAN_COLOR="accentCyanColor"
    const val ACCENT_YELLOW_COLOR="accentYellowColor"
    const val NORMAL_TEXT_COLOR="normalTextColor"
    const val SECOND_TEXT_COLOR="secondTextColor"
    const val DISABLE_TEXT_COLOR="disableTextColor"
    const val LINE_COLOR="lineColor"
    const val APP_BACKGROUND_COLOR="appBackgroundColor"
    const val POPUP_BACKGROUND_COLOR="popupBackgroundColor"

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

    /*
    ScreenCode Popup
     */

    const val SCAN = "scan"
    const val HOME = "home"
    const val PAGE_VERIFY = "page_verified"
    const val PAGE_UNVERIFIED = "page_unverified"
    const val PRODUCT_UNVERIFIED = "product_unverified"
    const val PRODUCT_VERIFY = "product_verified"

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

    fun getHtmlTextNotPadding(content:String):String{
        val head = "<head><style> img{max-width: 100% !important; width: 100% !important; height: auto !important;}</style></head>"

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                head+
                "\n" +
                "<body style=\"margin: 0; padding: 0; overflow: hidden\">\n" +
                content +
                "</body>\n" +
                "\n" +
                "</html>"
    }


    fun formatPhone(phone: String?): String? {
        return if (phone?.length ?: 0 >= 10) {
            StringBuilder(phone!!).insert(phone.length - 3, " ").insert(phone.length - 6, " ").toString()
        } else {
            phone
        }
    }

    fun getPath(type: String?, id: String?): String? {
        return if (!type.isNullOrEmpty()) {
            if (!id.isNullOrEmpty()) {
                "icheck://$type?id=$id"
            } else {
                "icheck://$type"
            }
        } else {
            null
        }
    }

    fun getAddress(address: String?, district: String?, city: String?, country: String?, default: String?): String {
        val stringBuilder = StringBuilder()

        var isAdded = false
        if (!address.isNullOrEmpty()) {
            isAdded = true
            stringBuilder.append(address)
        }

        if (!district.isNullOrEmpty()) {
            isAdded = true
            if (isAdded) {
                stringBuilder.append(", ")
            }
            stringBuilder.append(district)
        }

        if (!city.isNullOrEmpty()) {
            isAdded = true
            if (isAdded) {
                stringBuilder.append(", ")
            }
            stringBuilder.append(city)
        }

        if (!country.isNullOrEmpty()) {
            isAdded = true
            if (isAdded) {
                stringBuilder.append(", ")
            }
            stringBuilder.append(country)
        }

        if (!isAdded) {
            stringBuilder.append(default ?: "")
        }

        return stringBuilder.toString()
    }

    fun sendMail(context: Context, value: String?) {
        if (!value.isNullOrEmpty()) {
            var mail = when {
                value.startsWith("MAILTO") -> {
                    value.replace("MAILTO", "mailto")
                }
                else -> {
                    value.replace("mailto:email", "mailto")
                }
            }
            val mailTo = MailTo.parse(mail)

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/html"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailTo.to))
            intent.putExtra(Intent.EXTRA_SUBJECT, mailTo.subject)
            intent.putExtra(Intent.EXTRA_CC, mailTo.cc)
            intent.putExtra(Intent.EXTRA_TEXT, mailTo.body)
            context.startActivity(intent)
        }
    }

    fun isNullOrEmpty(value: String?): Boolean {
        return when {
            value.isNullOrEmpty() -> true
            value.contains("null") -> true
            else -> false
        }
    }

    fun getName(lastName: String?, firstName: String?, default: String = ""): String {
        return if (!lastName.isNullOrEmpty() || !firstName.isNullOrEmpty()) {
            "${lastName ?: ""} ${firstName ?: ""}".trim()
        } else {
            default
        }
    }
}