package vn.icheck.android.network.feature.history

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.network.R
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.detail_stamp_v6_1.ICShopVariantStamp
import vn.icheck.android.network.models.history.*
import vn.icheck.android.network.util.DeviceUtils
import kotlin.collections.HashMap

class HistoryInteractor : BaseInteractor() {

    fun getListHistoryProduct(page: Int, lat: String?, lon: String?, listener: ICApiListener<ICListResponse<ICHistory_Product>>) {
        val fields = hashMapOf<String, Any>()

        fields["device_id"] = DeviceUtils.getUniqueDeviceId()

        fields["offset"] = page
        fields["limit"] = APIConstants.LIMIT
        if (lat != null) {
            fields["lat"] = lat
        }
        if (lon != null) {
            fields["lon"] = lon
        }

        requestApi(ICNetworkClient.getApiClient().getListHistoryProduct(fields), listener)
    }

    fun getListHistoryProductNoLogin(page: Int, lat: String?, lon: String?, listener: ICApiListener<ICListResponse<ICHistory_Product>>) {
        val fields = hashMapOf<String, Any>()

        fields["device_id"] = DeviceUtils.getUniqueDeviceId()

        fields["offset"] = page
        fields["limit"] = APIConstants.LIMIT
        if (lat != null) {
            fields["lat"] = lat
        }
        if (lon != null) {
            fields["lon"] = lon
        }

        requestApi(ICNetworkClient.getApiClient().getListHistoryProduct(fields), listener)
    }

    fun getListSearchHistoryProduct(accessToken: String?, page: Int, pagesize: Int, lat: String?, lon: String?, key: String?, listener: ICApiListener<ICListResponse<ICHistory_Product>>) {
        val fields = HashMap<String, Any>()
        if (accessToken != null) {
            fields["device_id"] = accessToken
        }
        fields["offset"] = page
        fields["limit"] = pagesize
        if (lat != null) {
            fields["lat"] = lat
        }
        if (lon != null) {
            fields["lon"] = lon
        }
        if (key != null) {
            fields["search"] = key
        }

        requestApi(ICNetworkClient.getApiClient().getListHistoryProduct(fields), listener)
    }

    fun getListHistoryShop(page: Int, pagesize: Int, lat: String?, lon: String?, listener: ICApiListener<ICListResponse<ICHistory_Shop>>) {
        val fields = HashMap<String, Any>()

        fields["device_id"] = DeviceUtils.getUniqueDeviceId()

        fields["offset"] = page
        fields["limit"] = pagesize
        if (lat != null) {
            fields["lat"] = lat
        }
        if (lon != null) {
            fields["lon"] = lon
        }

        requestApi(ICNetworkClient.getApiClient().getListShop(fields), listener)
    }

    fun getListHistoryShopNoLogin(page: Int, pagesize: Int, lat: String?, lon: String?, listener: ICApiListener<ICListResponse<ICHistory_Shop>>) {
        val fields = HashMap<String, Any>()

        fields["device_id"] = DeviceUtils.getUniqueDeviceId()

        fields["offset"] = page
        fields["limit"] = pagesize
        if (lat != null) {
            fields["lat"] = lat
        }
        if (lon != null) {
            fields["lon"] = lon
        }

        requestApi(ICNetworkClient.getApiClient().getListShop(fields), listener)
    }

    fun getListSearchHistoryShop(accessToken: String?, page: Int, pagesize: Int, lat: String?, lon: String?, key: String?, listener: ICApiListener<ICListResponse<ICHistory_Shop>>) {
        val fields = HashMap<String, Any>()
        if (accessToken != null) {
            fields["device_id"] = accessToken
        }
        fields["offset"] = page
        fields["limit"] = pagesize
        if (lat != null) {
            fields["lat"] = lat
        }
        if (lon != null) {
            fields["lon"] = lon
        }
        if (key != null) {
            fields["search"] = key
        }

        requestApi(ICNetworkClient.getApiClient().getListShop(fields), listener)
    }

    fun deleteItemProductBarcode(id: Long?, listener: ICApiListener<ICDeleteItemProductBarcode>) {
        requestApi(ICNetworkClient.getApiClient().deleteItemProductBarcode(id!!), listener)
    }

    fun deleteItemProduct(url: String, listener: ICApiListener<ResponseBody>) {
        requestApi(ICNetworkClient.getApiClient().deleteItemProduct(url), listener)
    }

    fun getListBookmarkProduct(offset: Int, lat: String?, lon: String?, listener: ICApiListener<ICListResponse<ICHistory_Product>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        if (lat != null) {
            params["lat"] = lat
        }

        if (lon != null) {
            params["lon"] = lon
        }

        requestApi(ICNetworkClient.getApiClient().getProductBookmark(params), listener)
    }

    fun deleteItemProductBarcodeNoLogin(id: Long?, listener: ICApiListener<ICDeleteItemProductBarcode>) {
        val body = HashMap<String, Any>()
        body["device_id"] = DeviceUtils.getUniqueDeviceId()
        requestApi(ICNetworkClient.getApiClientNoHeaderWithRx().deleteItemProductBarcodeNoLogin(id!!, body), listener)
    }

    fun getShopVariant(sellerId: Long, barcode: String, listener: ICApiListener<ICListResponse<ICShopVariantStamp>>) {
        val params = hashMapOf<String, Any>()
        params["shop_id"] = sellerId
        params["barcode"] = barcode
        params["include"] = "shop"
        params["for_search"] = 1
        params["blocked"] = 0
        params["type"] = "product"
        params["offset"] = 0
        params["limit"] = 1

        val host = APIConstants.defaultHost + APIConstants.STAMPGETSHOPVARIANT()
        ICNetworkClient.getApiClientNoHeader().getShopVariant(host, params).enqueue(object : Callback<ICListResponse<ICShopVariantStamp>> {
            override fun onResponse(call: Call<ICListResponse<ICShopVariantStamp>>, response: Response<ICListResponse<ICShopVariantStamp>>) {
                val body = response.body()

                if (body?.rows != null) {
                    listener.onSuccess(body)
                } else {
                    val errorBody = ICBaseResponse()
                    errorBody.message = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)

                    if (errorBody is HttpException) {
                        val error = parseJson(errorBody.response()?.errorBody()?.string(), ICResponseErrorStamp::class.java)

                        if (error != null) {
                            if (!error.data?.message?.message.isNullOrEmpty()) {
                                errorBody.message = error.data?.message?.message
                            }
                        }
                    }
                    listener.onError(errorBody)
                }
            }

            override fun onFailure(call: Call<ICListResponse<ICShopVariantStamp>>, t: Throwable) {
                val errorBody = ICBaseResponse()
                errorBody.message = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)

                if (errorBody is HttpException) {
                    val error = parseJson(errorBody.response()?.errorBody()?.string(), ICResponseErrorStamp::class.java)

                    if (error != null) {
                        if (!error.data?.message?.message.isNullOrEmpty()) {
                            errorBody.message = error.data?.message?.message
                        }
                    }
                }
                listener.onError(errorBody)
            }
        })
    }

    fun getListPointReceived(offset: Int, type: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICHistoryPoint>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
        params["type"] = type // Loại giao dịch. 1= tiền vào. 2= tiền ra

        requestNewApi(ICNetworkClient.getSocialApi().getRankHistory(params), listener)
    }

    fun getListBigCorp(listener: ICNewApiListener<ICResponse<ICListResponse<ICBigCorp>>>) {
        requestNewApi(ICNetworkClient.getNewSocialApi().getListBigCorp(), listener)
    }

    fun getListScanHistory(offset: Int, sort: Int?, listIdBigCorp: MutableList<Any>?, listType: MutableList<Any>?, keySearch: String?, lat: Double, lon: Double, listener: ICNewApiListener<ICResponse<ICListResponse<ICItemHistory>>>) {
        val params = hashMapOf<String, Any>()

        if (lat != 0.0) {
            params["lat"] = lat
        }

        if (lon != 0.0) {
            params["lon"] = lon
        }

        val body = hashMapOf<String, Any>()
        if (sort != null) {
            body["sort"] = sort
        } else {
            body["sort"] = 2
        }

        if (!listIdBigCorp.isNullOrEmpty()) {
            body["offshopId"] = listIdBigCorp
        }

        if (!listType.isNullOrEmpty()) {
            body["actionType"] = listType
        }

        if (!keySearch.isNullOrEmpty()) {
            body["name"] = keySearch
        }
        body["offset"] = offset
        body["limit"] = APIConstants.LIMIT

        requestNewApi(ICNetworkClient.getNewSocialApi().getListScanHistory(params, body), listener)
    }

    fun getListScanHistoryBySearch(offset: Int, sort: Int?, listIdBigCorp: MutableList<Any>?, listType: MutableList<Any>?, keySearch: String?, listener: ICNewApiListener<ICResponse<ICListResponse<ICItemHistory>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = 100

        val body = hashMapOf<String, Any>()
        if (sort != null) {
            body["sort"] = sort
        } else {
            body["sort"] = 2
        }

        if (!listIdBigCorp.isNullOrEmpty()) {
            body["offshopId"] = listIdBigCorp
        }

        if (!listType.isNullOrEmpty()) {
            body["actionType"] = listType
        }

        if (!keySearch.isNullOrEmpty()) {
            body["name"] = keySearch
        }

        requestNewApi(ICNetworkClient.getNewSocialApi().getListScanHistory(params, body), listener)
    }

    fun getStoreSell(idProduct: Long, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICStoreNear>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
        requestNewApi(ICNetworkClient.getNewSocialApi().getListStoreSell(idProduct, params), listener)
    }

    fun getCartCount(listener: ICNewApiListener<ICResponse<Int>>) {
        requestNewApi(ICNetworkClient.getNewSocialApi().getCartCount(), listener)
    }

    fun getListFilterType(listener: ICNewApiListener<ICResponse<ICListResponse<ICTypeHistory>>>) {
        requestNewApi(ICNetworkClient.getNewSocialApi().getFilterHistory(), listener)
    }

    fun getSuggestStoreHistory(offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICSuggestStoreHistory>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
        requestNewApi(ICNetworkClient.getNewSocialApi().getSuggestStoreHistory(params), listener)
    }

    fun getProductOfShopHistory(offset: Int, id: Long, listener: ICNewApiListener<ICResponse<ICListResponse<ICProductOfShopHistory>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
        requestNewApi(ICNetworkClient.getNewSocialApi().getProductOfShopHistory(id, params), listener)
    }

    fun getRouteShop(latShop: Double, lonShop: Double, listener: ICNewApiListener<ICResponse<MutableList<ICRoutesShop>>>) {
        val params = hashMapOf<String, Any>()
        val latLonUser = "${APIConstants.LATITUDE},${APIConstants.LONGITUDE}"
        val latLonShop = "${latShop},${lonShop}"

        params["origin"] = latLonUser
        params["destination"] = latLonShop
        requestNewApi(ICNetworkClient.getNewSocialApi().getRouteShop(params), listener)
    }

    fun getStoreNear(idShopSelect: Long, offset: Int,listener: ICNewApiListener<ICResponse<ICListResponse<ICStoreNear>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT
        requestNewApi(ICNetworkClient.getNewSocialApi().getStoreNearHistory(idShopSelect,params), listener)
    }

    fun checkScanQrCode(code: String, listener: ICNewApiListener<ICResponse<ICValidStampSocial>>) {
        val body = hashMapOf<String, Any>()
        body["code"] = code

        requestNewApi2(ICNetworkClient.getStampClientSocial().checkScanQrCode(body), listener)
    }
}
