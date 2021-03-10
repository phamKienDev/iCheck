package vn.icheck.android.screen.user.campaign.calback

import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.network.models.ICReqDirectSurvey

interface IProductListener {

    fun onProductClicked(product: ICProduct)
}