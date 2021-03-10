package vn.icheck.android.screen.user.detail_stamp_v6_1.contact_support

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICSupport

/**
 * Created by PhongLH on 3/31/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
interface IContactSupportView : BaseActivityView {
    fun onGetListSupportSuccess(listSupport: MutableList<ICSupport>?)
    fun setItemClick(item: ICSupport)
}