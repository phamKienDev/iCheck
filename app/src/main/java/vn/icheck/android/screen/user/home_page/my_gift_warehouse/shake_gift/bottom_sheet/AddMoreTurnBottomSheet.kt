package vn.icheck.android.screen.user.home_page.my_gift_warehouse.shake_gift.bottom_sheet

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.bottom_sheet_add_more_turn.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog

class AddMoreTurnBottomSheet(val context: Context) : BaseBottomSheetDialog(context,R.layout.bottom_sheet_add_more_turn, true) {

    private val adapter = AddMoreTurnAdapter(dialog.context)

    fun show() {
        dialog.show()
        initRecyclerView()
        listener()
    }

    private fun initRecyclerView() {
        dialog.recyclerView.layoutManager = LinearLayoutManager(dialog.context)
        dialog.recyclerView.adapter = adapter
    }

    private fun listener() {
        dialog.imgCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.btnInforCampaign.setOnClickListener {

        }
    }

}