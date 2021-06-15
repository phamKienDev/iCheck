package vn.icheck.android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.util.ui.GlideUtil

class ImageSliderAdapter(var listImg: List<ImageChild>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_HEADER = 1
        const val TYPE_CCCN = 2
        const val TYPE_NULL = 3
    }

    var onClickListener: View.OnClickListener? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CCCN -> CccnHolder.create(parent)
            TYPE_HEADER -> ImageHolder.create(parent, listImg)
            else -> ImageHolder.create(parent, listImg)
        }
    }

    override fun getItemCount(): Int {
        return listImg.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (listImg.get(position).type) {
            TYPE_HEADER -> (holder as ImageHolder).bind(listImg[position].url)
            TYPE_CCCN -> (holder as CccnHolder).bind(listImg[position])
            TYPE_NULL -> (holder as ImageHolder).bind(listImg[position].url)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return listImg[position].type
    }

    class ImageHolder(view: View, val listImg: List<ImageChild>) : BaseHolder(view) {
        val image = view.findViewById<ImageView>(R.id.product_img)

        fun bind(url: String) {
            if (itemViewType == TYPE_NULL) {
                GlideUtil.bind(R.drawable.update_product_holder, image)
                setOnClick(image, View.OnClickListener {
                })
            } else {
                GlideUtil.loading(url, image)
                view.findViewById<ViewGroup>(R.id.root).setOnClickListener {
                    if (listImg.isNotEmpty()) {
                        val list = arrayListOf<String?>()
                        for (item in listImg) {
                            list.add(item.url)
                        }
                    }
                }
            }

        }

        companion object {
            fun create(parent: ViewGroup, listImg: List<ImageChild>): ImageHolder {
                return ImageHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.product_detail_img_holder, parent, false),
                        listImg)
            }
        }
    }

    class CccnHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.product_img)
        val imgDelete = view.findViewById<ImageView>(R.id.img_delete)

        fun bind(obj: ImageChild) {
            GlideUtil.loading(obj.url, image)

            if(obj.canDelete){
                imgDelete.visibility=View.VISIBLE
            }else{
                imgDelete.visibility=View.GONE
            }
        }

        companion object {
            fun create(parent: ViewGroup): CccnHolder {
                return CccnHolder(LayoutInflater.from(parent.context).inflate(R.layout.cccn_holder, parent, false))
            }
        }
    }

    class ImageChild(val url: String, val type: Int){
        var canDelete = false
    }
}
