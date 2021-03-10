package vn.icheck.android.screen.user.campaign.calback

import vn.icheck.android.network.models.ICAccount
import vn.icheck.android.network.models.ICAttachments
import vn.icheck.android.network.models.ICReview

interface IReviewListener {

    fun onReviewViewAllClicked()
    fun onReviewClicked(review: ICReview)
    fun onReviewAccountClicked(account: ICAccount)
    fun onReviewImageClicked(position: Int, list: List<ICAttachments>)
}