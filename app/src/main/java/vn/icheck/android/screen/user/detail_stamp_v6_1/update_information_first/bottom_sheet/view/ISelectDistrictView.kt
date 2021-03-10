package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.bottom_sheet.view

import android.content.Context
import vn.icheck.android.room.entity.ICDistrict

interface ISelectDistrictView {
    fun onSetListDistrict(list: MutableList<ICDistrict>)
    fun onMessageClicked()
    fun onItemClicked(item: ICDistrict)
    fun showError(message:String?)
    val mContext: Context?
}