package vn.icheck.android.screen.user.wall.mainuser

import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.helper.RelationshipHelper
import vn.icheck.android.base.holder.ShortMessageHolder
import vn.icheck.android.chat.icheckchat.screen.detail.ChatSocialDetailActivity
import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.component.friendrequestwall.FriendRequestWallHolder
import vn.icheck.android.component.friendsuggestion.FriendSuggestionComponent
import vn.icheck.android.component.post.IPostListener
import vn.icheck.android.component.post.PostHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.*
import vn.icheck.android.databinding.FriendInWallHolderBinding
import vn.icheck.android.databinding.ItemCreatePostBinding
import vn.icheck.android.databinding.ItemUserProfileWallBinding
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableColor
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.model.posts.PostViewModel
import vn.icheck.android.network.model.profile.IckUserFriendModel
import vn.icheck.android.network.model.profile.IckUserProfileModel
import vn.icheck.android.network.models.ICSearchUser
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.screen.user.campaign.calback.IMessageListener
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.screen.user.wall.ICWallModel
import vn.icheck.android.screen.user.wall.holder.friend.FriendWallHolder
import vn.icheck.android.util.ick.*

class IckUserWallAdapter(val postListener: IPostListener, val messageListener: IMessageListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = arrayListOf<ICViewModel>()
    var friendListPos = -1
    var iconMessage:Int?=null
    var errorMessage:String?=null

    fun updateList(list: List<ICViewModel>) {
        iconMessage=null
        errorMessage=null

        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setError(icon:Int?,message:String?){
        iconMessage=icon
        errorMessage=message
        notifyDataSetChanged()
    }

    fun addPosts(list: List<ICViewModel>) {
        iconMessage=null
        errorMessage=null

        listData.addAll(list)
        val set = mutableSetOf<ICViewModel>()
        set.addAll(listData)
        listData.clear()
        listData.addAll(set)
        notifyDataSetChanged()
    }

    fun addPost(position: Int, postModel: PostViewModel) {
        listData.add(position, postModel)
        notifyItemInserted(position)
    }

    fun updatePost(position: Int, postModel: PostViewModel) {
        listData.set(position, postModel)
        notifyItemChanged(position)
    }

    fun deletePost(postId: Long) {
        var positionRemove = -1
        for (i in 0 until listData.size) {
            if (listData[i].getViewType() == ICViewTypes.ITEM_USER_POST) {
                if ((listData[i] as PostViewModel).postData.id == postId) {
                    positionRemove = i
                }
            }
        }
        if (positionRemove != -1) {
            listData.removeAt(positionRemove)
            notifyItemRemoved(positionRemove)
        }
    }

    fun notifyFriendList() {
        if (friendListPos != -1) {
            notifyItemChanged(friendListPos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICViewTypes.PROFILE_USER -> ProfileUserHolder(ItemUserProfileWallBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ICViewTypes.FRIEND_WALL -> FriendWallHolder(FriendInWallHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ICViewTypes.FRIEND_INVITATION_TYPE -> FriendRequestWallHolder(parent)
            ICViewTypes.FRIEND_SUGGESTION_TYPE -> FriendSuggestionComponent(parent)
            ICViewTypes.ITEM_CREATE_POST -> CreatePostHolder(parent)
            ICViewTypes.ITEM_USER_POST -> PostHolder(parent, postListener)
            ICViewTypes.MESSAGE_TYPE -> ShortMessageHolder(parent)
            else -> NullHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return if(iconMessage!=null || !errorMessage.isNullOrBlank()){
            1
        }else{
            listData.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            when (holder.itemViewType) {
                ICViewTypes.PROFILE_USER -> {
                    (holder as ProfileUserHolder).bind(listData[position] as IckUserProfileModel)
                }
                ICViewTypes.FRIEND_WALL -> {
                    friendListPos = position
                    (holder as FriendWallHolder).bind(listData[position] as IckUserFriendModel)
                }
                ICViewTypes.FRIEND_INVITATION_TYPE -> {
                    (holder as FriendRequestWallHolder).apply {
                        bind((listData[position] as ICWallModel).data as ICListResponse<ICSearchUser>)

                        setOnRemoveListener {
                            listData.removeAt(position)
                            notifyItemRemoved(position)
                        }
                    }
                }
                ICViewTypes.FRIEND_SUGGESTION_TYPE -> {
                    (holder as FriendSuggestionComponent).apply {
                        bind(((listData[position] as ICWallModel).data as ICListResponse<ICUser>).rows)

                        setOnRemoveListener {
                            listData.removeAt(position)
                            notifyItemRemoved(position)
                        }
                    }
                }
                ICViewTypes.ITEM_USER_POST -> {
                    val postViewModel = listData[position] as PostViewModel
                    (holder as PostHolder).bind(postViewModel)
                }
                ICViewTypes.MESSAGE_TYPE->{
                    holder.itemView.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                    (holder as ShortMessageHolder).apply {
                        bind(iconMessage,errorMessage)
                        setListener {
                            messageListener.onMessageClicked()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logError(e)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(!errorMessage.isNullOrBlank() && iconMessage!=null){
            ICViewTypes.MESSAGE_TYPE
        } else{
            listData[position].getViewType()
        }
    }
}

class ProfileUserHolder(val binding: ItemUserProfileWallBinding) : RecyclerView.ViewHolder(binding.root) {

    private var isMyFriendInvitationUser: Boolean? = null // m??nh g???i k???t b???n cho ng?????i kh??c
    private var isMyFriend: Boolean? = null // b???n b?? c???a m??nh
    private var isFriendInvitationMeUser: Boolean? = null // ng?????i kh??c g???i k???t b???n cho m??nh

    fun bind(ickUserProfileModel: IckUserProfileModel) {

        isMyFriendInvitationUser = null
        isMyFriend = null
        isFriendInvitationMeUser = null

        binding.tvRequestSent.background=vn.icheck.android.ichecklibs.ViewHelper.bgGrayCorners4(binding.tvRequestSent.context)
        binding.btnSetting.fillDrawableColor(R.drawable.ic_btn_setting_blue)

        val data = ickUserProfileModel.profile.data
        Glide.with(binding.root.context.applicationContext)
            .load(data?.avatar)
            .error(R.drawable.ic_avatar_default_84dp)
            .placeholder(R.drawable.ic_avatar_default_84dp)
            .into(binding.userAvatar)
        if (!data?.avatar.isNullOrEmpty()) {
            binding.userAvatar.setOnClickListener {
                DetailMediaActivity.start(it.context, arrayListOf(data?.avatar))
            }
        }

        binding.tvName.apply {
            text = if (data?.lastName.isNullOrEmpty() && data?.firstName.isNullOrEmpty()) {
                data?.getPhoneOnly()
            } else {
                Constant.getName(data?.lastName, data?.firstName)
            }

            if (data?.kycStatus == 2) {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_user_24dp, 0)
            } else {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }

        if (!data?.background.isNullOrEmpty()) {
            Glide.with(binding.root.context)
                .load(data?.background)
                .error(R.drawable.left_menu_bg)
                .into(binding.imgWallCover)
            binding.imgWallCover.setOnClickListener {
                DetailMediaActivity.start(it.context, arrayListOf(data?.background))
            }
        }

        binding.btnSendMsg.setOnClickListener {
            if (SessionManager.isUserLogged) {
                ChatSocialDetailActivity.createRoomChat(it.context, data?.id ?: -1, "user")
            } else {
                ICheckApplication.currentActivity()?.let { act ->
                    (act as FragmentActivity).showLogin()
                }
            }
        }

        binding.icCrow.setRankUser36dp(data?.rank?.level)

        if (ickUserProfileModel.profile.getInfoPrivacy() == Privacy.ONLY_ME) {
            binding.groupAddress.beGone()
            binding.groupAcc.beGone()
            binding.groupFollowing.beGone()
            binding.groupFollowed.beGone()
            binding.moreInfo.beGone()
        } else {
            binding.groupAddress goneIf data?.infoPrivacyConfig?.city
            binding.groupAcc goneIf data?.infoPrivacyConfig?.phone
            binding.groupFollowing goneIf data?.infoPrivacyConfig?.email
            binding.groupFollowed goneIf data?.infoPrivacyConfig?.gender
            binding.moreInfo goneIf data?.infoPrivacyConfig?.birthday
            binding.tvAddress.text = data?.city?.name.getInfo()
            data?.id?.let{
                binding.tvId.setText(R.string.ic_d, it)
            }
            if (SessionManager.session.user?.id == ickUserProfileModel.id) {
                binding.tvFollow.text = "${RelationshipHelper.getTotalFollowed()}"
                binding.tvWatch.text = "${RelationshipHelper.getTotalFollow()}"
                binding.groupFollowing.beVisible()
            } else {
                binding.tvFollow.text = "${data?.userFollowingMeCount ?: 0}"
                binding.tvWatch.text = "${data?.myFollowingUserCount ?: 0}"
                binding.groupFollowing.beGone()
            }
            binding.moreInfo.beVisible()
        }

        binding.btnAddFriend.background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(itemView.context)
        binding.imgSettings.background = vn.icheck.android.ichecklibs.ViewHelper.bgOutlinePrimary1Corners4(itemView.context)

        when (ickUserProfileModel.id) {
            SessionManager.session.user?.id -> showMainUser()
            else -> checkStatusFirebase(ickUserProfileModel)
        }
    }

    private fun checkStatusFirebase(model: IckUserProfileModel) {
        if (ICheckApplication.getInstance().mFirebase.auth.currentUser != null) {
            //ng?????i kh??c g???i k???t b???n cho m??nh
            ICheckApplication.getInstance().mFirebase.registerRelationship(
                Constant.friendInvitationMeUserIdList,
                model.id.toString(),
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        isFriendInvitationMeUser = snapshot.value != null && snapshot.value is Long
                        checkStatusElseUser(model)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        isFriendInvitationMeUser = false
                        checkStatusElseUser(model)
                    }
                })

            //m??nh g???i k???t b???n ?????n ng?????i kh??c
            ICheckApplication.getInstance().mFirebase.registerRelationship(Constant.myFriendInvitationUserIdList,
                model.id.toString(),
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        isMyFriendInvitationUser = snapshot.value != null && snapshot.value is Long
                        checkStatusElseUser(model)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        isMyFriendInvitationUser = false
                        checkStatusElseUser(model)

                    }
                })

            //friend c???a m??nh
            ICheckApplication.getInstance().mFirebase.registerRelationship(
                Constant.myFriendIdList,
                model.id.toString(),
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        isMyFriend = snapshot.value != null && snapshot.value is Long
                        checkStatusElseUser(model)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        isMyFriend = false
                        checkStatusElseUser(model)
                    }
                })
        }
    }

    private fun checkStatusElseUser(model: IckUserProfileModel) {
        if (isFriendInvitationMeUser != null && isMyFriend != null && isMyFriendInvitationUser != null) {
            when {
                isMyFriend!! -> {
                    binding.groupMainUser.beGone()
                    binding.groupFriend.beVisible()
                    Handler().postDelayed({
                        binding.tvRequestSent.beGone()
                        binding.btnAddFriend.beGone()
                    }, 100)
                    binding.btnSendMsg.background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(itemView.context)
                    initClickElseUser()
                    binding.btnSendMsg.setTextColor(Color.WHITE)
                }
                else -> {
                    when {
                        isFriendInvitationMeUser!! -> {
                            checkPrivacyElseUser(model, isFriendInvitationMeUser)
                        }
                        isMyFriendInvitationUser!! -> {
                            checkPrivacyElseUser(model, isMyFriendInvitationUser)
                        }
                        else -> {
                            checkPrivacyElseUser(model, null)
                        }
                    }
                    initClickElseUser()

                    binding.groupMainUser.beGone()
                    binding.groupFriend.beVisible()
                    vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(itemView.context)
                    binding.btnSendMsg.setTextColor(vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(itemView.context))
                    binding.btnSendMsg.background = vn.icheck.android.ichecklibs.ViewHelper.bgOutlinePrimary1Corners4(itemView.context)
                    binding.btnAddFriend.beVisible()
                }
            }
        }
    }

    private fun initClickElseUser() {
        binding.imgSettings.setOnClickListener {
            if (SessionManager.isUserLogged) {
                it.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                    putExtra(USER_WALL_BROADCAST, USER_WALL_FRIEND_SETTINGS)
                })
            } else {
                ICheckApplication.currentActivity()?.let { act ->
                    (act as FragmentActivity).showLogin()
                }
            }
        }
        binding.moreInfo.setOnClickListener {
            it.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                putExtra(USER_WALL_BROADCAST, USER_WALL_SHOW_PUBLIC_INFO)
            })
        }
        binding.btnAddFriend.setOnClickListener {
            if (SessionManager.isUserLogged) {
                if ((binding.tvAddFriend.text == binding.tvAddFriend.context.getString(R.string.dong_y_ket_ban)||binding.tvAddFriend.text == "?????ng ?? k???t b???n")) {
                    it.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                        putExtra(USER_WALL_BROADCAST, USER_WALL_ACCEPT_FRIEND)
                    })
                } else {
                    binding.btnAddFriend.beGone()
                    binding.tvRequestSent.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    binding.tvRequestSent.beVisible()
                    it.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                        putExtra(USER_WALL_BROADCAST, USER_WALL_ADD_FRIEND)
                    })
                }

            } else {
                ICheckApplication.currentActivity()?.let { act ->
                    (act as FragmentActivity).showLogin()
                }
            }
        }
    }

    private fun checkPrivacyElseUser(model: IckUserProfileModel, status: Boolean?) {
        if (model.profile.getInvitePrivacy() == Privacy.EVERYONE) {
            when (status) {
                isMyFriendInvitationUser -> {
                    Handler().postDelayed({
                        binding.btnAddFriend.beGone()
                    }, 100)
                    binding.tvRequestSent.beVisible()
                }
                isFriendInvitationMeUser -> {
                    binding.btnAddFriend.beVisible()
                    binding.tvAddFriend.setText(R.string.dong_y_ket_ban)
                    binding.tvAddFriend.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    binding.tvRequestSent.beGone()
                }
                else -> {
                    binding.tvAddFriend.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_add_white_12px, 0, 0, 0)
                    binding.tvAddFriend.setText(R.string.ket_ban)
                    binding.btnAddFriend.beVisible()
                    binding.tvRequestSent.beGone()
                }
            }
        } else {
            binding.btnAddFriend.beGone()
        }


        // ???n th??ng tin n???u ng?????i ???? ch??? cho ph??p b???n b?? xem
        if (model.profile.getInfoPrivacy() == Privacy.FRIEND) {
            binding.groupAddress.beGone()
            binding.groupAcc.beGone()
            binding.groupFollowing.beGone()
            binding.groupFollowed.beGone()
            binding.moreInfo.beGone()
        }
    }

    private fun showMainUser() {
        binding.moreInfo.beGone()
        binding.groupFriend.beGone()
        binding.btnSetting.setOnClickListener {
            it.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                putExtra(USER_WALL_BROADCAST, USER_WALL_EVENT_SETTING)
            })

        }
        binding.groupMainUser.beVisible()
        binding.btnReview.setOnClickListener {
            it.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                putExtra(USER_WALL_BROADCAST, USER_WALL_OPEN_SCAN)
            })
        }
    }
}

class CreatePostHolder(parent: ViewGroup, val binding: ItemCreatePostBinding =
        ItemCreatePostBinding.inflate(LayoutInflater.from(parent.context), parent, false)) :
        RecyclerView.ViewHolder(binding.root) {

    init {
        binding.userAvatar.loadImageWithHolder(SessionManager.session.user?.avatar, R.drawable.ic_user_svg)
        binding.textView45.setOnClickListener {
            it.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                putExtra(USER_WALL_BROADCAST, USER_WALL_CREATE_POST)
            })
        }
        binding.imgStatus.setRankUser(SessionManager.session.user?.rank?.level)
    }
}