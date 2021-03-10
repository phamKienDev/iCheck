package vn.icheck.android.base.dialog.notify.internal_stamp.view

interface IInternalStampView {
    fun onClickPhone(target: String?)
    fun onClickLink(target: String?, content: String?)
    fun onClickEmail(target: String?, content: String?)
    fun onClickSms(target: String?, content: String?)
    fun onClickGoToDetail(target: String?)
}