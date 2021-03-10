package vn.icheck.android.screen.user.search_home.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_filter_time_review.*
import kotlinx.android.synthetic.main.item_list_category_filter.view.*
import kotlinx.android.synthetic.main.layout_title_filter_search.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.helper.TimeHelper

class FilterYearDialog(val listSelected: MutableList<String>, private val timeCallBack: TimeCallBack) : BaseBottomSheetDialogFragment() {
    private var selectedTime = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_filter_time_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTitle.setText(R.string.ngay_dang)
        selectedTime.addAll(listSelected)

        val list = mutableListOf<String>()
        list.add(getString(R.string.tat_ca))
        for (i in TimeHelper.getCurrentYear() downTo 2014) {
            list.add("$i")
        }

        val adapter = ListTimeFilterAdapter()
        rcv_list_time.adapter = adapter
        rcv_list_time.layoutManager = LinearLayoutManager(context)
        adapter.setData(list, selectedTime)

        imgClose.setOnClickListener {
            dismiss()
        }

        tvClear.setOnClickListener {
            adapter.resetSelected()
        }

        tvDone.setOnClickListener {
            dismiss()
            timeCallBack.getTime(adapter.getSelected())
        }
    }

    interface TimeCallBack {
        fun getTime(obj: MutableList<String>?)
    }

    class ListTimeFilterAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val listSelected = mutableListOf<String>()
        val listData = mutableListOf<String>()
        var isFirst = true

        fun setData(list: MutableList<String>, selected: MutableList<String>) {
            listData.addAll(list)
            listSelected.addAll(selected)
            notifyDataSetChanged()
        }

        fun resetSelected() {
            listSelected.clear()
            isFirst = true
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ItemCategoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_category_filter, parent, false))
        }

        override fun getItemCount(): Int {
            return listData.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ItemCategoryHolder).bind(listData[position])
        }

        fun getSelected(): MutableList<String> {
            listSelected.sort()
            return listSelected
        }

        inner class ItemCategoryHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(obj: String) {
                itemView.tvName.text = obj

                if (!listSelected.isNullOrEmpty()) {
                    for (selected in listSelected) {
                        if (selected == obj) {
                            itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)
                            break
                        } else {
                            itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        }
                    }
                } else {
                    itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

                    itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, if (isFirst) {
                        R.drawable.ic_checkbox_single_on_24px
                    } else {
                        0
                    }, 0)
                    isFirst = false
                }

                itemView.setOnClickListener {
                    if (obj == ICheckApplication.getString(R.string.tat_ca)) {
                        resetSelected()
                    } else {
                        var selected = false
                        for (item in listSelected) {
                            if (item == obj) {
                                selected = true
                                break
                            } else {
                                selected = false
                            }
                        }

                        if (!selected) {
                            listSelected.add(obj)
                        } else {
                            listSelected.remove(obj)
                        }
                    }

                    notifyDataSetChanged()
                }
            }
        }

    }
}