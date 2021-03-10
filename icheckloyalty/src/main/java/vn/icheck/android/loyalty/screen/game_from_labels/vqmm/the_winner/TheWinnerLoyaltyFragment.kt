package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.the_winner

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_the_winner_loyalty.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.fragment.BaseFragmentGame
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.dialog.DialogGuidePlayGame
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel.LuckyGameViewModel

class TheWinnerLoyaltyFragment : BaseFragmentGame(), IRecyclerViewCallback {

    private val args: TheWinnerLoyaltyFragmentArgs by navArgs()
    val luckyGameViewModel: LuckyGameViewModel by activityViewModels()

    private val adapter = TheWinnerLoyaltyAdapter(this)

    private val viewModel by viewModels<TheWinnerLoyaltyViewModel>()

    override val getLayoutID: Int
        get() = R.layout.fragment_the_winner_loyalty

    override fun onInitView() {
        viewModel.collectionID = args.campaignId

        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        txtTitle.text = args.title
    }

    override fun onDestroy() {
        super.onDestroy()
        luckyGameViewModel.updatePlay(args.currentCount)
    }


    private fun initRecyclerView() {
        var backgroundHeight = 0

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (backgroundHeight <= 0) {
                    backgroundHeight = toolbarAlpha.height + (toolbarAlpha.height / 2)
                }

                val visibility = viewModel.getHeaderAlpha(recyclerView.computeVerticalScrollOffset(), backgroundHeight)
                toolbarAlpha.alpha = visibility
                viewShadow.alpha = visibility
            }
        })
    }

    private fun initSwipeLayout() {
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
        viewModel.getTopWinnerLoyalty()
    }

    private fun initListener() {
        btnNhapMa.run {
            if (SharedLoyaltyHelper(requireContext()).getBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_VQMM)) {
                textView.text = getString(R.string.tiep_tuc_nhap_ma_du_thuong_de_ghi_danh_len_bang_vang_xep_hang_nao)
                text = getString(R.string.nhap_ma_de_them_luot_quay)
                setOnClickListener {
                    findNavController().popBackStack()
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.NMDT))
                }
            } else {
                textView.text = getString(R.string.tiep_tuc_quet_qr_code_du_thuong_de_ghi_danh_len_bang_vang_xep_hang_nao)
                text = getString(R.string.quet_ma_de_them_luot_quay)
                setOnClickListener {
                    object : DialogGuidePlayGame(requireContext()) {
                        override fun onClick() {
                            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.SCAN_GAME))
                            findNavController().popBackStack()
                        }
                    }.show()
                }
            }
        }

        viewModel.onError.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false

            if (adapter.isEmpty) {
                adapter.setError(it)
            } else {
                showLongError(it.title)
            }
        })
        viewModel.topWinner.observe(viewLifecycleOwner, Observer {
            if (it.listData.isNullOrEmpty()) {
                linearLayout.setVisible()
                recyclerView.setGone()
            } else {
                linearLayout.setGone()
                recyclerView.setVisible()

                adapter.addTopWinner(it)
            }
        })

        viewModel.onSetData.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            adapter.setTheWinner(it)
        })

        viewModel.onAddData.observe(viewLifecycleOwner, Observer {
            swipeLayout.isRefreshing = false
            adapter.addTheWinner(it)
        })
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getTheWinnerLoyalty(true)
    }
}