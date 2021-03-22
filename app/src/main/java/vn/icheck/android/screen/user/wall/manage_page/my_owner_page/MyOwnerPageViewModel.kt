package vn.icheck.android.screen.user.wall.manage_page.my_owner_page

import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.feature.page.PageRepository

class MyOwnerPageViewModel : BaseViewModel() {
    private val interactor = PageRepository()

    suspend fun getData(filter: String, offset: Int) = request { interactor.getMyOwnerPageV2(filter, APIConstants.LIMIT, offset) }
}