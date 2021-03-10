package vn.icheck.android.screen.user.detail_stamp_v5.select_store_stamp_v5.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.detail_stamp_v6.ICObjectStoreV6
import vn.icheck.android.network.models.detail_stamp_v6.ICStoreStampV6

/**
 * Created by PhongLH on 1/4/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
interface ISelectStoreStampV5View : BaseActivityView {
    fun onGetDataError(type: Int)
    fun onGetListStoreSuccess(obj: ICStoreStampV6)
    fun onClickItem(item: ICObjectStoreV6)
}