package com.example.doughbag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

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

    fun setActiveWheelSkin(skin: Skin) {

    }

    fun setActiveCrapsSkin(skin: Skin) {

    }

    fun setActiveSlotsSkin(skin: Skin) {

    }

    // Function to "buy" a skin
    fun purchaseSkin(user: User, skinId: Int) {
        val currentOwned = getOwnedIds(user).toMutableList()
        if (!currentOwned.contains(skinId)) {
            currentOwned.add(skinId)
            user.ownedSkinIds = currentOwned.joinToString(",")
            // Update the user in the database with the new string
            viewModelScope.launch { userDao.updateUser(user) }
        }
    }

    private val _cWins = MutableStateFlow(0)
    val cWins: StateFlow<Int> = _cWins
    fun fetchCWins(user_ID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _cWins.value = userDao.getCWins(user_ID)
        }
    }

    private val _cLosses = MutableStateFlow(0)
    val cLosses: StateFlow<Int> = _cLosses
    fun fetchCLosses(user_ID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _cLosses.value = userDao.getCLosses(user_ID)
        }
    }

    private val _rWins = MutableStateFlow(0)
    val rWins: StateFlow<Int> = _rWins
    fun fetchRWins(user_ID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _rWins.value = userDao.getRWins(user_ID)
        }
    }

    private val _rLosses = MutableStateFlow(0)
    val rLosses: StateFlow<Int> = _rLosses
    fun fetchRLosses(user_ID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _rLosses.value = userDao.getRLosses(user_ID)
        }
    }

    private val _sWins = MutableStateFlow(0)
    val sWins: StateFlow<Int> = _sWins
    fun fetchSWins(user_ID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _sWins.value = userDao.getSWins(user_ID)
        }
    }

    private val _sLosses = MutableStateFlow(0)
    val sLosses: StateFlow<Int> = _sLosses
    fun fetchSLosses(user_ID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _sLosses.value = userDao.getSLosses(user_ID)
        }
    }

    private val _numWins = MutableStateFlow(0)
    val numWins: StateFlow<Int> = _numWins
    fun fetchNumWins(user_ID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _numWins.value = userDao.getNumWins(user_ID)
        }
    }

    private val _numLosses = MutableStateFlow(0)
    val numLosses: StateFlow<Int> = _numLosses
    fun fetchNumLosses(user_ID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _numLosses.value = userDao.getNumLosses(user_ID)
        }
    }

    private val _winnings = MutableStateFlow(0.0)
    val winnings: StateFlow<Double> = _winnings
    fun fetchWinnings(user_ID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _winnings.value = userDao.getWinnings(user_ID)
        }
    }

    private val _losses = MutableStateFlow(0.0)
    val losses: StateFlow<Double> = _losses
    fun fetchLosses(user_ID: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _losses.value = userDao.getLosses(user_ID)
        }
    }
}

class DoughbagViewModelFactory(
    private val userDao: UserDao,
    private val skinDao: SkinDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // This MUST check for DoughbagViewModel
        if (modelClass.isAssignableFrom(DoughbagViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoughbagViewModel(userDao, skinDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
