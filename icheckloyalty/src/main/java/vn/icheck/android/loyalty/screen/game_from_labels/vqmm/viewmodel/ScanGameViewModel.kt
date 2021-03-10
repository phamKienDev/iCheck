package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.viewmodel

import androidx.lifecycle.ViewModel
import vn.icheck.android.loyalty.repository.VQMMRepository

internal class ScanGameViewModel : ViewModel() {
    val scanGameRepository = VQMMRepository()
}