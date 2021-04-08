package vn.icheck.android.ichecklibs.take_media

import android.net.Uri
import androidx.fragment.app.Fragment
import java.io.File

interface TakeMediaListener:TakeMediaHelper.TakeCameraListener {
    fun onPickMediaSucess(file: File)
    fun onPickMuliMediaSucess(file: MutableList<File>)
    fun onStartCrop( filePath: String?, uri: Uri?, ratio: String?, requestCode: Int? = null)
    fun onDismiss()
}