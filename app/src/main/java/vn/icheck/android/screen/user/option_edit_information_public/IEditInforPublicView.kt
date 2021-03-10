package vn.icheck.android.screen.user.option_edit_information_public

import vn.icheck.android.network.models.wall.ICUserPublicInfor

interface IEditInforPublicView {
    fun onClick(item: ICUserPublicInfor, checked: Boolean, position: Int)
}