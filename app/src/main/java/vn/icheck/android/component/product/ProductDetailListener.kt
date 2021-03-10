package vn.icheck.android.component.product

import vn.icheck.android.component.commentpost.ICCommentPostMore
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.network.models.ICProductQuestion

interface ProductDetailListener {
    fun clickGoQa(idProduct: ICCommentPostMore?)
    fun clickToContributionInfo(barcode:String)
    fun clickCancelContribution(position:Int)
    fun shareProduct(id: Long)
    fun clickBottomContact(item: ICClientSetting, position: Int)
    fun clickAnswersInQuestion(obj: ICProductQuestion)
}