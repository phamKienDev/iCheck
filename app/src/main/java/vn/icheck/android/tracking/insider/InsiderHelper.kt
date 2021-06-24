package vn.icheck.android.tracking.insider

import com.useinsider.insider.Insider
import com.useinsider.insider.InsiderIdentifiers
import com.useinsider.insider.InsiderUser
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.model.cart.ItemCartItem
import vn.icheck.android.network.model.cart.PurchasedOrderResponse
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.setting.SettingRepository
import vn.icheck.android.network.model.loyalty.ShipAddressResponse
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.product_detail.ICDataProductDetail
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import java.util.*
import kotlin.collections.ArrayList

object InsiderHelper {
    const val FIREBASE = "firebase"
    const val CATEGORY = "Danh mục"
    const val iCheckCoin = "icheck_coin"

    fun onLogin() {
        SessionManager.session.user?.let { user ->
            SettingRepository().getCoinOfMe(object : ICNewApiListener<ICResponse<ICSummary>> {
                override fun onSuccess(obj: ICResponse<ICSummary>) {
                    SessionManager.setCoin(obj.data?.availableBalance ?: 0)

                    setCustomAttribute(Insider.Instance.currentUser, user)
                        .login(InsiderIdentifiers().apply {
                            addEmail(user.email)
                            addPhoneNumber(user.phone)
                            addUserID(user.id.toString())
                        })
                }

                override fun onError(error: ICResponseCode?) {
                    setCustomAttribute(Insider.Instance.currentUser, user)
                        .login(InsiderIdentifiers().apply {
                            addEmail(user.email)
                            addPhoneNumber(user.phone)
                            addUserID(user.id.toString())
                        })
                }
            })
        }
    }

    fun setUserAttributes() {
        setCustomAttribute(Insider.Instance.currentUser, SessionManager.session.user)
    }

    fun onLogout() {
        Insider.Instance.currentUser.logout()
    }

    private fun setCustomAttribute(insiderUser: InsiderUser, user: ICUser?): InsiderUser {
        val deviceCategory =
            if (ICheckApplication.getInstance().resources.getBoolean(R.bool.isTablet)) {
                "tablet"
            } else {
                "mobile"
            }

        val userName: String
        val userCoin: Int
        val userLevel: String
        val userGender: String
        val userAge: Int
        val userEmail: String

        if (!SessionManager.isUserLogged) {
            userName = " "
            userCoin = 0
            userLevel = " "
            userGender = " "
            userAge = -1
            userEmail = " "
        } else {
            userName = user?.name ?: " "
            userCoin = SettingManager.getUserCoin.toInt()
            userLevel = getString(
                R.string.hang_xxx,
                Constant.getUserLevelName(ICheckApplication.getInstance(), user?.level ?: 1)
            )
            userGender = user?.gender ?: " "
            userAge = if (user?.birth_year != null) {
                Calendar.getInstance().get(Calendar.YEAR) - user.birth_year!!
            } else {
                -1
            }
            userEmail = user?.email ?: " "
        }

        return insiderUser.setCustomAttributeWithBoolean(
            "login_status",
            SessionManager.isUserLogged
        )
            .setCustomAttributeWithDouble("user_id", (user?.id ?: -1).toDouble())
            .setCustomAttributeWithString("user_name", userName)
            .setCustomAttributeWithInt("icheck_point", userCoin)
            .setCustomAttributeWithString("icheck_level", userLevel)
            .setCustomAttributeWithString("device_category", deviceCategory)
            .setCustomAttributeWithString("user_gender", userGender)
            .setCustomAttributeWithString("user_email", userEmail)
            .setCustomAttributeWithInt("user_age", userAge)
            .setEmailOptin(true)
    }

    fun tagSignupStart() {
        Insider.Instance.tagEvent("signup_start").build()
    }

    fun tagSignupSuccess() {
        Insider.Instance.tagEvent("signup_success").build()
    }

    fun tagLoginStart() {
        try {
            if (Insider.Instance != null) {
                Insider.Instance.tagEvent("login_start").build()
            }
        } catch (e: Exception) {
        }
    }

    fun tagLoginSuccess() {
        Insider.Instance.tagEvent("login_success").build()
    }

    fun tagAppStarted() {
        Insider.Instance.tagEvent("app_started").build()
    }

    fun tagHomePageViewed() {
        Insider.Instance.tagEvent("homepage_viewed").build()
    }

    fun tagMessageViewed() {
        Insider.Instance.tagEvent("message_viewed").build()
    }

    fun tagLeftMenuViewed() {
        Insider.Instance.tagEvent("left_menu_viewed").build()
    }

    fun tagTopupHistoryViewed() {
        Insider.Instance.tagEvent("topup_history_viewed").build()
    }

    fun tagBookmarkViewed() {
        Insider.Instance.tagEvent("bookmark_viewed").build()
    }

    fun tagIcoinWalletViewed() {
        Insider.Instance.tagEvent("icoinwallet_viewed").build()
    }

    fun tagCreateQrcodeViewed() {
        Insider.Instance.tagEvent("create_qrcode_viewed").build()
    }

    fun tagContactViewed() {
        Insider.Instance.tagEvent("contact_viewed").build()
    }

    /*Scan*/

    fun tagScanClicked() {
        Insider.Instance.tagEvent("scan_clicked").build()
    }

    fun tagScanSuccessful(obj: ICDataProductDetail) {
        val category = if (obj.categories.isNullOrEmpty())
            ""
        else
            obj.categories!![0].name

        Insider.Instance.tagEvent("scan_successful")
            .addParameterWithString("scan_id", obj.barcode)
            .addParameterWithString("scan_product_name", obj.basicInfo?.name)
            .addParameterWithDouble(
                "scan_product_price", obj.basicInfo?.price?.toDouble()
                    ?: 0.0
            )
            .addParameterWithDouble("scan_product_rating", obj.basicInfo?.rating ?: 0.0)
            .addParameterWithBoolean("verified_status", obj.verified ?: false)
            .addParameterWithString("country_of_origin", obj.owner?.city?.name)
            .addParameterWithString("scan_company_name", obj.manager?.name ?: obj.owner?.name)
            .addParameterWithString("scan_product_category", category)
            .build()
    }

    fun tagScanBarcodeSuccess(obj: ICDataProductDetail) {
        Insider.Instance.tagEvent("scan_barcode_success")
            .addParameterWithString("barcode", obj.barcode)
            .addParameterWithString("name", obj.basicInfo?.name)
            .addParameterWithDouble("price", obj.basicInfo?.price?.toDouble() ?: 0.0)
            .addParameterWithDouble("rating", obj.basicInfo?.rating ?: 0.0)
            .addParameterWithBoolean("verified", obj.verified ?: false)
            .addParameterWithString("country", obj.owner?.city?.name)
            .addParameterWithString("business", obj.manager?.name ?: obj.owner?.name)
            .build()
    }

    fun tagScanBarcodeViewedSuccess(obj: ICDataProductDetail) {
        Insider.Instance.tagEvent("barcode_viewed_success")
            .addParameterWithString("barcode", obj.barcode)
            .addParameterWithString("name", obj.basicInfo?.name)
            .addParameterWithDouble("price", obj.basicInfo?.price?.toDouble() ?: 0.0)
            .addParameterWithDouble("rating", obj.basicInfo?.rating ?: 0.0)
            .addParameterWithBoolean("verified", obj.verified ?: false)
            .addParameterWithString("country", obj.owner?.city?.name)
            .addParameterWithString("business", obj.manager?.name ?: obj.owner?.name)
            .build()
    }


    fun tagScanBarcodeFailed(barcode:String, status:String) {
        Insider.Instance.tagEvent("scan_barcode_failed")
            .addParameterWithString("barcode", barcode)
            .addParameterWithString("status", status)
            .build()
    }

    fun tagScanQrcode(content:String, icheck:Boolean) {
        Insider.Instance.tagEvent("scan_qrcode")
            .addParameterWithString("content", content)
            .addParameterWithBoolean("icheck", icheck)
            .build()
    }

    fun tagScanStart(scan_type: String) {
        Insider.Instance.tagEvent("scan_start")
            .addParameterWithString("scan_type", scan_type)
            .build()
    }

    fun tagScanFailed(scan_type: String) {
        Insider.Instance.tagEvent("scan_failed")
            .addParameterWithString("scan_failed_type", scan_type)
            .build()
    }

    fun tagCompanyView(obj: ICPageOverview) {
        Insider.Instance.tagEvent("company_view")
            .addParameterWithString("company_name", obj.name)
            .addParameterWithBoolean("verified_status", obj.isVerify)
            .build()
    }

    fun tagCategoryViewed(name: String?, fromViewName: String) {
        Insider.Instance.tagEvent("category_viewed")
            .addParameterWithString("category_name", name)
            .addParameterWithString("input_source", fromViewName)
            .build()
    }

    fun tagProductViewed(obj: ICDataProductDetail) {
        val category = if (obj.categories.isNullOrEmpty()) {
            ""
        } else {
            val listName = mutableListOf<String>()
            for (cate in obj.categories!!) {
                if (!cate.name.isNullOrEmpty()) {
                    listName.add(cate.name!!)
                }
            }
            listName.toString().substring(1, listName.toString().length - 1)
        }

        Insider.Instance.tagEvent("product_viewed")
            .addParameterWithDouble("product_id", obj.id?.toDouble() ?: 0.0)
            .addParameterWithString("product_name", obj.basicInfo?.name)
            .addParameterWithString("company_owner", obj.owner?.name)
            .addParameterWithDouble("product_rating", obj.basicInfo?.rating ?: 0.0)
            .addParameterWithBoolean("verified_status", obj.verified ?: false)
            .addParameterWithString("product_category", category)
            .build()
    }

    fun tagProductReviewStart(obj: ICDataProductDetail) {
        val category = if (obj.categories.isNullOrEmpty()) {
            ""
        } else {
            val listName = mutableListOf<String>()
            for (cate in obj.categories!!) {
                if (!cate.name.isNullOrEmpty()) {
                    listName.add(cate.name!!)
                }
            }
            listName.toString().substring(1, listName.toString().length - 1)
        }

        Insider.Instance.tagEvent("product_review_start")
            .addParameterWithDouble("product_id", obj.id?.toDouble() ?: 0.0)
            .addParameterWithString("product_name", obj.basicInfo?.name)
            .addParameterWithString("product_category", category)
            .addParameterWithString("company_owner", obj.owner?.name)
            .build()
    }

    fun tagProductReviewStart(obj: ICBarcodeProductV1) {
        val category = if (obj.categories.isNullOrEmpty()) {
            ""
        } else {
            val listName = mutableListOf<String>()
            for (cate in obj.categories) {
                if (!cate.name.isNullOrEmpty()) {
                    listName.add(cate.name!!)
                }
            }
            listName.toString().substring(1, listName.toString().length - 1)
        }

        Insider.Instance.tagEvent("product_review_start")
            .addParameterWithDouble("product_id", obj.id.toDouble())
            .addParameterWithString("product_name", obj.name)
            .addParameterWithString("product_category", category)
            .addParameterWithString("company_owner", obj.owner?.title?.title)
            .build()
    }


    fun tagProductReviewSuccess(obj: ICDataProductDetail) {
        val category = if (obj.categories.isNullOrEmpty()) {
            ""
        } else {
            val listName = mutableListOf<String>()
            for (cate in obj.categories!!) {
                if (!cate.name.isNullOrEmpty()) {
                    listName.add(cate.name!!)
                }
            }
            listName.toString().substring(1, listName.toString().length - 1)
        }

        Insider.Instance.tagEvent("product_review_success")
            .addParameterWithDouble("product_id", obj.id?.toDouble() ?: 0.0)
            .addParameterWithString("product_name", obj.basicInfo?.name)
            .addParameterWithString("product_category", category)
            .addParameterWithString("company_owner", obj.owner?.name)
            .build()
    }

    fun tagProductReviewSuccess(obj: ICBarcodeProductV1) {
        val category = if (obj.categories.isNullOrEmpty()) {
            ""
        } else {
            val listName = mutableListOf<String>()
            for (cate in obj.categories) {
                if (!cate.name.isNullOrEmpty()) {
                    listName.add(cate.name!!)
                }
            }
            listName.toString().substring(1, listName.toString().length - 1)
        }

        Insider.Instance.tagEvent("product_review_success")
            .addParameterWithDouble("product_id", obj.id.toDouble())
            .addParameterWithString("product_name", obj.name)
            .addParameterWithString("product_category", category)
            .addParameterWithString("company_owner", obj.owner?.title?.title)
            .build()
    }

    fun tagPhoneTopupStart() {
        Insider.Instance.tagEvent("phone_topup_start").build()
    }

    fun tagPhoneTopupSuccess() {
        Insider.Instance.tagEvent("phone_topup_success").build()
    }

    fun tagIcheckPhoneTopupStart() {
        Insider.Instance.tagEvent("icheck_phone_topup_start").build()
    }

    fun tagIcheckPhoneTopupSuccess() {
        Insider.Instance.tagEvent("icheck_phone_topup_success").build()
    }

    fun tagIcheckStoreClick() {
        Insider.Instance.tagEvent("icheck_store_click").build()
    }

    fun tagIcheckItemView(obj: ICStoreiCheck) {
        Insider.Instance.tagEvent("icheck_item_view")
            .addParameterWithString("item_name", obj.name)
            .addParameterWithInt("icheck_point", obj.price?.toInt() ?: 0)
            .build()
    }

    fun tagIcheckItemBuyStart(carts: MutableList<ItemCartItem>) {
        var priceTotal = 0L
        val name = if (carts.isNullOrEmpty()) {
            ""
        } else {
            val listName = mutableListOf<String>()
            for (item in carts) {
                if (!item.product?.name.isNullOrEmpty()) {
                    listName.add(item.product?.name!!)
                }
                priceTotal += (item.price ?: 0)
            }
            listName.toString().substring(1, listName.toString().length - 1)
        }

        Insider.Instance.tagEvent("icheck_item_buy_start")
            .addParameterWithString("item_name", name)
            .addParameterWithString("icheck_point", priceTotal.toString())
            .build()
    }

    fun tagIcheckItemBuySuccess(carts: MutableList<ItemCartItem>) {
        var priceTotal = 0L
        val name = if (carts.isNullOrEmpty()) {
            ""
        } else {
            val listName = mutableListOf<String>()
            for (item in carts) {
                if (!item.product?.name.isNullOrEmpty()) {
                    listName.add(item.product?.name!!)
                }
                priceTotal += (item.price ?: 0)
            }
            listName.toString().substring(1, listName.toString().length - 1)
        }

        Insider.Instance.tagEvent("icheck_item_buy_success")
            .addParameterWithString("item_name", name)
            .addParameterWithString("icheck_point", priceTotal.toString())
            .build()
    }


    fun tagAddToCartSuccessStoreIcheck(product: ICStoreiCheck) {
        Insider.Instance.tagEvent("add_to_cart_success")
            .addParameterWithString("product_id", product.id.toString())
            .addParameterWithString("product_name", product.name)
            .addParameterWithInt("number_of_products", 1)
            .build()
    }

    /*
    Ecommerce
     */

    fun tagPaymentStartAndSuccess(grandTotal: Long) {
        Insider.Instance.tagEvent("payment_start")
            .addParameterWithDouble("order_value", grandTotal.toDouble())
            .build()

        Insider.Instance.tagEvent("payment_success")
            .addParameterWithDouble("order_value", grandTotal.toDouble())
            .addParameterWithString("payment_method", iCheckCoin)
            .build()
    }

    fun tagCheckoutSuccess(
        obj: PurchasedOrderResponse?,
        carts: ArrayList<ItemCartItem>,
        address: ShipAddressResponse
    ) {
        if (obj?.id == null) {
            return
        }
        var totalValue = 0
        var totalProduct = 0
        for (item in carts) {
            totalValue += ((item.price ?: 1) * (item.quantity ?: 1)).toInt()
            totalProduct += (item.quantity ?: 1).toInt()
        }

        Insider.Instance.tagEvent("checkout_success")
            .addParameterWithDouble("order_id", obj.id?.toDouble() ?: 0.0)
            .addParameterWithInt("order_value", totalValue)
            .addParameterWithInt("number_of_products", totalProduct) // số lượng sản phẩm
            .addParameterWithString("phone number", obj.customer?.phone)
            .addParameterWithString(
                "address",
                "${address.address}, ${address.ward?.name}, ${address.district?.name}, ${address.city?.name}"
            )
            .addParameterWithString("payment_method", iCheckCoin)
            .build()
    }

    fun tagCampaignHomescreenButtonClicked(campaign_id: String?) {
        Insider.Instance.tagEvent("campaign_homescreen_button_clicked")
            .addParameterWithString("campaign_id", campaign_id).build()
    }

    fun tagCampaignListClicked() {
        Insider.Instance.tagEvent("campaign_list_clicked").build()
    }

    fun tagCampaignClicked(campaign_id: String?) {
        Insider.Instance.tagEvent("campaign_clicked")
            .addParameterWithString("campaign_id", campaign_id).build()
    }

    fun tagCampaignCTAClicked(campaign_id: String?) {
        Insider.Instance.tagEvent("campaign_cta_clicked")
            .addParameterWithString("campaign_id", campaign_id).build()
    }

    fun tagCampaignHomescreenViewed(campaign_id: String?) {
        Insider.Instance.tagEvent("campaign_homescreen_viewed")
            .addParameterWithString("campaign_id", campaign_id).build()
    }

    fun tagOpenGiftboxStarted(campaign_id: String?) {
        Insider.Instance.tagEvent("opengiftbox_started")
            .addParameterWithString("campaign_id", campaign_id)
            .build()
    }

    fun tagMisssionListViewed(campaign_id: String?) {
        Insider.Instance.tagEvent("mission_list_viewed")
            .addParameterWithString("campaign_id", campaign_id)
            .build()
    }

    fun tagMisssionDetailViewed(campaign_id: String?, mission_id: String?) {
        Insider.Instance.tagEvent("mission_detail_viewed")
            .addParameterWithString("campaign_id", campaign_id)
            .addParameterWithString("mission_id", mission_id)
            .build()
    }

    fun tagMisssionDetailCtaClicked(campaign_id: String?, mission_id: String?) {
        Insider.Instance.tagEvent("mission_detail_cta_clicked")
            .addParameterWithString("campaign_id", campaign_id)
            .addParameterWithString("mission_id", mission_id)
            .build()
    }

    fun tagOpenGiftBoxSuccessful(
        campaign_id: String?,
        gift_type: String?,
        gift_name: String?,
        coin: Long?,
        code: String?
    ) {
        Insider.Instance.tagEvent("opengiftbox_successful")
            .addParameterWithString("campaign_id", campaign_id)
            .addParameterWithString("giftbox_gift_type", gift_type)
            .addParameterWithString("giftbox_product_name", gift_name)
            .addParameterWithString("giftbox_icheck_point", (coin ?: 0).toString())
            .addParameterWithString("giftbox_code", code ?: "null")
            .build()
    }

    fun tagOpenGiftBoxDismissClicked(campaign_id: String?, gift_type: String?) {
        Insider.Instance.tagEvent("opengiftbox_dismiss_clicked")
            .addParameterWithString("campaign_id", campaign_id)
            .addParameterWithString("giftbox_gift_type", gift_type)
            .build()
    }

    fun tagOpenGiftBoxProceedCtaClicked(campaign_id: String?, gift_type: String?) {
        Insider.Instance.tagEvent("opengiftbox_proceedcta_clicked")
            .addParameterWithString("campaign_id", campaign_id)
            .addParameterWithString("giftbox_gift_type", gift_type)
            .build()
    }

    fun tagMyGiftBoxClick() {
        Insider.Instance.tagEvent("mygiftbox_click").build()
    }

    fun tagGiftDeliveryStarted(campaign_id: String?, giftbox_product_name: String?) {
        Insider.Instance.tagEvent("gift_delivery_started")
            .addParameterWithString("campaign_id", campaign_id)
            .addParameterWithString("giftbox_product_name", giftbox_product_name)
            .build()
    }

    fun tagGiftDeliverySuccess(campaign_id: String?, giftbox_product_name: String?) {
        Insider.Instance.tagEvent("gift_delivery_success")
            .addParameterWithString("campaign_id", campaign_id)
            .addParameterWithString("giftbox_product_name", giftbox_product_name)
            .build()
    }
}