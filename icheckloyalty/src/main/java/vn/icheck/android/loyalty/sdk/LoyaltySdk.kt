package vn.icheck.android.loyalty.sdk

import android.app.Activity
import android.content.Intent
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.dialog.DialogGlobalLoginLoyalty
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.SessionManager
import vn.icheck.android.loyalty.screen.game_from_labels.game_list.GameFromLabelsListActivity
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.onboarding.OnBoardingActivity
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.GameActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.campaign_of_business.CampaignOfBusinessActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.gift_shop.GiftShopActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.giftdetail.GiftDetailActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.home.HomePageEarnPointsActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.longtermprogramlist.LongTermProgramListActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.loyaltyvipdetail.LoyaltyVipDetailActivity
import vn.icheck.android.loyalty.screen.redemption_history.RedemptionHistoryActivity
import java.io.Serializable

object LoyaltySdk {
    private var listener: SdkLoyaltyFirebaseDynamicLinksListener? = null
    private var listenerLogin: IOpenLoginListener? = null

    /**
     * Mở 1 schema thông qua FirebaseDynamicLinks
     */
    fun startFirebaseDynamicLinksActivity(listener: SdkLoyaltyFirebaseDynamicLinksListener) {
        this.listener = listener
    }

    /**
     * Mở dialog yêu cầu login
     */
    fun startActivityForResultLogin(listener: IOpenLoginListener) {
        listenerLogin = listener
    }

    /**
     * Mở màn danh sách game của Loyalty
     */
    fun startActivityGameFromLabelsList(activity: Activity) {
        ActivityHelper.startActivity<GameFromLabelsListActivity>(activity)
    }

    /**
     * Mở màn vòng quay may mắn của Loyalty
     */
    fun startActivityVQMM(activity: Activity, id: String?) {
        if (!id.isNullOrEmpty()) {
            ActivityHelper.startActivity<GameActivity, Long>(activity, ConstantsLoyalty.DATA_1, id.toLong())
        } else {
            startActivityGameFromLabelsList(activity)
        }
    }

    /**
     * Mở OnBoarding Tích Điểm đổi quà của Loyalty
     */
    fun startActivityOnBoarding(activity: Activity, id: String?) {
        if (!id.isNullOrEmpty()) {
            ActivityHelper.startActivity<OnBoardingActivity, Long>(activity, ConstantsLoyalty.DATA_1, id.toLong())
        } else {
            startActivityGameFromLabelsList(activity)
        }
    }

    /**
     * Mở danh sách đầu điểm của tích điểm dài hạn
     */
    fun startActivityLoyaltyCustomers(activity: Activity) {
        ActivityHelper.startActivity<LongTermProgramListActivity>(activity)
    }

    /**
     * Mở màn home của tích điểm dài hạn
     */
    fun startActivityHomePageEarnPoints(activity: Activity, id: String?) {
        if (!id.isNullOrEmpty()) {
            ActivityHelper.startActivity<HomePageEarnPointsActivity, Long>(activity, ConstantsLoyalty.DATA_2, id.toLong())
        } else {
            startActivityLoyaltyCustomers(activity)
        }
    }

    /**
     * Mở màn danh sách chương trình của đầu điểm trong tích điểm dài hạn
     */
    fun startActivityCampaignOfBusiness(activity: Activity, id: String?) {
        if (!id.isNullOrEmpty()) {
            ActivityHelper.startActivity<CampaignOfBusinessActivity, Long>(activity, ConstantsLoyalty.DATA_1, id.toLong())
        } else {
            startActivityLoyaltyCustomers(activity)
        }
    }

    /**
     * Mở màn chi tiết chương trình tích trong tích điểm dài hạn
     */
    fun startActivityLoyaltyVipDetail(activity: Activity, id: String?) {
        if (!id.isNullOrEmpty()) {
            ActivityHelper.startActivity<LoyaltyVipDetailActivity, Long>(activity, ConstantsLoyalty.DATA_1, id.toLong())
        } else {
            startActivityLoyaltyCustomers(activity)
        }
    }

    /**
     * Mở màn cửa hàng quà tặng trong tích điểm dài hạn
     */
    fun startActivityGiftShop(activity: Activity, id: String?) {
        if (id != null) {
            ActivityHelper.startActivity<GiftShopActivity, Long>(activity, ConstantsLoyalty.ID, id.toLong())
        } else {
            startActivityLoyaltyCustomers(activity)
        }
    }

    /**
     * Mở màn chi tiết quà trong tích điểm dài hạn
     * @param type = 0 quà đã đổi
     * @param type = 1 quà của shop
     */
    fun startActivityGiftDetail(activity: Activity, id: String?, type: Int) {
        if (id != null) {
            GiftDetailActivity.startActivityGiftDetail(activity, id.toLong(), type)
        } else {
            startActivityLoyaltyCustomers(activity)
        }
    }

    /**
     * Mở lịch sử đổi quà tích điểm dài hạn
     * @param type = 0 Tích điểm đổi quà
     * @param type = 1 Tích điểm dài hạn
     */
    fun startActivityRedemptionHistory(activity: Activity, id: String?, type: Int) {
        if (!id.isNullOrEmpty()) {
            activity.startActivity(Intent(activity, RedemptionHistoryActivity::class.java).apply {
                putExtra(ConstantsLoyalty.DATA_1, id.toLong())
                putExtra(ConstantsLoyalty.DATA_2, type)
            })
        } else {
            startActivityLoyaltyCustomers(activity)
        }
    }

    /**
     * Mở 1 activity bên module app thông qua schema
     */
    fun openActivity(schema: String) {
        if (schema.startsWith("icheck://") || schema.startsWith("http://") || schema.startsWith("https://")) {
            listener?.startActivity(schema)
        } else {
            listener?.startActivity("icheck://$schema")
        }
    }

    /**
     * Mở activity Login của app trong module
     */
    fun openActivityLogin(obj: ICKLoyalty, code: String) {
        listenerLogin?.startActivityForResultLogin(obj, code)
    }

    /**
     * Cấu hình Session
     */
    var user: ICUser? = null
    var city: ICProvince? = null
    var district: ICDistrict? = null
    var country: ICCountry? = null
    var ward: ICWard? = null
    var avatar_thumbnails: ICThumbnail? = null

    /**
     * Check Login
     */
    fun isLogged(isLogged: Boolean){
        SessionManager.isLogged = isLogged
    }

    fun setSessionData(accessToken: String?, tokenType: String?, expiresIn: Long, refreshToken: String?, firebaseToken: String?, deviceId: String, userType: Int?) {
        SessionManager.session = ICSessionData(accessToken, tokenType, expiresIn, refreshToken, firebaseToken, userData, userType)
        SharedLoyaltyHelper(ApplicationHelper.getApplicationByReflect(), APIConstants.DEVICE_ID).putString(APIConstants.DEVICE_ID, deviceId)
    }

    fun setUserData(id: Long = 0,
                    name: String? = null,
                    email: String? = null,
                    phone: String? = null,
                    avatar: String? = null,
                    address: String? = null,
                    city_id: Int? = null,
                    district_id: Int? = null,
                    country_id: Int? = null,
                    ward_id: Long? = null,
                    device_id: String? = null) {

        user = ICUser(id, name, email, phone, avatar, address, cityData, districtData, countryData, city_id, district_id, country_id, avatarData, ward_id?.toInt(), wardData, device_id)
    }

    fun setCityData(id: Int?, name: String?, country_id: Int?) {
        city = ICProvince(id ?: 0, name ?: "", country_id ?: 0)
    }

    fun setDistrictData(id: Int?, city_id: Int?, name: String?) {
        district = ICDistrict(id ?: 0, city_id ?: 0, name ?: "")
    }

    fun setCountryData(id: Int?, code: String?, name: String?) {
        country = ICCountry(id ?: 0, code, name ?: "")
    }

    fun setWardData(id: Int?, name: String?, district_id: Int?) {
        ward = ICWard(id ?: 0, name ?: "", district_id ?: 0)
    }

    fun setAvatarData(thumbnail: String?, original: String?, small: String?, medium: String?, square: String?) {
        avatar_thumbnails = ICThumbnail(thumbnail, original, small, medium, square)
    }

    private val userData: ICUser?
        get() {
            return user
        }

    private val cityData: ICProvince?
        get() {
            return city
        }

    private val districtData: ICDistrict?
        get() {
            return district
        }

    private val countryData: ICCountry?
        get() {
            return country
        }

    private val wardData: ICWard?
        get() {
            return ward
        }

    private val avatarData: ICThumbnail?
        get() {
            return avatar_thumbnails
        }

    fun clearSession() {
        SessionManager.session = ICSessionData()
    }

    /**
     * End cấu hình session
     */

    /**
     * Dialog show Login
     */
    inline fun <reified T : Activity, O : Serializable> showDialogLogin(activity: Activity, key: String, value: O, requestCode: Int, obj: ICKLoyalty, code: String? = null) {
        val sub = if (!code.isNullOrEmpty()) {
            if (obj.type == "accumulate_point") {
                if (!obj.campaign_package_code.isNullOrEmpty()) {
                    "Tham gia ngay để có cơ hội nhận giải thưởng \n<b>${obj.campaign_package_code[0].points} Xu iCheck</b>"
                } else {
                    "Đăng nhập để có cơ hội nhận nhiều giải thưởng hấp dẫn"
                }
            } else {
                if (!obj.box?.gifts.isNullOrEmpty()) {
                    "Tham gia ngay để có cơ hội nhận giải thưởng \n<b>${obj.box?.gifts?.get(0)?.name}</b>"
                } else {
                    "Đăng nhập để có cơ hội nhận nhiều giải thưởng hấp dẫn"
                }
            }
        } else {
            if (!obj.box?.gifts.isNullOrEmpty()) {
                "Tham gia ngay để có cơ hội nhận giải thưởng \n<b>${obj.box?.gifts?.get(0)?.name}</b>"
            } else {
                "Đăng nhập để có cơ hội nhận nhiều giải thưởng hấp dẫn"
            }
        }

        object : DialogGlobalLoginLoyalty(activity, obj.image?.original, "Tem của bạn được tham gia Chương trình: \n<b>“${obj.name}”</b>", sub) {
            override fun onRegister() {
                ActivityHelper.startActivityForResult<T, O>(activity, key, value, requestCode)
            }

            override fun onLogin() {
                ActivityHelper.startActivityForResult<T>(activity, requestCode)
            }

            override fun onFacebook() {
                ActivityHelper.startActivityForResult<T>(activity, requestCode)
            }

        }.show()
    }

    /**
     * End Dialog Login
     */

    interface SdkLoyaltyFirebaseDynamicLinksListener {
        fun startActivity(schema: String?)
    }

    interface IOpenLoginListener {
        fun startActivityForResultLogin(obj: ICKLoyalty, code: String)
    }
}