package vn.icheck.android.helper

import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity


object ContactHelper {

    fun pickContact(fragmen: Fragment, requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        fragmen.startActivityForResult(intent, requestCode)
    }

    fun getPhone(activity: FragmentActivity?, data: Intent?): String? {
        if (activity == null || data == null) {
            return null
        }

        try {
            val cursor = activity.managedQuery(data.data, null, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if (hasPhone == "1") {
                   return getDataCurror(activity, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null, ContactsContract.CommonDataKinds.Phone.NUMBER)
                }
            }
        }catch (e:Exception){
        }
        return null
    }

    fun getDetailPhone(activity: FragmentActivity?, data: Intent?): List<String>? {
        if (activity == null || data == null) {
            return null
        }

        try {
            val listInfo = mutableListOf<String>()
            val cursor = activity.managedQuery(data.data, null, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if (hasPhone == "1") {
                    val selection = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"

                    // phone number
                    listInfo.add(getDataCurror(activity, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null, ContactsContract.CommonDataKinds.Phone.NUMBER))

                    //display name
                    listInfo.add(getDataCurror(activity, ContactsContract.Data.CONTENT_URI, selection,
                            arrayOf(id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE), ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME))

                    //middle name
                    listInfo.add(getDataCurror(activity, ContactsContract.Data.CONTENT_URI, selection,
                            arrayOf(id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE), ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME))

                    //sure name
                    listInfo.add(getDataCurror(activity, ContactsContract.Data.CONTENT_URI, selection,
                            arrayOf(id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE), ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME))

                    //email
                    listInfo.add(getDataCurror(activity, ContactsContract.CommonDataKinds.Email.CONTENT_URI, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id,
                            null, ContactsContract.CommonDataKinds.Email.DATA))

                    // address
                    val listAddress = mutableListOf<String>()
                    listAddress.add(getDataCurror(activity, ContactsContract.Data.CONTENT_URI, selection,
                            arrayOf(id, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE), ContactsContract.CommonDataKinds.StructuredPostal.STREET))

                    listAddress.add(getDataCurror(activity, ContactsContract.Data.CONTENT_URI, selection,
                            arrayOf(id, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE), ContactsContract.CommonDataKinds.StructuredPostal.CITY))

                    listAddress.add(getDataCurror(activity, ContactsContract.Data.CONTENT_URI, selection,
                            arrayOf(id, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE), ContactsContract.CommonDataKinds.StructuredPostal.REGION))

                    listAddress.add(getDataCurror(activity, ContactsContract.Data.CONTENT_URI, selection,
                            arrayOf(id, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE), ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY))

                    var address = ""
                    listAddress.forEach {
                        if (it.isNotEmpty()) {
                            address += "$it , "
                        }
                    }

                    listInfo.add(if(address.isEmpty()){
                        ""
                    }else{
                        address.substring(0, address.length - 3)
                    })

                    //note
                    listInfo.add(getDataCurror(activity, ContactsContract.Data.CONTENT_URI, selection,
                            arrayOf(id, Note.CONTENT_ITEM_TYPE), Note.NOTE))
                }
            }
            return listInfo
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun getDataCurror(activity: FragmentActivity, uri: Uri, selection: String?, selectionArgs: Array<String>?, columnIndex: String): String {
        try {
            val cursor = activity.contentResolver.query(uri, null, selection, selectionArgs, null)

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(columnIndex))
            }
            cursor?.close()
            return ""
        } catch (e: Exception) {
            return ""
        }
    }
}