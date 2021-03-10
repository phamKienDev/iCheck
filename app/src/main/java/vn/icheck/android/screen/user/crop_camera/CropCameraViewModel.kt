package vn.icheck.android.screen.user.crop_camera

import androidx.lifecycle.ViewModel

class CropCameraViewModel : ViewModel() {
    private var isFlash = false

    fun setIsFlash(flash: Boolean) {
        isFlash = flash
    }

    val getIsFlash:Boolean get() = isFlash
}