package vn.icheck.android.component.relatedpage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.holder_related_page.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICRelatedPage

class RelatedPageHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.holder_related_page, parent, false)) {
    private lateinit var adapter: RelatedPageAdapter

    fun bind(obj: MutableList<ICRelatedPage>, type: Int) {

        when (type) {
            Constant.PAGE_BRAND_TYPE -> {
                itemView.tvTitle.text = "Trang liên quan"
            }
            Constant.PAGE_EXPERT_TYPE -> {
                itemView.tvTitle.text = "Gợi ý cho bạn"
            }
            Constant.PAGE_ENTERPRISE_TYPE -> {
                itemView.tvTitle.text = "Trang liên quan"
            }
        }


        adapter = RelatedPageAdapter()
        ((itemView as ViewGroup).getChildAt(1) as RecyclerView).adapter = adapter
        adapter.setData(obj)
    }
}