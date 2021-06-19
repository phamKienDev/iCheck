package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.network.models.v1.ICImage
import vn.icheck.android.util.ui.GlideUtil

class EditReviewImgV1Adapter(val listImg: List<ICImage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CccnHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return listImg.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CccnHolder).bind(listImg.get(position).url, listImg)
    }

    class CccnHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.product_img)

        fun bind(url: String?, listImg: List<ICImage>) {
            GlideUtil.loading(url, image)
            image.setOnClickListener {
                val ar = listImg.map {
                    it.url
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): CccnHolder {
                return CccnHolder(LayoutInflater.from(parent.context).inflate(R.layout.cccn_holder, parent, false))
            }
        }
    }
}