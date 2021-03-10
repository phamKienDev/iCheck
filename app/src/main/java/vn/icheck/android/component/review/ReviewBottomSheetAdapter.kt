package vn.icheck.android.component.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willy.ratingbar.BaseRatingBar
import kotlinx.android.synthetic.main.item_review_bottom_sheet.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.network.models.ICCriteriaReview

class ReviewBottomSheetAdapter(val listData: List<ICCriteriaReview>, val isClick: Boolean = false) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewBottomSheetViewHolder {
        return ReviewBottomSheetViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_review_bottom_sheet, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ReviewBottomSheetViewHolder).bind(listData[position])
    }


    inner class ReviewBottomSheetViewHolder(view: View) : BaseViewHolder<ICCriteriaReview>(view) {
        override fun bind(obj: ICCriteriaReview) {
            if (!obj.name.isNullOrEmpty()) {
                itemView.tvVote.text = obj.name
            } else {
                itemView.tvVote.text = obj.criteriaName
            }

            itemView.ratingReview.rating = obj.point

            itemView.ratingReview.isClickable = isClick
            if (isClick) {
                itemView.ratingReview.setOnRatingChangeListener(object : BaseRatingBar.OnRatingChangeListener {
                    override fun onRatingChange(ratingBar: BaseRatingBar?, rating: Float, fromUser: Boolean) {
                        listData[adapterPosition].point = itemView.ratingReview.rating
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.CLICK_START_REVIEW))
                    }
                })
            }
        }
    }
}