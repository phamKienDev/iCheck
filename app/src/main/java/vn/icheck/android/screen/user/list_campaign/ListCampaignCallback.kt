package vn.icheck.android.screen.user.list_campaign

import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.network.models.ICCampaign

interface ListCampaignCallback: IRecyclerViewCallback {
    fun clickGift(objCampaign: ICCampaign)
}