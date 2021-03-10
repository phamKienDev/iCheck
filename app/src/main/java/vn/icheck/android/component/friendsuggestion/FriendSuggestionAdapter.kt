package vn.icheck.android.component.friendsuggestion

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.network.models.ICFriendSuggestion
import vn.icheck.android.network.models.ICUser

class FriendSuggestionAdapter(val maxItem:Int = -1) : RecyclerView.Adapter<FriendSuggestionHolder>() {
    private val listData = mutableListOf<ICUser>()

    private var listener: View.OnClickListener? = null

    fun setData(list: MutableList<ICUser>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setOnRemoveListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int{
        val result = listData.size
        if (result <= 3) {
            return result
        }
        return  if(maxItem == -1 ) listData.size else maxItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendSuggestionHolder = FriendSuggestionHolder(parent)

    override fun onBindViewHolder(holder: FriendSuggestionHolder, position: Int) {
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