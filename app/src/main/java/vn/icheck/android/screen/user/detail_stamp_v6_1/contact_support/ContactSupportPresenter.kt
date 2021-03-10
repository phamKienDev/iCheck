package vn.icheck.android.screen.user.detail_stamp_v6_1.contact_support

import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.network.base.SettingManager

/**
 * Created by PhongLH on 3/31/2020.
 * Phone: 0912651881
 * Email: phonglh@icheck.vn
 */
class ContactSupportPresenter(val view: IContactSupportView) : BaseActivityPresenter(view) {
    fun getListContact() {
        val listSupport = SettingManager.clientSetting?.supports
        view.onGetListSupportSuccess(listSupport)
    }
}