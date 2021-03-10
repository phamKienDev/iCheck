package vn.icheck.android.screen.user.mall.view

import androidx.lifecycle.LifecycleCoroutineScope
import vn.icheck.android.base.fragment.BaseFragmentView
import vn.icheck.android.network.models.ICCategory
import vn.icheck.android.screen.user.mall.ICMall

/**
 * Created by VuLCL on 9/27/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface IMallView : BaseFragmentView {

    val getLifecycleScope: LifecycleCoroutineScope
    fun showLoading()
    fun closeLoading()
    fun onGetDataError(icon: Int, errorMessage: String)

    fun onResetData()
    fun onGetListCategorySuccess(list: List<ICCategory>)
    fun onGetListBannerSuccess(obj: ICMall)
    fun onGetListBusinessSuccess(obj: ICMall)
    fun onGetListCampaign(list: MutableList<ICMall>)
}

