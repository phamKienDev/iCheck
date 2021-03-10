package vn.icheck.android.component.product.chat

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.ctsp_bmmh_new_holder.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.activities.chat.v2.ChatV2Activity
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.screen.account.home.AccountActivity

class ChatProductDetailHolder(view: View) : BaseViewHolder<ChatProductModel>(view) {
    override fun bind(obj: ChatProductModel) {
        itemView.img_chat.setOnClickListener {
            if (SessionManager.isUserLogged) {
                ICheckApplication.currentActivity()?.let { act ->
                    obj.icBarcodeProductV2?.let {
                        it.manager?.let { manager ->
                            ChatV2Activity.createChatBot(manager.id, it.barcode, act)
                        } ?: run {
                            ChatV2Activity.createChatBotIcheck(it.barcode, act)
                        }
                    }
                }
            } else {
                val account = Intent(itemView.context, AccountActivity::class.java)
                itemView.context.startActivity(account)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): ChatProductDetailHolder {
            return ChatProductDetailHolder(LayoutInflater.from(parent.context).inflate(R.layout.ctsp_bmmh_new_holder, parent, false))
        }
    }
}