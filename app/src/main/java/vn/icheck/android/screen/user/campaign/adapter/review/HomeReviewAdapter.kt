package vn.icheck.android.screen.user.campaign.adapter.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_home_review.view.*
import vn.icheck.android.R
import vn.icheck.android.network.models.ICReview
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.screen.user.campaign.calback.IReviewListener
import vn.icheck.android.util.kotlin.WidgetUtils

class HomeReviewAdapter(private val listData: List<ICReview>, private val reviewListener: IReviewListener?) : RecyclerView.Adapter<HomeReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_review, parent, false))
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(review: ICReview) {
            if (review.product != null) {
                itemView.layoutProduct.visibility = View.VISIBLE

                WidgetUtils.loadImageUrlRounded6(itemView.imgProduct, review.product?.thumbnails?.small)
                itemView.txtProductName.text = review.product?.name
                itemView.tvProductPrice.text = (TextHelper.formatMoney(review.product?.price) + " đ")
            } else {
                itemView.layoutProduct.visibility = View.GONE
            }

            review.account?.let {
                WidgetUtils.loadImageUrl(itemView.imgAvatar, it.avatar_thumbnails?.small)
                itemView.tvName.text = it.name
            }

            itemView.txtDate.text = TimeHelper.convertDateTimeSvToDateTimeVn(review.updated_at)
            itemView.ratingBar.rating = review.rating.toFloat()
            itemView.txtContent.text = review.content

            val attachments = review.attachments

            if (attachments != null && attachments.isNotEmpty()) {
                itemView.layoutImage.visibility = View.VISIBLE

                WidgetUtils.loadImageUrlRounded4(itemView.img1, attachments[0].thumbnails?.small)

                if (attachments.size > 1) {
                    WidgetUtils.loadImageUrlRounded4(itemView.img2, attachments[1].thumbnails?.small)

                    if (attachments.size > 2) {
                        WidgetUtils.loadImageUrlRounded4(itemView.img2, attachments[1].thumbnails?.small)

                        if (attachments.size > 3) {
                            itemView.txtProductCount.visibility = View.VISIBLE
                            itemView.txtProductCount.text = ("+${(attachments.size - 3)}")
                        } else {
                            itemView.txtProductCount.visibility = View.GONE
                        }
                    } else {
                        itemView.img3.visibility = View.INVISIBLE
                        itemView.txtProductCount.visibility = View.GONE
                    }
                } else {
                    itemView.img2.visibility = View.INVISIBLE
                    itemView.img3.visibility = View.INVISIBLE
                }
            } else {
                itemView.layoutImage.visibility = View.GONE
            }
        }

        fun initListener() {
            itemView.setOnClickListener {
                listData[adapterPosition].let { useful ->
                    reviewListener?.onReviewClicked(useful)
                }
            }

            itemView.layoutAccount.setOnClickListener {
                listData[adapterPosition].account?.let { account ->
                    reviewListener?.onReviewAccountClicked(account)
                }
            }

            itemView.img1.setOnClickListener {

            }

            itemView.img2.setOnClickListener {

            }

            itemView.img3.setOnClickListener {

            }

            itemView.txtProductCount.setOnClickListener {

            }
        }

//        private fun updateListener() {
//            if (itemView.recyclerView.visibility == View.GONE)
//                return
//
//            val first = (itemView.recyclerView.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
//            val last = (itemView.recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
//
//            for (i in last downTo first) {
//                itemView.recyclerView.findViewHolderForAdapterPosition(i)?.let {
//                    if (it is HomeReviewImageAdapter.ViewHolder) {
//                        it.initListener()
//                    }
//                }
//            }
//        }
    }
}