package vn.icheck.android.component.product_for_you

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_product_for_you_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import kotlin.math.round

class ProductForYouViewHolder(parent: ViewGroup) : BaseViewHolder<MutableList<ICProductForYouMedia>>(LayoutInflater.from(parent.context).inflate(R.layout.layout_product_for_you_holder, parent, false)) {

    override fun bind(obj: MutableList<ICProductForYouMedia>) {
        val adapter = ListProductForYouAdapter(obj)
        itemView.rcv_product_for_you.adapter = adapter
        itemView.rcv_product_for_you.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)


        var total_witdth = 0
        var itemHolderWidth=0
        itemView.rcv_product_for_you.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                total_witdth += dx

                val holder = recyclerView.findViewHolderForAdapterPosition(0)
                if (holder != null && holder is ListProductForYouAdapter.ItemProductForYouHolder) {
                    holder.itemView.post {
                        /**
                         * Tính vị trí holder khi scroll recyclerview = vị trí scroll / độ width của item holder
                         */
                        itemHolderWidth = holder.itemView.width
                        val pos = (total_witdth.toDouble() / itemHolderWidth.toDouble())
                        val posRound = round(pos).toInt()

                        for (i in obj.indices) {
                            obj[i].exoPlayer?.playWhenReady = i == posRound
                        }
                    }
                }else{
                    val pos = (total_witdth.toDouble() / itemHolderWidth.toDouble())
                    val posRound = round(pos).toInt()

                    for (i in obj.indices) {
                        obj[i].exoPlayer?.playWhenReady = i == posRound
                    }
                }
            }
        })
    }

}