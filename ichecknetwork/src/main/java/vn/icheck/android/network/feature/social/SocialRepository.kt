package vn.icheck.android.network.feature.social

import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.chat.DetailSticker
import vn.icheck.android.network.models.chat.StickerWrapper

class SocialRepository : BaseInteractor() {

    suspend fun getStickerPackages(): StickerWrapper {
        return ICNetworkClient.getSimpleChat().getStickerPackages()
    }

    suspend fun getStickers(id: String): DetailSticker {
        return ICNetworkClient.getSimpleChat().getStickers(id)
    }
}