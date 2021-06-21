package vn.icheck.android.component.product.bottominfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_contact_setting.view.*
import vn.icheck.android.R
import vn.icheck.android.component.product.ProductDetailListener
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.util.ick.rText

class ContactSettingAdapter(val listener: ProductDetailListener) : RecyclerView.Adapter<ContactSettingAdapter.ViewHolder>() {

    private val listData = mutableListOf<ICClientSetting>()

    fun setListData(list: MutableList<ICClientSetting>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_contact_setting,parent,false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener.clickBottomContact(item,position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ICClientSetting) {
            itemView.tvName.background=ViewHelper.bgWhiteCornersLeft20(itemView.context)
            when(item.key){
                "product-detail.dang-ky-ma-vach" -> {
                    itemView.tvName rText R.string.dang_ky_ma_so_ma_vach
                    itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_footer_barcode_36dp,0,0,0)
                }
                "product-detail.thong-tin-thuong-pham" -> {
                    itemView.tvName rText R.string.thong_tin_thuong_pham
                    itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_footer_information_36dp,0,0,0)
                }
                "product-detail.tem-chong-gia" -> {
                    itemView.tvName rText R.string.tem_dien_tu_chong_gia
                    itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_footer_electronic_stamp_36dp,0,0,0)
                }
                "product-detail.tem-truy-nguon-goc" -> {
                    itemView.tvName rText R.string.tem_truy_xuat_nguon_goc_qr_code
                    itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_footer_qrcode_origin_36dp,0,0,0)
                }
                "product-detail.tem-bao-hanh" -> {
                    itemView.tvName rText R.string.tem_bao_hanh_dien_tu_qr_code
                    itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_footer_qrcode_guarantee_36dp,0,0,0)
                }
                "product-detail.tem-chong-tran" -> {
                    itemView.tvName rText R.string.tem_chong_tran_hang_qr_code
                    itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_footer_location_36dp,0,0,0)
                }
                "product-detail.ma-noi-bo" -> {
                    itemView.tvName rText R.string.ma_noi_bo_truy_xuat_thong_tin
                    itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_footer_internal_36dp,0,0,0)
                }
            }
        }
    }
}