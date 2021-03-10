package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.bottom_sheet.view

import android.content.Context
import vn.icheck.android.room.entity.ICProvince

interface ISelectCityView {
    fun onSetListProvince(list: MutableList<ICProvince>)
    fun onMessageClicked()
    fun onItemClicked(item: ICProvince)
    fun showError(message:String?)
    val mContext: Context?
}