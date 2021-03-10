package vn.icheck.android.activities.product.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.flow.flow
import vn.icheck.android.R
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.helper.ImageHelper
import vn.icheck.android.network.models.ICInfo

class BottomInfoAdapter(val listInfo:List<ICInfo>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return InfoHolder.create(parent)
    }

    override fun getItemCount(): Int {
        return listInfo.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as InfoHolder).bind(listInfo[position])
    }

    class InfoHolder(view: View) : BaseHolder(view) {

        fun bind(icInfo: ICInfo) {
            val x = flow<Int> {

            }
            Glide.with(view.context.applicationContext)
                    .load(icInfo.icon_url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(getImg(R.id.img_icon))
            getTv(R.id.tv_title).text = icInfo.title
        }

        companion object{
            fun create(parent: ViewGroup):InfoHolder{
                return InfoHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.bottom_info_holder, parent, false))
            }
        }
    }
}