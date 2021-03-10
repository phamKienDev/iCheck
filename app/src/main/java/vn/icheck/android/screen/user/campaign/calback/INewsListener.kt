package vn.icheck.android.screen.user.campaign.calback

import vn.icheck.android.network.models.ICNews

interface INewsListener {

    fun onHomeNewsClicked(news: ICNews)
}