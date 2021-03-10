package vn.icheck.android.screen.user.follow.following.view

import vn.icheck.android.base.fragment.BaseFragmentView
import vn.icheck.android.network.models.ICUserFollowing

/**
 * Created by VuLCL on 11/14/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface IListFollowingView : BaseFragmentView {

    fun onGetDataError()
    fun onShowError(icon: Int, message: String)
    fun onGetListSuccess(list: MutableList<ICUserFollowing>, isLoadMore: Boolean)
}

