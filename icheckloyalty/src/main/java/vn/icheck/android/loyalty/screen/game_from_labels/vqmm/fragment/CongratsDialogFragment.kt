package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.fragment

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.dialog_congrats.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.LuckyGameViewModel
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.LuckyGameViewModelFactory

class CongratsDialogFragment : DialogFragment() {

    private val args: CongratsDialogFragmentArgs by navArgs()

    private val luckyGameViewModel: LuckyGameViewModel by activityViewModels() {
        LuckyGameViewModelFactory(requireActivity(), args.campaignId, null)
    }

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
        tv_info.text = "Bạn có thêm ${args.playCount} lượt quay"
        img_close.setOnClickListener {
            dismiss()
        }
        btn_spin.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        luckyGameViewModel.updatePlay(args.playCount + args.currentCount)
        super.onDismiss(dialog)
    }
}