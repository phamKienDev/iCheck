package vn.icheck.android.network.feature.detail_stamp_v6_1

import android.util.Base64
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.network.R
import vn.icheck.android.network.base.*
import vn.icheck.android.network.models.ICStampV61
import vn.icheck.android.network.models.detail_stamp_v6_1.*
import vn.icheck.android.network.util.DeviceUtils
import vn.icheck.android.network.util.JsonHelper

class DetailStampRepository : BaseRepository() {

    suspend fun getDetailStampV61(user: ICUpdateCustomerGuarantee?, barcode: String, lat: Double?, lon: Double?): ICResponse<ICStampV61> {
        val body = JsonObject()

        if (user != null) {
            body.add("customer", JsonObject().apply {
                if (!user.name.isNullOrEmpty()) {
                    addProperty("name", user.name!!)
                }
                if (!user.phone.isNullOrEmpty()) {
                    addProperty("phone", user.phone!!)
                }
                if (!user.email.isNullOrEmpty()) {
                    addProperty("email", user.email!!)
                }
                if (!user.address.isNullOrEmpty()) {
                    addProperty("address", user.address!!)
                }
                if (user.district ?: 0 > 0)
                    addProperty("district", user.district!!)
                if (user.city ?: 0 > 0)
                    addProperty("city", user.city!!)

                if (!user.fields.isNullOrEmpty()) {
                    add("fields", JsonObject().apply {
                        for (obj in user.fields!!.entries) {
                            when (obj.value) {
                                is Int -> addProperty(obj.key, obj.value as Int)
                                is Long -> addProperty(obj.key, obj.value as Long)
                                else -> addProperty(obj.key, obj.value.toString())
                            }
                        }
                    })
                }
            })
        }

        val loginUser = SessionManager.session.user

        if (loginUser != null) {
            if (loginUser.id != 0L) {
                body.addProperty("icheck_id", "i-${loginUser.id}")
            }
            val name = Constant.getName(loginUser.last_name, loginUser.first_name, "")
            if (name.isNotEmpty()) {
                body.addProperty("name", name)
            }
            if (!loginUser.phone.isNullOrEmpty()) {
                body.addProperty("phone", loginUser.phone!!)
            }
            if (!loginUser.email.isNullOrEmpty()) {
                body.addProperty("email", loginUser.email!!)
            }
            if (!loginUser.address.isNullOrEmpty()) {
                body.addProperty("address", loginUser.address!!)
            }
        }

        val currentTime = System.currentTimeMillis()
        body.addProperty("sourceTime", currentTime)
        val sourceApp = Base64.encodeToString("isIcheck=true&time=$currentTime".toByteArray(Charsets.UTF_8), Base64.DEFAULT).toString().trim()
        body.addProperty("sourceApp", sourceApp)

        val agent = DeviceUtils.getModel()
        if (!agent.isNullOrEmpty()) {
            body.addProperty("agent", agent)
            body.addProperty("os", agent)
        }

        val userid = SessionManager.session.user?.id
        if (userid != null) {
            body.addProperty("user_id", userid)
        }

        body.addProperty("code", barcode)
        body.addProperty("device_id", DeviceUtils.getUniqueDeviceId())
        body.addProperty("rankLevel", SettingManager.getRankLevel)

        if (lat != null) {
            body.addProperty("lat", lat)
        }
        if (lon != null) {
            body.addProperty("lon", lon)
        }

        val url = APIConstants.DETAIL_STAMP_HOST + APIConstants.stampDetailV61()
        return ICNetworkClient.getSimpleStampClient().getStampDetail(url, body)
    }

    fun getDetailStamp(code: String, lat: Double?, lon: Double?, listener: ICApiListener<ICDetailStampV6_1>) {
        val body = hashMapOf<String, Any>()

        val user = SessionManager.session.user

        val userID = if (user?.id != null) {
            "i-${user.id}"
        } else {
            null
        }

        val name = if (user?.last_name.isNullOrEmpty()) {
            user?.first_name
        } else {
            if (!user?.first_name.isNullOrEmpty()) {
                user?.last_name + " " + user?.first_name
            } else {
                user?.last_name
            }
        }

        val phone = user?.phone
        val email = user?.email
        val address = user?.address

        if (!userID.isNullOrEmpty()) {
            body["icheck_id"] = userID
        }

        if (!name.isNullOrEmpty()) {
            body["name"] = name
        }

        if (!phone.isNullOrEmpty()) {
            body["phone"] = phone
        }

        if (!email.isNullOrEmpty()) {
            body["email"] = email
        }

        if (!address.isNullOrEmpty()) {
            body["address"] = address
        }

        body["code"] = code
        body["device_id"] = DeviceUtils.getUniqueDeviceId()

        val agent = DeviceUtils.getModel()
        if (!agent.isNullOrEmpty()) {
            body["agent"] = agent
            body["os"] = agent
        }

        val userid = SessionManager.session.user?.id
        if (userid != null) {
            body["user_id"] = userid
        }

        val rank = SettingManager.getRankLevel
        body["rank_level"] = rank

        if (lat != null) {
            body["lat"] = lat.toString()
        }
        if (lon != null) {
            body["lon"] = lon.toString()
        }

        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPDETAIL()
        val disposable = ICNetworkClient.getSimpleStampClient().getDetailStamp(host, body)
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
                            errorBody.message = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)

                            when (it) {
                                is RetrofitException -> {
                                    val error = parseJson(it.response?.errorBody()?.string(), ICBaseResponse::class.java)

                                    if (error != null) {
                                        listener.onError(error)
                                    }
                                }
                                is HttpException -> {
                                    val error = parseJson(it.response()?.errorBody()?.string(), ICBaseResponse::class.java)

                                    if (error != null) {
                                        if (!error.message.isNullOrEmpty()) {
                                            errorBody.message = error.message
                                            errorBody.statusCode = error.statusCode
                                        }
                                    }
                                }
                            }

                            listener.onError(errorBody)
                        }
                )

        composite.add(disposable)
    }

    suspend fun getDetailStampHoaPhat(phone: String?, mId: String?, code: String, lat: String?, lon: String?): ICDetailStampV6_1 {
        val body = hashMapOf<String, Any>()
        if (!phone.isNullOrEmpty()) {
            body["phone"] = phone
        }

        if (!mId.isNullOrEmpty()) {
            body["icheck_id"] = mId
        }

        body["code"] = code

        body["device_id"] = DeviceUtils.getUniqueDeviceId()

        val agent = DeviceUtils.getModel()
        if (!agent.isNullOrEmpty()) {
            body["agent"] = agent
            body["os"] = agent
        }

        val userid = SessionManager.session.user?.id
        if (userid != null) {
            body["user_id"] = userid
        }

        val rank = SettingManager.getRankLevel
        body["rank_level"] = rank

        if (lat != null) {
            body["lat"] = lat
        }
        if (lon != null) {
            body["lon"] = lon
        }

        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPDETAIL()
        return ICNetworkClient.getSimpleStampClient().getDetailStampHoaPhat(host, body)
    }

    fun getListMoreProductVerifiedDistributor(distributorId: Long?, listener: ICApiListener<ICMoreProductVerified>) {
        val fields = HashMap<String, Any>()
        fields["status"] = 1
        fields["offset"] = 0
        fields["limit"] = 4

        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPMOREPRODUCTVERIFIEDDISTRIBUTOR().replace("{id}", distributorId.toString())
        val disposable = ICNetworkClient.getStampClient().getListMoreProductVerifiedDistributor(host, fields)
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
                            errorBody.message = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)

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

    fun getListMoreProductVerifiedDistributorSeccond(distributorId: Long?, page: Int, listener: ICApiListener<ICMoreProductVerified>) {
        val fields = HashMap<String, Any>()
        fields["status"] = 1
        fields["offset"] = page
        fields["limit"] = APIConstants.LIMIT

        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPMOREPRODUCTVERIFIEDDISTRIBUTOR().replace("{id}", distributorId.toString())
        val disposable = ICNetworkClient.getStampClient().getListMoreProductVerifiedDistributor(host, fields)
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
                            errorBody.message = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)

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

    suspend fun getWarrantyHistory(serial: String): ICResponse<MutableList<ICListHistoryGuarantee>> {
        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPHISTORYGUARANTEE().replace("{serial}", serial)
        return ICNetworkClient.getStampClient().getWarrantyHistory(host)
    }

    fun getListNoteHistoryGuarantee(id: String, listener: ICApiListener<ICResp_Note_Guarantee>) {
        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPNOTEHISTORYGUARANTEE().replace("{log_id}", id)

        val params = hashMapOf<String, Any>()
        params["limit"] = 50
        params["offset"] = 0

        val disposable = ICNetworkClient.getStampClient().getListNoteHistoryGuarantee(host, params)
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
                            errorBody.message = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)

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

    fun getListMoreProductVerifiedVendor(vendorId: Long?, page: Int, listener: ICApiListener<ICMoreProductVerified>) {
        val fields = HashMap<String, Any>()
        fields["status"] = 1
        fields["offset"] = page
        fields["limit"] = APIConstants.LIMIT

        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPMOREPRODUCTVERIFIEDVENDOR().replace("{id}", vendorId.toString())
        val disposable = ICNetworkClient.getStampClient().getListMoreProductVerifiedVendor(host, fields)
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
                            errorBody.message = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)

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

    fun onVerifiedPhoneNumber(serial: String, phone: String, listener: ICApiListener<ICVerifiedPhone>) {
        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPVERIFIEDNUMBERGUARANTEE().replace("{serial}", serial).replace("{phone}", phone)
        val disposable = ICNetworkClient.getStampClient().getVerifiedNumberPhone(host)
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
                            errorBody.message = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)

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

    fun getDetailCustomerGurantee(id: Long, phoneNumber: String, listener: ICApiListener<ICDetailCustomerGuranteeVerified>) {
        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPDETAILCUSTOMERGUARANTEE().replace("{distributor_id}", id.toString()).replace("{phone}", phoneNumber)
        val disposable = ICNetworkClient.getStampClient().getDetailCustomerGuarantee(host)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            listener.onSuccess(it)
                        },
                        {
                            try {
                                val errorBody = ICBaseResponse()
                                errorBody.message = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)

                                if (it is HttpException) {
                                    val error = parseJson(it.response()?.errorBody()?.string(), ICResponseErrorStamp::class.java)

                                    if (error != null) {
                                        if (!error.data?.message?.message.isNullOrEmpty()) {
                                            errorBody.message = error.data?.message?.message
                                        }
                                    }
                                }
                                listener.onError(errorBody)
                            } catch (e: Exception) {
                            }
                        }
                )
        composite.add(disposable)
    }

    suspend fun getGuaranteeCustomerDetail(distributionID: Long, phoneNumber: String): ICResponse<ICGuaranteeCustomerDetail> {
        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPDETAILCUSTOMERGUARANTEE().replace("{distributor_id}", distributionID.toString()).replace("{phone}", phoneNumber)
        return ICNetworkClient.getStampClient().getGuaranteeCustomerDetail(host)
    }

    fun getNameCity(city: Int?, listener: ICApiListener<ICNameCity>) {
        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.ADDRESSDETAILCITY().replace("{id}", city.toString())
        ICNetworkClient.getStampClient2().getNameCity(host).enqueue(object : Callback<ICNameCity> {
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
                    try {
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
                    } catch (e: Exception) {
                    }
                }
            }

            override fun onFailure(call: Call<ICNameCity>, t: Throwable) {
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

    fun getNameDistricts(district: Int?, listener: ICApiListener<ICNameDistricts>) {
        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPGETNAMEDISTRICTSGUARANTEE().replace("{districts_id}", "$district")
        ICNetworkClient.getStampClient2().getNameDistrict(host).enqueue(object : Callback<ICNameDistricts> {
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

            override fun onFailure(call: Call<ICNameDistricts>, t: Throwable) {
                try {
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
                } catch (e: Exception) {
                }
            }
        })
    }

    fun getDetailStampWhenUpdate(obj: ICUpdateCustomerGuarantee?, phone: String?, mId: String?, code: String, lat: String?, lon: String?, listener: ICApiListener<ICDetailStampV6_1>) {
        val body = hashMapOf<String, Any>()
        if (!phone.isNullOrEmpty()) {
            body["phone"] = phone
        }

        if (obj != null) {
            body["customer"] = obj
        }

        if (!mId.isNullOrEmpty()) {
            body["icheck_id"] = mId
        }

        body["code"] = code

        body["device_id"] = DeviceUtils.getUniqueDeviceId()

        val agent = DeviceUtils.getModel()
        if (!agent.isNullOrEmpty()) {
            body["agent"] = agent
            body["os"] = agent
        }

        val userid = SessionManager.session.user?.id
        if (userid != null) {
            body["user_id"] = userid
        }

        val rank = SettingManager.getRankLevel
        body["rank_level"] = rank

        if (lat != null) {
            body["lat"] = lat
        }
        if (lon != null) {
            body["lon"] = lon
        }

        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPDETAIL()
        val disposable = ICNetworkClient.getSimpleStampClient().getDetailStamp(host, body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            dispose()
                            listener.onSuccess(it)
                        },
                        {
                            dispose()

                            try {
                                val errorBody = ICBaseResponse()
                                errorBody.message = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)

                                when (it) {
                                    is RetrofitException -> {
                                        val error = parseJson(it.response?.errorBody()?.string(), ICBaseResponse::class.java)

                                        if (error != null) {
                                            listener.onError(error)
                                        }
                                    }
                                    is HttpException -> {
                                        val error = JsonHelper.parseJson(it.response()?.errorBody()?.string(), ICBaseResponse::class.java)

                                        if (error != null) {
                                            if (!error.message.isNullOrEmpty()) {
                                                errorBody.message = error.message
                                                errorBody.statusCode = error.statusCode
                                            }
                                        }
                                    }
                                }

                                listener.onError(errorBody)
                            } catch (e: Exception) {
                            }
                        }
                )
        composite.add(disposable)
    }

    fun updateInfomationGuarantee(obj: ICUpdateCustomerGuarantee, deviceId: String?, mId: String?,
                                  productCode: String?, mSerial: String?, variant: Long?,
                                  guaranteeData: HashMap<String, Any>, listener: ICApiListener<IC_RESP_UpdateCustomerGuarantee>) {
        val body = hashMapOf<String, Any>()
        body["customer"] = obj

        if (mId != null) {
            body["icheck_id"] = mId
        }

        if (deviceId != null) {
            body["device_id"] = deviceId
        }

        if (!productCode.isNullOrEmpty()) {
            body["product_code"] = productCode
        }

        if (variant != null && variant != 0L) {
            body["variant_id"] = variant
        }

        body["fields"] = guaranteeData

        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPUPDATEINFORMATIONCUSTOMERGUARANTEE().replace("{serial}", mSerial
                ?: "")
        val disposable = ICNetworkClient.getStampClient().updateInformationCustomerGuarantee(host, body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            dispose()
                            listener.onSuccess(it)
                        },
                        {
                            dispose()
                            try {
                                val errorBody = ICBaseResponse()
                                errorBody.message = "C?? l???i x???y ra. Vui l??ng th??? l???i."

                                if (it is HttpException) {
                                    val error = parseJson(it.response()?.errorBody()?.string(), ICResponseErrorStamp::class.java)

                                    if (error != null) {
                                        if (!error.data?.message?.message.isNullOrEmpty()) {
                                            errorBody.message = error.data?.message?.message
                                        }
                                    }
                                }
                                listener.onError(errorBody)
                            } catch (e: Exception) {
                            }
                        }
                )
        composite.add(disposable)
    }

    fun updateInformationGuarantee(user: ICUpdateCustomerGuarantee, deviceId: String?, mId: String?,
                                   productCode: String?, variant: Long?,
                                   customerData: HashMap<String, Any>, guaranteeData: HashMap<String, Any>,
                                   mSerial: String?,
                                   listener: ICApiListener<IC_RESP_UpdateCustomerGuarantee>) {
        val body = hashMapOf<String, Any>()
        body["customer"] = hashMapOf<String, Any>().apply {
            if (!user.name.isNullOrEmpty())
                put("name", user.name!!)
            if (!user.phone.isNullOrEmpty())
                put("phone", user.phone!!)
            if (!user.email.isNullOrEmpty())
                put("email", user.email!!)
            if (!user.address.isNullOrEmpty())
                put("address", user.address!!)
            if (user.district ?: 0 > 0)
                put("district", user.district!!)
            if (user.city ?: 0 > 0)
                put("city", user.city!!)
            if (!customerData.isNullOrEmpty()) {
                put("fields", customerData)
            }
        }

        if (mId != null) {
            body["icheck_id"] = mId
        }

        if (deviceId != null) {
            body["device_id"] = deviceId
        }

        if (!productCode.isNullOrEmpty()) {
            body["product_code"] = productCode
        }

        if (variant != null && variant != 0L) {
            body["variant_id"] = variant
        }

        if (!guaranteeData.isNullOrEmpty())
            body["fields"] = guaranteeData

        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPUPDATEINFORMATIONCUSTOMERGUARANTEE().replace("{serial}", mSerial
                ?: "")
        val disposable = ICNetworkClient.getStampClient().updateInformationCustomerGuarantee(host, body)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            dispose()
                            listener.onSuccess(it)
                        },
                        {
                            dispose()
                            try {
                                val errorBody = ICBaseResponse()
                                errorBody.message = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)

                                if (it is HttpException) {
                                    val error = parseJson(it.response()?.errorBody()?.string(), ICResponseErrorStamp::class.java)

                                    if (error != null) {
                                        if (!error.data?.message?.message.isNullOrEmpty()) {
                                            errorBody.message = error.data?.message?.message
                                        }
                                    }
                                }
                                listener.onError(errorBody)
                            } catch (e: Exception) {
                            }
                        }
                )
        composite.add(disposable)
    }

    fun getShopVariant(lat: String?, lon: String?, sellerId: Long, barcode: String?, listener: ICApiListener<ICListResponse<ICShopVariantStamp>>) {
        val params = hashMapOf<String, Any>()
        if (lat != null) {
            params["lat"] = lat
        }

        if (lon != null) {
            params["lon"] = lon
        }

        params["shop_id"] = sellerId

        if (barcode != null) {
            params["barcode"] = barcode
        }

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

            override fun onFailure(call: Call<IC_Config_Error>, t: Throwable) {
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

    suspend fun getStampConfig(): ICResponse<ICStampConfig> {
        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.STAMPGETCONFIGERROR()
        return ICNetworkClient.getStampClient2().getStampConfig(host)
    }

    fun getInforProductById(id: Long, listener: ICApiListener<IC_RESP_InformationProduct>) {
        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.PRODUCTINFO().replace("{id}", id.toString())
        val disposable = ICNetworkClient.getStampClient().getInformationProduct(host)
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
                            errorBody.message = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)

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

    fun getVariantProduct(productId: Long, page: Int?, listener: ICApiListener<ICVariantProductStampV6_1>) {
        val fields = HashMap<String, Any>()
        if (page != null) {
            fields["offset"] = page
        }
        fields["limit"] = APIConstants.LIMIT

        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.VARIANTPRODUCT().replace("{product_id}", productId.toString())
        val disposable = ICNetworkClient.getStampClient().getVariantProduct(host, fields)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            listener.onSuccess(it)
                        },
                        {
                            try {
                                val errorBody = ICBaseResponse()
                                errorBody.message = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)

                                if (it is HttpException) {
                                    val error = parseJson(it.response()?.errorBody()?.string(), ICResponseErrorStamp::class.java)

                                    if (error != null) {
                                        if (!error.data?.message?.message.isNullOrEmpty()) {
                                            errorBody.message = error.data?.message?.message
                                        }
                                    }
                                }
                                listener.onError(errorBody)
                            } catch (e: Exception) {
                            }
                        }
                )
        composite.add(disposable)
    }

    suspend fun getProductVariant(productID: Long, page: Int? = null): ICResponse<ICVariantProductStampV6_1> {
        val fields = HashMap<String, Any>()

        if (page != null) {
            fields["offset"] = page
        }
        fields["limit"] = APIConstants.LIMIT

        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.VARIANTPRODUCT().replace("{product_id}", productID.toString())
        return ICNetworkClient.getStampClient().getProductVariant(host, fields)
    }

    suspend fun getCustomerVariant(barcode: String): ICResponse<MutableList<ICFieldGuarantee>> {
        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.getCustomerVariant().replace("{code}", barcode)
        return ICNetworkClient.getStampClient().getGuaranteeVariant(host)
    }

    suspend fun getGuaranteeVariant(barcode: String): ICResponse<MutableList<ICFieldGuarantee>> {
        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.GETFIELDLISTGUARANTEE().replace("{code}", barcode)
        return ICNetworkClient.getStampClient().getGuaranteeVariant(host)
    }

    fun getFieldListGuarantee(codeStamp: String, listener: ICApiListener<ICResponse<MutableList<ICFieldGuarantee>>>) {
        val host = APIConstants.DETAIL_STAMP_HOST + APIConstants.GETFIELDLISTGUARANTEE().replace("{code}", codeStamp)
        val disposable = ICNetworkClient.getStampClient().getFieldListGuarantee(host)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            listener.onSuccess(it)
                        },
                        {
                            try {
                                val errorBody = ICBaseResponse()
                                errorBody.message = getString(R.string.co_loi_xay_ra_vui_long_thu_lai)

                                if (it is HttpException) {
                                    val error = parseJson(it.response()?.errorBody()?.string(), ICResponseErrorStamp::class.java)

                                    if (error != null) {
                                        if (!error.data?.message?.message.isNullOrEmpty()) {
                                            errorBody.message = error.data?.message?.message
                                        }
                                    }
                                }
                                listener.onError(errorBody)
                            } catch (e: Exception) {
                            }
                        }
                )
        composite.add(disposable)
    }
}