package vn.icheck.android.tracking.teko

import org.json.JSONObject
import vn.icheck.android.ICheckApplication
import vn.icheck.android.network.model.cart.ItemCartItem
import vn.icheck.android.network.model.cart.PurchasedOrderResponse
import vn.icheck.android.network.model.loyalty.ShipAddressResponse
import vn.icheck.android.network.models.ICMissionDetail
import vn.icheck.android.network.models.ICPageOverview
import vn.icheck.android.network.models.ICStoreiCheck
import vn.icheck.android.network.models.product_detail.ICDataProductDetail
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.util.ick.logError
import vn.teko.android.tracker.core.event.ImportantLevel
import vn.teko.android.tracker.event.v2.CustomEventIdentifier
import vn.teko.android.tracker.event.v2.InteractionContentEventIdentifier
import vn.teko.android.tracker.event.v2.ScreenViewEventIdentifier
import vn.teko.android.tracker.event.v2.body.CustomEventBody
import vn.teko.android.tracker.event.v2.body.InteractionContentEventBody
import vn.teko.android.tracker.event.v2.body.ScreenViewEventBody

object TekoHelper {
    const val FIREBASE = "firebase"
    const val CATEGORY = "Danh mục"
    const val icheckCoin = "icheck_coin"

    private fun trackCustomEvent(content: String) {
        try {
            ICheckApplication.getTracker().saveEvent(CustomEventBody(property = content).toLogEntryWrapper(CustomEventIdentifier.EVENT_NAME, ICheckApplication.getTracker()), ImportantLevel.NORMAL)
        } catch (e: Exception) {
            logError(e)
        }
    }

    private fun trackInteraction(payload: String) {
        try {
            ICheckApplication.getTracker().saveEvent(InteractionContentEventBody(
                    interaction = InteractionContentEventBody.Interaction.Click,
                    payload = payload,
                    contentName = TekoContentName.Empity,
                    regionName = TekoRegionName.Empity,
                    target = TekoTarget.Empity).toLogEntryWrapper(InteractionContentEventIdentifier.EVENT_NAME, ICheckApplication.getTracker()), ImportantLevel.NORMAL)
        } catch (e: Exception) {
            logError(e)
        }
    }

    private fun trackEnterScrenView(screenName: String?, referer: String) {
        try {
            ICheckApplication.getTracker().saveEvent(ScreenViewEventBody.toLogEntryWrapper(
                    eventName = ScreenViewEventIdentifier.EVENT_TYPE,
                    tracker = ICheckApplication.getTracker(),
                    screenName = screenName ?: " ",
                    referrer = referer,
                    contentType = " ",
                    navigationStart = 0L,
                    loadEventEnd = 0L,
                    skuName = " ",
                    skuId = " "
            ), ImportantLevel.NORMAL)
        } catch (e: Exception) {
            logError(e)
        }
    }

    fun tagAppStarted() {
        val json = JSONObject()
        json.put("event_name", "app_started")
        trackCustomEvent(json.toString())
    }

    fun tagScanClicked() {
        val json = JSONObject()
        json.put("event_name", "scan_click")
        trackInteraction(json.toString())
    }

    fun tagScanSuccessful(obj: ICDataProductDetail) {
        val json = JSONObject()
        json.put("event_name", "scan_successful")
        json.put("scan_id", obj.barcode ?: "")
        json.put("scan_product_name", obj.basicInfo?.name ?: "")
        json.put("scan_product_price", obj.basicInfo?.price?.toDouble() ?: "")
        json.put("scan_product_rating", obj.basicInfo?.rating ?: 0.0)
        json.put("verified_status", obj.verified ?: false)
        json.put("country_of_origin", obj.owner?.city?.name ?: "Việt Nam")

        trackCustomEvent(json.toString())
    }

    fun tagHomePageViewed() {
        val json = JSONObject()
        json.put("event_name", "homepage_viewed")

        trackEnterScrenView("home", json.toString())
    }

    fun tagCategoryViewed(name: String?, fromViewName: String) {
        val json = JSONObject()
        json.put("event_name", "category_viewed")
        json.put("category_name", name ?: "")
        json.put("input_source", fromViewName ?: "")

        trackEnterScrenView("category", json.toString())
    }

    fun tagProductViewed(obj: ICDataProductDetail) {
        val json = JSONObject()
        json.put("event_name", "product_viewed")

        json.put("product_id", obj.id?.toDouble() ?: 0.0)
        json.put("product_name", obj.basicInfo?.name ?: "")
        json.put("company_owner", obj.owner?.name ?: "")
        json.put("product_rating", obj.basicInfo?.rating ?: 0.0)
        json.put("verified_status", obj.verified ?: false)
        trackEnterScrenView("product", json.toString())
    }

    fun tagProductReviewStart(obj: ICDataProductDetail) {
        val json = JSONObject()
        json.put("event_name", "product_review_start")
        json.put("product_id", obj.id?.toDouble() ?: 0.0)
        json.put("product_name", obj.basicInfo?.name ?: "")

        trackEnterScrenView("review", json.toString())
    }

    fun tagProductReviewStart(obj: ICBarcodeProductV1) {
        val json = JSONObject()
        json.put("event_name", "product_review_start")
        json.put("product_id", obj.id.toDouble())
        json.put("product_name", obj.name ?: "")

        trackEnterScrenView("review", json.toString())
    }

    fun tagProductReviewSuccess(obj: ICDataProductDetail) {
        val json = JSONObject()
        json.put("event_name", "product_review_success")
        json.put("product_id", obj.id?.toDouble() ?: 0.0)
        json.put("product_name", obj.basicInfo?.name ?: "")

        trackCustomEvent(json.toString())
    }

    fun tagProductReviewSuccess(obj: ICBarcodeProductV1) {
        val json = JSONObject()
        json.put("event_name", "product_review_success")
        json.put("product_id", obj.id.toDouble())
        json.put("product_name", obj.name)

        trackCustomEvent(json.toString())
    }

    fun tagMallViewed() {
        val json = JSONObject()
        json.put("event_name", "company_view")

        trackEnterScrenView("mall", json.toString())
    }

    fun tagCompanyView(obj: ICPageOverview) {
        val json = JSONObject()
        json.put("event_name", "company_view")
        json.put("company_name", obj.name ?: "")
        json.put("verified_status", obj.isVerify ?: false)

        trackEnterScrenView("business", json.toString())
    }

    fun tagSellerChatClicked(accountName: String?) {
        val json = JSONObject()
        json.put("event_name", "seller_chat_clicked")
        json.put("chat_account_name", accountName ?: "")
        json.put("seller_verify", "seller")

        trackInteraction(json.toString())
    }

    fun tagNonSellerChatClicked(accountName: String?) {
        val json = JSONObject()
        json.put("event_name", "seller_chat_clicked")
        json.put("chat_account_name", accountName ?: "")
        json.put("seller_verify", "non-seller")

        trackCustomEvent(json.toString())
    }

    fun tagSellerChatSuccessful() {
        val json = JSONObject()
        json.put("event_name", "seller_chat_successful")
        json.put("seller_verify", "seller")

        trackCustomEvent(json.toString())
    }

    fun tagNonSellerChatSuccessful() {
        val json = JSONObject()
        json.put("event_name", "seller_chat_successful")
        json.put("seller_verify", "non-seller")

        trackCustomEvent(json.toString())
    }

    fun tagScanAndBuyClicked() {
        val json = JSONObject()
        json.put("event_name", "scan_and_buy_clicked")
        trackInteraction(json.toString())
    }

    fun tagMissionImpression() {
        val json = JSONObject()
        json.put("event_name", "mission_impression")

        trackEnterScrenView("missions", json.toString())
    }

    fun tagMissionClicked(missionName: String?) {
        val json = JSONObject()
        json.put("event_name", "mission_clicked")
        json.put("mission_name", missionName ?: "")

        trackInteraction(json.toString())
    }

    fun tagMissionCTAClick(missionName: String?) {
        val json = JSONObject()
        json.put("event_name", "mission_CTA_click")
        json.put("mission_name", missionName ?: "")

        trackInteraction(json.toString())
    }

    fun tagMissionCTASuccess(mission: ICMissionDetail) {
        val json = JSONObject()
        json.put("event_name", "mission_CTA_click")
        json.put("mission_name", mission.missionName ?: "")

        trackCustomEvent(json.toString())
    }

    fun tagMyGiftBoxClick() {
        val json = JSONObject()
        json.put("event_name", "mygiftbox_click")

        trackInteraction(json.toString())
    }

    fun tagPhoneTopupStart() {
        val json = JSONObject()
        json.put("event_name", "phone_topup_start")

        trackEnterScrenView("topup", json.toString())
    }

    fun tagPhoneTopupSuccess() {
        val json = JSONObject()
        json.put("event_name", "phone_topup_success")

        trackCustomEvent(json.toString())
    }

    fun tagIcheckPhoneTopupStart() {
        val json = JSONObject()
        json.put("event_name", "icheck_phone_topup_start")

        trackEnterScrenView("card", json.toString())
    }

    fun tagIcheckPhoneTopupSuccess() {
        val json = JSONObject()
        json.put("event_name", "icheck_phone_topup_success")

        trackCustomEvent(json.toString())
    }

    fun tagIcheckStoreClick() {
        val json = JSONObject()
        json.put("event_name", "icheck_store_click")

        trackInteraction(json.toString())
    }

    fun tagIcheckItemView(obj: ICStoreiCheck) {
        val json = JSONObject()
        json.put("event_name", "icheck_item_view")
        json.put("item_name", obj.name ?: "")
        json.put("icheck_point", obj.icheckShop?.icoin?.toInt() ?: 0)

        trackEnterScrenView("reward_store_detail", json.toString())
    }

    fun tagIcheckItemBuyStart(obj: ICStoreiCheck) {
        val json = JSONObject()
        json.put("event_name", "icheck_item_buy_start")
        json.put("item_name", obj.name ?: "")
        json.put("icheck_point", obj.icheckShop?.icoin?.toInt() ?: 0)

        trackInteraction(json.toString())
    }

    fun tagIcheckItemBuySuccess(obj: ICStoreiCheck) {
        val json = JSONObject()
        json.put("event_name", "icheck_item_buy_success")
        json.put("item_name", obj.name ?: "")
        json.put("icheck_point", obj.icheckShop?.icoin?.toInt() ?: 0)

        trackCustomEvent(json.toString())
    }

    fun tagAddToCartSuccessProductDetail(product: ICDataProductDetail, number: Int) {
        val json = JSONObject()
        json.put("event_name", "add_to_cart_success")
        json.put("product_id", product.id.toString())
        json.put("product_name", product.basicInfo?.name ?: "")
        json.put("number_of_products", number)

        trackCustomEvent(json.toString())
    }

    fun tagPaymentStartAndSuccess(grandTotal: Long) {
        val json = JSONObject()
        json.put("event_name", "payment_start")
        json.put("order_value", grandTotal.toDouble())
        trackInteraction(json.toString())

        val jsonSuccess = JSONObject()
        jsonSuccess.put("event_name", "payment_success")
        jsonSuccess.put("order_value", grandTotal.toDouble())
        jsonSuccess.put("payment_method", icheckCoin)
        trackCustomEvent(jsonSuccess.toString())
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

        val json = JSONObject()
        json.put("event_name", "checkout_success")
        json.put("order_id", obj.id?.toDouble())
        json.put("order_value", totalValue)
        json.put("number_of_products", totalProduct)
        json.put("phone", obj.customer?.phone ?: "")
        json.put("address", "${address.address}, ${address.ward?.name}, ${address.district?.name}, ${address.city?.name}")
        json.put("payment_method", icheckCoin)

        trackCustomEvent(json.toString())
    }

    fun tagSignupStart() {
        val json = JSONObject()
        json.put("event_name", "signup_start")

        trackEnterScrenView("signup_start", json.toString())
    }

    fun tagSignupSuccess() {
        val json = JSONObject()
        json.put("event_name", "signup_success")

        trackCustomEvent(json.toString())
    }

    fun tagLoginStart() {
        val json = JSONObject()
        json.put("event_name", "login_start")

        trackEnterScrenView("login", json.toString())
    }

    fun tagLoginSuccess() {
        val json = JSONObject()
        json.put("event_name", "login_success")

        trackCustomEvent(json.toString())

    }
}


