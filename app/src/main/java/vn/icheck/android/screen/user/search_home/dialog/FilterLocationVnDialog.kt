package vn.icheck.android.screen.user.search_home.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_filter_location_vn.*
import kotlinx.android.synthetic.main.item_list_category_filter.view.*
import kotlinx.android.synthetic.main.layout_title_filter_search.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.address.AddressInteractor
import vn.icheck.android.network.models.ICProvince
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.kotlin.ToastUtils

class FilterLocationVnDialog(val callback: LocationCallback, selectedID: MutableList<ICProvince>? = null) : BaseBottomSheetDialogFragment(), IRecyclerViewCallback {
    private val adapter = ListCategoryAdapter(selectedID ?: mutableListOf(), this)
    private val listData = mutableListOf<ICProvince>()

    private val addressInteraction = AddressInteractor()
    private var offset = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_filter_location_vn, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTitle.setText(R.string.tinh_thanh_pho)
        setupRecyclerView()
        getListProvince()

        tvDone.setOnClickListener {
            dismiss()
            callback.getLocation(adapter.getListSelected)
        }

        imgClose.setOnClickListener {
            dismiss()
        }

        tv_clear.setOnClickListener {
            adapter.isAll = true
            adapter.oldSelectedID.clear()
            adapter.setData(listData)
        }
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun getListProvince() {
        if (NetworkHelper.isNotConnected(context)) {
            ToastUtils.showShortError(ICheckApplication.getInstance(), ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            dismiss()
            tv_clear.beGone()
            return
        }

        addressInteraction.getListProvinceV2(offset, 100, object : ICNewApiListener<ICResponse<ICListResponse<ICProvince>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICProvince>>) {

                listData.clear()
                listData.add(0, ICProvince(-1, requireContext().getString(R.string.tat_ca), -1, ""))
                listData.addAll(obj.data?.rows ?: mutableListOf())

                adapter.setData(listData)
            }

            override fun onError(error: ICResponseCode?) {
                tv_clear.beGone()
                val message = if (error?.message.isNullOrEmpty()) {
                    ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                } else {
                    error!!.message!!
                }
                ToastUtils.showShortError(ICheckApplication.getInstance(), message)
                dismiss()
            }
        })
    }


    class ListCategoryAdapter(val oldSelectedID: MutableList<ICProvince>, val callback: IRecyclerViewCallback) : RecyclerViewAdapter<ICProvince>(callback) {
        private val listSelected = mutableListOf<ICProvince>()
        var isAll = true

        override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return ViewHolder(parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ViewHolder) {
                holder.bind(listData[position])
            } else {
                super.onBindViewHolder(holder, position)
            }
        }

        fun setData(list: MutableList<ICProvince>) {
            listData.clear()
            listData.addAll(list)

            for (item in listData) {
                item.selected = isSelected(item.id)
            }

            notifyDataSetChanged()
        }

        private fun isSelected(id: Long): Boolean {
            for (item in oldSelectedID) {
                if (item.id == id) {
                    return item.selected
                }
            }
            return false
        }

        val getListSelected: MutableList<ICProvince>
            get() {
                for (it in listData) {
                    if (it.selected) {
                        listSelected.add(it)
                    }
                }
                return listSelected
            }


        override fun getItemCount(): Int {
            return listData.size
        }

        inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICProvince>(LayoutInflater.from(parent.context).inflate(R.layout.item_list_category_filter, parent, false)) {
            override fun bind(obj: ICProvince) {
                itemView.tvName.apply {
                    text = obj.name

                    setCompoundDrawablesWithIntrinsicBounds(0, 0, if (obj.selected) {
                        R.drawable.ic_checkbox_single_on_24px
                    } else {
                        0
                    }, 0)

                    if (isAll) {
                        if (oldSelectedID.isNullOrEmpty()) {
                            if ((obj.name == "Tất cả")||(obj.name == context.getString(R.string.tat_ca))) {
                                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)
                            }
                        }
                        isAll = false
                    }

                    setOnClickListener {
                        if (adapterPosition == 0) {
                            for (i in listData.indices) {
                                if (listData[i].selected) {
                                    listData[i].selected = false
                                }
                            }
                        } else {
                            listData[0].selected = false
                        }
                        obj.selected = !obj.selected
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    interface LocationCallback {
        fun getLocation(obj: MutableList<ICProvince>?)
    }

    override fun onMessageClicked() {
        getListProvince()
    }

    override fun onLoadMore() {
    }
}
