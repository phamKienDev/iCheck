package vn.icheck.android.screen.user.selectdistrict.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6_1.DistrictsItem

/**
 * Created by VuLCL on 11/10/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface SelectDistrictStampView : BaseActivityView {

    fun onGetDataError()
    fun onShowLoading()
    fun onCloseLoading()
    fun onSetListDistrict(list: MutableList<DistrictsItem>?)
    fun onMessageClicked()
    fun onItemClicked(item: DistrictsItem)
}

