package vn.icheck.android.screen.user.campaign.calback

/**
 * Created by VuLCL on 11/14/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface IRecyclerViewAdapterListener<T> {

    fun onLoadMore()
    fun onItemClicked(obj: T)
    fun onMessageClicked()
}

