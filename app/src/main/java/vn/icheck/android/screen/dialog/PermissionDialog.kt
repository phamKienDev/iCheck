package vn.icheck.android.screen.dialog

import android.Manifest
import android.content.Context
import kotlinx.android.synthetic.main.dialog_ask_permission.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseDialog
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.ichecklibs.ViewHelper

class PermissionDialog(context: Context, val type: Int, val listener: PermissionListener): BaseDialog(context, R.style.DialogTheme) {

    companion object {
        const val LOCATION = 1
        const val CONTACT = 2
        const val CAMERA = 3
        const val GALLERY = 4
        const val CALL = 5
        const val STORAGE = 6

        fun checkPermission(context: Context?, type: Int, listener: PermissionListener) {
            val isAllow = when (type) {
                LOCATION -> {
                    PermissionHelper.isAllowPermission(context, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                }
                CONTACT -> {
                    PermissionHelper.isAllowPermission(context, Manifest.permission.READ_CONTACTS)
                }
                CAMERA -> {
                    PermissionHelper.isAllowPermission(context, Manifest.permission.CAMERA)
                }
                GALLERY -> {
                    PermissionHelper.isAllowPermission(context, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
                STORAGE -> {
                    PermissionHelper.isAllowPermission(context, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
                else -> {
                    PermissionHelper.isAllowPermission(context, Manifest.permission.CALL_PHONE)
                }
            }

            if (isAllow) {
                listener.onPermissionAllowed()
            } else {
                context?.let {
                    PermissionDialog(context, type, listener).show()
                }
            }
        }
    }

    override val getLayoutID: Int
        get() = R.layout.dialog_ask_permission

    override val getIsCancelable: Boolean
        get() = false

    override fun onInitView() {
        val icon: Int
        val title: Int
        val message: Int
        val button: Int

        setupView()

        when (type) {
            LOCATION -> {
                icon = R.drawable.ic_permission_location
                title = R.string.ask_permission_location_title
                message = R.string.ask_permission_location_message
                button = R.string.ask_permission_location_button
            }
            CONTACT -> {
                icon = R.drawable.ic_permission_contact
                title = R.string.ask_permission_contact_title
                message = R.string.ask_permission_contact_message
                button = R.string.ask_permission_contact_button
            }
            CAMERA -> {
                icon = R.drawable.ic_permission_camera
                title = R.string.ask_permission_camera_title
                message = R.string.ask_permission_camera_message
                button = R.string.ask_permission_camera_button
            }
            GALLERY -> {
                icon = R.drawable.ic_permission_gallery
                title = R.string.ask_permission_gallery_title
                message = R.string.ask_permission_gallery_message
                button = R.string.ask_permission_gallery_button
            }
            STORAGE -> {
                icon = R.drawable.ic_permission_gallery
                title = R.string.ask_permission_storage_title
                message = R.string.ask_permission_storage_message
                button = R.string.ask_permission_storage_button
            }
            else -> {
                icon = R.drawable.ic_permission_call
                title = R.string.ask_permission_call_title
                message = R.string.ask_permission_call_message
                button = R.string.ask_permission_call_button
            }
        }


        imgIcon.setImageResource(icon)
        tvTitle.setText(title)
        tvMessage.setText(message)
        tvAllow.setText(button)

        tvAllow.setOnClickListener {
            dismiss()
            listener.onRequestPermission()
        }

        tvSkip.setOnClickListener {
            dismiss()
            listener.onPermissionNotAllow()
        }
    }

    private fun setupView() {
        constraintLayout.background=ViewHelper.bgWhiteCorners10(constraintLayout.context)
        tvAllow.background=ViewHelper.btnSecondaryCorners6(constraintLayout.context)
    }

    interface PermissionListener {
        // Quyền đã được cho phép
        fun onPermissionAllowed()

        // User cho phép sử dụng quyền
        fun onRequestPermission()

        // User không cho phép sử dụng quyền
        fun onPermissionNotAllow()
    }
}