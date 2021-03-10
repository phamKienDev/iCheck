package vn.icheck.android.activities

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_invite_friend.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.icheck.android.R
import vn.icheck.android.adapters.InviteFriendAdapter

class InviteFriendActivity : AppCompatActivity(), View.OnClickListener, InviteFriendAdapter.OnInviteFriendClick {
    private val listContact = MutableLiveData<List<FriendContact>>()
    private val inviteFriendAdapter = InviteFriendAdapter(this)

    companion object {
        private val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_friend)
        initRecycleView()
        img_back.setOnClickListener(this)
        listContact.observe(this, Observer {
            inviteFriendAdapter.updateList(it)
        })
    }

    private fun initRecycleView() {
        rcv_friends.adapter = inviteFriendAdapter
        rcv_friends.layoutManager = LinearLayoutManager(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_back -> {
                onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                getAllContacts()
            }
        }
    }

    private fun getAllContacts() {
        try {
            val contentResolver: ContentResolver = application.contentResolver
            val myList = mutableListOf<FriendContact>()
            val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null)
            if (cursor != null) {
                try {
                    val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    while (cursor.moveToNext()) {
                        val name = cursor.getString(nameIndex)
                        val number = cursor.getString(numberIndex)
                        myList.add(FriendContact(name, number, null))
                    }
                } finally {
                    cursor.close()
                }
            }
            listContact.postValue(myList)
        } catch (e: Exception) {
        }
    }

    override fun onClick(friendContact: FriendContact) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms: " + friendContact.phone))
        val message = getString(R.string.invite_sms)
        intent.putExtra("sms_body", message)
        startActivity(intent)
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.from_left_in, R.anim.from_right_out)
    }
}

data class FriendContact(
        val name: String,
        val phone: String,
        val avatar: String?
)
