package vn.icheck.android.tracking

import com.useinsider.insider.Insider
import vn.icheck.android.network.model.cart.ItemCartItem
import vn.icheck.android.network.model.cart.PurchasedOrderResponse
import vn.icheck.android.network.model.loyalty.ShipAddressResponse
import vn.icheck.android.network.models.ICPageOverview
import vn.icheck.android.network.models.ICStoreiCheck
import vn.icheck.android.network.models.product_detail.ICDataProductDetail
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.tracking.firebase.TrackingFirebaseHelper
import vn.icheck.android.tracking.insider.InsiderHelper
import vn.icheck.android.tracking.teko.TekoHelper

object TrackingAllHelper {
    fun trackSignupStart() {
        TrackingFirebaseHelper.tagSignupStart()
        InsiderHelper.tagSignupStart()
        TekoHelper.tagSignupStart()
    }

    fun trackSignupSuccess() {
        TrackingFirebaseHelper.tagSignupSuccess()
        InsiderHelper.tagSignupSuccess()
        TekoHelper.tagSignupSuccess()
    }

    fun trackLoginStart() {
        TrackingFirebaseHelper.tagLoginStart()
        InsiderHelper.tagLoginStart()
        TekoHelper.tagLoginStart()
    }

    fun trackLoginSuccess() {
        TrackingFirebaseHelper.tagLoginSuccess()
        InsiderHelper.tagLoginSuccess()
        TekoHelper.tagLoginSuccess()
    }

    fun trackAppStarted() {
        TrackingFirebaseHelper.tagAppStarted()
        InsiderHelper.tagAppStarted()
        TekoHelper.tagAppStarted()
    }

    fun trackHomePageViewed() {
        TrackingFirebaseHelper.tagHomePageViewed()
        InsiderHelper.tagHomePageViewed()
        TekoHelper.tagHomePageViewed()
    }

    fun trackMessageViewed() {
        TrackingFirebaseHelper.tagMessageViewed()
        InsiderHelper.tagMessageViewed()
    }

    fun trackLeftMenuViewed() {
        TrackingFirebaseHelper.tagLeftMenuViewed()
        InsiderHelper.tagLeftMenuViewed()
    }

    fun trackTopupHistoryViewed() {
        TrackingFirebaseHelper.tagTopupHistoryViewed()
        InsiderHelper.tagTopupHistoryViewed()
    }

    fun trackBookmarkViewed() {
        TrackingFirebaseHelper.tagBookmarkViewed()
        InsiderHelper.tagBookmarkViewed()
    }

    fun trackIcoinWalletViewed() {
        TrackingFirebaseHelper.tagIcoinWalletViewed()
        InsiderHelper.tagIcoinWalletViewed()
    }

    fun trackCreateQrcodeViewed() {
        TrackingFirebaseHelper.tagCreateQrcodeViewed()
        InsiderHelper.tagCreateQrcodeViewed()
    }

    fun trackContactViewed() {
        TrackingFirebaseHelper.tagContactViewed()
        InsiderHelper.tagContactViewed()
    }

    fun trackScanClicked() {
        TrackingFirebaseHelper.tagScanClicked()
        InsiderHelper.tagScanClicked()
        TekoHelper.tagScanClicked()
    }

    fun trackScanSuccessful(obj: ICDataProductDetail) {
        TrackingFirebaseHelper.tagScanSuccessful(obj)
        InsiderHelper.tagScanSuccessful(obj)
        TekoHelper.tagScanSuccessful(obj)
    }


    fun trackScanBarcodeSuccess(obj: ICDataProductDetail) {
        InsiderHelper.tagScanBarcodeSuccess(obj)
    }

    fun trackScanBarcodeViewedSuccess(obj: ICDataProductDetail) {
        InsiderHelper.tagScanBarcodeViewedSuccess(obj)
    }

    fun trackScanBarcodeFailed(barcode:String, status:String) {
        InsiderHelper.tagScanBarcodeFailed(barcode, status)
    }

    fun trackScanQrcode(content:String, icheck:Boolean) {
        InsiderHelper.tagScanQrcode(content, icheck)
    }

    fun trackScanStart(scan_type: String) {
        TrackingFirebaseHelper.tagScanStart(scan_type)
        InsiderHelper.tagScanStart(scan_type)
    }

    fun trackScanFailed(scan_type: String) {
        TrackingFirebaseHelper.tagScanFailed(scan_type)
        InsiderHelper.tagScanFailed(scan_type)
    }

    fun trackCompanyView(obj: ICPageOverview) {
        TrackingFirebaseHelper.tagCompanyView(obj)
        InsiderHelper.tagCompanyView(obj)
        TekoHelper.tagCompanyView(obj)
    }

    fun trackCategoryViewed(name: String?, fromViewName: String) {
        TrackingFirebaseHelper.tagCategoryViewed(name, fromViewName)
        InsiderHelper.tagCategoryViewed(name, fromViewName)
        TekoHelper.tagCategoryViewed(name, fromViewName)
    }

    fun trackProductViewed(obj: ICDataProductDetail) {
        TrackingFirebaseHelper.tagProductViewed(obj)
        InsiderHelper.tagProductViewed(obj)
        TekoHelper.tagProductViewed(obj)
    }

    fun trackProductReviewStart(obj: ICDataProductDetail) {
        TrackingFirebaseHelper.tagProductReviewStart(obj)
        InsiderHelper.tagProductReviewStart(obj)
        TekoHelper.tagProductReviewStart(obj)
    }

    fun trackProductReviewStart(obj: ICBarcodeProductV1) {
        TrackingFirebaseHelper.tagProductReviewStart(obj)
        InsiderHelper.tagProductReviewStart(obj)
        TekoHelper.tagProductReviewStart(obj)
    }

    fun trackProductReviewSuccess(obj: ICDataProductDetail) {
        TrackingFirebaseHelper.tagProductReviewSuccess(obj)
        InsiderHelper.tagProductReviewSuccess(obj)
        TekoHelper.tagProductReviewSuccess(obj)
    }

    fun trackProductReviewSuccess(obj: ICBarcodeProductV1) {
        TrackingFirebaseHelper.tagProductReviewSuccess(obj)
        InsiderHelper.tagProductReviewSuccess(obj)
        TekoHelper.tagProductReviewSuccess(obj)
    }

    fun trackPhoneTopupStart() {
        TrackingFirebaseHelper.tagPhoneTopupStart()
        InsiderHelper.tagPhoneTopupStart()
        TekoHelper.tagPhoneTopupStart()
    }

    fun trackPhoneTopupSuccess() {
        TrackingFirebaseHelper.tagPhoneTopupSuccess()
        InsiderHelper.tagPhoneTopupSuccess()
        TekoHelper.tagPhoneTopupSuccess()
    }

    fun trackIcheckPhoneTopupStart() {
        TrackingFirebaseHelper.tagIcheckPhoneTopupStart()
        InsiderHelper.tagIcheckPhoneTopupStart()
        TekoHelper.tagIcheckPhoneTopupStart()
    }

    fun trackIcheckPhoneTopupSuccess() {
        TrackingFirebaseHelper.tagIcheckPhoneTopupSuccess()
        InsiderHelper.tagIcheckPhoneTopupSuccess()
        TekoHelper.tagIcheckPhoneTopupSuccess()
    }

    fun trackIcheckStoreClick() {
        TrackingFirebaseHelper.tagIcheckStoreClick()
        InsiderHelper.tagIcheckStoreClick()
        TekoHelper.tagIcheckStoreClick()
    }

    fun tagIcheckItemView(obj: ICStoreiCheck) {
        TrackingFirebaseHelper.tagIcheckItemView(obj)
        InsiderHelper.tagIcheckItemView(obj)
        TekoHelper.tagIcheckItemView(obj)
    }

    fun tagIcheckItemBuyStart(carts: MutableList<ItemCartItem>) {
        TrackingFirebaseHelper.tagIcheckItemBuyStart(carts)
        InsiderHelper.tagIcheckItemBuyStart(carts)
    }

    fun tagIcheckItemBuySuccess(carts: MutableList<ItemCartItem>) {
        TrackingFirebaseHelper.tagIcheckItemBuySuccess(carts)
        InsiderHelper.tagIcheckItemBuySuccess(carts)
    }

    /*
    Ecommerce
   */

    fun tagAddToCartSuccessStoreIcheck(product: ICStoreiCheck) {
        TrackingFirebaseHelper.tagAddToCartSuccessStoreIcheck(product)
        InsiderHelper.tagAddToCartSuccessStoreIcheck(product)
    }

    fun tagPaymentStartAndSuccess(grandTotal: Long) {
        TrackingFirebaseHelper.tagPaymentStartAndSuccess(grandTotal)
        InsiderHelper.tagPaymentStartAndSuccess(grandTotal)
    }

    fun tagCheckoutSuccess(obj: PurchasedOrderResponse?, carts: ArrayList<ItemCartItem>, address: ShipAddressResponse) {
        TrackingFirebaseHelper.tagCheckoutSuccess(obj, carts, address)
        InsiderHelper.tagCheckoutSuccess(obj, carts, address)
    }

    /*
    END Ecommerce
    */

    fun tagCampaignHomescreenButtonClicked(campaign_id: String?) {
        TrackingFirebaseHelper.tagCampaignHomescreenButtonClicked(campaign_id)
        InsiderHelper.tagCampaignHomescreenButtonClicked(campaign_id)
    }

    fun tagCampaignListClicked() {
        TrackingFirebaseHelper.tagCampaignListClicked()
        InsiderHelper.tagCampaignListClicked()
    }

    fun tagCampaignClicked(campaign_id: String?) {
        TrackingFirebaseHelper.tagCampaignClicked(campaign_id)
        InsiderHelper.tagCampaignClicked(campaign_id)
    }

    fun tagCampaignCTAClicked(campaign_id: String?) {
        TrackingFirebaseHelper.tagCampaignCTAClicked(campaign_id)
        InsiderHelper.tagCampaignCTAClicked(campaign_id)
    }

    fun tagCampaignHomescreenViewed(campaign_id: String?) {
        TrackingFirebaseHelper.tagCampaignHomescreenViewed(campaign_id)
        InsiderHelper.tagCampaignHomescreenViewed(campaign_id)
    }

    fun tagOpenGiftboxStarted(campaign_id: String?) {
        TrackingFirebaseHelper.tagOpenGiftboxStarted(campaign_id)
        InsiderHelper.tagOpenGiftboxStarted(campaign_id)
    }

    fun tagMisssionListViewed(campaign_id: String?) {
        TrackingFirebaseHelper.tagMisssionListViewed(campaign_id)
        InsiderHelper.tagMisssionListViewed(campaign_id)
    }

    fun tagMisssionDetailViewed(campaign_id: String?, mission_id: String?) {
        TrackingFirebaseHelper.tagMisssionDetailViewed(campaign_id, mission_id)
        InsiderHelper.tagMisssionDetailViewed(campaign_id, mission_id)
    }

    fun tagMisssionDetailCtaClicked(campaign_id: String?, mission_id: String?) {
        TrackingFirebaseHelper.tagMisssionDetailCtaClicked(campaign_id, mission_id)
        InsiderHelper.tagMisssionDetailCtaClicked(campaign_id, mission_id)
    }

    fun tagOpenGiftBoxSuccessful(campaign_id: String?, gift_type: String?, gift_name: String?, coin: Long?, code: String?) {
        TrackingFirebaseHelper.tagOpenGiftBoxSuccessful(campaign_id, gift_type, gift_name, coin, code)
        InsiderHelper.tagOpenGiftBoxSuccessful(campaign_id, gift_type, gift_name, coin, code)
    }

    fun tagOpenGiftBoxDismissClicked(campaign_id: String?, gift_type: String?) {
        TrackingFirebaseHelper.tagOpenGiftBoxDismissClicked(campaign_id, gift_type)
        InsiderHelper.tagOpenGiftBoxDismissClicked(campaign_id, gift_type)
    }

    fun tagOpenGiftBoxProceedCtaClicked(campaign_id: String?, gift_type: String?) {
        TrackingFirebaseHelper.tagOpenGiftBoxProceedCtaClicked(campaign_id, gift_type)
        InsiderHelper.tagOpenGiftBoxProceedCtaClicked(campaign_id, gift_type)
    }

    fun tagMyGiftBoxClick() {
        TrackingFirebaseHelper.tagMyGiftBoxClick()
        InsiderHelper.tagMyGiftBoxClick()
        TekoHelper.tagMyGiftBoxClick()
    }

    fun tagGiftDeliveryStarted(campaign_id: String?, giftbox_product_name: String?) {
        TrackingFirebaseHelper.tagGiftDeliveryStarted(campaign_id,giftbox_product_name)
        InsiderHelper.tagGiftDeliveryStarted(campaign_id,giftbox_product_name)
    }

    fun tagGiftDeliverySuccess(campaign_id: String?, giftbox_product_name: String?) {
        TrackingFirebaseHelper.tagGiftDeliverySuccess(campaign_id,giftbox_product_name)
        InsiderHelper.tagGiftDeliverySuccess(campaign_id,giftbox_product_name)
    }
}