package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.ctsp_show_more_comment.view.*
import vn.icheck.android.R
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.models.ICComments
import vn.icheck.android.network.models.ICProductReviews
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.model.ICStampItem
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.model.ICViewType
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText

class ReviewsMoreCommentHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ctsp_show_more_comment, parent, false)) {

    fun bind(obj: ICProductReviews.ReviewsRow, listener: ItemClickListener<MutableList<ICStampItem>>) {
        itemView.tv_more.setText(R.string.xem_them_x_tra_loi_khac, obj.commentCounts - 1)

        itemView.tv_more.setOnClickListener {
            val host = APIConstants.defaultHost + APIConstants.CRITERIALISTCOMMENT().replace("{review_id}", obj.id.toString())
            ICNetworkClient.getApiClient()
                    .getComments(host)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<ICComments> {
                        override fun onSuccess(list: ICComments) {
                            val listData = mutableListOf<ICStampItem>()

                            for (item in list.comments) {
                                listData.add(ICStampItem(ICViewType.REVIEWS_COMMENT, item))
                            }

                            listener.onItemClick(adapterPosition, listData)
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onError(e: Throwable) {
                        }
                    })
        }
    }
}