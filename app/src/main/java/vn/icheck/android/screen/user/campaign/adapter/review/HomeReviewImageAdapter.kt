package vn.icheck.android.screen.user.campaign.adapter.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_home_review_image.view.*
import vn.icheck.android.R
import vn.icheck.android.network.models.ICAttachments
import vn.icheck.android.screen.user.campaign.calback.IReviewListener
import vn.icheck.android.util.kotlin.WidgetUtils

class HomeReviewImageAdapter(private val listData: List<ICAttachments>, private val reviewListener: IReviewListener?) : RecyclerView.Adapter<HomeReviewImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_review_image, parent, false))
    }

    override fun getItemCount(): Int {
        return if (listData.size > 3) {
            3
        } else {
            listData.size
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            listData[adapterPosition].let { attachments ->
                WidgetUtils.loadImageUrlRounded4(itemView.imgProduct, attachments.thumbnails?.small)
            }

            itemView.txtProductCount.visibility = if (listData.size > 3) {
                if (adapterPosition == 3) {
                    itemView.txtProductCount.text = (listData.size - 3).toString()
                    View.VISIBLE
                } else {
                    View.GONE
                }
            } else {
                View.GONE
            }
        }

        fun initListener() {
            itemView.setOnClickListener {
                reviewListener?.onReviewImageClicked(adapterPosition, listData)
            }
        }
    }
}