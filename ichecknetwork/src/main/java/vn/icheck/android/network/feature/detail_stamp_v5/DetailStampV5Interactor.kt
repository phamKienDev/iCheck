package vn.icheck.android.network.feature.detail_stamp_v5

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.detail_stamp_v6.ICDetailStampV6
import vn.icheck.android.network.models.detail_stamp_v6.ICListHistoryGuaranteeV6
import vn.icheck.android.network.models.detail_stamp_v6.ICStoreStampV6
import vn.icheck.android.network.models.detail_stamp_v6.IC_RESP_UpdateCustomerGuaranteeV6
import vn.icheck.android.network.models.detail_stamp_v6_1.ICNameCity
import vn.icheck.android.network.models.detail_stamp_v6_1.ICNameDistricts
import vn.icheck.android.network.models.detail_stamp_v6_1.IC_Config_Error
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.network.util.DeviceUtils
import java.util.HashMap

class DetailStampV5Interactor : BaseInteractor() {

    fun onGetDataStampV6(code: String,iCheckId: String?, listener: ICApiListener<ICDetailStampV6>) {
        val body = hashMapOf<String, Any>()
        body["code"] = code

        if (!iCheckId.isNullOrEmpty()) {
            body["icheck_id"] = iCheckId
        }

        body["device_id"] = DeviceUtils.getUniqueDeviceId()

        val disposable = ICNetworkClient.getStampClientV6().getDetailStampV5(body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            dispose()
                            listener.onSuccess(it)
                        },
                        {
                            dispose()
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

    fun getProductBySku(sku: String, listener: ICApiListener<ICBarcodeProductV1>) {
        val disposable = ICNetworkClient.getApiClient().getDetailProductBySku(sku)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            dispose()
                            listener.onSuccess(it)
                        },
                        {
                            dispose()
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

    fun getConfigError(listener: ICApiListener<IC_Config_Error>) {
        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPGETCONFIGERROR()
        ICNetworkClient.getStampClient2().configErrorStamp(host).enqueue(object : Callback<IC_Config_Error> {
            override fun onResponse(call: Call<IC_Config_Error>, response: Response<IC_Config_Error>) {
                val body = response.body()

                if (body != null) {
                    when (body.status) {
                        200 -> {
                            if (body.data != null) {
                                listener.onSuccess(body)
                            }
                        }
                    }
                } else {
                    val errorBody = ICBaseResponse()
                    errorBody.message = "Có lỗi xảy ra. Vui lòng thử lại."

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

            override fun onFailure(call: Call<IC_Config_Error>, t: Throwable) {
                val errorBody = ICBaseResponse()
                errorBody.message = "Có lỗi xảy ra. Vui lòng thử lại."

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

    fun getHistoryGuaranteeV5(qrm:String,listener:ICApiListener<ICListHistoryGuaranteeV6>){
        val params = HashMap<String,Any>()
        params["typeCode"] = "qrm"
        params["desc"] = "created_time"

        val disposable = ICNetworkClient.getStampClientV6().getListHistoryGuaranteeV6(qrm,params)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            dispose()
                            listener.onSuccess(it)
                        },
                        {
                            dispose()
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

    fun getNameCity(city: Int?, listener: ICApiListener<ICNameCity>) {
        ICNetworkClient.getStampClient2().getNameCity(city.toString()).enqueue(object : Callback<ICNameCity> {
            override fun onResponse(call: Call<ICNameCity>, response: Response<ICNameCity>) {
                val body = response.body()

                if (body != null) {
                    when (body.status) {
                        200 -> {
                            if (body.data != null) {
                                listener.onSuccess(body)
                            }
                        }
                    }
                } else {
                    val errorBody = ICBaseResponse()
                    errorBody.message = "Có lỗi xảy ra. Vui lòng thử lại."

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

            override fun onFailure(call: Call<ICNameCity>, t: Throwable) {
                val errorBody = ICBaseResponse()
                errorBody.message = "Có lỗi xảy ra. Vui lòng thử lại."

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

    fun getNameDistricts(district: Int?, listener: ICApiListener<ICNameDistricts>) {
        ICNetworkClient.getStampClient2().getNameDistrict(district.toString()).enqueue(object : Callback<ICNameDistricts> {
            override fun onResponse(call: Call<ICNameDistricts>, response: Response<ICNameDistricts>) {
                val body = response.body()

                if (body != null) {
                    when (body.status) {
                        200 -> {
                            if (body.data != null) {
                                listener.onSuccess(body)
                            }
                        }
                    }
                } else {
                    val errorBody = ICBaseResponse()
                    errorBody.message = "Có lỗi xảy ra. Vui lòng thử lại."

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

            override fun onFailure(call: Call<ICNameDistricts>, t: Throwable) {
                val errorBody = ICBaseResponse()
                errorBody.message = "Có lỗi xảy ra. Vui lòng thử lại."

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

    fun onGetListStoreStampV6(id: String, listener: ICApiListener<ICStoreStampV6>) {
        val disposable = ICNetworkClient.getStampClientV6().getListStoreStampV6(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            dispose()
                            listener.onSuccess(it)
                        },
                        {
                            dispose()
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

    fun onUpdateInformationStampV5(district: String, city: String, name: String, email: String, mIdStamp: String, mIdStore: Int, address: String, phone: String, devideId: String, listener: ICApiListener<IC_RESP_UpdateCustomerGuaranteeV6>) {
        val body = hashMapOf<String, Any>()
        body["district"] = district
        body["city"] = city
        body["name"] = name
        body["email"] = email
        body["id"] = mIdStamp
        body["store_id"] = mIdStore
        body["address"] = address
        body["phone"] = phone
        body["device_id"] = devideId

        val disposable = ICNetworkClient.getStampClientV6().updateInformationCustomerGuaranteeV6(body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            dispose()
                            listener.onSuccess(it)
                        },
                        {
                            dispose()
                            when (it) {
                                is RetrofitException -> {
                                    val error = parseJson(it.response?.errorBody()?.string(), ICBaseResponse::class.java)

                                    if (error != null) {
                                        listener.onError(error)
                                        return@subscribe
                                    }
                                }
                                is HttpException -> {
                                    val error = parseJson(it.response()?.errorBody()?.string(), ICBaseResponse::class.java)

                                    if (error != null) {
                                        listener.onError(error)
                                        return@subscribe
                                    }
                                }
                            }

                            val errorBody = ICBaseResponse()
                            errorBody.message = "Có lỗi xảy ra. Vui lòng thử lại."
                        }
                )
        composite.add(disposable)
    }
}