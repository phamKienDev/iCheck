package vn.icheck.android.screen.user.listnotification.viewholder.othernotification

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.screen.user.listnotification.othernotification.OtherNotificationHolder

class OtherNotificationAdapter : RecyclerView.Adapter<OtherNotificationHolder>() {
    private val listData = mutableListOf<ICNotification>()

    private var listener: View.OnClickListener? = null

    fun setData(list: MutableList<ICNotification>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setOnRemoveListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int = listData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherNotificationHolder = OtherNotificationHolder(parent)

    override fun onBindViewHolder(holder: OtherNotificationHolder, position: Int) {
        holder.bind(listData[position])

        holder.setOnRemoveListener(View.OnClickListener {
            listData.removeAt(position)

            if (listData.isNotEmpty()) {
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)
            } else {
                listener?.onClick(null)
            }
        })
    }
}