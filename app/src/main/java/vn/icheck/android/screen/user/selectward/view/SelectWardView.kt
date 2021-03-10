package vn.icheck.android.screen.user.selectward.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICWard

/**
 * Created by VuLCL on 11/10/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface SelectWardView : BaseActivityView {

    fun onGetDataError()

    fun onShowLoading()
    fun onCloseLoading()

    fun onSetListWard(list: MutableList<ICWard>, isLoadMore: Boolean)
    fun onMessageClicked()
    fun onItemClicked(item: ICWard)
}

