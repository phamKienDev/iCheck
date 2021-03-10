package vn.icheck.android.screen.user.scan_history.view

import vn.icheck.android.network.models.history.ICBigCorp

interface IScanHistoryView {
    fun onLoadmore()
    fun onClickBigCorp(item: ICBigCorp)
    fun onMessageErrorMenu()
    fun unCheckAllFilterHistory()
    fun onCloseDrawer()
    fun onValidStamp(item: String?)
    fun onClickQrType(item: String?)
}