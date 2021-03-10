package vn.icheck.android.network.feature.ads

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response
import vn.icheck.android.network.BuildConfig
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.util.DeviceUtils
import vn.icheck.android.network.util.JsonHelper

/**
 * Created by tamdv on 6/2/18.
 */
class AdsRepository : BaseInteractor() {

    fun getAds(position: String, tags: String?, limit: Int?, productLimit: Int?, listener: ICApiListener<ICListResponse<ICAds>>) {
        val params = hashMapOf<String, Any>()
        params["position"] = position
        params["device_id"] = DeviceUtils.getUniqueDeviceId()

        if (tags != null) {
            params["tags"] = tags
        }

        if (limit != null) {
            params["limit"] = limit
        }

        if (productLimit != null) {
            params["products_limit"] = productLimit
        }

        requestApi(ICNetworkClient.getApiClient().getAds(params), listener)
    }

    fun getAdsDetail(id: Long, listener: ICApiListener<ICListResponse<ICAds>>) {
        val params = hashMapOf<String, Any>()
        params["ids"] = id
        params["device_id"] = DeviceUtils.getUniqueDeviceId()

        requestApi(ICNetworkClient.getApiClient().getListAds(params), listener)
    }

    fun hideAds(surveyID: Long, listener: ICApiListener<Response<Void>>) {
        val body = hashMapOf<String, String>()
        body["device_id"] = DeviceUtils.getUniqueDeviceId()
        requestApi(ICNetworkClient.getApiClient().hideAds(surveyID, body), listener)
    }

    fun answerAds(surveyID: Long, url: String, body: List<ICReqDirectSurvey>, listener: ICApiListener<ICNone>) {
        val json = JsonHelper.toJson(body)

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)

        requestApi(ICNetworkClient.getApiClient().answerAds(url, requestBody), object : ICApiListener<Response<Void>> {
            override fun onSuccess(obj: Response<Void>) {
                hideAds(surveyID, object : ICApiListener<Response<Void>> {
                    override fun onSuccess(obj: Response<Void>) {
                        listener.onSuccess(ICNone())
                    }

                    override fun onError(error: ICBaseResponse?) {
                        listener.onError(error)
                    }
                })
            }

            override fun onError(error: ICBaseResponse?) {
                listener.onError(error)
            }
        })
    }

    fun getAds(listener: ICNewApiListener<ICResponse<ICListResponse<ICAdsNew>>>) {
        val url =if(BuildConfig.FLAVOR.contentEquals("dev")) APIConstants.adsSocialHost + APIConstants.Ads.ADS_SOCIAL else APIConstants.adsSocialHost + APIConstants.PATH_CDN + APIConstants.Ads.ADS_SOCIAL
        requestNewApi(ICNetworkClient.getNewSocialApi().getAds(url), listener)
    }

    fun getSurvey(path: String?, listener: ICNewApiListener<ICResponse<ICListResponse<ICSurvey>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getSocialApi().getAdsSurvey(url), listener)
    }

    fun getCollection(path: String?, listener: ICNewApiListener<ICResponse<ICListResponse<ICCollection>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getSocialApi().getAdsCollection(url), listener)
    }

    fun getLayout(layoutName: String, listener: ICNewApiListener<ICLayoutData<ICNone>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getLayoutHome(layoutName), listener)
    }
}