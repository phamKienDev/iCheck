package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list_game.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.model.RowsItem
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.adapter.GameListAdapter
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.GameListViewModel

class FragmentGameList : Fragment(), GameListAdapter.GameItemClickListener {

    private val gameListViewModel: GameListViewModel by activityViewModels<GameListViewModel>()
    private val gameListAdapter = GameListAdapter(this)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        pull_refresh.setOnRefreshListener {
            observe()
        }
    }

    override fun onResume() {
        super.onResume()
        observe()
    }

    private fun observe() {
        gameListViewModel.getListGames().observe(viewLifecycleOwner, Observer { gameRep ->
            pull_refresh.isRefreshing = false
            gameRep?.let { data ->
                try {
                    if (gameListViewModel.campaignId != -1L) {
                        val filter = data.data?.rows?.firstOrNull {
                            it?.campaignId == gameListViewModel.campaignId
                        }
                        if (filter != null) {
                            val name = filter.campaign?.owner?.name ?: ""
//                            val action = FragmentGameListDirections.actionFragmentListGameToOnBoardingGameFragment(filter.campaign?.id!!, name,
//                                    filter.campaign.image?.original!!, filter.campaign?.statusTime!!,
//                                    filter.campaign.name!!, filter, filter.campaign?.landingPage, filter.campaign?.titleButton, filter.campaign?.schemaButton)
//                            findNavController().navigate(action)
                            gameListViewModel.campaignId = -1L
                        }
                    } else {
                        if (data.data?.rows?.isNotEmpty()!!) {
                            hideNoGame()
                            gameListAdapter.submitList(data.data?.rows)
                        } else {
                            showNoGame()
                        }
                    }

                } catch (e: Exception) {
                    showNoGame()
                }
            } ?: kotlin.run {
                activity?.finish()
            }
        })
    }

    private fun initView() {
        rcv_list_game.adapter = gameListAdapter
        rcv_list_game.layoutManager = LinearLayoutManager(context)
        btn_back.setOnClickListener {
            activity?.finish()
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME))
        }
        btnCheckCodeNow.setOnClickListener {
            activity?.finish()
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.GO_TO_HOME, 5))
        }
    }

    private fun hideNoGame() {
        rcv_list_game.visibility = View.VISIBLE
        tv_no_game.visibility = View.GONE
        tv_desc_no_game.visibility = View.GONE
        img_no_game.visibility = View.GONE
        btnCheckCodeNow.visibility = View.GONE
    }

    private fun showNoGame() {
        rcv_list_game.visibility = View.GONE
        tv_no_game.visibility = View.VISIBLE
        tv_desc_no_game.visibility = View.VISIBLE
        img_no_game.visibility = View.VISIBLE
        btnCheckCodeNow.visibility = View.VISIBLE
    }

    override fun onItemClick(rowsItem: RowsItem) {
        if (rowsItem.campaign?.hasChanceCode != null) {
            SharedLoyaltyHelper(requireContext()).putBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_VQMM, rowsItem.campaign.hasChanceCode)
        } else {
//            DialogHelper.showNotification(context, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
//                override fun onDone() {
//                    DialogHelper.closeDialog()
//                }
//            })
        }
        val name = rowsItem.campaign?.owner?.name ?: ""
//        val action = FragmentGameListDirections.actionFragmentListGameToOnBoardingGameFragment(rowsItem.campaign?.id!!, name,
//                rowsItem.campaign.image?.original!!, rowsItem.campaign.statusTime!!,
//                rowsItem.campaign.name!!, rowsItem, rowsItem.campaign.landingPage, rowsItem.campaign.titleButton, rowsItem.campaign.schemaButton)
//        findNavController().navigate(action)
    }
}