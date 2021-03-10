package vn.icheck.android.screen.user.shopreview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.synthetic.main.item_shop_review_image.view.*
import vn.icheck.android.R
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.screen.user.shopreview.entity.ShopReviewImage
import vn.icheck.android.screen.user.shopreview.view.IOrderReviewView
import java.io.File

class ShopReviewAdapter(val bonus: Int, val listener: IOrderReviewView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ShopReviewImage>()

    fun addImage(obj: ShopReviewImage) {
        listData.add(obj)
        notifyDataSetChanged()
    }

    val getListData: MutableList<ShopReviewImage>
        get() {
            return listData
        }

    fun deleteImage(position: Int) {
        listData.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = listData.size + bonus

    override fun getItemViewType(position: Int): Int {
        return if (position < listData.size) {
            1
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == 1) {
            ImageHolder(layoutInflater.inflate(R.layout.item_shop_review_image, parent, false))
        } else {
            AddHolder(layoutInflater.inflate(R.layout.item_shop_review_image, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddHolder -> {
                holder.bind()

                holder.itemView.setOnClickListener {
                    listener.onAddImage()
                }
            }
            is ImageHolder -> {
                holder.bind(listData[position])

                holder.itemView.tvClose.setOnClickListener {
                    listener.onDeleteImage(position)
                }
            }
        }
    }

    private class AddHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            itemView.tvClose.visibility = View.INVISIBLE

            Glide.with(itemView.context.applicationContext)
                    .load(R.drawable.ic_shop_review_add_image_72dp)
                    .transform(FitCenter(), RoundedCorners(SizeHelper.size10))
                    .into(itemView.imgIcon)
        }
    }

    private class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(obj: ShopReviewImage) {
            itemView.tvClose.visibility = View.VISIBLE

            Glide.with(itemView.context.applicationContext)
                    .load(File(obj.filePath))
                    .transform(CenterCrop(), RoundedCorners(SizeHelper.size10))
                    .into(itemView.imgIcon)
        }
    }
}