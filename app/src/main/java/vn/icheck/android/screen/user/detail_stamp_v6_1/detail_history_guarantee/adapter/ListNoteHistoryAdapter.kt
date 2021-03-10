package vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_note_history_guarantee.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICResp_Note_Guarantee
import vn.icheck.android.screen.user.view_item_image_stamp.ViewItemImageActivity
import vn.icheck.android.util.kotlin.WidgetUtils

class ListNoteHistoryAdapter(val context: Context?) : RecyclerView.Adapter<ListNoteHistoryAdapter.ViewHolder>() {

    private val listData = mutableListOf<ICResp_Note_Guarantee.ObjectLog.ObjectChildLog.ICItemNote>()

    fun setListData(list: MutableList<ICResp_Note_Guarantee.ObjectLog.ObjectChildLog.ICItemNote>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_note_history_guarantee, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ICResp_Note_Guarantee.ObjectLog.ObjectChildLog.ICItemNote) {
            itemView.tvDate.text = TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.created_at)
            itemView.tvTitle.text = item.note

            if (!item.images.isNullOrEmpty()) {
                itemView.layoutImage.removeAllViews()
                for (i in item.images ?: mutableListOf()) {
                    itemView.layoutImage.addView(AppCompatImageView(itemView.context).also { itemImage ->
                        val paramsImage = LinearLayout.LayoutParams(SizeHelper.size40, SizeHelper.size40)
                        paramsImage.setMargins(0, 0, SizeHelper.size8, 0)
                        itemImage.layoutParams = paramsImage
                        itemImage.scaleType = ImageView.ScaleType.FIT_CENTER
                        WidgetUtils.loadImageUrlRounded(itemImage, i, SizeHelper.size4)

                        itemImage.setOnClickListener {
                            val intent = Intent(itemView.context, ViewItemImageActivity::class.java)
                            intent.putExtra(Constant.DATA_1, i)
                            itemView.context.startActivity(intent)
                        }
                    })
                }
            }

        }
    }
}