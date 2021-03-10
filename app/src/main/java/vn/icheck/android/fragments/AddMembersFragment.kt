package vn.icheck.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_add_members.*
import vn.icheck.android.R
import vn.icheck.android.activities.chat.CreateGroupChatActivity
import vn.icheck.android.adapters.ContactsAdapter
import vn.icheck.android.adapters.MembersAdapter
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.chat.ContactsViewModel
import vn.icheck.android.network.models.ICFollowing

class AddMembersFragment: Fragment(), ContactsAdapter.OnContactClick, MembersAdapter.OnMemberGroupClick {

    private lateinit var contactsViewModel: ContactsViewModel
    private val contactsAdapter = ContactsAdapter(this)
    private lateinit var memberAdapter: MembersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_members, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        memberAdapter = MembersAdapter(this)
        memberAdapter.dataSize.observe(this, Observer {
            if (it == 0) {
                CreateGroupChatActivity.getInstance().hideContinue()
            } else {
                CreateGroupChatActivity.getInstance().showContinue()
            }
            CreateGroupChatActivity.getInstance().save(memberAdapter.listData)
        })
        initRecycleView()
        initViewModel()
    }

    private fun initViewModel(){
        contactsViewModel = ViewModelProviders.of(this).get(ContactsViewModel::class.java)
        contactsViewModel.mUserFollowing.observe(this, Observer {
            for (item in it.rows) {
                contactsAdapter.addItem(item)
            }
        })
        if (SessionManager.isUserLogged) {
            contactsViewModel.getUserFollowing(SessionManager.session.user!!.id)
        }
    }

    private fun initRecycleView() {
        rcv_suggest_friend.adapter = contactsAdapter
        rcv_suggest_friend.layoutManager = LinearLayoutManager(context)
        rcv_members.adapter = memberAdapter
        rcv_members.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onContactChildClick(childContact: ICFollowing.Rows) {
        memberAdapter.addData(childContact)
        contactsAdapter.removeContact(childContact)
    }

    override fun onChildMemberClick(row: ICFollowing.Rows) {
        contactsAdapter.addItem(row)
        memberAdapter.removeContact(row)
    }
}