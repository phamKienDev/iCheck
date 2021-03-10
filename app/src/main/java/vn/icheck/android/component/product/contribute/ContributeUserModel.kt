package vn.icheck.android.component.product.contribute

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICBarcodeProductV2

class ContributeUserModel(var contributions: ICBarcodeProductV2.Contributions?, val contributionCallback: ContributionCallback):ICViewModel {

    override fun getTag(): String {
        return ICViewTags.CONTRIBUTE_MODULE
    }

    override fun getViewType(): Int {
        return ICViewTypes.CONTRIBUTE_USER
    }
}

interface ContributionCallback{
    fun upvote(contributions: ICBarcodeProductV2.Contributions?, type: Int)
    fun downvote(contributions: ICBarcodeProductV2.Contributions?)
    fun showUser(contributions: ICBarcodeProductV2.Contributions?)
}