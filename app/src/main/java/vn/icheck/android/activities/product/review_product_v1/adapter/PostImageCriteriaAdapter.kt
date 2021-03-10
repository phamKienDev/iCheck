package vn.icheck.android.activities.product.review_product_v1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_image_send_product_question.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.activities.product.review_product_v1.view.IReviewProductView
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.File

class PostImageCriteriaAdapter(val listener: IReviewProductView):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listData = mutableListOf<File>()

    fun setData(list: MutableList<File>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setItem(obj: File) {
        listData.add(obj)
        notifyDataSetChanged()
    }
    fun clearData(){
        listData.clear()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image_send_product_question, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size

    }

    fun deleteItem(position: Int) {
        listData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun deleteData() {
        listData.clear()
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ImageHolder
        holder.bind(listData[position])
    }

    inner class ImageHolder(view: View) : BaseViewHolder<File>(view) {
        override fun bind(obj: File) {
            WidgetUtils.loadImageFileRounded(itemView.imgImageMess, obj, SizeHelper.size10)

            itemView.imgCloseImage.visibility = View.VISIBLE
            itemView.imgCloseImage.setOnClickListener {
                listener.deletePhotoCriteria(adapterPosition)
            }
        }

    }
}