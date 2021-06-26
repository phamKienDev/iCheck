package vn.icheck.android.component.product.infor_contribution

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.product_information_contribution.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.product.ProductDetailListener

class EmptyContributionEnterpriseHolder(parent: ViewGroup, val listener: ProductDetailListener) : BaseViewHolder<InformationContributionModel>(LayoutInflater.from(parent.context).inflate(R.layout.product_information_contribution, parent, false)) {

    override fun bind(obj: InformationContributionModel) {
        itemView.btn_contribution.apply {
            background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(context)
            setOnClickListener {
                listener.clickToContributionInfo(obj.barcode)
            }
        }

        itemView.btn_cancel.setOnClickListener {
            listener.clickCancelContribution(adapterPosition)
        }
    }
}