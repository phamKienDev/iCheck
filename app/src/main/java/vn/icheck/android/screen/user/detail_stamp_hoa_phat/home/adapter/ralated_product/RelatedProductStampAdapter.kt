package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.ralated_product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_product_related_stamp_hoa_phat.view.*
import vn.icheck.android.R
import vn.icheck.android.network.models.ICRelatedProduct
import vn.icheck.android.network.models.v1.ICRelatedProductV1
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back.SlideHeaderStampHoaPhatListener
import vn.icheck.android.util.ui.GlideUtil

class RelatedProductStampAdapter(val list: MutableList<ICRelatedProductV1.RelatedProductRow>, private val headerImagelistener: SlideHeaderStampHoaPhatListener) : RecyclerView.Adapter<RelatedProductStampAdapter.RelatedProductStampHolder>() {

    override fun getItemCount(): Int {
        if (list.size > 10) return 10
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatedProductStampHolder {
        return RelatedProductStampHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product_related_stamp_hoa_phat, parent, false))
    }

    override fun onBindViewHolder(holder: RelatedProductStampHolder, position: Int) {
        val child = list[position]
        holder.bind(child)

        holder.itemView.setOnClickListener {
            headerImagelistener.onClickRalatedProduct(list[position].barcode)
        }
    }

    class RelatedProductStampHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(child: ICRelatedProductV1.RelatedProductRow) {
            GlideUtil.loading(child.thumbnails?.small, itemView.img_product)
            if (!child.name.isNullOrEmpty()) {
                itemView.product_name.text = child.name
            }
            itemView.product_rating.rating = child.rating
            if (child.price > 0) {
                itemView.product_price.text = String.format("%,dđ", child.price)
            } else {
                itemView.product_price.text = itemView.context.getString(R.string.dang_cap_nhat_gia)
            }

            if (child.verified != null && !child.verified!! || child.verified == null) {
                itemView.img_verfified.visibility = View.INVISIBLE
            } else {
                itemView.img_verfified.visibility = View.VISIBLE
            }
        }
    }
}