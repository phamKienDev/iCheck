package vn.icheck.android.component.product.banner

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.network.models.ICAds
import vn.icheck.android.screen.user.campaign.calback.IBannerListener

data class BannerModel(
        val type: Int,
        var ads: ICAds?,
        val listAds: MutableList<ICAds>?,
        val listener: IBannerListener
) : ICViewModel {
    override fun getTag(): String = ""

    override fun getViewType(): Int = type
}