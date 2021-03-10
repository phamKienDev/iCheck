package vn.icheck.android.network.feature.news

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICNews
import java.util.*

class NewsInteractor : BaseInteractor() {

    fun getListNews(listener: ICApiListener<ICListResponse<ICNews>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = 0
        params["limit"] = 5

        requestApi(ICNetworkClient.getApiClient().getListNews(params), listener)
    }

    fun getListNewsSocial(path: String?, listener: ICNewApiListener<ICResponse<ICListResponse<ICNews>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getSocialApi().getListNewsSocial(url), listener)
    }

    fun getListNewsV2(listener: ICApiListener<ICListResponse<ICNews>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = 0
        params["limit"] = 4

        requestApi(ICNetworkClient.getApiClient().getListNews(params), listener)
    }

    fun getListNewsV2Social(listener: ICNewApiListener<ICResponse<ICListResponse<ICNews>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = 0
        params["limit"] = 4

        requestNewApi(ICNetworkClient.getSocialApi().getListNewsSocial(params), listener)
    }

    fun getNewsDetail(id: Long, listener: ICApiListener<ICNews>) {
        requestApi(ICNetworkClient.getApiClient().getNewsDetail(id), listener)
    }

    fun getNewsDetail(id: Long, listener: ICNewApiListener<ICResponse<ICNews>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getNewsDetailSocial(id), listener)
    }

    fun getListNews(offset: Int, limit: Int, listener: ICApiListener<ICListResponse<ICNews>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = limit

        requestApi(ICNetworkClient.getApiClient().getListNews(params), listener)
    }

    fun getListNews(offset: Int, limit: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICNews>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = limit

        requestNewApi(ICNetworkClient.getSocialApi().getListNewsSocial(params), listener)
    }
}
