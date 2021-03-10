package vn.icheck.android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.willy.ratingbar.ScaleRatingBar
import vn.icheck.android.R
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.util.ui.GlideUtil

class RelatedProductAdapter( val list: List<ICProduct>): RecyclerView.Adapter<RelatedProductAdapter.RelatedProductHolder>() {

    var callback: OnProductClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatedProductHolder {
        return RelatedProductHolder(LayoutInflater.from(parent.context).inflate(R.layout.child_product_item, parent, false))
    }

    override fun getItemCount(): Int {
        if(list.size > 10 ) return 10
        return list.size
    }

    override fun onBindViewHolder(holder: RelatedProductHolder, position: Int) {
        val child = list.get(position)
        if (position == 9) {
            GlideUtil.bind(R.drawable.group_more_product, holder.img)
            Glide.with(holder.itemView.context.applicationContext).load(R.drawable.group_more_product).centerCrop()
                    .apply(RequestOptions().transform(RoundedCorners(50))).into(holder.img)
            holder.root.setOnClickListener {
                callback?.onMoreProduct()
            }
            holder.price.visibility = View.GONE
            holder.verified.visibility = View.GONE
            holder.rating.visibility = View.GONE
            holder.tvName.visibility = View.GONE
        } else {
            GlideUtil.loading(child.thumbnails?.small, holder.img)
            if (!child.name.isNullOrEmpty()) {
                holder.tvName.text = child.name
            }
            holder.rating.rating = child.rating
            if (child.price > 0) {
                holder.price.text = String.format("%,dđ", child.price)
            } else {
                holder.price.visibility = View.INVISIBLE
            }
            holder.price.visibility = View.VISIBLE
            holder.verified.visibility = View.VISIBLE
            holder.rating.visibility = View.VISIBLE
            holder.tvName.visibility = View.VISIBLE
            holder.tvRate.visibility = View.VISIBLE
            holder.root.setOnClickListener {
                callback?.onProductClick(child)
            }
            if (child.verified != null && !child.verified!! || child.verified == null) {
                holder.verified.visibility = View.INVISIBLE
            }
        }
    }

    class RelatedProductHolder(view: View): RecyclerView.ViewHolder(view){
        val img = view.findViewById<ImageView>(R.id.imgProduct)
        val tvName = view.findViewById<TextView>(R.id.product_name)
        val rating = view.findViewById<ScaleRatingBar>(R.id.product_rating)
        val tvRate = view.findViewById<TextView>(R.id.tv_rating)
        val price = view.findViewById<TextView>(R.id.product_price)
        val verified = view.findViewById<ImageView>(R.id.img_verfified)
        val root = view.findViewById<ViewGroup>(R.id.root_child)

        init {
            tvRate.visibility = View.INVISIBLE
        }
    }

    interface OnProductClick{
        fun onProductClick(product: ICProduct)
        fun onMoreProduct()
    }
}