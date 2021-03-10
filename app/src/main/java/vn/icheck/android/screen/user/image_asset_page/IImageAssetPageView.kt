package vn.icheck.android.screen.user.image_asset_page

import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICMediaPage

interface IImageAssetPageView {
    fun onLoadMore()
    fun onRefresh()
    fun onClickImage(item: ICMediaPage)
}