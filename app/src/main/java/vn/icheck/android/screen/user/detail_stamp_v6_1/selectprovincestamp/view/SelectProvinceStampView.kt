package vn.icheck.android.screen.user.selectprovincestamp.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6_1.CitiesItem

/**
 * Created by VuLCL on 11/10/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface SelectProvinceStampView : BaseActivityView {

    fun onShowLoading()
    fun onCloseLoading()
    fun onSetListProvince(list: MutableList<CitiesItem>?)
    fun onMessageClicked()
    fun onItemClicked(item: CitiesItem)
}

