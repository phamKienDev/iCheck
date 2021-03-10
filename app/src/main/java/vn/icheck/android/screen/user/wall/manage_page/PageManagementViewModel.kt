package vn.icheck.android.screen.user.wall.manage_page

import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.network.feature.page.PageRepository

class PageManagementViewModel : BaseViewModel() {
    private val interactor = PageRepository()

    suspend fun getMyFollowPage() = interactor.getMyFollowPageV2(null, 5, 0)
    suspend fun getMyOwnerPage() = interactor.getMyOwnerPageV2(null, 5, 0)
}