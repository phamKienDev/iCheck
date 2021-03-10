package vn.icheck.android.network.feature.address

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICDistrict
import vn.icheck.android.network.models.ICProvince
import vn.icheck.android.network.models.ICWard
import vn.icheck.android.network.models.detail_stamp_v6_1.ICDistrictStamp
import vn.icheck.android.network.models.detail_stamp_v6_1.ICProvinceStamp

class AddressInteractor : BaseInteractor() {

    fun getListProvince(offset: Int, limit: Int, listener: ICApiListener<ICListResponse<ICProvince>>) {
        val params = hashMapOf<String, Int>()
        params["offset"] = offset
        params["limit"] = limit
        requestApi(ICNetworkClient.getApiClient().listProvince(params), listener)
    }

    fun getListProvinceV2(offset: Int, limit: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICProvince>>>) {
        val params = hashMapOf<String, Int>()
        params["offset"] = offset
        params["limit"] = limit
        requestNewApi(ICNetworkClient.getSocialApi().listProvinceV2(params), listener)
    }

    fun getListDistrict(provinceID: Int, offset: Int, limit: Int, listener: ICApiListener<ICListResponse<ICDistrict>>) {
        val params = hashMapOf<String, Int>()
        params["city_id"] = provinceID
        params["offset"] = offset
        params["limit"] = limit

        requestApi(ICNetworkClient.getApiClient().getListDistrict(params), listener)
    }

    fun getListWard(districtID: Int, offset: Int, limit: Int, listener: ICApiListener<ICListResponse<ICWard>>) {
        val params = hashMapOf<String, Int>()
        params["district_id"] = districtID
        params["offset"] = offset
        params["limit"] = limit

        requestApi(ICNetworkClient.getApiClient().getListWard(params), listener)
    }

    fun getListDistrictStamp(provinceID: Int, listener: ICApiListener<ICDistrictStamp>) {
        val params = hashMapOf<String, Int>()
        params["city_id"] = provinceID

        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.ADDRESSDISTRICTS()
        val disposable = ICNetworkClient.getStampClient().getListDistrictStamp(host,params)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            listener.onSuccess(it)
                        },
                        {
                            val errorBody = ICBaseResponse()
                            errorBody.message = "Có lỗi xảy ra. Vui lòng thử lại."

                            if (it is HttpException) {
                                val error = parseJson(it.response()?.errorBody()?.string(), ICResponseErrorStamp::class.java)

                                if (error != null) {
                                    if (!error.data?.message?.message.isNullOrEmpty()) {
                                        errorBody.message = error.data?.message?.message
                                    }
                                }
                            }
                            listener.onError(errorBody)
                        }
                )
        composite.add(disposable)
    }

    fun getListProvinceStamp(listener: ICApiListener<ICProvinceStamp>) {
        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.ADDRESSCITIES()
        val disposable = ICNetworkClient.getStampClient().listProvinceStamp(host)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            listener.onSuccess(it)
                        },
                        {
                            val errorBody = ICBaseResponse()
                            errorBody.message = "Có lỗi xảy ra. Vui lòng thử lại."

                            if (it is HttpException) {
                                val error = parseJson(it.response()?.errorBody()?.string(), ICResponseErrorStamp::class.java)

                                if (error != null) {
                                    if (!error.data?.message?.message.isNullOrEmpty()) {
                                        errorBody.message = error.data?.message?.message
                                    }
                                }
                            }
                            listener.onError(errorBody)
                        }
                )
        composite.add(disposable)
    }
}