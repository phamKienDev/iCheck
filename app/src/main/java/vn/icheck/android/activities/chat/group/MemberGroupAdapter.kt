package vn.icheck.android.activities.chat.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import vn.icheck.android.R
import vn.icheck.android.adapters.base.BaseHolder

class MemberGroupAdapter(val listMember: ArrayList<MemberView>, private val adapterType: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_SUGGEST = 1
        const val TYPE_CHECKED = 2
        const val TYPE_NAMING = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (adapterType) {
            TYPE_SUGGEST -> MemberSuggest.create(parent)
            TYPE_CHECKED -> SelectedMember.create(parent)
            else -> NamingMember.create(parent)
        }
    }

    override fun getItemCount(): Int {
        return listMember.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (adapterType) {
            TYPE_SUGGEST -> (holder as MemberSuggest).bind(listMember.get(position))
            TYPE_CHECKED ->  (holder as SelectedMember).bind(listMember.get(position))
            else -> (holder as NamingMember).bind(listMember[position])
        }
    }

    class MemberSuggest(view: View) : BaseHolder(view) {

        fun bind(memberView: MemberView) {
            Glide.with(view.context.applicationContext)
                    .load(memberView.avatar)
                    .error(R.drawable.user_placeholder)
                    .placeholder(R.drawable.user_placeholder)
                    .into(getImg(R.id.user_avar))
            getTv(R.id.tv_contact_name).text = memberView.name
            if (memberView.checked) {
                getImg(R.id.img_tick).setImageResource(R.drawable.icon_check_alt)
            } else {
                getImg(R.id.img_tick).setImageResource(R.drawable.ic_circle_stroke_gray)
            }
            setOnClick(R.id.root, View.OnClickListener {
                memberView.checked = !memberView.checked
                AddMemFragment.instance?.check(adapterPosition, memberView.checked)
            })
        }

        companion object {
            fun create(parent: ViewGroup): MemberSuggest {
                return MemberSuggest(LayoutInflater.from(parent.context)
                        .inflate(R.layout.member_suggest_holder, parent, false))
            }
        }
    }

    class SelectedMember(view: View) : BaseHolder(view) {
        fun bind(memberView: MemberView) {
            getTv(R.id.tv_user_name).text = memberView.name
            Glide.with(view.context.applicationContext)
                    .load(memberView.avatar)
                    .error(R.drawable.user_placeholder)
                    .placeholder(R.drawable.user_placeholder)
                    .into(getImg(R.id.avatar))
            setOnClick(R.id.ic_remove, View.OnClickListener {
                AddMemFragment.instance?.uncheck(adapterPosition)
            })
        }

        companion object {
            fun create(parent: ViewGroup): SelectedMember {
                return SelectedMember(LayoutInflater.from(parent.context)
                        .inflate(R.layout.selected_member_holder, parent, false))
            }
        }
    }

    class NamingMember(view: View) : BaseHolder(view) {
        fun bind(memberView: MemberView) {
            Glide.with(view.context.applicationContext)
                    .load(memberView.avatar)
                    .error(R.drawable.user_placeholder)
                    .placeholder(R.drawable.user_placeholder)
                    .into(getImg(R.id.user_avar))
            getTv(R.id.tv_contact_name).text = memberView.name
            hideView(R.id.img_tick)
        }

        companion object {
            fun create(parent: ViewGroup): NamingMember {
                return NamingMember(LayoutInflater.from(parent.context)
                        .inflate(R.layout.member_suggest_holder, parent, false))
            }
        }
    }
}