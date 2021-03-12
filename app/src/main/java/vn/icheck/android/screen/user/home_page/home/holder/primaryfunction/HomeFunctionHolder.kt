package vn.icheck.android.screen.user.home_page.home.holder.primaryfunction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.item_home_function.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.databinding.ItemHomeFunctionInfoBinding
import vn.icheck.android.databinding.NullHolderBinding
import vn.icheck.android.helper.FileHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.loyalty.base.setVisible
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICTheme
import vn.icheck.android.network.models.pvcombank.ICListCardPVBank
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.screen.user.home_page.home.holder.secondfunction.HomeSecondaryFunctionAdapter
import vn.icheck.android.util.kotlin.WidgetUtils
import java.io.File

class HomeFunctionHolder(parent: ViewGroup, private val isExistTheme: Boolean) : BaseViewHolder<ICTheme>(LayoutInflater.from(parent.context).inflate(R.layout.item_home_function, parent, false)) {
    private val primaryAdapter = HomeFunctionAdapter()
    private val secondaryAdapter = HomeSecondaryFunctionAdapter(mutableListOf(), isExistTheme)

    override fun bind(obj: ICTheme) {
        val path = FileHelper.getPath(itemView.context)
        File(path + FileHelper.homeBackgroundImage).let {
            if (it.exists()) {
                itemView.imgBackground.apply {
                    setVisible()
                    WidgetUtils.loadImageFile(this, it)
                }
            } else {
                itemView.imgBackground.setGone()
            }
        }

        File(path + FileHelper.homeHeaderImage).let {
            if (it.exists()) {
                itemView.imgHeader.apply {
                    WidgetUtils.loadImageFileFitCenter(this, it)
                }
            }
        }

        hfg

        itemView.rcvSecond.apply {
            layoutManager = if (obj.secondary_functions!!.size >= 4) {
                GridLayoutManager(itemView.context, 4)
            } else {
                GridLayoutManager(itemView.context, obj.secondary_functions!!.size)
            }
            adapter = secondaryAdapter.apply {
                setData(obj.secondary_functions ?: mutableListOf())
            }
        }
    }

    inner class Adapter: PagerAdapter() {
        private val listData = mutableListOf<Any>()

        fun setData(list: MutableList<Any>) {
            listData.clear()
            listData.addAll(list)
            notifyDataSetChanged()
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean = obj == view

        override fun getCount(): Int = listData.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val obj = listData[position]

            val itemView = when (obj) {
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
                                itemView.context.getString(R.string.nguoi_la)
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
                                    GridLayoutManager(itemView.context, 4)
                                } else {
                                    GridLayoutManager(itemView.context, obj.primary_functions!!.size)
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
                is ICListCardPVBank -> {

                }
                else -> {
                    NullHolderBinding.inflate(LayoutInflater.from(container.context), container, false)
                }
            }

            container.addView(itemView.root)
            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as View)
        }
    }
}