package vn.icheck.android.screen.user.orderdetail.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICOrderDetail

/**
 * Created by VuLCL on 1/31/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface IOrderDetailView : BaseActivityView {

    fun onGetIDError()

    fun onShowLoading()
    fun onCloseLoading()

    fun onGetDetailError(error: String)
    fun onDetailSuccess(obj: ICOrderDetail, list: MutableList<Int>)
    fun onTryAgainClicked()
    fun onActionClicked(status: Int)
    fun onTrackingClicked(link: String)
    fun onGoToShop(shopID: Long)

    fun onPaySuccess(url: String?)
    fun onCancelSuccess(orderID: Long)
    fun onCompleteSuccess(orderID: Long)

    fun onShowNotification(title: String?, message: String)
    fun onOrderAgainError()
    fun onOrderAgainSuccess()
}