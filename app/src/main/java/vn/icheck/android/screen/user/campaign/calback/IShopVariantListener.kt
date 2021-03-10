package vn.icheck.android.screen.user.campaign.calback

import vn.icheck.android.network.models.detail_stamp_v6_1.ICShopVariantStamp

interface IShopVariantListener {

    fun onShopClicked(product: ICShopVariantStamp)

}