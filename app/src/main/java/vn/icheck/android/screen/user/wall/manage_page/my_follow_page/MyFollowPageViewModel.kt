package vn.icheck.android.screen.user.wall.manage_page.my_follow_page

import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.feature.page.PageRepository

class MyFollowPageViewModel : BaseViewModel() {
    private val interactor = PageRepository()

    suspend fun getData(key: String, offset: Int) = request {
        interactor.getMyFollowPageV2(key, APIConstants.LIMIT, offset)
    }

}