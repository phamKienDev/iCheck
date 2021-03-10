package vn.icheck.android.network.models

import vn.icheck.android.network.models.campaign.CampaignRewardItem

class ICGiftWaiting (
        val title:String,
        val count:Int,
        val products:MutableList<ICProduct>? = null
){
    var gifts = arrayListOf<CampaignRewardItem>()
}