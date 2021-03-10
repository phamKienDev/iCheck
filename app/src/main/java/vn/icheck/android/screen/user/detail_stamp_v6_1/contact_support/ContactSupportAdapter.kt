package vn.icheck.android.screen.user.detail_stamp_v6_1.contact_support

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_contact_and_support_stamp.view.*
import vn.icheck.android.R
import vn.icheck.android.network.models.ICSupport

class ContactSupportAdapter(val view: IContactSupportView) : RecyclerView.Adapter<ContactSupportAdapter.ViewHolder>() {

    private val listData = mutableListOf<ICSupport>()

    fun setListData(list: MutableList<ICSupport>?) {
        listData.clear()
        if (list != null) {
            listData.addAll(list)
        } else {
            listData.addAll(mutableListOf())
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_contact_and_support_stamp, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            view.setItemClick(item)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ICSupport) {
            itemView.tvName.text = item.name
            itemView.tvValue.text = item.contact
            when (item.contact_type) {
                "phone" -> {
                    itemView.imgAva.setImageResource(R.drawable.ic_contact_support_blue_24)
                }

                "email" -> {
                    itemView.imgAva.setImageResource(R.drawable.ic_mail_support_blue_24)
                }
            }
        }
    }
}