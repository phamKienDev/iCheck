package vn.icheck.android.screen.user.home_page.home.holder.primaryfunction

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.databinding.ItemHomeFunctionBinding
import vn.icheck.android.databinding.ItemHomeFunctionCreatePvcombankBinding
import vn.icheck.android.databinding.ItemHomeFunctionDetailPvcombankBinding
import vn.icheck.android.databinding.ItemHomeFunctionInfoBinding
import vn.icheck.android.helper.FileHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.loyalty.base.setVisible
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICTheme
import vn.icheck.android.network.models.pvcombank.ICListCardPVBank
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.home_page.home.callback.IHomePageView
import vn.icheck.android.screen.user.home_page.home.holder.secondfunction.HomeSecondaryFunctionAdapter
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.File
import kotlin.math.abs

class HomeFunctionHolder(parent: ViewGroup, isExistTheme: Boolean, listener: IHomePageView,
                         val binding: ItemHomeFunctionBinding = ItemHomeFunctionBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<MutableList<Any?>>(binding.root) {

    private val primaryAdapter = HomePrimaryAdapterV2(listener)
    private val secondaryAdapter = HomeSecondaryFunctionAdapter(mutableListOf(), isExistTheme)

    override fun bind(obj: MutableList<Any?>) {
        updateTheme()

        binding.viewPager.apply {
            adapter = primaryAdapter
            primaryAdapter.setData(obj)

            offscreenPageLimit = primaryAdapter.itemCount
            setPageTransformer { page: View, position: Float ->
                page.translationX = -SizeHelper.size24 * position
            }
        }

        binding.rcvSecond.apply {
            val functions = (obj[0] as ICTheme).secondary_functions
            layoutManager = GridLayoutManager(itemView.context, if (functions?.size ?: 0 >= 4) {
                4
            } else {
                functions?.size ?: 0
            })
            adapter = secondaryAdapter.apply {
                setData(functions ?: mutableListOf())
            }
        }
    }

    fun updateHomeHeader() {
        primaryAdapter.notifyItemChanged(0)
        binding.viewPager.apply {
            offscreenPageLimit = primaryAdapter.itemCount
            setPageTransformer { page: View, position: Float ->
                page.translationX = -SizeHelper.size24 * position
            }
        }
    }

    fun updateHomePVCombank(obj: ICListCardPVBank?) {
        primaryAdapter.addMoreDate(obj)
    }

    fun updateTheme() {
        val path = FileHelper.getPath(itemView.context)
        File(path + FileHelper.homeBackgroundImage).let {
            if (it.exists()) {
                binding.imgBackground.apply {
                    setVisible()
                    WidgetUtils.loadImageFile(this, it)
                }
            } else {
                binding.imgBackground.setGone()
            }
        }

        File(path + FileHelper.homeHeaderImage).let {
            if (it.exists()) {
                binding.imgHeader.apply {
                    WidgetUtils.loadImageFileFitCenter(this, it)
                }
            }
        }
    }

    class HomePrimaryAdapterV2(private val listener: IHomePageView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val listData = mutableListOf<Any?>()

        private val headerType = 1
        private val detailType = 2

        fun setData(list: MutableList<Any?>) {
            listData.clear()
            listData.addAll(list)
            notifyItemRangeChanged(0, itemCount)
        }

        fun addMoreDate(obj: Any?) {
            if (listData.size > 1) {
                listData.removeLast()
                listData.add(obj)
                notifyItemRangeChanged(0, itemCount)
            } else {
                listData.add(obj)
                notifyDataSetChanged()
            }
        }

        override fun getItemViewType(position: Int): Int {
            return when (val obj = listData[position]) {
                is ICTheme -> {
                    headerType
                }
                is ICListCardPVBank -> {
                    detailType
                }
                else -> {
                    super.getItemViewType(position)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                headerType -> {
                    HeaderHolder(parent)
                }
                detailType -> {
                    DetailHolder(parent)
                }
                else -> {
                    CreateHolder(parent)
                }
            }
        }

        override fun getItemCount() = listData.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is HeaderHolder -> {
                    holder.bind(listData[position] as ICTheme)
                }
                is DetailHolder -> {
                    holder.bind(listData[position] as ICListCardPVBank)
                }
                is CreateHolder -> {
                    holder.bind(true)
                }
            }
        }

        inner class HeaderHolder(parent: ViewGroup, val binding: ItemHomeFunctionInfoBinding = ItemHomeFunctionInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICTheme>(binding.root) {
            private val primaryAdapter = HomeFunctionAdapter()

            override fun bind(obj: ICTheme) {
                val user = SessionManager.session.user

                binding.avatarUser.apply {
                    avatarSize = SizeHelper.size32
                    rankSize = SizeHelper.size12
                    setData(user?.avatar, user?.rank?.level, R.drawable.ic_avatar_default_84px)

                    setOnClickListener {
                        if (SessionManager.isUserLogged) {
                            ICheckApplication.currentActivity()?.let { activity ->
                                FirebaseDynamicLinksActivity.startDestinationUrl(activity, "icheck://user")
                            }
                        } else {
                            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_REQUIRE_LOGIN))
                        }
                    }
                }

                binding.tvName.apply {
                    text = if (user != null && SessionManager.isUserLogged) {
                        if (user.getName == "Chưa cập nhật") {
                            user.getPhoneOnly()
                        } else {
                            user.getName
                        }
                    } else {
                        context.getString(R.string.nguoi_la)
                    }
                    setOnClickListener {
                        ICheckApplication.currentActivity()?.let { activity ->
                            FirebaseDynamicLinksActivity.startDestinationUrl(activity, "icheck://user")
                        }
                    }
                }

                binding.tvIcheckXu.text = TextHelper.formatMoneyPhay(SessionManager.getCoin())

                binding.imgShowOrHidePassword.setOnClickListener {
                    if (binding.tvIcheckXu.visibility == View.GONE) {
                        binding.imgShowOrHidePassword.setImageResource(R.drawable.ic_eye_off_gray_24dp)
                        binding.tvHide.visibility = View.GONE
                        binding.tvIcheckXu.visibility = View.VISIBLE
                    } else {
                        binding.imgShowOrHidePassword.setImageResource(R.drawable.ic_eye_on_24px)
                        binding.tvHide.visibility = View.VISIBLE
                        binding.tvIcheckXu.visibility = View.GONE
                    }
                }

                binding.rcvPrimary.apply {
                    if (!obj.primary_functions.isNullOrEmpty()) {
                        setVisible()
                        layoutManager = if (obj.primary_functions!!.size >= 4) {
                            GridLayoutManager(context, 4)
                        } else {
                            GridLayoutManager(context, obj.primary_functions!!.size)
                        }
                        adapter = primaryAdapter.apply {
                            setData(obj.primary_functions!!)
                        }
                    } else {
                        setGone()
                    }
                }
            }
        }

        inner class DetailHolder(parent: ViewGroup, val binding: ItemHomeFunctionDetailPvcombankBinding = ItemHomeFunctionDetailPvcombankBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICListCardPVBank>(binding.root) {

            override fun bind(obj: ICListCardPVBank) {
                binding.tvMoney.text = if (binding.tvMoney.isChecked) {
                    TextHelper.formatMoney(obj.avlBalance ?: "").replace("[0-9]".toRegex(), "*") + "**"
                } else {
                    TextHelper.formatMoney(obj.avlBalance ?: "") + " đ"
                }

                binding.tvMoney.setOnClickListener {
                    binding.tvMoney.isChecked = !binding.tvMoney.isChecked

                    binding.tvMoney.text = if (binding.tvMoney.isChecked) {
                        TextHelper.formatMoney(obj.avlBalance ?: "").replace("[0-9]".toRegex(), "*") + "**"
                    } else {
                        TextHelper.formatMoney(obj.avlBalance ?: "") + " đ"
                    }
                }

                binding.tvRecharge.setOnClickListener {
                    listener.onRechargePVCombank()
                }

                binding.tvInfo.setOnClickListener {
                    listener.onInfoPVCombank()
                }

                binding.tvTransaction.setOnClickListener {
                    listener.onTransactionCombank()
                }
            }
        }

        inner class CreateHolder(parent: ViewGroup, val binding: ItemHomeFunctionCreatePvcombankBinding = ItemHomeFunctionCreatePvcombankBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<Boolean>(binding.root) {

            override fun bind(obj: Boolean) {
                binding.imgImage.setOnClickListener {
                    listener.onCreatePVCombank()
                }

                binding.btnCreate.setOnClickListener {
                    listener.onCreatePVCombank()
                }
            }
        }
    }

    class HomePrimaryAdapter(private val listener: IHomePageView) : PagerAdapter() {
        private val listData = mutableListOf<Any?>()

        private val primaryAdapter = HomeFunctionAdapter()

        fun setData(list: MutableList<Any?>) {
            listData.clear()
            listData.addAll(list)
            notifyDataSetChanged()
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean = obj == view

        override fun getCount(): Int = listData.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView = when (val obj = listData[position]) {
                is ICTheme -> {
                    val user = SessionManager.session.user
                    ItemHomeFunctionInfoBinding.inflate(LayoutInflater.from(container.context), container, false).apply {
                        avatarUser.apply {
                            avatarSize = SizeHelper.size32
                            rankSize = SizeHelper.size12
                            setData(user?.avatar, user?.rank?.level, R.drawable.ic_avatar_default_84px)

                            setOnClickListener {
                                if (SessionManager.isUserLogged) {
                                    ICheckApplication.currentActivity()?.let { activity ->
                                        FirebaseDynamicLinksActivity.startDestinationUrl(activity, "icheck://user")
                                    }
                                } else {
                                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_REQUIRE_LOGIN))
                                }
                            }
                        }

                        tvName.apply {
                            text = if (user != null && SessionManager.isUserLogged) {
                                if (user.getName == "Chưa cập nhật") {
                                    user.getPhoneOnly()
                                } else {
                                    user.getName
                                }
                            } else {
                                context.getString(R.string.nguoi_la)
                            }
                            setOnClickListener {
                                ICheckApplication.currentActivity()?.let { activity ->
                                    FirebaseDynamicLinksActivity.startDestinationUrl(activity, "icheck://user")
                                }
                            }
                        }

                        tvIcheckXu.text = TextHelper.formatMoneyPhay(SessionManager.getCoin())

                        imgShowOrHidePassword.setOnClickListener {
                            if (tvIcheckXu.visibility == View.GONE) {
                                imgShowOrHidePassword.setImageResource(R.drawable.ic_eye_off_gray_24dp)
                                tvHide.visibility = View.GONE
                                tvIcheckXu.visibility = View.VISIBLE
                            } else {
                                imgShowOrHidePassword.setImageResource(R.drawable.ic_eye_on_24px)
                                tvHide.visibility = View.VISIBLE
                                tvIcheckXu.visibility = View.GONE
                            }
                        }

                        rcvPrimary.apply {
                            if (!obj.primary_functions.isNullOrEmpty()) {
                                setVisible()
                                layoutManager = if (obj.primary_functions!!.size >= 4) {
                                    GridLayoutManager(context, 4)
                                } else {
                                    GridLayoutManager(context, obj.primary_functions!!.size)
                                }
                                adapter = primaryAdapter.apply {
                                    setData(obj.primary_functions!!)
                                }
                            } else {
                                setGone()
                            }
                        }
                    }.root
                }
                is ICListCardPVBank -> {
                    ItemHomeFunctionDetailPvcombankBinding.inflate(LayoutInflater.from(container.context), container, false).apply {
                        tvMoney.text = if (tvMoney.isChecked) {
                            TextHelper.formatMoney(obj.avlBalance
                                    ?: "").replace("[0-9]".toRegex(), "*")
                        } else {
                            TextHelper.formatMoney(obj.avlBalance ?: "")
                        }

                        tvRecharge.setOnClickListener {
                            listener.onRechargePVCombank()
                        }

                        tvInfo.setOnClickListener {
                            listener.onInfoPVCombank()
                        }

                        tvTransaction.setOnClickListener {
                            listener.onTransactionCombank()
                        }
                    }.root
                }
                else -> {
                    ItemHomeFunctionCreatePvcombankBinding.inflate(LayoutInflater.from(container.context), container, false).apply {
                        imgImage.setOnClickListener {
                            listener.onCreatePVCombank()
                        }

                        btnCreate.setOnClickListener {
                            listener.onCreatePVCombank()
                        }
                    }.root
                }
            }

            container.addView(itemView)
            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as View)
        }
    }
}