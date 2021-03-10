package vn.icheck.android.fragments

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import vn.icheck.android.R
import vn.icheck.android.activities.InviteFriendActivity
import vn.icheck.android.activities.chat.v2.ChatV2Activity
import vn.icheck.android.adapters.ContactsAdapter
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.chat.ContactsViewModel
import vn.icheck.android.network.models.ICFollowing
import java.lang.Exception

class ContactsFragment : Fragment(), View.OnClickListener, ContactsAdapter.OnContactClick {

    private lateinit var contactsViewModel: ContactsViewModel
    private val contactsAdapter = ContactsAdapter(this)
    private var init = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    private fun initViewModel() {
        contactsViewModel = ViewModelProvider(this).get(ContactsViewModel::class.java)
        contactsViewModel.mContacts.observe(viewLifecycleOwner, Observer { phoneList ->
            val phoneMap = HashMap<String, List<String>>()
            phoneMap["contacts"] = phoneList
            contactsViewModel.callSyncContacts(phoneMap)
        })
        contactsViewModel.mUserCall.observe(viewLifecycleOwner, Observer {
            //            contactsAdapter.addItem(it.name!!)
        })
//        contactsViewModel.mUserFollowing.observe(this, Observer {
//            for (item in it.rows) {
//                contactsAdapter.addItem(item)
//            }
//        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycleView()
        initViews()
        initViewModel()
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun initViews() {
        btn_sync_contact.setOnClickListener(this)
        btn_invite_friend.setOnClickListener(this)
    }

    private fun initRecycleView() {
        rcv_contacts.adapter = contactsAdapter
        rcv_contacts.layoutManager = LinearLayoutManager(context)
    }

    override fun onResume() {
        super.onResume()

        if (!PermissionHelper.isAllowPermission(context, Manifest.permission.READ_CONTACTS) && !init) {
            PermissionHelper.checkPermission(activity!!, Manifest.permission.READ_CONTACTS, 1)
        }
        init = true
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ICMessageEvent) {
        when (event.type) {
            ICMessageEvent.Type.ON_LOG_OUT -> {
                contactsAdapter.list.clear()
                contactsAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_sync_contact -> {
                if (!PermissionHelper.isAllowPermission(context, Manifest.permission.READ_CONTACTS)) {
                    PermissionHelper.checkPermission(activity!!, Manifest.permission.READ_CONTACTS, 1)
                } else {
                    if (SessionManager.isUserLogged) {
//            contactsViewModel.getUserFollowing(SessionManager.session.user!!.id)
                        lifecycleScope.launch {
                            try {
                                contactsAdapter.list.clear()
                                val response = ICNetworkClient.getSimpleApiClient().getFollow("user",
                                        SessionManager.session.user?.id)
                                contactsAdapter.list.addAll(response.rows)
                                contactsAdapter.notifyDataSetChanged()
                            } catch (e: Exception) {
                                Log.e("e", "${e.message}")
                            }
                        }
                    }
                    viewLifecycleOwner.lifecycleScope.launch {
                        withContext(Dispatchers.IO)
                        {
                            delay(100)
                            contactsViewModel.getContacts()
                        }
                    }
                }
            }
            R.id.btn_invite_friend -> {
                startActivity(Intent(context, InviteFriendActivity::class.java))
            }
        }
    }

    override fun onContactChildClick(childContact: ICFollowing.Rows) {
        if (SessionManager.isUserLogged) {
            SessionManager.session.user?.let {
                val id = "i-${it.id}|i-${childContact.rowObject.id}"
//                ChatActivity.createChatUser(childContact.rowObject.id, activity!!)
                ChatV2Activity.createNewChat(childContact.rowObject.id, activity!!)
            }
        }

    }

    private fun setMetadata(id: String, name: String) {
        val userId = "i-" + SessionManager.session.user!!.id
        val database = FirebaseDatabase.getInstance()
        val messagesRef = database.reference
        messagesRef.child("room-metadata").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                var roomId = ""
                for (item in p0.children) {
                    if (item.key!!.contains(id) && item.key!!.contains(userId)) {
                        roomId = item.key!!
                    }
                }
                if (roomId.isNotEmpty()) {
                    val intent = Intent(context, ChatV2Activity::class.java)
                    intent.putExtra("id", roomId)
                    intent.putExtra("name", name)
                    startActivity(intent)
                }
            }
        })
    }
}
