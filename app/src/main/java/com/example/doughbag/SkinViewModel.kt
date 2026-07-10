package com.example.doughbag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import androidx.lifecycle.ViewModelProvider

class SkinViewModel(private val skinDao: SkinDao) : ViewModel() {

    // 1. Your master catalog of skins


    // 2. Track which user is currently logged in/active
    private val _currentUserId = MutableStateFlow<Int?>(null)

    // Tell the ViewModel who the active user is so it fetches their skins
    fun loadSkinsForUser(userId: Int) {
        _currentUserId.value = userId
    }

    // 3. Fetch the raw database records for the active user
    @OptIn(ExperimentalCoroutinesApi::class)
    private val userOwnedSkinsFromDb: Flow<List<Owned_Skins>> = _currentUserId.flatMapLatest { userId ->
        if (userId != null) {
            skinDao.getOwnedSkinsForUser(userId)
        } else {
            flowOf(emptyList()) // Return empty if no user is set
        }
    }

    private val _activeSkin = MutableStateFlow<Skin?>(null)
    val activeSkin: StateFlow<Skin?> = _activeSkin.asStateFlow()

    fun setActiveSkin(skin: Skin) {
        _activeSkin.value = skin
    }

    class SkinViewModelFactory(private val skinDao: SkinDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // This MUST check for SkinViewModel, NOT DoughbagViewModel
            if (modelClass.isAssignableFrom(SkinViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SkinViewModel(skinDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    // skins here

}