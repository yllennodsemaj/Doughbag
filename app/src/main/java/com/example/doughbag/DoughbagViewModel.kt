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

// view model
class DoughbagViewModel(private val userDao: UserDao, private val skinDao: SkinDao) : ViewModel() {

    // pulls live list of users from db
    // stateflow auto updates live data
    val userList: StateFlow<List<User>> = userDao.getAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
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
    fun updateActiveUser(updatedUser: User) {
        viewModelScope.launch {
            userDao.updateUser(updatedUser)
            activeUser = updatedUser
        }
    }

    fun updateBalance(user_ID: Int, win: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val rowsUpdated = userDao.updateBalance(user_ID, win)
            Log.d("DatabaseDebug", "Rows updated: $rowsUpdated for User ID: $user_ID")
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
