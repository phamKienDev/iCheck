package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.listofgiftsreceived

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list_of_gifts_received.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.fragment.BaseFragmentGame
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICMessageEvent
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.dialog.DialogGuidePlayGame
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.LuckyGameViewModel

class ListOfGiftsReceivedFragment : BaseFragmentGame(), IRecyclerViewCallback {
    val args: ListOfGiftsReceivedFragmentArgs by navArgs()

    private val viewModel by viewModels<ListOfGiftsReceivedViewModel>()

    private val adapter = ListOfGiftsReceivedAdapter(this)

    val luckyGameViewModel: LuckyGameViewModel by activityViewModels()

    private var errorApi = false

    override val getLayoutID: Int
        get() = R.layout.fragment_list_of_gifts_received

    override fun onInitView() {
        viewModel.collectionID = args.campaignId

        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    fun initToolbar() {
        imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            adapter.disableLoadMore()
            getData()
        }
        swipeLayout.post {
            getData()
        }
    }

    fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getListOfGiftsReceived()
    }

    fun initListener() {
        viewModel.onSetData.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            adapter.setListData(it)
        })

        viewModel.onAddData.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            adapter.addListData(it)
        })

        viewModel.onError.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false

            if (adapter.isEmpty) {
                errorApi = false
                adapter.setError(it)
            } else {
                showLongError(it.title)
            }
        })

        viewModel.onErrorEmpty.observe(viewLifecycleOwner, Observer {
            errorApi = true
            swipeLayout.isRefreshing = false
            adapter.setError(it)
        })
    }

    override fun onMessageClicked() {
        if (errorApi) {
            if (SharedLoyaltyHelper(requireContext()).getBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_VQMM)) {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.NMDT))
                findNavController().popBackStack()
            } else {
                object : DialogGuidePlayGame(requireContext()) {
                    override fun onClick() {
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.SCAN_GAME))
                        findNavController().popBackStack()
                    }
                }.show()
            }
        } else {
            getData()
        }
    }

    override fun onLoadMore() {
        viewModel.getListOfGiftsReceived(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        errorApi = false
        luckyGameViewModel.updatePlay(args.currentCount)
    }
}