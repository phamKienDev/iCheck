package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.attachment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_attachment_horizontal_stamp.view.*
import vn.icheck.android.R
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.util.kotlin.WidgetUtils
import java.util.regex.Pattern

class HorizontalAttachmentStampAdapter(val listData: List<ICBarcodeProductV1.Attachments>, val listener: ItemClickListener<String>) : RecyclerView.Adapter<HorizontalAttachmentStampAdapter.ViewHolder>()  {

    private var selectedPosition = 0

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_attachment_horizontal_stamp, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ICBarcodeProductV1.Attachments) {
            if (item.type == "video"){
                val urlImage = getYoutubeVideoId(listData[position].thumbnails.original)
                WidgetUtils.loadImageUrl(itemView.imgAttachment, "https://img.youtube.com/vi/$urlImage/hqdefault.jpg", R.drawable.img_product_shop_default)
            } else {
                WidgetUtils.loadImageUrl(itemView.imgAttachment, item.thumbnails.original, R.drawable.img_product_shop_default)
            }
            itemView.setOnClickListener {
                if (selectedPosition != adapterPosition) {
                    selectedPosition = adapterPosition
                    listener.onItemClick(selectedPosition, item.thumbnails.original)
                }
            }
        }

        private fun getYoutubeVideoId(youtubeUrl: String?): String? {
            var videoId: String? = ""
            if (youtubeUrl != null && youtubeUrl.trim { it <= ' ' }.isNotEmpty() && youtubeUrl.startsWith("http")) {
                val expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"
                val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
                val matcher = pattern.matcher(youtubeUrl)
                if (matcher.matches()) {
                    val groupIndex1: String = matcher.group(7)
                    if (groupIndex1 != null && groupIndex1.length == 11) videoId = groupIndex1
                }
            }
            return videoId
        }
    }
}