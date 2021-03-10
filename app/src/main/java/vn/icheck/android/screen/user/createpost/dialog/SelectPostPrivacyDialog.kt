package vn.icheck.android.screen.user.createpost.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_create_or_update_post.*
import kotlinx.android.synthetic.main.dialog_select_post_privacy.*
import kotlinx.android.synthetic.main.dialog_select_post_privacy_item.view.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.network.models.ICPrivacy

abstract class SelectPostPrivacyDialog(context: Context, private val listPrivacy: MutableList<ICPrivacy>) : BaseBottomSheetDialog(context, R.layout.dialog_select_post_privacy, true) {
    private var selectedPosition = -1

    fun show() {
        setupView()
        setupListener()
        dialog.show()
    }

    private fun setupView() {
        dialog.layoutContent.apply {
            for (i in 0 until listPrivacy.size) {
                val parent = LayoutInflater.from(context).inflate(R.layout.dialog_select_post_privacy_item, this, false) as ViewGroup

                if (listPrivacy[i].selected) {
                    selectedPosition = i
                    parent.imgPublic.setImageResource(R.drawable.ic_radio_on_24dp)
                } else {
                    parent.imgPublic.setImageResource(R.drawable.ic_radio_un_checked_gray_24dp)
                }
                parent.tvPublicName.text = listPrivacy[i].privacyElementName
                parent.tvPublicContent.text = listPrivacy[i].privacyElementDescription

                parent.setOnClickListener {
                    unSelected(selectedPosition)
                    selectedPosition = i
                    selected(selectedPosition)
                }

                addView(parent, childCount - 1)
            }
        }

        if (listPrivacy.isNotEmpty() && selectedPosition == -1) {
            selectedPosition = 0
            listPrivacy[0].selected = true
            selected(0)
        }
    }

    private fun setupListener() {
        dialog.imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.btnDone.setOnClickListener {
            dialog.dismiss()
            onDone(listPrivacy[selectedPosition])
        }
    }

    private fun unSelected(position: Int) {
        (dialog.layoutContent.getChildAt(position) as ViewGroup).imgPublic.apply {
            setImageResource(R.drawable.ic_radio_un_checked_gray_24dp)
        }
    }

    private fun selected(position: Int) {
        (dialog.layoutContent.getChildAt(position) as ViewGroup).imgPublic.apply {
            setImageResource(R.drawable.ic_radio_on_24dp)
        }
    }

    protected abstract fun onDone(privacy: ICPrivacy)
}