package vn.icheck.android.screen.user.listnotification.viewholder.interactive

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.screen.user.listnotification.interactive.InteractiveHolder

class InteractiveAdapter : RecyclerView.Adapter<InteractiveHolder>() {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InteractiveHolder = InteractiveHolder(parent)

    override fun onBindViewHolder(holder: InteractiveHolder, position: Int) {
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