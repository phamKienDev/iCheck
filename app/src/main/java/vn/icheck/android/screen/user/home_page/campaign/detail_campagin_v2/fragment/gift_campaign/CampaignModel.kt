package vn.icheck.android.screen.user.home_page.campaign.detail_campagin_v2.fragment.gift_campaign

import vn.icheck.android.network.models.ICCampaign

class CampaignModel(
        val type: Int,
        val listData: MutableList<ICCampaign>?,
        var campaign: ICCampaign?
)