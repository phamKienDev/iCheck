package vn.icheck.android.tracking.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import vn.icheck.android.ICheckApplication
import vn.icheck.android.network.model.cart.ItemCartItem
import vn.icheck.android.network.model.cart.PurchasedOrderResponse
import vn.icheck.android.network.model.loyalty.ShipAddressResponse
import vn.icheck.android.network.models.ICPageOverview
import vn.icheck.android.network.models.ICStoreiCheck
import vn.icheck.android.network.models.product_detail.ICDataProductDetail
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.tracking.insider.InsiderHelper

object TrackingFirebaseHelper {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(ICheckApplication.getInstance())

    fun tagSignupStart() {
        firebaseAnalytics.logEvent("signup_start", null)
    }

    fun tagSignupSuccess() {
        firebaseAnalytics.logEvent("signup_success", null)
    }

    fun tagLoginStart() {
        firebaseAnalytics.logEvent("login_start", null)
    }

    fun tagLoginSuccess() {
        firebaseAnalytics.logEvent("login_success", null)
    }

    fun tagAppStarted() {
        firebaseAnalytics.logEvent("app_started", null)
    }

    fun tagHomePageViewed() {
        firebaseAnalytics.logEvent("homepage_viewed", null)
    }

    fun tagMessageViewed() {
        firebaseAnalytics.logEvent("message_viewed", null)
    }

    fun tagLeftMenuViewed() {
        firebaseAnalytics.logEvent("left_menu_viewed", null)
    }

    fun tagTopupHistoryViewed() {
        firebaseAnalytics.logEvent("topup_history_viewed", null)
    }

    fun tagBookmarkViewed() {
        firebaseAnalytics.logEvent("bookmark_viewed", null)
    }

    fun tagIcoinWalletViewed() {
        firebaseAnalytics.logEvent("icoinwallet_viewed", null)
    }

    fun tagCreateQrcodeViewed() {
        firebaseAnalytics.logEvent("create_qrcode_viewed", null)
    }

    fun tagContactViewed() {
        firebaseAnalytics.logEvent("contact_viewed", null)
    }

    /*Scan*/

    fun tagScanClicked() {
        firebaseAnalytics.logEvent("scan_clicked", null)
    }

    fun tagScanSuccessful(obj: ICDataProductDetail) {
        val category = if (obj.categories.isNullOrEmpty())
            " "
        else
            obj.categories!![0].name

        val bundle = Bundle()
        bundle.putString("scan_id", obj.barcode ?: " ")
        bundle.putString("scan_product_name", obj.basicInfo?.name ?: " ")
        bundle.putDouble("scan_product_price", obj.basicInfo?.price?.toDouble()
                ?: 0.0)
        bundle.putDouble("scan_product_rating", obj.basicInfo?.rating ?: 0.0)
        bundle.putBoolean("verified_status", obj.verified ?: false)
        bundle.putString("country_of_origin", obj.owner?.city?.name ?: "Việt Nam")
        bundle.putString("scan_company_name", obj.owner?.name ?: " ")
        bundle.putString("scan_product_category", category)

        firebaseAnalytics.logEvent("scan_successful", bundle)
    }

    fun tagScanStart(scan_type: String) {
        val bundle = Bundle()
        bundle.putString("scan_type", scan_type)
        firebaseAnalytics.logEvent("scan_start", bundle)
    }

    fun tagScanFailed(scan_type: String) {
        val bundle = Bundle()
        bundle.putString("scan_failed_type", scan_type)
        firebaseAnalytics.logEvent("scan_failed", bundle)
    }

    fun tagCompanyView(obj: ICPageOverview) {
        val bundle = Bundle()
        bundle.putString("company_name", obj.name)
        bundle.putBoolean("verified_status", obj.isVerify)
        firebaseAnalytics.logEvent("company_view", bundle)
    }

    fun tagCategoryViewed(name: String?, fromViewName: String) {
        val bundle = Bundle()
        bundle.putString("category_name", name)
        bundle.putString("input_source", fromViewName)
        firebaseAnalytics.logEvent("category_viewed", bundle)
    }

    fun tagProductViewed(obj: ICDataProductDetail) {
        val category = if (obj.categories.isNullOrEmpty()) {
            " "
        } else {
            val listName = mutableListOf<String>()
            for (cate in obj.categories!!) {
                if (!cate.name.isNullOrEmpty()) {
                    listName.add(cate.name!!)
                }
            }
            listName.toString().substring(1, listName.toString().length - 1)
        }

        val bundle = Bundle()
        bundle.putString("product_id", (obj.id ?: 0).toString())
        bundle.putString("product_name", obj.basicInfo?.name)
        bundle.putString("company_owner", obj.owner?.name)
        bundle.putDouble("product_rating", obj.basicInfo?.rating ?: 0.0)
        bundle.putBoolean("verified_status", obj.verified ?: false)
        bundle.putString("product_category", category)

        firebaseAnalytics.logEvent("product_viewed", bundle)
    }

    fun tagProductReviewStart(obj: ICDataProductDetail) {
        val category = if (obj.categories.isNullOrEmpty()) {
            " "
        } else {
            val listName = mutableListOf<String>()
            for (cate in obj.categories!!) {
                if (!cate.name.isNullOrEmpty()) {
                    listName.add(cate.name!!)
                }
            }
            listName.toString().substring(1, listName.toString().length - 1)
        }
        val bundle = Bundle()
        bundle.putDouble("product_id", obj.id?.toDouble() ?: 0.0)
        bundle.putString("product_name", obj.basicInfo?.name ?: " ")
        bundle.putString("product_category", category)
        bundle.putString("company_owner", obj.owner?.name ?: " ")

        firebaseAnalytics.logEvent("product_review_start", bundle)
    }

    fun tagProductReviewStart(obj: ICBarcodeProductV1) {
        val category = if (obj.categories.isNullOrEmpty()) {
            " "
        } else {
            val listName = mutableListOf<String>()
            for (cate in obj.categories) {
                if (!cate.name.isNullOrEmpty()) {
                    listName.add(cate.name!!)
                }
            }
            listName.toString().substring(1, listName.toString().length - 1)
        }

        val bundle = Bundle()
        bundle.putString("product_id", obj.id.toString())
        bundle.putString("product_name", obj.name)
        bundle.putString("product_category", category)
        bundle.putString("company_owner", obj.owner?.title?.title ?: " ")

        firebaseAnalytics.logEvent("product_review_start", bundle)
    }


    fun tagProductReviewSuccess(obj: ICDataProductDetail) {
        val category = if (obj.categories.isNullOrEmpty()) {
            " "
        } else {
            val listName = mutableListOf<String>()
            for (cate in obj.categories!!) {
                if (!cate.name.isNullOrEmpty()) {
                    listName.add(cate.name!!)
                }
            }
            listName.toString().substring(1, listName.toString().length - 1)
        }

        val bundle = Bundle()
        bundle.putString("product_id", (obj.id ?: 0).toString())
        bundle.putString("product_name", obj.basicInfo?.name ?: " ")
        bundle.putString("product_category", category)
        bundle.putString("company_owner", obj.owner?.name ?: " ")

        firebaseAnalytics.logEvent("product_review_success", bundle)
    }

    fun tagProductReviewSuccess(obj: ICBarcodeProductV1) {
        val category = if (obj.categories.isNullOrEmpty()) {
            " "
        } else {
            val listName = mutableListOf<String>()
            for (cate in obj.categories) {
                if (!cate.name.isNullOrEmpty()) {
                    listName.add(cate.name!!)
                }
            }
            listName.toString().substring(1, listName.toString().length - 1)
        }
        val bundle = Bundle()
        bundle.putString("product_id", obj.id.toString())
        bundle.putString("product_name", obj.name)
        bundle.putString("product_category", category)
        bundle.putString("company_owner", obj.owner?.title?.title ?: " ")

        firebaseAnalytics.logEvent("product_review_success", bundle)
    }

    fun tagPhoneTopupStart() {
        firebaseAnalytics.logEvent("phone_topup_start", null)
    }

    fun tagPhoneTopupSuccess() {
        firebaseAnalytics.logEvent("phone_topup_success", null)
    }

    fun tagIcheckPhoneTopupStart() {
        firebaseAnalytics.logEvent("icheck_phone_topup_start", null)
    }

    fun tagIcheckPhoneTopupSuccess() {
        firebaseAnalytics.logEvent("icheck_phone_topup_success", null)
    }

    fun tagIcheckStoreClick() {
        firebaseAnalytics.logEvent("icheck_store_click", null)
    }

    fun tagIcheckItemView(obj: ICStoreiCheck) {
        val bundle = Bundle()
        bundle.putString("item_name", obj.name ?: " ")
        bundle.putLong("icheck_point", obj.price ?: 0)

        firebaseAnalytics.logEvent("icheck_item_view", bundle)
    }

    fun tagIcheckItemBuyStart(carts: MutableList<ItemCartItem>) {
        var priceTotal = 0L
        val name = if (carts.isNullOrEmpty()) {
            " "
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

        val bundle = Bundle()
        bundle.putString("item_name", name)
        bundle.putLong("icheck_point", priceTotal)

        firebaseAnalytics.logEvent("icheck_item_buy_start", bundle)
    }

    fun tagIcheckItemBuySuccess(carts: MutableList<ItemCartItem>) {
        var priceTotal = 0L
        val name = if (carts.isNullOrEmpty()) {
            " "
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

        val bundle = Bundle()
        bundle.putString("item_name", name)
        bundle.putLong("icheck_point", priceTotal)

        firebaseAnalytics.logEvent("icheck_item_buy_success", bundle)
    }


    fun tagAddToCartSuccessStoreIcheck(product: ICStoreiCheck) {
        val bundle = Bundle()
        bundle.putLong("product_id", product.id)
        bundle.putString("product_name", product.name ?: " ")
        bundle.putInt("number_of_products", 1)

        firebaseAnalytics.logEvent("add_to_cart_success", bundle)
    }

    /*
    Ecommerce
     */

    fun tagPaymentStartAndSuccess(grandTotal: Long) {
        val bundle = Bundle()
        bundle.putLong("order_value", grandTotal)
        firebaseAnalytics.logEvent("payment_start", bundle)

        val bundleSuccess = Bundle()
        bundleSuccess.putLong("order_value", grandTotal)
        bundleSuccess.putString("payment_method", InsiderHelper.iCheckCoin)
        firebaseAnalytics.logEvent("payment_success", bundleSuccess)
    }

    fun tagCheckoutSuccess(obj: PurchasedOrderResponse?, carts: ArrayList<ItemCartItem>, address: ShipAddressResponse) {
        if (obj?.id == null) {
            return
        }
        var totalValue = 0
        var totalProduct = 0
        for (item in carts) {
            totalValue += ((item.price ?: 1) * (item.quantity ?: 1)).toInt()
            totalProduct += (item.quantity ?: 1).toInt()
        }

        val bundle = Bundle()
        bundle.putLong("order_id", obj.id ?: 0)
        bundle.putInt("order_value", totalValue)
        bundle.putInt("number_of_products", totalProduct) // số lượng sản phẩm
        bundle.putString("phone number", obj.customer?.phone)
        bundle.putString("address", "${address.address}, ${address.ward?.name}, ${address.district?.name}, ${address.city?.name}")
        bundle.putString("payment_method", InsiderHelper.iCheckCoin)

        firebaseAnalytics.logEvent("checkout_success", bundle)
    }

    fun tagCampaignHomescreenButtonClicked(campaign_id: String?) {
        val bundleSuccess = Bundle()
        bundleSuccess.putString("campaign_id", campaign_id?:" ")
        firebaseAnalytics.logEvent("campaign_homescreen_button_clicked", bundleSuccess)
    }

    fun tagCampaignListClicked() {
        firebaseAnalytics.logEvent("campaign_list_clicked", null)

    }

    fun tagCampaignClicked(campaign_id: String?) {
        val bundle = Bundle()
        bundle.putString("campaign_id", campaign_id?:" ")
        firebaseAnalytics.logEvent("campaign_clicked", bundle)

    }

    fun tagCampaignCTAClicked(campaign_id: String?) {
        val bundle = Bundle()
        bundle.putString("campaign_id", campaign_id)
        firebaseAnalytics.logEvent("campaign_cta_clicked", bundle)
    }

    fun tagCampaignHomescreenViewed(campaign_id: String?) {
        val bundle = Bundle()
        bundle.putString("campaign_id", campaign_id)
        firebaseAnalytics.logEvent("campaign_homescreen_viewed", bundle)
    }

    fun tagOpenGiftboxStarted(campaign_id: String?) {
        val bundle = Bundle()
        bundle.putString("campaign_id", campaign_id)
        firebaseAnalytics.logEvent("opengiftbox_started", bundle)
    }

    fun tagMisssionListViewed(campaign_id: String?) {
        val bundle = Bundle()
        bundle.putString("campaign_id", campaign_id ?: " ")
        firebaseAnalytics.logEvent("mission_list_viewed", bundle)
    }

    fun tagMisssionDetailViewed(campaign_id: String?, mission_id: String?) {
        val bundle = Bundle()
        bundle.putString("campaign_id", campaign_id ?: " ")
        bundle.putString("mission_id", mission_id ?: " ")
        firebaseAnalytics.logEvent("mission_detail_viewed", bundle)
    }

    fun tagMisssionDetailCtaClicked(campaign_id: String?, mission_id: String?) {
        val bundle = Bundle()
        bundle.putString("campaign_id", campaign_id ?: " ")
        bundle.putString("mission_id", mission_id ?: " ")
        firebaseAnalytics.logEvent("mission_detail_cta_clicked", bundle)
    }

    fun tagOpenGiftBoxSuccessful(campaign_id: String?, gift_type: String?, gift_name: String?, coin: Long?, code: String?) {
        val bundle = Bundle()
        bundle.putString("campaign_id", campaign_id ?: " ")
        bundle.putString("giftbox_gift_type", gift_type ?: " ")
        bundle.putString("giftbox_product_name", gift_name ?: " ")
        bundle.putString("giftbox_icheck_point", (coin ?: 0).toString())
        bundle.putString("giftbox_code", code ?: "null")
        firebaseAnalytics.logEvent("opengiftbox_successful", bundle)
    }

    fun tagOpenGiftBoxDismissClicked(campaign_id: String?, gift_type: String?) {
        val bundle = Bundle()
        bundle.putString("campaign_id", campaign_id)
        bundle.putString("giftbox_gift_type", gift_type)
        firebaseAnalytics.logEvent("opengiftbox_dismiss_clicked", bundle)
    }

    fun tagOpenGiftBoxProceedCtaClicked(campaign_id: String?, gift_type: String?) {
        val bundle = Bundle()
        bundle.putString("campaign_id", campaign_id ?: " ")
        bundle.putString("giftbox_gift_type", gift_type ?: " ")
        firebaseAnalytics.logEvent("opengiftbox_proceedcta_clicked", bundle)
    }

    fun tagMyGiftBoxClick() {
        firebaseAnalytics.logEvent("mygiftbox_click", null)
    }

    fun tagGiftDeliveryStarted(campaign_id: String?, giftbox_product_name: String?) {
        val bundle = Bundle()
        bundle.putString("campaign_id", campaign_id ?: " ")
        bundle.putString("giftbox_product_name", giftbox_product_name?:" ")
        firebaseAnalytics.logEvent("gift_delivery_started", bundle)
    }

    fun tagGiftDeliverySuccess(campaign_id: String?, giftbox_product_name: String?) {
        val bundle = Bundle()
        bundle.putString("campaign_id", campaign_id ?: " ")
        bundle.putString("giftbox_product_name", giftbox_product_name?:" ")
        firebaseAnalytics.logEvent("gift_delivery_success", bundle)
    }
}