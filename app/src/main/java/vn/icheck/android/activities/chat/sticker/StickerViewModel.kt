package vn.icheck.android.activities.chat.sticker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.icheck.android.network.feature.social.SocialRepository
import vn.icheck.android.network.models.chat.DetailSticker
import vn.icheck.android.network.models.chat.StickerWrapper

class StickerViewModel : ViewModel() {
    val stickerPackage = MutableLiveData<StickerWrapper>()
    var stickers = MutableLiveData<DetailSticker>()
    private val socialRepository = SocialRepository()

    init {
        viewModelScope.launch {
            try {
                val result = socialRepository.getStickerPackages()
                stickerPackage.postValue(result)
            } catch (e: Exception) {
            }
        }
    }

    fun getStickers(id: String) {
        viewModelScope.launch {
            try {
                val result = socialRepository.getStickers(id)
                stickers.postValue(result)
            } catch (e: Exception) {
            }
        }
    }
}