package vn.icheck.android.activities.chat.group

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_add_members.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.R

class AddMemFragment:Fragment() {
    lateinit var viewModel: CreateGroupViewModel
    lateinit var suggestedMemberAdaper: MemberGroupAdapter
    lateinit var checkedMemberAdapter:MemberGroupAdapter
    val copyData = arrayListOf<MemberView>()

    companion object{
        var instance:AddMemFragment? = null
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_members, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        instance = this
        viewModel = ViewModelProvider(activity!!).get(CreateGroupViewModel::class.java)
        val listFollowing = viewModel.friendLiveData.value
        listFollowing?.let {
            val arr = arrayListOf<MemberView>()
            for (item in it.rows) {
                try {
                    if (item.rowObject != null) {
                        arr.add(MemberView(item.rowObject.id).apply {
                            name = item.rowObject.name
                            avatar = item.rowObject.avatarThumbnails.small
                        })
                    }
                } catch (e: Exception) {
                }
            }
            copyData.addAll(arr)
            suggestedMemberAdaper = MemberGroupAdapter(arr, MemberGroupAdapter.TYPE_SUGGEST)
            rcv_suggest_friend.adapter = suggestedMemberAdaper
            rcv_suggest_friend.layoutManager = LinearLayoutManager(context)
            checkedMemberAdapter = MemberGroupAdapter(arrayListOf(), MemberGroupAdapter.TYPE_CHECKED)
            rcv_members.adapter = checkedMemberAdapter
            rcv_members.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            edt_search.addTextChangedListener(object : TextWatcher{
                var searchFor = ""
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val searchText = s.toString().trim()
                    if (searchText == searchFor) {
                        return
                    }
                    searchFor = searchText
                    lifecycleScope.launch {
                        delay(300)
                        if (searchText != searchFor) {
                            suggestedMemberAdaper.listMember.clear()
                            suggestedMemberAdaper.listMember.addAll(copyData)
                            suggestedMemberAdaper.notifyDataSetChanged()
                            return@launch
                        }
                        val copy = copyData.filter {
                            it.name!!.contains(searchFor, true)
                        }
                        suggestedMemberAdaper.listMember.clear()
                        suggestedMemberAdaper.listMember.addAll(copy)
                        suggestedMemberAdaper.notifyDataSetChanged()
                    }
                }
            })
            img_clear.setOnClickListener {
                edt_search.setText("")
            }
        }
    }

    fun check(position: Int, checked:Boolean) {
        suggestedMemberAdaper.listMember[position].checked = checked
        suggestedMemberAdaper.notifyItemChanged(position)
        val item = suggestedMemberAdaper.listMember[position]
        if (item.checked) {
            checkedMemberAdapter.listMember.add(item)
            checkedMemberAdapter.notifyItemInserted(checkedMemberAdapter.itemCount - 1)
        } else {
            val pos = checkedMemberAdapter.listMember.indexOf(item)
            if (pos != -1) {
                checkedMemberAdapter.listMember.removeAt(pos)
            }
            checkedMemberAdapter.notifyItemRemoved(pos)
        }
        viewModel.listMember.postValue(checkedMemberAdapter.listMember)
    }

    fun uncheck(position: Int) {
        val item = checkedMemberAdapter.listMember[position]
        checkedMemberAdapter.listMember.removeAt(position)
        checkedMemberAdapter.notifyItemRemoved(position)
        val pos = suggestedMemberAdaper.listMember.indexOf(item)
        if (pos != -1) {
            suggestedMemberAdaper.listMember[pos].checked = false
        }
        suggestedMemberAdaper.notifyItemChanged(pos)
        viewModel.listMember.postValue(checkedMemberAdapter.listMember)
    }
}