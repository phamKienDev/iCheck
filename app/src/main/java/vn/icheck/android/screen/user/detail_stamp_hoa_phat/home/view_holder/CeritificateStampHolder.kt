package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.certificate_stamp.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.cccn.CccnStampHoaPhatAdapter
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back.SlideHeaderStampHoaPhatListener

class CeritificateStampHolder(parent: ViewGroup,val headerImagelistener: SlideHeaderStampHoaPhatListener) : BaseViewHolder<MutableList<ICBarcodeProductV1.Certificate>>(LayoutInflater.from(parent.context).inflate(R.layout.certificate_stamp, parent, false)) {

    override fun bind(certificates: MutableList<ICBarcodeProductV1.Certificate>) {
        val listImg = mutableListOf<CccnStampHoaPhatAdapter.ImageChild>()
        certificates.forEach {
            listImg.add(CccnStampHoaPhatAdapter.ImageChild(it.thumbnails.original))
        }
        itemView.rcv_cccn.adapter = CccnStampHoaPhatAdapter(listImg,headerImagelistener)
        itemView.rcv_cccn.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
    }
}