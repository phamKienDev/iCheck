package vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter

import android.os.Build
import android.text.Html
import android.text.Spannable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectInfo
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.view.IDetailStampView
import vn.icheck.android.util.kotlin.GlideImageGetter

class InformationProductStampAdapter(val view: IDetailStampView) : RecyclerView.Adapter<InformationProductStampAdapter.ViewHolder>() {

    private val listData = mutableListOf<ICObjectInfo>()

    fun setListData(list: MutableList<ICObjectInfo>?) {
        listData.clear()
        listData.addAll(list!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_accumulation_history, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
//            val intent = Intent(this@DetailStampActivity, MoreInformationProductActivity::class.java)
//            intent.putExtra(Constant.DATA_1, item.id)
//            startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ICObjectInfo) {
//            itemView.tvTitle.text = item.title
//
//            val imageGetter = GlideImageGetter(itemView.expandTextView)
//
//            val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Html.fromHtml(item.short_content, Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
//            } else {
//                Html.fromHtml(item.short_content, imageGetter, null)
//            }
//            itemView.expandTextView.text = html
//
//            itemView.expandTextView.post(Runnable {
//                val lineCount = itemView.expandTextView.lineCount
//                if (lineCount < 5) {
//                    itemView.toggle_expand.visibility = View.GONE
//                } else {
//                    itemView.toggle_expand.visibility = View.VISIBLE
//                }
//            })
        }
    }
}