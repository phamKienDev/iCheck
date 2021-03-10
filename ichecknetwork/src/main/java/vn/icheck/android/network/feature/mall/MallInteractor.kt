package vn.icheck.android.network.feature.mall

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICShoppingCatalog

class MallInteractor : BaseInteractor() {

    fun getMallCatalog(path: String?, listener: ICNewApiListener<ICResponse<ICListResponse<ICShoppingCatalog>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getSocialApi().getListMallCatalog(url), listener)
    }
}