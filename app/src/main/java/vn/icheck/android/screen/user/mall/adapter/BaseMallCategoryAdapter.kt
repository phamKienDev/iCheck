package vn.icheck.android.screen.user.mall.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.holder.BaseViewHolder

abstract class BaseMallCategoryAdapter<C, V : BaseViewHolder<C>> : RecyclerView.Adapter<V>() {
    private val listData = mutableListOf<C>()

    fun setData(list: List<C>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(list: List<C>) {
        listData.addAll(list)
        notifyDataSetChanged()
    }

    protected abstract fun getViewHolder(parent: ViewGroup): V

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): V {
        return getViewHolder(parent)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: V, position: Int) {
        holder.bind(listData[position])
    }
}