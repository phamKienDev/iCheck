package vn.icheck.android.component.infomation_contribution

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.screen.user.contribute_product.IckContributeProductActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity

class NoContributionHolder(parent: ViewGroup) : BaseViewHolder<ContributrionModel>(ViewHelper.createNoContributionHolder(parent.context)) {
    override fun bind(obj: ContributrionModel) {
        (itemView as ViewGroup).run {
            (getChildAt(1) as AppCompatTextView).run {
                setOnClickListener {
                    ICheckApplication.currentActivity()?.let {
                        if (SessionManager.isUserLogged) {
                            IckContributeProductActivity.start(it, obj.barcode, obj.productId)
                        } else {
                            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REQUEST_VOTE_CONTRIBUTION))
                        }
                    }
                }
            }
        }
    }
}