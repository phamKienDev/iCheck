package vn.icheck.android.screen.user.search_home.dialog

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.dialog_filter_page_search.*
import kotlinx.android.synthetic.main.dialog_filter_user_search.*
import kotlinx.android.synthetic.main.dialog_filter_user_search.containerLocation
import kotlinx.android.synthetic.main.dialog_filter_user_search.img_clear_location
import kotlinx.android.synthetic.main.dialog_filter_user_search.tv_clear
import kotlinx.android.synthetic.main.dialog_filter_user_search.tv_location
import kotlinx.android.synthetic.main.layout_title_filter_search.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICProvince
import vn.icheck.android.util.ick.beVisible

abstract class FilterUserDialog(val activity: FragmentActivity, val city: MutableList<ICProvince>?,val gender: MutableList<String>?) : BaseBottomSheetDialog(activity, R.layout.dialog_filter_user_search, true) {
    private var selectedCity = mutableListOf<ICProvince>()
    private var selectedGender = mutableListOf<String>()

    fun show() {
        dialog.tvTitle.setText(R.string.bo_loc)
        selectedCity.addAll(city ?: mutableListOf())

        setLocation(selectedCity)
        setGender(gender)

        dialog.tv_clear.setOnClickListener {
            setLocation(null)
            setGender(null)
        }

        dialog.containerLocation.setOnClickListener {
            FilterLocationVnDialog(object : FilterLocationVnDialog.LocationCallback {
                override fun getLocation(obj: MutableList<ICProvince>?) {
                    setLocation(obj)
                }
            }, selectedCity).show(activity.supportFragmentManager, null)
        }

        dialog.containerGender.setOnClickListener {
            FilterGenderDialog(selectedGender, object : FilterGenderDialog.GenderCallback {
                override fun getGender(gender: MutableList<String>?) {
                    setGender(gender)
                }
            }).show(activity.supportFragmentManager, null)
        }

        dialog.imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.tvDone.setOnClickListener {
            dialog.dismiss()
            onSelected(selectedCity, selectedGender)
        }

        dialog.img_clear_gender.setOnClickListener {
            setGender(null)
        }

        dialog.img_clear_location.setOnClickListener {
            setLocation(null)
        }

        dialog.show()
    }

    private fun setLocation(list: MutableList<ICProvince>?) {
        selectedCity = list ?: mutableListOf()
        if (list.isNullOrEmpty()) {
            dialog.tv_location.setText(R.string.tat_ca)
            dialog.img_clear_location.visibility = View.GONE
            dialog.tv_location.setTextColor(ContextCompat.getColor(ICheckApplication.getInstance(), R.color.fast_survey_gray))
        } else {
            if (list[0].id != -1L) {
                val names = mutableListOf<String>()
                for (i in 0 until if (list.size > 3) {
                    3
                } else {
                    list.size
                }) {
                    names.add(list[i].name)
                }

                var city = names.toString().substring(1, names.toString().length - 1)
                if (list.size > 3) {
                    city = "$city,..."
                }

                dialog.tv_location.text = city
                dialog.tv_location.setTextColor(ContextCompat.getColor(ICheckApplication.getInstance(), R.color.lightBlue))

                dialog.img_clear_location.beVisible()
            } else {
                dialog.tv_location.setText(R.string.tat_ca)
                dialog.img_clear_location.visibility = View.GONE
                dialog.tv_location.setTextColor(ContextCompat.getColor(ICheckApplication.getInstance(), R.color.fast_survey_gray))
            }
        }
    }

    private fun setGender(gender: MutableList<String>?) {
        selectedGender.clear()
        selectedGender.addAll(gender ?: mutableListOf())

        if (selectedGender.isNullOrEmpty() || selectedGender.size == 3) {
            dialog.tv_gender.setText(R.string.tat_ca)
            dialog.img_clear_gender.visibility = View.GONE
            dialog.tv_gender.setTextColor(ContextCompat.getColor(ICheckApplication.getInstance(), R.color.fast_survey_gray))
        } else {
            dialog.tv_gender.setText(selectedGender.toString().substring(1, selectedGender.toString().length - 1))
            dialog.img_clear_gender.visibility = View.VISIBLE
            dialog.tv_gender.setTextColor(ContextCompat.getColor(ICheckApplication.getInstance(), R.color.lightBlue))
        }
    }

    protected abstract fun onSelected(city: MutableList<ICProvince>?, gender: MutableList<String>?)
}