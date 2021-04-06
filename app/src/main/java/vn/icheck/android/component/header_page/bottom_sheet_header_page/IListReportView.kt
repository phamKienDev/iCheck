package vn.icheck.android.component.header_page.bottom_sheet_header_page

import vn.icheck.android.network.models.ICMediaPage
import vn.icheck.android.network.models.ICPageOverview

interface IListReportView {
    fun onShowReportForm()
    fun onRequireLogin()
    fun followAndUnFollowPage(obj: ICPageOverview)
    fun unSubcribeNotification(obj: ICPageOverview)
    fun subcribeNotification(obj: ICPageOverview)
    fun onClickImage(item: ICMediaPage)
}