package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_nmdt.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.LuckyGameViewModel
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.LuckyGameViewModelFactory

class NmdtDialogFragment : DialogFragment() {

    val args: NmdtDialogFragmentArgs by navArgs()
    private val luckyGameViewModel: LuckyGameViewModel by activityViewModels() {
        LuckyGameViewModelFactory(requireActivity(), args.campaignId, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.fragment_nmdt, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.game_lucky_wheel_dialog_animation
        btn_close.setOnClickListener {
            dismiss()
        }
        btn_xn.setOnClickListener {
            if (!edt_nmdt.text.isNullOrEmpty()) {
                luckyGameViewModel.receiveGame(edt_nmdt.text.toString().trim()).observe(viewLifecycleOwner, Observer { response ->
                    try {
                        if (response?.data?.play != null) {
                            val action = NmdtDialogFragmentDirections.actionNmdtDialogFragmentToCongratsDialogFragment(response.data.play.toInt(), args.currentCount, args.campaignId)
                            findNavController().navigate(action)
                        } else {
                            root.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.error_shake))
                            tv_error.visibility = View.VISIBLE
                            tv_error.text = response?.data?.message ?: "Mã ${edt_nmdt.text} đã được sử dụng"
                        }
                    } catch (e: Exception) {
                        tv_error.visibility = View.VISIBLE
                        tv_error.text =  "Mã ${edt_nmdt.text} đã được sử dụng"
                        root.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.error_shake))
                    }
                })
            }else{
                tv_error.visibility = View.VISIBLE
                tv_error.text =  "Bạn chưa nhập mã dự thưởng"
                root.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.error_shake))
            }
        }
    }
}