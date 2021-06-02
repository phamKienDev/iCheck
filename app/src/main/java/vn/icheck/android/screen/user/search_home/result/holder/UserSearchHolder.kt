package vn.icheck.android.screen.user.search_home.result.holder

import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_users_search_result_holder.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICSearchUser
import vn.icheck.android.screen.user.search_home.user.SearchUserAdapter
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible

class UserSearchHolder(parent: ViewGroup, val recyclerViewPool: RecyclerView.RecycledViewPool?) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_users_search_result_holder, parent, false)) {
    fun bind(list: MutableList<ICSearchUser>) {
        Handler().post {
            if (list.isNullOrEmpty()) {
                itemView.tvTitle.beGone()
                itemView.rcv_users_search.beGone()
                itemView.tv_xem_them.beGone()
            }else{
                itemView.tvTitle.beVisible()
                itemView.rcv_users_search.beVisible()
                itemView.tv_xem_them.beVisible()
            }
        }
        itemView.rootView.background=ViewHelper.bgWhiteStrokeLineColor0_5Corners4(itemView.context)

        val listSecond = mutableListOf<ICSearchUser>()
        for (i in 0 until if (list.size > 3) {
            3
        } else {
            list.size
        }) {
            listSecond.add(list[i])
        }
        val adapter = SearchUserAdapter(1)
        itemView.rcv_users_search.adapter = adapter
        adapter.setListData(listSecond)
        itemView.rcv_users_search.setRecycledViewPool(recyclerViewPool)
        itemView.rcv_users_search.layoutManager = LinearLayoutManager(itemView.context)

        if (list.size > 3) {
            itemView.tv_xem_them.beVisible()
        } else {
            itemView.tv_xem_them.beGone()
        }

        itemView.tv_xem_them.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_SEARCH_USER))
        }
    }

}