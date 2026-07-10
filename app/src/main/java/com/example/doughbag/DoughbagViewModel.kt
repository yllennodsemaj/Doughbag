package com.example.doughbag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

// view model
class DoughbagViewModel(private val userDao: UserDao) : ViewModel() {

    // pulls live list of users from db
    // stateflow auto updates live data
    val userList: StateFlow<List<User>> = userDao.getAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Keeps flow active for 5s after UI disconnects
            initialValue = emptyList() // The value provided before the DB returns data
        )

    // sets & remembers active user
    var activeUser: User? = null
        private set

    fun setActiveUser(user: User) {
        activeUser = user
    }

    // runs on background to prevent lagging
    fun createNewUser(name: String) {
        viewModelScope.launch {
            val newUser = User(user_name = name)
            userDao.insertUser(newUser)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            userDao.deleteUser(user)
        }
        activeUser = null
    }

    fun updateBalance(user_ID: Int, newBalance: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.updateBalance(user_ID, newBalance)
        }
    }

    fun updateStats(
        userId: Int,
        wins: Int = 0, losses: Int = 0,
        rWins: Int = 0, rLosses: Int = 0,
        cWins: Int = 0, cLosses: Int = 0,
        sWins: Int = 0, sLosses: Int = 0,
        winnings: Double = 0.0, lossAmt: Double = 0.0
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.updateStats(userId, wins, losses, rWins, rLosses,
                cWins, cLosses, sWins, sLosses, winnings, lossAmt)
        }
    }

    // Helper to get the list of IDs as a List<Int>
    fun getOwnedIds(user: User): List<Int> {
        return user.ownedSkinIds.split(",").mapNotNull { it.toIntOrNull() }
    }
    fun setActiveSkin(user: User, skin: Skin){
        val listActiveSkins: MutableList<Int> = // transform string of active skins to mutable list
            user.activeSkins.split(",").mapNotNull{ it.toIntOrNull() }.toMutableList()
        if (skin is Skin.WheelSkin) {
            listActiveSkins[0] = skin.skinID
        }
        else if (skin is Skin.DiceSkin) {
            listActiveSkins[1] = skin.skinID
        }
        else if (skin is Skin.SlotSkin) {
            listActiveSkins[2] = skin.skinID
        }
        user.activeSkins = listActiveSkins.joinToString(separator = ",")
        return
    }

    // Function to "buy" a skin
    // not yet implemented :(
    fun purchaseSkin(user: User, skinId: Int) {
        val currentOwned = getOwnedIds(user).toMutableList()
        if (!currentOwned.contains(skinId)) {
            currentOwned.add(skinId)
            user.ownedSkinIds = currentOwned.joinToString(",")
            // Update the user in the database with the new string
            viewModelScope.launch { userDao.updateUser(user) }
        }
    }

}

class DoughbagViewModelFactory(
    private val userDao: UserDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // This MUST check for DoughbagViewModel
        if (modelClass.isAssignableFrom(DoughbagViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoughbagViewModel(userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
