package vn.icheck.android.screen.dialog

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_custom_button_page.*
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.ICButtonOfPage
import vn.icheck.android.network.models.ICUpdateButtonPage
import vn.icheck.android.util.ick.logDebug
import vn.icheck.android.util.kotlin.ToastUtils

class CustomButtomPageDialog(val pageId: Long, val buttonConfigs: ArrayList<ICButtonOfPage>?) : BaseBottomSheetDialogFragment() {
    lateinit var chinhAdapter: ButtonAdapter
    lateinit var phuAdapter: ButtonAdapter

    private val buttons = arrayListOf<ICButtonOfPage>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_custom_button_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logDebug("created view $this")
        imgClose.setOnClickListener {
            dismiss()
        }

        tvDone.setOnClickListener {
            updateButton()
        }

        buttons.addAll(buttonConfigs ?: arrayListOf())
        logDebug(buttons.hashCode().toString())
        logDebug(buttonConfigs.hashCode().toString())
        initRecyclerView()
        getData()
    }

    private fun updateButton() {
        if (NetworkHelper.isNotConnected(context)) {
            ToastUtils.showShortError(context, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        val listUpdate = mutableListOf<ICUpdateButtonPage>()
        for (i in chinhAdapter.getListData) {
            listUpdate.add(ICUpdateButtonPage(i.buttonId!!, i.status ?: 2))
        }
        for (i in phuAdapter.getListData) {
            listUpdate.add(ICUpdateButtonPage(i.buttonId!!, i.status ?: 2))
        }

        PageRepository().updateButtonCustomizes(pageId, listUpdate, object : ICNewApiListener<ICResponse<MutableList<ICButtonOfPage>>> {
            override fun onSuccess(obj: ICResponse<MutableList<ICButtonOfPage>>) {
                dismiss()
                DialogHelper.showDialogSuccessBlack(requireContext(), requireContext().getString(R.string.ban_da_thay_doi_nut_thanh_cong))
            }

            override fun onError(error: ICResponseCode?) {
                DialogHelper.showDialogSuccessBlack(requireContext(), requireContext().getString(R.string.ban_da_thay_doi_nut_that_bai))
            }
        })
    }

    private fun initRecyclerView() {
        chinhAdapter = ButtonAdapter()
        rcvChinh.adapter = chinhAdapter

        phuAdapter = ButtonAdapter()
        rcvPhu.adapter = phuAdapter
    }

    private fun getData() {
        if (!buttons.isNullOrEmpty()) {
            val listChinh = mutableListOf<ICButtonOfPage>()
            val listPhu = mutableListOf<ICButtonOfPage>()
            for (i in buttons) {
                if (i.objectType == "primary_button") {
                    listChinh.add(i)
                } else {
                    listPhu.add(i)
                }
            }
            chinhAdapter.setListData(listChinh)
            phuAdapter.setListData(listPhu)
        } else {
            ToastUtils.showShortError(context, R.string.co_loi_xay_ra_vui_long_thu_lai)
            dismiss()
        }
    }

    class ButtonAdapter : RecyclerViewAdapter<ICButtonOfPage>() {
        override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return ButtonHolder(parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)

            if (holder is ButtonHolder) {
                holder.bind(listData[position])
            }
        }

        inner class ButtonHolder(parent: ViewGroup) : BaseViewHolder<ICButtonOfPage>(createView(parent)) {
            override fun bind(obj: ICButtonOfPage) {
                (itemView as AppCompatTextView).run {
                    text = obj.buttonName

                    if (obj.status == 1) {
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checkbox_single_on_24px, 0, 0, 0)
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_radio_un_checked_gray_24dp, 0, 0, 0)
                    }

                    if (adapterPosition == 0) {
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                            it.setMargins(SizeHelper.size14, 0, SizeHelper.size14, SizeHelper.size20)
                        }
                    }

                    setOnClickListener {
                        if (obj.status != 1) {
                            for (i in listData) {
                                i.status = if (i.buttonId == obj.buttonId) {
                                    1
                                } else {
                                    2
                                }
                            }
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        fun createView(parent: ViewGroup): AppCompatTextView {
            return AppCompatTextView(parent.context).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(SizeHelper.size14, 0, SizeHelper.size14, SizeHelper.size12)
                }
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTextColor(ContextCompat.getColor(parent.context, R.color.collection_product_name))
                typeface = Typeface.create(ViewHelper.barlowMedium, Typeface.NORMAL)
                compoundDrawablePadding = SizeHelper.size10
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logDebug("destroyed")
    }
}