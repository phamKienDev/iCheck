package vn.icheck.android.screen.user.social_chat

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.databinding.ItemPhoneBookHolderBinding
import vn.icheck.android.network.models.wall.RowsItem
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.util.ick.*
import vn.icheck.android.util.ick.setRankUser

class PhoneBookAdapter:PagingDataAdapter<RowsItem, RecyclerView.ViewHolder>(DIFF_UTIL) {

    companion object{
        val DIFF_UTIL = object : DiffUtil.ItemCallback<RowsItem>() {
            override fun areItemsTheSame(oldItem: RowsItem, newItem: RowsItem): Boolean  = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: RowsItem, newItem: RowsItem): Boolean {
               return oldItem.updatedAt == newItem.updatedAt
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val lf = parent.getLayoutInflater()
        return PhoneBookHolder(ItemPhoneBookHolderBinding.inflate(lf, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PhoneBookHolder).bind(getItem(position))
    }

    inner class PhoneBookHolder(val binding: ItemPhoneBookHolderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(rowsItem: RowsItem?) {
            rowsItem?.let {
                binding.avatar.loadImageWithHolder(it.avatar, R.drawable.ic_user_svg)
                binding.tvNameUser simpleText it.getName
                binding.imgRank.setRankUserBig(it.rank?.level)
                binding.avatar.setOnClickListener {
                    IckUserWallActivity.create(rowsItem.id, binding.root.context)
                }
            }
        }
    }
}