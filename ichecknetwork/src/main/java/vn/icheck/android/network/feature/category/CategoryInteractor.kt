package vn.icheck.android.network.feature.category

import java.util.HashMap

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICCategory

class CategoryInteractor : BaseInteractor() {

    fun getListCategories(listener: ICApiListener<ICListResponse<ICCategory>>) {
        val params = HashMap<String, Any>()
        params["root"] = 1
        params["empty_product"] = 0

        requestApi(ICNetworkClient.getApiClient().getListCategories(params), listener)

        //        ICNetworkClient.getApiClient().getListCategories(queries)
        //                .subscribeOn(Schedulers.io())
        //                .observeOn(AndroidSchedulers.mainThread())
        //                .subscribe(
        //                        response -> {
        //                            apiCall.call(new ICNetworkListResponse<ICCategory>());
        ////                            delaySuccess(apiCall, response);
        //                        },
        //                        throwable -> {
        //                            throwable.printStackTrace();
        //                            delayError(apiCall, RetrofitErrorUtils.getError(throwable));
        //                        }
        //                );
    }

    fun getListCategoriesChild(id: Long, listener: ICApiListener<ICListResponse<ICCategory>>) {
        val params = HashMap<String, Any>()
        params["parent_id"] = id

        requestApi(ICNetworkClient.getApiClient().getListCategories(params), listener)
    }
}
