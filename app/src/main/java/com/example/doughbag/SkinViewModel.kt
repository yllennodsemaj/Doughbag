package com.example.doughbag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import androidx.lifecycle.ViewModelProvider

class SkinViewModel(private val skinDao: SkinDao) : ViewModel() {

    // 1. Your master catalog of skins
    private val defaultSkins = listOf(
        Skin.WheelSkin(0, "Default Wheel", 0, 0),

        Skin.DiceSkin(1, "Default Dice", 0,
            dice1 = R.drawable.dice_1, dice2 = R.drawable.dice_2,
            dice3 = R.drawable.dice_3, dice4 = R.drawable.dice_4,
            dice5 = R.drawable.dice_5, dice6 = R.drawable.dice_6),

        Skin.SlotSkin(2, "Default Slots", 0, R.drawable.spade,
            R.drawable.clover,
            R.drawable.heart, R.drawable.diamond, R.drawable.jackpot, R.drawable.seven),

        Skin.DiceSkin(skinID = 3, skinName = "White Dice", skinPrice = 1000,
            dice1 = R.drawable.white_dice_1, dice2 = R.drawable.white_dice_2,
            dice3 = R.drawable.white_dice_3, dice4 = R.drawable.white_dice_4,
            dice5 = R.drawable.white_dice_5, dice6 = R.drawable.white_dice_6),

        Skin.DiceSkin(4, "Neon Dice", 5000,
            dice1 = R.drawable.dice1_neon_512, dice2 = R.drawable.dice2_neon_512,
            dice3 = R.drawable.dice3_neon_512, dice4 = R.drawable.dice4_neon_512,
            dice5 = R.drawable.dice5_neon_512, dice6 = R.drawable.dice6_neon_512),

        Skin.SlotSkin(5, "Neon Slots", 5000,
            spade = R.drawable.spade_neon_512,
            club = R.drawable.club_neon_512,
            heart = R.drawable.heart_neon_512,
            diamond = R.drawable.diamond_neon_512,
            jack = R.drawable.jackpot_neon_512,
            seven = R.drawable.seven_neon_512),

        Skin.DiceSkin(6, "Pixel Dice", 5000,
            dice1 = R.drawable.dice1_pixel_512, dice2 = R.drawable.dice2_pixel_512,
            dice3 = R.drawable.dice3_pixel_512, dice4 = R.drawable.dice4_pixel_512,
            dice5 = R.drawable.dice5_pixel_512, dice6 = R.drawable.dice6_pixel_512),

        Skin.SlotSkin(7, "Pixel Slots", 5000,
            spade = R.drawable.spade_pixel_512,
            club = R.drawable.club_pixel_512,
            heart = R.drawable.heart_pixel_512,
            diamond = R.drawable.diamond_pixel_512,
            jack = R.drawable.jackpot_pixel_512,
            seven = R.drawable.seven_pixel_512)
    )

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

    // 4. INVENTORY LIST: Only skins where the skinName is found in the database
    val inventorySkins: StateFlow<List<Skin>> = combine(
        flowOf(defaultSkins),
        userOwnedSkinsFromDb
    ) { catalog, ownedDbRecords ->
        val ownedNames = ownedDbRecords.map { it.name }
        catalog.filter { skin -> skin.skinName in ownedNames }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 5. SHOP LIST: Only skins where the skinName is NOT found in the database
    val shopSkins: StateFlow<List<Skin>> = combine(
        flowOf(defaultSkins),
        userOwnedSkinsFromDb
    ) { catalog, ownedDbRecords ->
        val ownedNames = ownedDbRecords.map { it.name }
        catalog.filter { skin -> skin.skinName !in ownedNames }
    }.stateIn(viewModelScope, SharingStarted.Lazily, defaultSkins)

    // ... (keep your setActiveSkin logic here)
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