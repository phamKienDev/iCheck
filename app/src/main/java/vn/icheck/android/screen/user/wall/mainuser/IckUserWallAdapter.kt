package vn.icheck.android.screen.user.wall.mainuser

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.RelationshipManager
import vn.icheck.android.activities.image.DetailImagesActivity
import vn.icheck.android.chat.icheckchat.screen.conversation.ListConversationFragment
import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.component.friendrequestwall.FriendRequestWallHolder
import vn.icheck.android.component.friendsuggestion.FriendSuggestionComponent
import vn.icheck.android.component.post.IPostListener
import vn.icheck.android.component.post.PostHolder
import vn.icheck.android.constant.*
import vn.icheck.android.databinding.FriendInWallHolderBinding
import vn.icheck.android.databinding.ItemCreatePostBinding
import vn.icheck.android.databinding.ItemUserProfileWallBinding
import vn.icheck.android.model.posts.PostViewModel
import vn.icheck.android.model.profile.IckUserFriendModel
import vn.icheck.android.model.profile.IckUserProfileModel
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICSearchUser
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.screen.user.social_chat.SocialChatActivity
import vn.icheck.android.screen.user.wall.ICWallModel
import vn.icheck.android.screen.user.wall.holder.friend.FriendWallHolder
import vn.icheck.android.util.ick.*

class IckUserWallAdapter(val listener: IPostListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = arrayListOf<ICViewModel>()
    var friendListPos = -1
    fun updateList(list: List<ICViewModel>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addPosts(list: List<ICViewModel>) {
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
            ICViewTypes.ITEM_CREATE_POST -> CreatePostHolder(ItemCreatePostBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ICViewTypes.ITEM_USER_POST -> PostHolder(parent, listener)
            else -> NullHolder.create(parent)
        }
    }

    override fun getItemCount(): Int {
        return listData.size
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

                        setOnRemoveListener(View.OnClickListener {
                            listData.removeAt(position)
                            notifyItemRemoved(position)
                        })
                    }
                }
                ICViewTypes.FRIEND_SUGGESTION_TYPE -> {
                    (holder as FriendSuggestionComponent).apply {
                        bind(((listData[position] as ICWallModel).data as ICListResponse<ICUser>).rows)

                        setOnRemoveListener(View.OnClickListener {
                            listData.removeAt(position)
                            notifyItemRemoved(position)
                        })
                    }
                }
                ICViewTypes.ITEM_USER_POST -> {
                    val postViewModel = listData[position] as PostViewModel
                    (holder as PostHolder).bind(postViewModel)
                }
            }
        } catch (e: Exception) {
            logError(e)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return listData[position].getViewType()
    }
}

class ProfileUserHolder(val binding: ItemUserProfileWallBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ickUserProfileModel: IckUserProfileModel) {
        val data = ickUserProfileModel.profile.data
        Glide.with(binding.root.context.applicationContext)
                .load(data?.avatar)
                .error(R.drawable.ic_avatar_default_84px)
                .placeholder(R.drawable.ic_avatar_default_84px)
                .into(binding.userAvatar)
        if (!data?.avatar.isNullOrEmpty()) {
            binding.userAvatar.setOnClickListener {
                DetailImagesActivity.start(arrayListOf(data?.avatar), it.context)
            }
        }


        binding.tvName.apply {
            text = if (data?.lastName.isNullOrEmpty() && data?.firstName.isNullOrEmpty()) {
                data?.getPhoneOnly()
            } else {
                Constant.getName(data?.lastName, data?.firstName)
//                val spannableString = SpannableString(Constant.getName(data?.lastName, data?.firstName) + "  ")
//                spannableString.setSpan(ImageSpan(itemView.context, R.drawable.ic_verified_user_24dp, ImageSpan.ALIGN_BASELINE), spannableString.length - 1, spannableString.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//                setText(spannableString, TextView.BufferType.SPANNABLE)
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
                DetailImagesActivity.start(arrayListOf(data?.background), it.context)
            }
        }
//        binding.imgWallCover.viewTreeObserver.addOnGlobalLayoutListener {
//            Glide.with(binding.root.context)
//                    .load(data?.background)
//                    .error(R.drawable.left_menu_bg)
//                    .into(binding.imgWallCover)
//            if (!data?.background.isNullOrEmpty()) {
//                binding.imgWallCover.setOnClickListener {
//                    DetailImagesActivity.start(arrayListOf(data?.background), it.context)
//                }
//            }
//        }
        binding.btnSendMsg.setOnClickListener {
//            val arrMember = arrayListOf<ChatMember>()
//            arrMember.add(ChatMember(SessionManager.session.user?.id, role = ADMIN))
//            arrMember.add(ChatMember(data?.id))
//            SocialChatActivity.createRoomChat(it.context, arrMember.toTypedArray())
            if (SessionManager.isUserLogged) {
                SocialChatActivity.createRoomChat(it.context, data?.id)
                ListConversationFragment.finishAllChat()
//                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_FINISH_ALL_CHAT))
//                ChatSocialDetailActivity.createRoomChat(it.context, data?.id ?: -1, "user")
            } else {
                ICheckApplication.currentActivity()?.let { act ->
                    (act as FragmentActivity).showLogin()
                }

            }
        }
//        binding.imgWallCover.loadImageWithHolder(data?.background, R.drawable.bg_image_cover_in_wall)
        when (data?.rank?.level) {
            Constant.USER_LEVEL_GOLD -> {
                binding.icCrow.setImageResource(R.drawable.ic_leftmenu_avatar_gold_36dp)
            }
            Constant.USER_LEVEL_DIAMOND -> {
                binding.icCrow.setImageResource(R.drawable.ic_leftmenu_avatar_diamond_36dp)
            }
            Constant.USER_LEVEL_STANDARD -> {
                binding.icCrow.setImageResource(R.drawable.ic_leftmenu_avatar_standard_36dp)
            }
            Constant.USER_LEVEL_SILVER -> {
                binding.icCrow.setImageResource(R.drawable.ic_leftmenu_avatar_silver_36dp)
            }
            else -> {
                binding.icCrow.setImageResource(R.drawable.ic_leftmenu_avatar_standard_36dp)
            }
        }
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
            binding.tvId.text = "IC - " + data?.id
            if (SessionManager.session.user?.id == ickUserProfileModel.id) {
                binding.tvFollow.text = "${RelationshipManager.getTotalFollowed()}"
                binding.tvWatch.text = "${RelationshipManager.getTotalFollow()}"
                binding.groupFollowing.beVisible()
            } else {
                binding.tvFollow.text = "${data?.userFollowingMeCount ?: 0}"
                binding.tvWatch.text = "${data?.myFollowingUserCount ?: 0}"
                binding.groupFollowing.beGone()
            }
            binding.moreInfo.beVisible()
        }
//        Glide.with(binding.root)
//                .load(data?.background)
//                .error(R.drawable.bg_image_cover_in_wall)
//                .placeholder(R.drawable.bg_image_cover_in_wall)
//                .into(binding.imgWallCover)
        when {
            ickUserProfileModel.id == SessionManager.session.user?.id -> showMainUser()
            AppDatabase.getDatabase().myFriendIdDao().getUserByID(ickUserProfileModel.id) != null -> showFriend()
            else -> showStranger(ickUserProfileModel)
        }
    }

    private fun showStranger(ickUserProfileModel: IckUserProfileModel) {
        if (ickUserProfileModel.profile.getInfoPrivacy() == Privacy.FRIEND) {
            binding.groupAddress.beGone()
            binding.groupAcc.beGone()
            binding.groupFollowing.beGone()
            binding.groupFollowed.beGone()
            binding.moreInfo.beGone()
        }
        binding.groupMainUser.beGone()
        binding.groupFriend.beVisible()
        binding.btnSendMsg.setTextColor(Color.parseColor("#057DDA"))
        binding.btnSendMsg.background = ResourcesCompat.getDrawable(binding.root.context.resources, R.drawable.bg_corners_4_light_blue_no_solid, null)
        binding.btnAddFriend.beVisible()
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
                if (binding.tvAddFriend.text == "Đồng ý kết bạn") {
                    showFriend()
                    it.context.sendBroadcast(Intent(USER_WALL_BROADCAST).apply {
                        putExtra(USER_WALL_BROADCAST, USER_WALL_ACCEPT_FRIEND)
                    })
                } else {
                    binding.btnAddFriend.beGone()
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
        if (ickUserProfileModel.profile.getInvitePrivacy() == Privacy.EVERYONE) {
            if (RelationshipManager.checkMyFriendInvitation(ickUserProfileModel.id)) {
                binding.btnAddFriend.beGone()
                binding.tvRequestSent.beVisible()
            }else if (RelationshipManager.checkFriendInvitationMe(ickUserProfileModel.id)) {
                binding.btnAddFriend.beVisible()
                binding.tvAddFriend.setText("Đồng ý kết bạn")
                binding.tvRequestSent.beGone()
            } else {
                binding.btnAddFriend.beVisible()
                binding.tvRequestSent.beGone()
            }
        } else {
            binding.btnAddFriend.beGone()
        }
    }

    private fun showFriend() {
        binding.groupMainUser.beGone()
        binding.groupFriend.beVisible()
        binding.btnAddFriend.beGone()
        binding.btnSendMsg.background = ResourcesCompat.getDrawable(binding.root.context.resources, R.drawable.background_button_enable, null)
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
        binding.btnSendMsg.setTextColor(Color.WHITE)
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

class CreatePostHolder(val binding: ItemCreatePostBinding) : RecyclerView.ViewHolder(binding.root) {
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