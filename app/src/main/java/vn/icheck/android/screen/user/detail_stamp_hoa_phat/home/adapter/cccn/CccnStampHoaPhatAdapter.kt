package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.cccn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.cccn_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back.SlideHeaderStampHoaPhatListener
import vn.icheck.android.util.kotlin.WidgetUtils

class CccnStampHoaPhatAdapter(var listImg: List<ImageChild>,val headerImagelistener: SlideHeaderStampHoaPhatListener) : RecyclerView.Adapter<CccnStampHoaPhatAdapter.CccnHolder>() {

    private val listImage = arrayListOf<String?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CccnHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CccnHolder(inflater.inflate(R.layout.cccn_holder, parent, false))
    }

    override fun getItemCount(): Int {
        return listImg.size
    }

    override fun onBindViewHolder(holder: CccnHolder, position: Int) {
        val item = listImg[position]
        holder.bind(item)

        for (i in listImg) {
            listImage.add(i.url)
        }

        holder.itemView.product_img.setOnClickListener {
            headerImagelistener.itemPagerClick(listImage,position)
        }
    }

    class CccnHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(obj: ImageChild) {
            WidgetUtils.loadImageUrl(itemView.product_img,obj.url,R.drawable.ic_error_load_url)
            itemView.product_img.setOnClickListener {
//                ProductDetailActivity.INSTANCE?.showDetailCertificates()
            }

//            if(obj.canDelete){
//                imgDelete.visibility=View.VISIBLE
//            }else{
//                imgDelete.visibility=View.GONE
//            }

//            imgDelete.setOnClickListener {
//                ProductDetailActivity.INSTANCE?.deleteImageSend(adapterPosition)
//            }
        }
    }

    class ImageChild(val url: String?)
}
