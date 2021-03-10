package vn.icheck.android.network.feature.review

import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICReview
import java.util.*

class ReviewInteractor : BaseInteractor() {

    fun getListReviewsUseful(listener: ICApiListener<ICListResponse<ICReview>>) {
        val queries = HashMap<String, Any>()
        queries["skip_hidden"] = true
        queries["limit"] = 5

        requestApi(ICNetworkClient.getApiClient().getListReviewsUseful(queries), listener)
    }

}