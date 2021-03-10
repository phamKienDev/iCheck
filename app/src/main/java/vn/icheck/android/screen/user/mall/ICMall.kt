package vn.icheck.android.screen.user.mall

import vn.icheck.android.component.ICComponent
import vn.icheck.android.network.models.*
import vn.icheck.android.screen.user.campaign.entity.ICCampaignHeader

data class ICMall(
        var business: MutableList<ICBusiness>? = null
) : ICComponent()