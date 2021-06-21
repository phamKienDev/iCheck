package vn.icheck.android.chat.icheckchat.screen.detail_image

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.yarolegovich.discretescrollview.DiscreteScrollView
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.BaseActivityChat
import vn.icheck.android.chat.icheckchat.base.ConstantChat.DATA_1
import vn.icheck.android.chat.icheckchat.base.ConstantChat.IMAGE
import vn.icheck.android.chat.icheckchat.base.ConstantChat.POSITION
import vn.icheck.android.chat.icheckchat.base.view.showToastError
import vn.icheck.android.chat.icheckchat.databinding.ActivityImageDetailBinding
import vn.icheck.android.chat.icheckchat.helper.MCExoMedia
import vn.icheck.android.chat.icheckchat.helper.NetworkHelper.parseListAttachment
import vn.icheck.android.chat.icheckchat.helper.NetworkHelper.toJson
import vn.icheck.android.chat.icheckchat.model.MCMedia
import vn.icheck.android.chat.icheckchat.model.MCMessageEvent

class ImageDetailActivity : BaseActivityChat<ActivityImageDetailBinding>() {

    companion object {
        fun startImageDetail(context: Context, listMedia: MutableList<MCMedia>, position: Int) {
            val json = toJson(listMedia)
            context.startActivity(Intent(context, ImageDetailActivity::class.java).apply {
                putExtra(DATA_1, json)
                putExtra(POSITION, position)
            })
        }
    }

    private val adapter = ImageDetailAdapter()

    private var positionView: Int = -1

    override val bindingInflater: (LayoutInflater) -> ActivityImageDetailBinding
        get() = ActivityImageDetailBinding::inflate

    override fun onInitView() {
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        initRecyclerView()
    }

    @SuppressLint("SetTextI18n")
    private fun initRecyclerView() {

        binding.recyclerView.adapter = adapter

        val listData = parseListAttachment(intent.getStringExtra(DATA_1))

        if (listData.isNullOrEmpty()) {
            showToastError(getString(R.string.error_default))
            finish()
        } else {
            val listExo = mutableListOf<MCExoMedia>()
            for (i in listData) {
                listExo.add(MCExoMedia(i.content, i.type).also {
                    it.checkTypeMedia()
                })
            }

            adapter.setData(listExo)

            val positionIntent = intent.getIntExtra(POSITION, -1)

            if (positionIntent != -1) {
                binding.recyclerView.scrollToPosition(positionIntent)
                binding.tvSlide.text = "${positionIntent + 1}/${listExo.size}"
                listExo[positionIntent].exoPlayer?.playWhenReady = true
                positionView = positionIntent
            }else{
                binding.tvSlide.text = "1/${listExo.size}"
                listExo[0].exoPlayer?.playWhenReady = true
                positionView = 0
            }

            binding.recyclerView.addScrollStateChangeListener(object : DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder> {
                override fun onScroll(p0: Float, p1: Int, p2: Int, p3: RecyclerView.ViewHolder?, p4: RecyclerView.ViewHolder?) {

                }

                override fun onScrollEnd(p0: RecyclerView.ViewHolder, p1: Int) {
                    adapter.getListData[p1].exoPlayer?.playWhenReady = true
                    binding.tvSlide.text = "${p1 + 1}/${listExo.size}"
                    if (positionView != p1) {
                        if (adapter.getListData[positionView].type == IMAGE) {
                            adapter.getListData[positionView].resetImage = true
                            adapter.notifyItemChanged(positionView)
                        }
                    }
                    positionView = p1
                }

                override fun onScrollStart(p0: RecyclerView.ViewHolder, p1: Int) {
                    adapter.getListData[p1].exoPlayer?.playWhenReady = false
                }
            })
        }
    }

    override fun onMessageEvent(event: MCMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            MCMessageEvent.Type.IS_SCROLL_MEDIA -> {
                binding.recyclerView.post {
                    if (event.data != null) {
                        binding.recyclerView.suppressLayout(false)
                    } else {
                        binding.recyclerView.suppressLayout(true)
                    }
                }
            }
            else -> {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        for (i in adapter.getListData) {
            i.release()
        }
    }
}