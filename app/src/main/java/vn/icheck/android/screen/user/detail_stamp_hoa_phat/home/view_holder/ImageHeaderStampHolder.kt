package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.image_header_stamp.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.attachment.BannerHoaPhatStampAdapter
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.attachment.HorizontalAttachmentStampAdapter
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back.SlideHeaderStampHoaPhatListener

class ImageHeaderStampHolder(parent: ViewGroup, private val headerImagelistener: SlideHeaderStampHoaPhatListener) : BaseViewHolder<ICBarcodeProductV1>(LayoutInflater.from(parent.context).inflate(R.layout.image_header_stamp, parent, false)), ItemClickListener<String> {

    private var bannerAdapter = BannerHoaPhatStampAdapter(itemView.context, headerImagelistener)

    override fun bind(obj: ICBarcodeProductV1) {
        itemView.viewPagerImgProduct.adapter = bannerAdapter
        bannerAdapter.setListImageData(obj.attachments)

        val horizontalAttachment = HorizontalAttachmentStampAdapter(obj.attachments,this)
        itemView.recyclerViewHorizontal.layoutManager = LinearLayoutManager(itemView.context,LinearLayoutManager.HORIZONTAL,false)
        itemView.recyclerViewHorizontal.adapter = horizontalAttachment
    }

    override fun onItemClick(position: Int, item: String?) {
        itemView.viewPagerImgProduct.currentItem = position
    }
}