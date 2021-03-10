package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.onboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_onboard_second.*
import vn.icheck.android.loyalty.R

class OnBoardSecondFragment(val navigateOnBoardListener: NavigateOnBoardListener) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_onboard_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_prev.setOnClickListener {
            navigateOnBoardListener.onPrev()
        }
        btn_continue.setOnClickListener {
            navigateOnBoardListener.onNextStep()
        }
        btn_back.setOnClickListener {
            navigateOnBoardListener.onBack()
        }
    }
}