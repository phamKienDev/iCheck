package vn.icheck.android.loyalty.screen.game_from_labels.vqmm

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_congrats.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty

class GlobalCongratsGameDialog(private val gamePlayTurn:Int, val id: Long): DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.dialog_congrats, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.game_lucky_wheel_dialog_animation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_info.text = "Bạn có thêm $gamePlayTurn lượt quay"
        img_close.setOnClickListener {
            dismiss()
        }
        btn_spin.setOnClickListener {
            requireContext().startActivity(Intent(requireContext(), GameActivity::class.java).apply {
                putExtra(ConstantsLoyalty.DATA_1, id)
            })
            dismiss()
        }
    }
}