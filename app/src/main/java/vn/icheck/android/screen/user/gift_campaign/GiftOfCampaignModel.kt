package vn.icheck.android.screen.user.gift_campaign

import vn.icheck.android.network.models.campaign.ICGiftOfCampaign

data class GiftOfCampaignModel(val type: Int, val data: MutableList<ICGiftOfCampaign>, val title: String)