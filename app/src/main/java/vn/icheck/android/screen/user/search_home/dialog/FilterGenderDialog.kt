package vn.icheck.android.screen.user.search_home.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import kotlinx.android.synthetic.main.dialog_filter_gender.*
import kotlinx.android.synthetic.main.layout_title_filter_search.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.util.ick.logDebug
import java.util.*

class FilterGenderDialog(val genders: MutableList<String>?, val callback: GenderCallback) : BaseBottomSheetDialogFragment() {

    private val listSelected = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_filter_gender, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTitle.setText(R.string.gioi_tinh)
        listSelected.addAll(genders ?: mutableListOf())

        checkSelected()

        tvAll.setOnClickListener {
            listSelected.clear()
            checkSelected()
        }
        tvMale.setOnClickListener {
            setSelected(requireContext().getString(R.string.nam), tvMale)
        }
        tvFemale.setOnClickListener {
            setSelected(requireContext().getString(R.string.nu), tvFemale)
        }
        tvOther.setOnClickListener {
            setSelected(requireContext().getString(R.string.khac), tvOther)
        }

        tvClear.setOnClickListener {
            listSelected.clear()
            checkSelected()
        }

        imgClose.setOnClickListener {
            dismiss()
        }

        tvDone.setOnClickListener {
            if (listSelected.size == 2) {
                if (listSelected[0] == requireContext().getString(R.string.khac)) {
                    Collections.swap(listSelected, 0, 1)
                }
            }
            callback.getGender(listSelected)
            dismiss()
        }
    }


    private fun setSelected(gender: String, tv: AppCompatTextView) {
        tvAll.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

        if (listSelected.contains(gender)) {
            listSelected.remove(gender)
            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            listSelected.add(gender)
            tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)
        }
    }

    private fun checkSelected() {
        if (listSelected.isNullOrEmpty() || listSelected.size == 3) {
            tvAll.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)

            tvMale.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            tvFemale.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            tvOther.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            for (gender in listSelected) {
                when (gender) {
                    requireContext().getString(R.string.nam) -> {
                        tvMale.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)
                    }
                    requireContext().getString(R.string.nu) -> {
                        tvFemale.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)
                    }
                    else -> {
                        tvOther.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)
                    }
                }
            }
        }
    }

    interface GenderCallback {
        fun getGender(gender: MutableList<String>?)
    }
}
