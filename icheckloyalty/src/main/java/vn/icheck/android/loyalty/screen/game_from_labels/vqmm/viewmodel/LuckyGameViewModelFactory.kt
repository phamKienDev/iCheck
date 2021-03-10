package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

class LuckyGameViewModelFactory(private val owner: SavedStateRegistryOwner, private val campaignId:Long, private val count:Int?): AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T  = LuckyGameViewModel(campaignId, count) as T
}