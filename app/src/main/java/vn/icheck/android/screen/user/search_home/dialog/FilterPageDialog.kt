package vn.icheck.android.screen.user.search_home.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_filter_page_search.*
import kotlinx.android.synthetic.main.layout_title_filter_search.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICCategorySearch
import vn.icheck.android.network.models.ICProvince

class FilterPageDialog(val locations: MutableList<ICProvince>?, val verified: Boolean, val category: MutableList<ICCategorySearch>?, val listener: FilterPageCallback) : BaseBottomSheetDialogFragment() {

    private var selectedLocations = mutableListOf<ICProvince>()
    private var selectedVerified = true
    private var selectedCategory = mutableListOf<ICCategorySearch>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_filter_page_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedLocations.addAll(locations ?: mutableListOf())
        selectedCategory.addAll(category ?: mutableListOf())
        selectedVerified = verified

        switch_verfied.trackDrawable=ViewHelper.btnSwitchGrayUncheckedGreenCheckedWidth50Height30(requireContext())

        setLocation(selectedLocations)
        switch_verfied.isChecked = selectedVerified
        setFiltterCategory(selectedCategory)

        imgClose.setOnClickListener {
            dismiss()
        }

        tv_clear.apply {
            background = ViewHelper.bgOutlinePrimary1Corners4(context)
            setOnClickListener {
                switch_verfied.isChecked = false
                setFiltterCategory(null)
                setLocation(null)
            }
        }

        img_clear_location.setOnClickListener {
            setLocation(null)
        }

        img_clear_cate.setOnClickListener {
            setFiltterCategory(null)
        }

        containerLocation.setOnClickListener {
            FilterLocationVnDialog(object : FilterLocationVnDialog.LocationCallback {
                override fun getLocation(obj: MutableList<ICProvince>?) {
                    setLocation(obj)
                }
            }, selectedLocations).show(parentFragmentManager, null)
        }

        containerCate.setOnClickListener {
            val categoryDialog = FilterCategoryDialog(selectedCategory, object : FilterCategoryDialog.CategoryCallback {
                override fun getSelected(obj: MutableList<ICCategorySearch>?) {
                    setFiltterCategory(obj)
                }
            })
            categoryDialog.show(parentFragmentManager, null)
        }

        tvDone.setOnClickListener {
            dismiss()
            listener.filterPage(selectedLocations, switch_verfied.isChecked, selectedCategory)
        }
    }

    private fun setLocation(locations: MutableList<ICProvince>?) {
        selectedLocations = locations ?: mutableListOf()
        if (locations.isNullOrEmpty()) {
            tv_location.setTextColor(ColorManager.getSecondTextColor(dialog?.context!!))
            img_clear_location.visibility = View.GONE
            tv_location.text = getString(R.string.tat_ca)
        } else {
            tv_location.setTextColor(ColorManager.getPrimaryColor(dialog!!.context))
            img_clear_location.visibility = View.VISIBLE

            val list = mutableListOf<String>()

            for (i in 0 until if (locations.size > 3) {
                3
            } else {
                locations.size
            }) {
                list.add(locations[i].name)
            }

            var city = list.toString().substring(1, list.toString().length - 1)
            if (locations.size > 3) {
                city = "$city,..."
            }
            tv_location.text = city
        }
    }

    private fun setFiltterCategory(category: MutableList<ICCategorySearch>?) {
        selectedCategory = category ?: mutableListOf()
        if (category.isNullOrEmpty()) {
            tv_category.setTextColor(ColorManager.getSecondTextColor(dialog?.context!!))
            img_clear_cate.visibility = View.GONE
            tv_category.text = getString(R.string.tat_ca)
        } else {
            tv_category.setTextColor(ColorManager.getPrimaryColor(dialog!!.context))
            img_clear_cate.visibility = View.VISIBLE
            tv_category.text = category.last().name.toString()
        }
    }

    interface FilterPageCallback {
        fun filterPage(location: MutableList<ICProvince>?, verified: Boolean, category: MutableList<ICCategorySearch>?)
    }
}