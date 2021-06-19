package vn.icheck.android.screen.user.search_home.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.dialog_filter_category_page.*
import kotlinx.android.synthetic.main.item_ick_category.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.search.SearchInteractor
import vn.icheck.android.network.models.ICCategorySearch
import java.util.concurrent.TimeUnit

class FilterCategoryDialog(val category: MutableList<ICCategorySearch>?, val callback: CategoryCallback) : BaseBottomSheetDialogFragment(), IRecyclerViewCallback {

    private val adapter = ListCategoryAdapter(this)
    private val interactor = SearchInteractor()
    private var offset = 0
    private var cateParent: ICCategorySearch? = null
    private var addCateAll = true
    private var dispose: Disposable? = null
    private var selectedCategory = mutableListOf<ICCategorySearch>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_filter_category_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        edt_search.background=vn.icheck.android.ichecklibs.ViewHelper.bgGrayCorners4(requireContext())

        rcv_category.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.dpToPx(500))
        rcv_category.adapter = adapter
        rcv_category.layoutManager = LinearLayoutManager(context)

        selectedCategory.addAll(category ?: mutableListOf())
        selectedCategory.let {
            if (it.size > 1) {
                it.remove(it.last())
                cateParent = it.last()
            } else {
                it.clear()
            }
        }
        dispose = RxTextView.afterTextChangeEvents(edt_search)
                .skipInitialValue()
                .distinctUntilChanged()
                .debounce(600, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    getData()
                }

        setSpannedCategory()
        getData()

        btn_clear.setOnClickListener {
            dismiss()
        }

        btn_done.setOnClickListener {
            getCategorySelected()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dispose?.dispose()
    }

    private fun getCategoryParent(key: String, isLoadmore: Boolean = false) {
        if (NetworkHelper.isNotConnected(context)) {
            adapter.setError(R.drawable.ic_error_network, requireContext().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), R.string.thu_lai)
            return
        }

        if (!isLoadmore) {
            offset = 0
            DialogHelper.showLoading(this)
        }

        interactor.getCategoryParent(offset, key, 1, object : ICNewApiListener<ICResponse<ICListResponse<ICCategorySearch>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCategorySearch>>) {
                DialogHelper.closeLoading(this@FilterCategoryDialog)
                offset += APIConstants.LIMIT
                if (!isLoadmore) {
                    adapter.setListData(obj.data!!.rows)
                } else {
                    adapter.addListData(obj.data!!.rows)
                }
            }

            override fun onError(error: ICResponseCode?) {
                DialogHelper.closeLoading(this@FilterCategoryDialog)
                adapter.setError(R.drawable.ic_error_request, requireContext().getString(R.string.co_loi_xay_ra_vui_long_thu_lai), R.string.thu_lai)
            }
        })
    }

    private fun getCategoryChildren(parent: ICCategorySearch, key: String, isLoadmore: Boolean = false) {
        if (NetworkHelper.isNotConnected(context)) {
            adapter.setError(R.drawable.ic_error_network, requireContext().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), R.string.thu_lai)
            return
        }

        if (!isLoadmore) {
            offset = 0
            DialogHelper.showLoading(this)
        }

        interactor.getCategoryChildren(offset, key, parent.id!!, object : ICNewApiListener<ICResponse<ICListResponse<ICCategorySearch>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICCategorySearch>>) {
                DialogHelper.closeLoading(this@FilterCategoryDialog)
                offset += APIConstants.LIMIT
                if (!isLoadmore) {
                    adapter.setListData(obj.data!!.rows)
                } else {
                    adapter.addListData(obj.data!!.rows)
                }
            }

            override fun onError(error: ICResponseCode?) {
                DialogHelper.closeLoading(this@FilterCategoryDialog)
                adapter.setError(R.drawable.ic_error_request, requireContext().getString(R.string.co_loi_xay_ra_vui_long_thu_lai), R.string.thu_lai)
            }
        })
    }

    fun setSpannedCategory() {
        if (!selectedCategory.isNullOrEmpty()) {
            if (addCateAll) {
                selectedCategory.add(0, ICCategorySearch(null, ICheckApplication.getString(R.string.tat_ca)))
                addCateAll = false
            }

            // get chuỗi String name từ list category
            val cateString = mutableListOf<String>()
            selectedCategory.forEach {
                it.name?.let { it1 -> cateString.add(it1) }
            }
            var cates = ""
            for (i in 0 until selectedCategory.size) {
                if (i == 0) {
                    cates += "${selectedCategory[i].name}"
                } else {
                    cates += " > ${selectedCategory[i].name}"
                }
            }
            val spannable = SpannableString(cates)

            //set màu xanh cho dấu > và category cuối cùng
            for (i in 0 until cates.length) {
                if (cates[i].toString() == ">") {
                    spannable.setSpan(ForegroundColorSpan(Constant.getPrimaryColor(tv_list_category.context)), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }

            val lastItem = cates.indexOf(cateString.last())
            spannable.setSpan(ForegroundColorSpan(Constant.getPrimaryColor(tv_list_category.context)), lastItem, lastItem + cateString.last().length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            //callback Parent khi ấn Tất cả
            val callCateParent = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    cateParent = null
                    addCateAll = true
                    selectedCategory.clear()
                    setSpannedCategory()

                    edt_search.setText("")
                }

                override fun updateDrawState(ds: TextPaint) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        ds.underlineColor = Color.TRANSPARENT
                    }
                }
            }
            val firstItem = cates.indexOf(cateString.first())
            spannable.setSpan(callCateParent, firstItem, firstItem + cateString.first().length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(Constant.getSecondTextColor(tv_list_category.context)), firstItem, firstItem + cateString.first().length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            // callback các category ở giữa
            if (cateString.size > 2) {
                for (i in 0 until cateString.size) {
                    if (i != 0 && i != cateString.size - 1) {
                        val midItem = cates.indexOf(cateString[i])
                        spannable.setSpan(object : ClickableSpan() {
                            override fun onClick(p0: View) {
                                //set lại listSelected đã chọn
                                val listDelete = mutableListOf<ICCategorySearch>()
                                for (pos in selectedCategory.size - 1 downTo 0) {
                                    if (selectedCategory[pos].name == selectedCategory[i].name) {
                                        cateParent = selectedCategory[pos]
                                        break
                                    } else {
                                        listDelete.add(selectedCategory[pos])
                                    }
                                }
                                selectedCategory.removeAll(listDelete)
                                setSpannedCategory()
                                edt_search.setText("")
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                    ds.underlineColor = Color.TRANSPARENT
                                }
                            }
                        }, midItem, midItem + cateString[i].length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        spannable.setSpan(ForegroundColorSpan(Constant.getSecondTextColor(tv_list_category.context)), midItem, midItem + cateString[i].length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }

            tv_list_category.setText(spannable)
            tv_list_category.movementMethod = LinkMovementMethod.getInstance()
        } else {
            tv_list_category.setText("")
        }
    }

    fun getData(isLoadmore: Boolean = false) {
        if (cateParent == null) {
            getCategoryParent(edt_search.text.toString(), isLoadmore)
        } else {
            getCategoryChildren(cateParent!!, edt_search.text.toString(), isLoadmore)
        }
    }

    override fun onMessageClicked() {
        edt_search.setText("")
    }

    override fun onLoadMore() {
        getData(true)
    }

    fun getCategorySelected() {
        if (!selectedCategory.isNullOrEmpty()) {
            if (selectedCategory[0].name == ICheckApplication.getString(R.string.tat_ca)) {
                selectedCategory.remove(selectedCategory[0])
            }
        }
        callback.getSelected(selectedCategory)

        dismiss()
    }

    interface CategoryCallback {
        fun getSelected(obj: MutableList<ICCategorySearch>?)
    }

    inner class ListCategoryAdapter(val callback: IRecyclerViewCallback) : RecyclerViewAdapter<ICCategorySearch>(callback) {

        override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return ItemCategoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_ick_category, parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)

            if (holder is ItemCategoryHolder) {
                holder.bind(listData[position])
            }
        }

        inner class ItemCategoryHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(obj: ICCategorySearch) {
                itemView.tv_category_name.text = obj.name

                if (obj.childrenCount > 0) {
                    itemView.tv_category_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right_light_blue_24dp, 0)
                } else {
                    itemView.tv_category_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }

                itemView.setOnClickListener {
                    if (selectedCategory.isNullOrEmpty())
                        selectedCategory = mutableListOf()
                    selectedCategory.add(obj)

                    if (obj.childrenCount > 0) {
                        cateParent = obj
                        edt_search.setText("")
                        setSpannedCategory()
                    } else {
                        getCategorySelected()
                    }
                }
            }
        }
    }
}