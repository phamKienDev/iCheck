package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.cstsp_slidesp_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICRelatedProduct
import vn.icheck.android.network.models.v1.ICRelatedProductV1
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.ralated_product.RelatedProductStampAdapter
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back.SlideHeaderStampHoaPhatListener
import vn.icheck.android.util.ick.rText

class ProductRelatedStampHolder(parent: ViewGroup,private val headerImagelistener: SlideHeaderStampHoaPhatListener) : BaseViewHolder<MutableList<ICRelatedProductV1.RelatedProductRow>>(LayoutInflater.from(parent.context).inflate(R.layout.cstsp_slidesp_holder, parent, false)) {

    override fun bind(obj: MutableList<ICRelatedProductV1.RelatedProductRow>) {
        itemView.slide_title rText R.string.san_pham_cung_doanh_nghiep_so_huu
        val adapter = RelatedProductStampAdapter(obj,headerImagelistener)
        itemView.rcv_slide_product.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        itemView.rcv_slide_product.adapter = adapter
    }
}