package vn.icheck.android.screen.user.contribute_product.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import vn.icheck.android.R
import vn.icheck.android.constant.*
import vn.icheck.android.constant.ADD_IMAGE
import vn.icheck.android.screen.user.contribute_product.viewmodel.ImageModel
import vn.icheck.android.ui.RoundedCornersTransformation
import vn.icheck.android.util.DimensionUtil
import vn.icheck.android.util.ick.loadSimpleFile
import java.io.File

class ListImageAdapter(var listData:List<ImageModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var type:Int = 0 // Main == 0, Holder == 1, SINGLE = 2
    var parentPosition = 0
    var lastClickTime = System.currentTimeMillis()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val lf = LayoutInflater.from(parent.context)
        val v = lf.inflate(R.layout.item_add_more_image, parent, false)
        return ImageHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val imageHolder = holder as ImageHolder
        val img = imageHolder.view.findViewById<ImageView>(R.id.img)
        val delete = imageHolder.view.findViewById<ImageView>(R.id.img_delete)
        if (listData.get(position).file != null) {
            delete.visibility = View.VISIBLE
            img.loadSimpleFile(listData.get(position).file, 4)
            delete.setOnClickListener {
                if (type == 0) {
                    holder.view.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                        putExtra(CONTRIBUTIONS_ACTION, REMOVE_IMAGE)
                        putExtra(REMOVE_IMAGE, holder.bindingAdapterPosition)
                    })
                } else {
                    holder.view.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                        putExtra(CONTRIBUTIONS_ACTION, REMOVE_IMAGE_HOLDER)
                        putExtra(PARENT_POSITION, parentPosition)
                        putExtra(REMOVE_IMAGE_HOLDER, holder.bindingAdapterPosition)
                    })
                }
            }
        } else {
            delete.visibility = View.INVISIBLE
            Glide.with(img.context.applicationContext)
                    .load(R.drawable.ick_add_more)
                    .into(img)
            img.setOnClickListener {
                if (System.currentTimeMillis() - lastClickTime < 500) {
                    return@setOnClickListener
                }
                lastClickTime = System.currentTimeMillis()
                if (type == 1) {
                    if (listData.get(position).file == null) {
                        holder.view.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                            putExtra(CONTRIBUTIONS_ACTION, ADD_IMAGE_HOLDER)
                            putExtra(PARENT_POSITION, parentPosition)
                            putExtra(ADD_IMAGE_HOLDER, holder.bindingAdapterPosition)
                        })
                    }
                } else {
                    holder.view.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                        putExtra(CONTRIBUTIONS_ACTION, ADD_IMAGE)
                    })
                }
//                else {
//                    holder.view.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
//                        putExtra(CONTRIBUTIONS_ACTION, ADD_IMAGE_HOLDER)
//                        putExtra(PARENT_POSITION, parentPosition)
//                        putExtra(ADD_IMAGE_HOLDER, holder.bindingAdapterPosition)
//                    })
//                }

            }
        }
    }

    override fun getItemCount() :Int{
        return listData.size
    }

    class ImageHolder(val view: View) : RecyclerView.ViewHolder(view)

}