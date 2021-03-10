package vn.icheck.android.screen.user.selectprovince.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICProvince

/**
 * Created by VuLCL on 11/10/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface SelectProvinceView : BaseActivityView {

    fun onShowLoading()
    fun onCloseLoading()
    fun onSetListProvince(list: MutableList<ICProvince>, isLoadMore: Boolean)
    fun onMessageClicked()
    fun onItemClicked(item: ICProvince)
}

