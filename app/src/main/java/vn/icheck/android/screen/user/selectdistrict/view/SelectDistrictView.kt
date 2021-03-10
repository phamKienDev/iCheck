package vn.icheck.android.screen.user.selectdistrict.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICDistrict

/**
 * Created by VuLCL on 11/10/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface SelectDistrictView : BaseActivityView {

    fun onGetDataError()
    fun onShowLoading()
    fun onCloseLoading()
    fun onSetListDistrict(list: MutableList<ICDistrict>, isLoadMore: Boolean)
    fun onMessageClicked()
    fun onItemClicked(item: ICDistrict)
}

