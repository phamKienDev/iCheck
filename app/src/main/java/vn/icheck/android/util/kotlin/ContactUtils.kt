package vn.icheck.android.util.kotlin

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.RemoteException
import android.provider.CallLog
import android.provider.ContactsContract
import android.text.TextUtils
import android.widget.Toast
import java.util.ArrayList
import java.util.HashMap
import java.nio.file.Files.delete
import android.provider.ContactsContract.PhoneLookup
import android.annotation.SuppressLint
import androidx.core.app.ActivityCompat
import vn.icheck.android.R


object ContactUtils {

    fun callFast(activity: Activity, phone: String) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone"))
        activity.startActivity(intent)
    }

    fun writePhoneContact(contentResolver: ContentResolver, displayName: String, number: String /*App or Activity Ctx*/): Int {
        val cntProOper = ArrayList<ContentProviderOperation>()
        val contactIndex = cntProOper.size//ContactSize

        //Newly Inserted contact
        // A raw contact will be inserted ContactsContract.RawContacts table in contacts database.
        cntProOper.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)//Step1
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build()
        )

        //Display name will be inserted in ContactsContract.Data table
        cntProOper.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step2
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )
                .withValue(
                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                    displayName
                ) // Name of the contact
                .build()
        )

        //Mobile number will be inserted in ContactsContract.Data table
        cntProOper.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step 3
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number) // Number to be added
                .withValue(
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                )
                .build()
        )

        try {
            // We will do batch operation to insert all above data
            //Contains the output of the app of a ContentProviderOperation.
            //It is sure to have exactly one of uri or count set
            val results = contentResolver.applyBatch(
                ContactsContract.AUTHORITY,
                cntProOper
            ) //apply above data insertion into contacts list
            return Integer.parseInt(results[0].uri?.lastPathSegment.toString())
        } catch (exp: RemoteException) {
            //logs;
            return -1
        } catch (exp: OperationApplicationException) {
            //logs
            return -1
        }

    }


    fun updateContactByNumber(contentResolver: ContentResolver, oldNumber: String, newName: String?, newCompany: String?, newAddress:String?): Boolean {
        // These are the Contacts rows that we will retrieve.
        val baseUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val idProjection = arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
        val udSelection = "REPLACE(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ",' ', '')=?"
        val udSelectionArgs = arrayOf(oldNumber)

        val cursorID = contentResolver.query(
            baseUri,
            idProjection,
            udSelection,
            udSelectionArgs,
            null
        )

        val rawID: String
        if (cursorID != null && cursorID.moveToFirst()) {
            rawID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
            cursorID.close()
        } else {
            return false
        }


        val projection = arrayOf(ContactsContract.RawContacts._ID)
        val idSelection = ContactsContract.RawContacts.CONTACT_ID + "=?"
        val idSelectionArgs = arrayOf(rawID)

        val c = contentResolver.query(
            ContactsContract.RawContacts.CONTENT_URI,
            projection,
            idSelection,
            idSelectionArgs,
            null
        )

        val rawContactId: String
        if (c != null && c.moveToFirst()) {
            rawContactId = c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID)).toString()
            c.close()
        } else {
            return false
        }

        val ops = ArrayList<ContentProviderOperation>()
        var builder: ContentProviderOperation.Builder


        // Name
        if (newName != null) {
            val strings = newName.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            var family_name = ""
            var middle_name = ""
            var given_name = ""

            if (strings.size == 1) {
                given_name = strings[0]
            } else if (strings.size == 2) {
                family_name = strings[strings.size - 1]
                given_name = strings[0] + " "
            } else if (strings.size > 2) {
                family_name = strings[strings.size - 1]

                for (i in 1 until strings.size - 1) {
                    strings[i] += " "
                    middle_name += strings[i]
                }

                given_name = strings[0] + " "
            }

            val selection =
                ContactsContract.Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?"
            val selectArg = arrayOf(rawContactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            val nameCursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, selection, selectArg, null)

            if (nameCursor != null && nameCursor.moveToFirst()) {
                builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                builder.withSelection(selection, selectArg)

                builder.withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, given_name)
                builder.withValue(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, middle_name)
                builder.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, family_name)

                builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newName)
                ops.add(builder.build())
            } else {
                builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)

                builder.withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                builder.withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )

                builder.withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, given_name)
                builder.withValue(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, middle_name)
                builder.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, family_name)

                ops.add(builder.build())
            }

            nameCursor?.close()

        }

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Company
        if (newCompany != null) {
            val companySelection =
                ContactsContract.Data.RAW_CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?"
            val companySelectArg = arrayOf(rawContactId, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
            val companylCursor =
                contentResolver.query(ContactsContract.Data.CONTENT_URI, null, companySelection, companySelectArg, null)

            if (companylCursor != null && companylCursor.moveToFirst()) {
                builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                builder.withSelection(companySelection, companySelectArg)
                builder.withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, newCompany)
                builder.withValue(
                    ContactsContract.CommonDataKinds.Organization.TYPE,
                    ContactsContract.CommonDataKinds.Organization.TYPE_WORK
                )
                ops.add(builder.build())
            } else {
                builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                builder.withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                builder.withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
                )
                builder.withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, newCompany)
                builder.withValue(
                    ContactsContract.CommonDataKinds.Organization.TYPE,
                    ContactsContract.CommonDataKinds.Organization.TYPE_WORK
                )
                ops.add(builder.build())
            }

            companylCursor?.close()
        }

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Address
        if (newAddress != null) {
            val addressSelection =
                ContactsContract.Data.RAW_CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?"
            val addressSelectArg = arrayOf(rawContactId, ContactsContract.CommonDataKinds.SipAddress.CONTENT_ITEM_TYPE)
            val addressCursor =
                contentResolver.query(ContactsContract.Data.CONTENT_URI, null, addressSelection, addressSelectArg, null)

            if (addressCursor != null && addressCursor.moveToFirst()) {
                builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                builder.withSelection(addressSelection, addressSelectArg)
                builder.withValue(ContactsContract.CommonDataKinds.SipAddress.DATA, newAddress)
                builder.withValue(
                    ContactsContract.CommonDataKinds.SipAddress.TYPE,
                    ContactsContract.CommonDataKinds.SipAddress.TYPE_WORK
                )
                ops.add(builder.build())
            } else {
                builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                builder.withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                builder.withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
                )
                builder.withValue(ContactsContract.CommonDataKinds.SipAddress.DATA, newAddress)
                builder.withValue(
                    ContactsContract.CommonDataKinds.SipAddress.TYPE,
                    ContactsContract.CommonDataKinds.SipAddress.TYPE_WORK
                )
                ops.add(builder.build())
            }

            addressCursor?.close()
        }

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    private fun getContactEmail(contentResolver: ContentResolver, id: Long): String? {
        var email: String? = null

        val emailCur = contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
            ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
            arrayOf(id.toString()), null
        )

        if (emailCur != null && emailCur.moveToFirst()) {
            email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
            emailCur.close()
        }

        return email
    }
}