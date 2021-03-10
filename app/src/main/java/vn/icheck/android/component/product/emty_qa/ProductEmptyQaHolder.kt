package vn.icheck.android.component.product.emty_qa

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.product_empty_qa.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent

class ProductEmptyQaHolder(parent: ViewGroup) : BaseViewHolder<EmptyQAModel>(LayoutInflater.from(parent.context).inflate(R.layout.product_empty_qa, parent, false)) {

    override fun bind(obj: EmptyQAModel) {
        itemView.btnQueston.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_LIST_QUESTIONS, true))
        }
    }
}