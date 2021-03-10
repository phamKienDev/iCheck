package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_campaign.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.model.RowsItem
import java.text.SimpleDateFormat
import java.util.*

class GameListAdapter(val gameItemClickListener: GameItemClickListener) : ListAdapter<RowsItem, RecyclerView.ViewHolder>(DIFF_UTIL) {
    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<RowsItem>() {
            override fun areItemsTheSame(oldItem: RowsItem, newItem: RowsItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RowsItem, newItem: RowsItem): Boolean {
                return oldItem.play == newItem.play
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GameItemHolder.create(parent, gameItemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as GameItemHolder).bind(getItem(position))
    }

    class GameItemHolder(val view: View, val gameItemClickListener: GameItemClickListener) : RecyclerView.ViewHolder(view) {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val show = SimpleDateFormat("dd/MM", Locale.getDefault())
        fun bind(gameListData: RowsItem) {
//            Glide.with(view)
//                    .load(gameListData.campaign?.image?.original)
//                    .into(view.imgBanner)
//
////            Glide.with(view)
////                    .load(ImageHelper.getImageUrl(gameListData.campaign?.owner?.logo?.small, ImageHelper.thumbSmallSize))
////                    .into(view.imgAvatar)
//            val startDate = sdf.parse(gameListData.campaign?.startAt)
//            val endDate = sdf.parse(gameListData.campaign?.endAt)
//            val timeString = "${show.format(startDate)} - ${show.format(endDate)}"
//            view.txtDate.text = timeString
//            if (gameListData.campaign?.statusTime != "RUNNING") {
//                view.linearLayout.visibility = View.GONE
//                view.imgUpcoming.visibility = View.VISIBLE
//            } else {
////                view.linearLayout.setBackgroundResource(R.drawable.bg_border_bottom_left_top_right_green_50)
//                view.tvCheck.text = "ĐANG THAM GIA"
//                view.imgUpcoming.visibility = View.GONE
//                view.linearLayout.visibility = View.VISIBLE
//            }
//            try {
//                if (gameListData.play!! > 0) {
//                    view.tv_number.setTextColor(Color.parseColor("#057DDA"))
//                    view.tv_number.text = "${gameListData.play} lượt quay"
//                } else {
//                    view.tv_number.setTextColor(Color.parseColor("#828282"))
//                    view.tv_number.text = "Hết lượt quay"
//                }
//                view.cardView.setOnClickListener {
//                    gameItemClickListener.onItemClick(gameListData)
//                }
//                view.cardView1.setOnClickListener {
//                    gameItemClickListener.onItemClick(gameListData)
//                }
//            } catch (e: Exception) {
//
//            }

        }

        companion object {
            fun create(parent: ViewGroup, gameItemClickListener: GameItemClickListener): GameItemHolder {
                val lf = LayoutInflater.from(parent.context)
                val v = lf.inflate(R.layout.item_campaign, parent, false)
                return GameItemHolder(v, gameItemClickListener)
            }
        }
    }

    interface GameItemClickListener {
        fun onItemClick(rowsItem: RowsItem)
    }
}