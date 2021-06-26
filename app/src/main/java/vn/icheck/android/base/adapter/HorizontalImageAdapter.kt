package vn.icheck.android.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_image_product_question.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.util.kotlin.WidgetUtils

class HorizontalImageAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<String>()
    fun setData(list: MutableList<String>) {
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image_product_question, parent, false))
    }

    override fun getItemCount(): Int {
        return if (listData.size >= 3) {
            3
        } else {
            listData.size
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ImageHolder
        holder.bind(listData[position])
    }

    inner class ImageHolder(view: View) : BaseViewHolder<String>(view) {
        override fun bind(obj: String) {

            WidgetUtils.loadImageUrlRounded10FitCenter(itemView.imgImageMess, obj)


            if (adapterPosition == 2) {
                if (listData.size - 3 > 0) {
                    itemView.bgCover.visibility = View.VISIBLE
                    itemView.tvCoundImage.visibility = View.VISIBLE
                    itemView.tvCoundImage.apply {
                        text = context.getString(R.string.format_plus_d, listData.size - 3)
                    }
                } else {
                    itemView.bgCover.visibility = View.GONE
                    itemView.tvCoundImage.visibility = View.GONE
                }
            } else {
                itemView.bgCover.visibility = View.GONE
                itemView.tvCoundImage.visibility = View.GONE
            }
            val listUrl = arrayListOf<String?>()
            for (url in listData) {
                listUrl.add(url)
            }

            itemView.setOnClickListener {
                if (listUrl.isNotEmpty()) {
                    DetailMediaActivity.start(itemView.context,listUrl)
                }

            }
        }

    }
}