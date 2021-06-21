package vn.icheck.android.chat.icheckchat.screen.user_information

import android.graphics.Bitmap
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.BaseActivityChat
import vn.icheck.android.chat.icheckchat.base.ConstantChat.DATA_1
import vn.icheck.android.chat.icheckchat.base.ConstantChat.KEY
import vn.icheck.android.chat.icheckchat.base.ConstantChat.NAME
import vn.icheck.android.chat.icheckchat.base.recyclerview.IRecyclerViewCallback
import vn.icheck.android.chat.icheckchat.base.view.*
import vn.icheck.android.chat.icheckchat.databinding.ActivityUserInformationBinding
import vn.icheck.android.chat.icheckchat.helper.rText
import vn.icheck.android.chat.icheckchat.model.MCConversation
import vn.icheck.android.chat.icheckchat.model.MCMedia
import vn.icheck.android.chat.icheckchat.model.MCMessageEvent
import vn.icheck.android.chat.icheckchat.model.MCStatus
import vn.icheck.android.chat.icheckchat.sdk.ChatSdk.openActivity

class UserInformationActivity : BaseActivityChat<ActivityUserInformationBinding>(), IRecyclerViewCallback {

    private lateinit var viewModel: UserInformationViewModel

    val adapter = UserInformationAdapter(this)

    var deleteAt = -1L

    private var key: String? = null
    private var nameUser: String? = null

    private var kycStatus: Long? = null

    private var listTime = 0L

    override val bindingInflater: (LayoutInflater) -> ActivityUserInformationBinding
        get() = ActivityUserInformationBinding::inflate

    override fun onInitView() {
        viewModel = ViewModelProvider(this@UserInformationActivity)[UserInformationViewModel::class.java]

        initToolbar()
        setUpView()
    }

    private fun initToolbar() {
        binding.toolbar.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.toolbar.txtTitle.text = rText(R.string.cai_dat_tin_nhan)
    }

    private fun setUpView() {
        key = intent.getStringExtra(KEY)
        nameUser = intent.getStringExtra(NAME)

        binding.recyclerView.layoutManager = GridLayoutManager(this@UserInformationActivity, 3, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        viewModel.loginFirebase({
            binding.btnDeleteMessage.setOnClickListener {
                this.showConfirm(rText(R.string.xoa_cuoc_tro_chuyen), rText(R.string.message_delete_chat), rText(R.string.de_sau), rText(R.string.dong_y), false, object : ConfirmDialogListener {
                    override fun onDisagree() {

                    }

                    override fun onAgree() {
                        if (!key.isNullOrEmpty()) {
                            viewModel.deleteConversation(key!!).observe(this@UserInformationActivity, {
                                when (it.status) {
                                    MCStatus.ERROR_NETWORK -> {
                                        showToastError(it.message)
                                    }
                                    MCStatus.ERROR_REQUEST -> {
                                        showToastError(it.message)
                                    }
                                    MCStatus.SUCCESS -> {
                                        onBackPressed()
                                        EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.BACK))
                                    }
                                }
                            })
                        }
                    }
                })
            }
        }, {

        })

        if (!key.isNullOrEmpty()) {
            getChatRoom(key!!)
            getImage(0, key!!)
        }
    }

    private fun getChatRoom(key: String) {
        viewModel.getChatRoom(key, { obj ->
            if (obj.value != null) {
                var toId = ""
                var toType = ""
                var id = ""
                var notification = true

                if (obj.child("members").hasChildren()) {
                    for (item in obj.child("members").children) {
                        if (!FirebaseAuth.getInstance().uid.toString().contains(item.child("source_id").value.toString())) {
                            id = item.child("id").value.toString().trim()
                            toId = item.child("source_id").value.toString()
                            toType = item.child("type").value.toString()
                        } else {
                            notification = item.child("is_subscribe").value.toString().toBoolean()
                            deleteAt = if (item.child("deleted_at").value != null) {
                                item.child("deleted_at").value.toString().toLong()
                            } else {
                                -1
                            }
                        }
                    }
                }

                getChatSender(id, toType)

                binding.btnViewProfile.apply {
                    if (obj.child("members").hasChildren()) {
                        setVisible()

                        if (toType.contains("page")) {
                            binding.layoutImage.setVisible()
                            text = getString(R.string.xem_trang_doanh_nghiep)

                            setOnClickListener {
                                openActivity("page?id=$toId")
                            }
                        } else {
                            binding.layoutImage.setVisible()
                            text = getString(R.string.xem_trang_ca_nhan)

                            setOnClickListener {
                                openActivity("user?id=$toId")
                            }
                        }
                    } else {
                        setGone()
                    }
                }

                binding.btnBlock.apply {
                    text = if (toType.contains("page")) {
                        context.rText(R.string.chan_tin_nhan_tu_trang)
                    } else {
                        context.rText(R.string.chan_tin_nhan_tu_nguoi_nay)
                    }

                    if (obj.child("is_block").value != null) {
                        binding.btnBlock.isChecked = obj.child("is_block").child("status").value == true
                    } else {
                        binding.btnBlock.isChecked = false
                    }

                    setOnClickListener {
                        this@UserInformationActivity.showConfirm(context.rText(R.string.chan_tin_nhan), context.rText(R.string.message_block), context.rText(R.string.de_sau), context.rText(R.string.dong_y), false, object : ConfirmDialogListener {
                            override fun onDisagree() {

                            }

                            override fun onAgree() {
                                blockMessage(key, toId, toType)
                            }
                        })
                    }
                }

                binding.btnCheckedNotification.apply {
                    binding.btnCheckedNotification.isChecked = notification

                    setOnClickListener {
                        if (isChecked) {
                            turnOffNotification(key, FirebaseAuth.getInstance().uid.toString(), toType)
                        } else {
                            turnOnNotification(key, FirebaseAuth.getInstance().uid.toString(), toType)
                        }
                    }
                }
            }
        }, { error ->
            showToastError(error.message)
        })
    }

    private fun getImage(lastTimeStamp: Long, key: String) {

        viewModel.getImage(lastTimeStamp, key,
                { success ->
                    val listContent = mutableListOf<MCMedia>()

                    if (success.hasChildren()) {
                        for (item in success.children.reversed()) {
                            listTime = item.child("time").value.toString().toLong()

                            if (item.child("time").value.toString().toLong() > deleteAt) {

                                if (item.child("message").child("media").hasChildren()) {
                                    for (i in item.child("message").child("media").children) {
                                        listContent.add(MCMedia(i.child("content").value.toString(), i.child("type").value.toString()))
                                    }
                                }
                            }
                        }

                        if (listContent.isEmpty()) {
                            onLoadMore()
                            return@getImage
                        }
                    }

                    if (lastTimeStamp == 0L) {
                        if (listContent.isNullOrEmpty()) {
                            viewModel.checkError(true, dataEmpty = true)
                        } else {
                            adapter.setListDataMedia(listContent)
                        }
                    } else {
                        adapter.addListDataMedia(listContent)
                    }
                },
                { error ->
                    showToastError(error.message)
                })
    }

    private fun getChatSender(key: String, toType: String) {
        viewModel.getChatSender(key, { success ->

            if (if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        !this@UserInformationActivity.isDestroyed
                    } else {
                        TODO("VERSION.SDK_INT < JELLY_BEAN_MR1")
                    }) {
                if (toType.contains("page")) {
                    loadImageUrl(binding.imgAvatar, success.child("image").value.toString(), R.drawable.ic_default_avatar_page_chat, R.drawable.ic_default_avatar_page_chat)
                } else {
                    loadImageUrl(binding.imgAvatar, success.child("image").value.toString(), R.drawable.ic_user_default_52dp, R.drawable.ic_user_default_52dp)
                }
            }

            if (success.exists()) {
                kycStatus = success.child("kyc_status").value as Long? ?: 0L

                binding.imgAvatar.apply {
                    val ssb = SpannableStringBuilder(success.child("name").value.toString().replace("null", "") + "   ")

                    if (success.child("is_verify").value != null && success.child("is_verify").value.toString().toBoolean()) {
                        ssb.setSpan(getImageSpan(R.drawable.ic_verified_24dp_chat), ssb.length - 1, ssb.length, 0)

                        binding.tvNameUser.setText(ssb, TextView.BufferType.SPANNABLE)

                        if (toType.contains("page")) {
                            setBackgroundResource(R.drawable.ic_bg_avatar_page)
                        } else {
                            setBackgroundResource(0)
                        }

                    } else {
                        ssb.setSpan(getImageSpan(R.drawable.ic_verified_user_16px), ssb.length - 1, ssb.length, 0)

                        setBackgroundResource(0)

                        if (toType != "page" && kycStatus == 2L){
                            binding.tvNameUser.setText(ssb, TextView.BufferType.SPANNABLE)
                        }else{
                            binding.tvNameUser.text = success.child("name").value.toString().replace("null", "")
                        }
                    }
                }
            } else {
                binding.tvNameUser.text = success.child("name").value.toString().replace("null", "")
            }
        }, { error ->
            binding.tvNameUser.text = nameUser
        })
    }

    private fun blockMessage(key: String, toId: String, toType: String) {
        viewModel.blockMessage(key, toId, toType).observe(this@UserInformationActivity, {
            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    showToastError(it.message)
                }
                MCStatus.ERROR_REQUEST -> {
                    showToastError(it.message)
                }
                MCStatus.SUCCESS -> {
                    showToastSuccess(rText(R.string.ban_da_chan_tin_nhan_thanh_cong))
                    onBackPressed()
                    EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.BLOCK))
                }
            }
        })
    }

    private fun turnOffNotification(key: String, memberId: String, memberType: String) {
        viewModel.turnOffNotification(key, memberId, memberType).observe(this@UserInformationActivity, {
            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    showToastError(it.message)
                }
                MCStatus.ERROR_REQUEST -> {
                    showToastError(it.message)
                }
                MCStatus.SUCCESS -> {
                    binding.btnCheckedNotification.isChecked = false
                    EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.UPDATE_DATA))
                }
            }
        })
    }

    private fun turnOnNotification(key: String, memberId: String, memberType: String) {
        viewModel.turnOnNotification(key, memberId, memberType).observe(this@UserInformationActivity, {
            when (it.status) {
                MCStatus.ERROR_NETWORK -> {
                    showToastError(it.message)
                }
                MCStatus.ERROR_REQUEST -> {
                    showToastError(it.message)
                }
                MCStatus.SUCCESS -> {
                    binding.btnCheckedNotification.isChecked = true
                    EventBus.getDefault().post(MCMessageEvent(MCMessageEvent.Type.UPDATE_DATA))
                }
            }
        })
    }

    private fun getImageSpan(resource: Int): ImageSpan {
        val textTitle = AppCompatTextView(this@UserInformationActivity)
        textTitle.includeFontPadding = false
        textTitle.setCompoundDrawablesWithIntrinsicBounds(resource, 0, 0, 0)

        textTitle.isDrawingCacheEnabled = true
        textTitle.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        textTitle.layout(0, 0, textTitle.measuredWidth, textTitle.measuredHeight)
        textTitle.buildDrawingCache(true)
        val bitmap = Bitmap.createBitmap(textTitle.drawingCache)
        textTitle.isDrawingCacheEnabled = false

        return ImageSpan(this@UserInformationActivity, bitmap)
    }

    override fun onMessageClicked() {

    }

    override fun onLoadMore() {
        if (!key.isNullOrEmpty()) {
            getImage(listTime, key!!)
        }
    }
}