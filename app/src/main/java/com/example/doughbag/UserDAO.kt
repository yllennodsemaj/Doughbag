package com.example.doughbag

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>> // flow updates if database changes

    @Query("Select balance from users where user_ID = :user_ID")
    fun getBalance(user_ID: Int): Flow<Double>

    @Query("update users set balance = balance + :amount where user_ID = :user_ID")
    fun updateBalance(user_ID: Int, amount: Double)

}