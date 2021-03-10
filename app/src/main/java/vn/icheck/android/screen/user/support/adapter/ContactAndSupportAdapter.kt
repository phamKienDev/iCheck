package vn.icheck.android.screen.user.support.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_contact_and_support.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICSupport
import vn.icheck.android.screen.user.support.view.IContactAndSupportView

class ContactAndSupportAdapter(val listener: IContactAndSupportView, val listData: MutableList<ICSupport>) : RecyclerView.Adapter<ContactAndSupportAdapter.ViewHolder>() {

    override fun getItemCount(): Int = listData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contact_and_support, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(view: View) : BaseViewHolder<ICSupport>(view) {

        override fun bind(obj: ICSupport) {
            val icon = if (obj.contact_type == "phone") R.drawable.ic_support_phone_24 else R.drawable.ic_support_mail_24
            itemView.tvIcon.setImageResource(icon)

            itemView.txtTitle.text = obj.name
            itemView.txtContact.text = obj.contact

            itemView.setOnClickListener {
                val ct = obj.contact

                if (!ct.isNullOrEmpty()) {
                    when (obj.contact_type) {
                        "phone" -> {
                            listener.onCallPhone(ct)
                        }
                        "email" -> {
                            listener.onSendEmail(ct)
                        }
                    }
                }
            }
        }
    }
}