package vn.icheck.android.loyalty.screen.loyalty_customers.history.adapter

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_filter_text.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.loyalty.base.setVisible

class FilterAdapter(val listFilter: List<String>, val onRemove: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val lf = LayoutInflater.from(parent.context)
        val v = lf.inflate(R.layout.item_filter_text, parent, false)
        return FilterItemHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as FilterItemHolder
        holder.view.tv_filter_name.text = listFilter[position]
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (listFilter[position] == "Tất cả") {
                holder.view.tv_filter_name.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            } else {
                holder.view.tv_filter_name.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_delete_18px, 0)
            }
        }
        if (position == listFilter.lastIndex) {
            holder.view.img_filter.setVisible()
            holder.view.img_filter.setOnClickListener {
                Handler().postDelayed({
                    onRemove(holder.bindingAdapterPosition)
                }, 200)
            }
            holder.view.tv_filter_name.setOnClickListener {
                Handler().postDelayed({
                    onRemove(holder.bindingAdapterPosition)
                }, 200)
            }
        } else {
            holder.view.img_filter.setGone()
            holder.view.tv_filter_name.setOnClickListener {
                Handler().postDelayed({
                    onRemove(holder.bindingAdapterPosition)
                }, 200)
            }
        }


    }

    override fun getItemCount() = listFilter.size

    inner class FilterItemHolder(val view: View) : RecyclerView.ViewHolder(view)
}