package vn.icheck.android.component.product.contribute

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import vn.icheck.android.R
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.ui.GlideUtil

class ContributeModule(view: View): BaseHolder(view) {

    lateinit var btnRight: ButtonState
    lateinit var btnWrong: ButtonState

    fun setUpVote(contributeUserModel: ContributeUserModel) {
        SessionManager.session.user?.let {
            if (contributeUserModel.contributions?.user?.id != it.id) {
                btnRight.changeState()
                if (btnWrong.state == 1) {
                    btnWrong.changeState()
                }

                getTv(R.id.btn_right).rText(R.string.dung_d, btnRight.vote)
                getTv(R.id.btn_wrong).rText(R.string.sai_d, btnWrong.vote)

                if (-1 == btnWrong.state && btnRight.state == -1) {
                    contributeUserModel.contributionCallback.downvote(contributeUserModel.contributions)
                } else {
                    contributeUserModel.contributionCallback.upvote(contributeUserModel.contributions, 1)
                }
            }
        }

    }

    fun setDownVote(contributeUserModel: ContributeUserModel) {
        SessionManager.session.user?.let {
            if (contributeUserModel.contributions?.user?.id != it.id) {
                btnWrong.changeState()
                if (btnRight.state == 1) {
                    btnRight.changeState()
                }
                getTv(R.id.btn_right).rText(R.string.dung_d, btnRight.vote)
                getTv(R.id.btn_wrong).rText(R.string.sai_d, btnWrong.vote)
                if (btnRight.state == btnWrong.state && btnRight.state == -1) {
                    contributeUserModel.contributionCallback.downvote(contributeUserModel.contributions)
                } else {
                    contributeUserModel.contributionCallback.upvote(contributeUserModel.contributions, -1)
                }
            }
        }
    }

    fun bind(contributeUserModel: ContributeUserModel) {
        btnRight = ButtonState(getTv(R.id.btn_right))
        btnWrong = ButtonState(getTv(R.id.btn_wrong))
        contributeUserModel.contributions?.userVote?.let {
            if (it == 1) {
                btnRight.changeState()
            } else if (it == -1) {
                btnWrong.changeState()
            }
        }
        if (contributeUserModel.contributions != null) {
            btnRight.apply {
                vote = contributeUserModel.contributions!!.upvotes
            }
            btnWrong.apply {
                vote = contributeUserModel.contributions!!.downvotes
            }
        }
        getTv(R.id.tv_user_name).text = contributeUserModel.contributions?.user?.name
        getTv(R.id.btn_right).rText(R.string.dung_d, contributeUserModel.contributions?.upvotes)
        getTv(R.id.btn_wrong).rText(R.string.sai_d, contributeUserModel.contributions?.downvotes)
        GlideUtil.loadAva(contributeUserModel.contributions?.user?.thumbnails?.small,
                getImg(R.id.ava_user))
        setOnClick(R.id.ava_user, View.OnClickListener {
            contributeUserModel.contributionCallback.showUser(contributeUserModel.contributions)
        })
        setOnClick(R.id.tv_user_name, View.OnClickListener {
            contributeUserModel.contributionCallback.showUser(contributeUserModel.contributions)
        })

        setOnClick(R.id.btn_right, View.OnClickListener {
            if (SessionManager.isUserLogged) {
                setUpVote(contributeUserModel)
            }
//            else {
//                ProductDetailActivity.checkContribute = true
//                ProductDetailActivity.positionContribute = adapterPosition
//                ProductDetailActivity.dataContributions = contributeUserChild
//                ProductDetailActivity.INSTANCE?.loginAccount(ProductDetailActivity.REQUEST_CONTRIBUTE)
//            }
        })
        setOnClick(R.id.btn_wrong, View.OnClickListener {
            if (SessionManager.isUserLogged) {
                setDownVote(contributeUserModel)
            }
//            else {
//                ProductDetailActivity.checkContribute = false
//                ProductDetailActivity.positionContribute = adapterPosition
//                ProductDetailActivity.dataContributions = contributeUserChild
//                ProductDetailActivity.INSTANCE?.loginAccount(ProductDetailActivity.REQUEST_CONTRIBUTE)
//            }
        })
    }


    class ButtonState(var view: TextView) {
        var state = -1
        var vote: Long = 0
        fun changeState() {
            if (state == -1) {
                view.setBackgroundResource(R.drawable.bg_solid_blue_corner_5)
                view.setTextColor(Color.WHITE)
                state = 1
                vote++
            } else {
                state = -1
                view.setBackgroundResource(R.drawable.bg_solid_white_corner_5)
                view.setTextColor(Color.parseColor("#3C5A99"))
                vote--
            }
        }
    }

    companion object{
        fun create(parent:ViewGroup):ContributeModule{
            val lf = LayoutInflater.from(parent.context)
            val v = lf.inflate(R.layout.ctsp_contribute_holder, parent, false)
            return ContributeModule(v)
        }
    }
}