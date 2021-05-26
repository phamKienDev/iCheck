package vn.icheck.android.network.feature.search

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.*

class SearchInteractor : BaseInteractor() {

    suspend fun getDataProduct(limit: Int, offset: Int, nameCode: String?, isVerified: Int = 0, order: String?, ratings: String?): ICResponse<ICListResponse<ICProductTrend>> {
        val query = hashMapOf<String, Any>()
        query["limit"] = limit
        query["offset"] = offset

        if (!nameCode.isNullOrEmpty()) {
            query["nameCode"] = nameCode
        }

        if (isVerified != 0) {
            query["verified"] = isVerified
        }
        if (!order.isNullOrEmpty()) {
            query["order"] = order
        }
        if (!ratings.isNullOrEmpty()) {
            query["ratings"] = ratings
        }

        return ICNetworkClient.getSocialApi().getProductSearch(query)
    }

    suspend fun getDataReview(limit: Int, offset: Int, search: String?, watcheds: Boolean, years: String?, fromObjectTypes: String?): ICResponse<ICListResponse<ICPost>> {
        val query = hashMapOf<String, Any>()
        query["limit"] = limit
        query["offset"] = offset
        if (!search.isNullOrEmpty()) {
            query["search"] = search
        }

        if (watcheds) {
            query["viewed"] = watcheds
        }
        if (!years.isNullOrEmpty()) {
            query["years"] = years
        }
        if (!fromObjectTypes.isNullOrEmpty()) {
            query["fromObjectTypes"] = fromObjectTypes
        }

        return ICNetworkClient.getSocialApi().getReviewSearch(query)
    }

    suspend fun getDataPage(limit: Int?, offset: Int?, filterString: String?, isVerified: Boolean, city: MutableList<Long>?, category: Long?): ICResponse<ICListResponse<ICPageQuery>> {
        val query = hashMapOf<String, Any>()

        if (limit != null)
            query["limit"] = limit

        if (offset != null)
            query["offset"] = offset

        if (!filterString.isNullOrEmpty()) {
            query["filterString"] = filterString
        }

        if (isVerified) {
            query["isVerify"] = isVerified
        }
        if (category != null) {
            query["categoryId"] = category
        }
        if (!city.isNullOrEmpty()) {
            query["cityIds"] = city
        }

        return ICNetworkClient.getSocialApi().getPageSearch(query)
    }

    fun getDataShop(limit: Int, offset: Int, filterString: String?, isVerified: Boolean = false, city: MutableList<Long>?, category: Long?, listener: ICNewApiListener<ICResponse<ICListResponse<ICShopQuery>>>) {
        val query = hashMapOf<String, Any>()
        query["limit"] = limit
        query["offset"] = offset
        if (!filterString.isNullOrEmpty()) {
            query["filterString"] = filterString
        }
        if (isVerified) {
            query["isVerify"] = isVerified
        }
        if (category != null) {
            val listCate = mutableListOf<Long>()
            listCate.add(category)
            query["categoryIds"] = listCate
        }
        if (!city.isNullOrEmpty()) {
            query["cityIds"] = city
        }
        query["objectType"] = "shop"
        requestNewApi(ICNetworkClient.getSocialApi().getShopSearch(query), listener)
    }

    fun getCategoryParent(offset: Int, filterString: String?, level: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICCategorySearch>>>) {
        val query = hashMapOf<String, Any>()
        query["limit"] = APIConstants.LIMIT
        query["offset"] = offset
        if (!filterString.isNullOrEmpty()) {
            query["filterString"] = filterString
        }
        query["level"] = level
        requestNewApi(ICNetworkClient.getSocialApi().getCategoryParent(query), listener)
    }

    fun getCategoryChildren(offset: Int, filterString: String?, parentId: Long, listener: ICNewApiListener<ICResponse<ICListResponse<ICCategorySearch>>>) {
        val query = hashMapOf<String, Any>()
        query["limit"] = APIConstants.LIMIT
        query["offset"] = offset
        if (!filterString.isNullOrEmpty()) {
            query["filterString"] = filterString
        }
        query["parentId"] = parentId
        requestNewApi(ICNetworkClient.getSocialApi().getCategoryChildren(query), listener)
    }

    fun getPopularSearch(listener: ICNewApiListener<ICResponse<ICListResponse<ICCategorySearch>>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getPopularSearch(), listener)
    }

    fun getAutoSearch(key: String?, listener: ICNewApiListener<ICResponse<ICListResponse<String?>>>) {
        if (!key.isNullOrEmpty()) {
            requestNewApi(ICNetworkClient.getSocialApi().getAutoSearch(key), listener)
        }
    }

    fun getRecentSearch(listener: ICNewApiListener<ICResponse<ICListResponse<ICCategorySearch>>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getRecentSearch(), listener)
    }

    fun deleteRecentSearch(listener: ICNewApiListener<ICResponse<Boolean>>) {
        val body = hashMapOf<String, Any>()
        body[""] = ""

        requestNewApi(ICNetworkClient.getSocialApi().deleteRecentSearch(body), listener)
    }
}