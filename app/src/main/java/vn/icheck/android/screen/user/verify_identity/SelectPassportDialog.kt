package vn.icheck.android.screen.user.verify_identity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_select_passport.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment

class SelectPassportDialog(var passport: String?, val callback: SelectedPassportCallback) : BaseBottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_select_passport, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (passport == null) {
            selectPassport(requireContext().getString(R.string.chung_minh_nhan_dan))
        } else {
            selectPassport(passport!!)
        }

        btn_confirm.setOnClickListener {
            dismiss()
            callback.getSelectPassport(passport!!)
        }

        tvCmnd.setOnClickListener {
            selectPassport(requireContext().getString(R.string.chung_minh_nhan_dan))
        }
        tvCccd.setOnClickListener {
            selectPassport(requireContext().getString(R.string.can_cuoc_cong_dan))
        }

        imgClose.setOnClickListener {
            dismiss()
        }
    }

    private fun selectPassport(select: String) {
        if (select == requireContext().getString(R.string.chung_minh_nhan_dan)) {
            tvCmnd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)
            tvCccd.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            tvCccd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)
            tvCmnd.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
        passport = select
    }

    interface SelectedPassportCallback {
        fun getSelectPassport(passport: String)
    }
}