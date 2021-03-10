package vn.icheck.android.screen.user.viewimage.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICThumbnail

/**
 * Created by VuLCL on 11/14/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface IViewImageView : BaseActivityView {

    fun onGetDataError()
    fun onGetDataSuccess(list: MutableList<ICThumbnail>, position: Int)
}

