package vn.icheck.android.loyalty.repository

import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.network.BaseRepository
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.network.ICNetworkClient
import vn.icheck.android.loyalty.network.SessionManager

internal class AddressRepository : BaseRepository() {

    fun getListProvince(offset: Int, limit: Int, listener: ICApiListener<ICKListResponse<ICProvince>>) {
        val params = hashMapOf<String, Int>()
        params["offset"] = offset
        params["limit"] = limit

        val host = APIConstants.USER_HOST + "cities"
        requestApi(ICNetworkClient.getApiClientLoyalty().listProvince(host, params), listener)
    }

    fun getListDistrict(provinceID: Int, offset: Int, limit: Int, listener: ICApiListener<ICKListResponse<ICDistrict>>) {
        val params = hashMapOf<String, Int>()
        params["city_id"] = provinceID
        params["offset"] = offset
        params["limit"] = limit

        val host = APIConstants.USER_HOST + "districts"
        requestApi(ICNetworkClient.getApiClientLoyalty().getListDistrict(host, params), listener)
    }

    fun getListWard(districtID: Int, offset: Int, limit: Int, listener: ICApiListener<ICKListResponse<ICWard>>) {
        val params = hashMapOf<String, Int>()
        params["district_id"] = districtID
        params["offset"] = offset
        params["limit"] = limit

        val host = APIConstants.USER_HOST + "wards"
        requestApi(ICNetworkClient.getApiClientLoyalty().getListWard(host, params), listener)
    }

    fun confirmGiftLoyalty(giftId: Long, name: String, phone: String, email: String?, cityId: Int, districtId: Int, address: String, city_name: String, district_name: String, wardId: Int, ward_name: String, listener: ICApiListener<ICKResponse<ICKWinner>>) {
        val params = hashMapOf<String, Any>()

        params["district_id"] = districtId

        params["district_name"] = district_name

        params["phone"] = phone

        params["name"] = name

        params["city_id"] = cityId

        params["city_name"] = city_name

        params["ward_id"] = wardId

        params["ward_name"] = ward_name

        params["address"] = address

        params["status"] = "waiting_receive_gift"

        if (!email.isNullOrEmpty()) {
            params["email"] = email
        }

        if (!SessionManager.session.user?.avatar.isNullOrEmpty()) {
            params["avatar"] = SessionManager.session.user?.avatar!!
        }

        val url = APIConstants.LOYALTY_HOST + "loyalty/winner/$giftId/customer-update"
        requestApi(ICNetworkClient.getApiClientLoyalty().confirmGiftLoyalty(url, params), listener)
    }
}