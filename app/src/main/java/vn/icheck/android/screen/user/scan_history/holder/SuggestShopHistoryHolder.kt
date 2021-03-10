package vn.icheck.android.screen.user.scan_history.holder

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.screen.user.suggest_store_history.SuggestStoreHistoryActivity

class SuggestShopHistoryHolder (parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_suggest_shop_history_holder, parent, false))  {
    fun bind() {
        itemView.setOnClickListener {
            val intent = Intent(itemView.context, SuggestStoreHistoryActivity::class.java)
            itemView.context.startActivity(intent)
        }
    }
}