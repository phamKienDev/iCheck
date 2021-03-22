package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.onboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_onboard.*
import kotlinx.android.synthetic.main.fragment_onboard_running.*
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.awaitAll
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.model.RowsItem
import vn.icheck.android.loyalty.room.database.LoyaltyDatabase
import vn.icheck.android.loyalty.room.entity.ICKCampaignId
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.GameActivity
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.GameListViewModel

class OnBoardingGameFragment : Fragment(), NavigateOnBoardListener {

    private val gameListViewModel: GameListViewModel by activityViewModels<GameListViewModel>()
    lateinit var onBoardRunningAdapter: OnBoardRunningAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        gameListViewModel.campaignId = activity?.intent?.getLongExtra(ConstantsLoyalty.DATA_1, -1L) ?: -1
        gameListViewModel.owner = activity?.intent?.getStringExtra("owner") ?: ""
        gameListViewModel.banner = activity?.intent?.getStringExtra("banner") ?: ""
        gameListViewModel.state = activity?.intent?.getStringExtra("state") ?: ""
        gameListViewModel.campaignName = activity?.intent?.getStringExtra("campaignName") ?: ""
        gameListViewModel.landing = activity?.intent?.getStringExtra("landing")
        gameListViewModel.titleButton = activity?.intent?.getStringExtra("titleButton")
        gameListViewModel.schema = activity?.intent?.getStringExtra("schema")
        gameListViewModel.data = activity?.intent?.getSerializableExtra("data") as RowsItem?

        return when (gameListViewModel.state) {
            "PENDING", "COMPLETED" -> inflater.inflate(R.layout.fragment_onboard, container, false)
            "RUNNING" -> inflater.inflate(R.layout.fragment_onboard_running, container, false)
            else -> {
                null
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (gameListViewModel.state.isNullOrEmpty()) {
            requireActivity().finish()
            return
        }

        val id = LoyaltyDatabase.getDatabase(ApplicationHelper.getApplicationByReflect()).idCampaignDao().getIDCampaignByID(gameListViewModel.campaignId)?.id
        if (id == gameListViewModel.campaignId) {
            if (gameListViewModel.data != null) {
                val action = OnBoardingGameFragmentDirections.actionOnBoardingGameFragmentToFragmentLoadingGame(gameListViewModel.campaignId, gameListViewModel.owner, gameListViewModel.data!!)
                findNavController().navigate(action)
            }
        } else {
            when (gameListViewModel.state) {
                "RUNNING" -> {
                    onBoardRunningAdapter = OnBoardRunningAdapter(childFragmentManager, this, gameListViewModel.landing, gameListViewModel.titleButton, gameListViewModel.schema, gameListViewModel.campaignId.toString())
                    onboard_running.adapter = onBoardRunningAdapter
                }
                "PENDING" -> {

                }
                "COMPLETED" -> {
                    Glide.with(requireContext().applicationContext)
                            .load(gameListViewModel.banner)
                            .into(banner)
                    tv_event_name.text = gameListViewModel.campaignName
                    btn_wares.setOnClickListener {

                    }
                }
            }
        }

    }

    override fun onFinalStep() {
        LoyaltyDatabase.getDatabase(ApplicationHelper.getApplicationByReflect()).idCampaignDao().insertIDCampaign(ICKCampaignId(gameListViewModel.campaignId))
        if (gameListViewModel.data != null) {
            val action = OnBoardingGameFragmentDirections.actionOnBoardingGameFragmentToFragmentLoadingGame(gameListViewModel.campaignId, gameListViewModel.owner, gameListViewModel.data!!)
            findNavController().navigate(action)
        }
    }

    override fun onNextStep() {
        val cur = onboard_running.currentItem
        onboard_running.setCurrentItem(cur + 1, false)
    }

    override fun onPrev() {
        val cur = onboard_running.currentItem
        onboard_running.setCurrentItem(cur - 1, false)
    }

    override fun onBack() {
        activity?.finish()
    }
}