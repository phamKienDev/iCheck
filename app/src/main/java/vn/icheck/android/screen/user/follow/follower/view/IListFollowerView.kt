package vn.icheck.android.screen.user.follow.follower.view

import vn.icheck.android.base.fragment.BaseFragmentView
import vn.icheck.android.network.models.ICUserFollower

/**
 * Created by VuLCL on 11/14/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface IListFollowerView : BaseFragmentView {

    fun onGetDataError()
    fun onShowError(icon: Int, message: String)
    fun onGetListSuccess(list: MutableList<ICUserFollower>, isLoadMore: Boolean)
}

