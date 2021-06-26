/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vn.icheck.android.ichecklibs.camera.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import vn.icheck.android.ichecklibs.R

private const val PERMISSIONS_REQUEST_CODE = 10
private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

/**
 * This [Fragment] requests permissions and, once granted, it will navigate to the next fragment
 */
class PermissionsFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cameraManager =
                requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager

        val cameraList = SelectorFragment.enumerateCameras(cameraManager)
        val id = cameraList.firstOrNull {
            it.title.contains("JPEG") && it.title.contains("Back")
        }
        if (hasPermissions(requireContext())) {
            // If permissions have already been granted, proceed
           findNavController().navigate(PermissionsFragmentDirections.actionPermissionsFragmentToCameraFragment(id?.cameraId.toString(), ImageFormat.JPEG))
        } else {
            // Request camera-related permissions
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val cameraManager =
                        requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager

                val cameraList = SelectorFragment.enumerateCameras(cameraManager)
                val id = cameraList.firstOrNull {
                    it.title.contains("JPEG") && it.title.contains("Back")
                }

                // Takes the user to the success fragment when permission is granted
               findNavController().navigate(PermissionsFragmentDirections.actionPermissionsFragmentToCameraFragment(id?.cameraId.toString(), ImageFormat.JPEG))
            } else {
                Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {

        /** Convenience method used to check if all permissions required by this app are granted */
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
