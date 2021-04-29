package vn.icheck.android.screen.user.winner_campaign

import vn.icheck.android.network.models.ICCampaign

class CampaignModel(
        val type: Int,
        val listData: MutableList<ICCampaign>?,
        var campaign: ICCampaign?
)