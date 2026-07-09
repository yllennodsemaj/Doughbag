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
    suspend fun insertUser(user: User): Long

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>> // flow updates if database changes

    @Query("Select balance from users where user_ID = :user_ID")
    fun getBalance(user_ID: Int): Flow<Double>

    @Query("select balance from users where user_ID = :user_ID")
    suspend fun getBalanceSync(user_ID: Int): Double

    @Query("update users set balance = :newBalance where user_ID = :user_ID")
    suspend fun updateBalance(user_ID: Int, newBalance: Double): Int

    // can update stats based on what is passed, based on user_ID
    @Query(""" 
    update users set 
    total_wins = total_wins + :wins, total_losses = total_losses + :losses, 
    rWins = rWins + :rWins, rLosses = rLosses + :rLosses, 
    cWins = cWins + :cWins, cLosses = cLosses + :cLosses, 
    sWins = sWins + :sWins, sLosses = sLosses + :sLosses, 
    winnings = winnings + :winnings, losses = losses + :lossAmt 
    where user_ID = :userId 
""")
    suspend fun updateStats( // updates any stat passed or defaults to 0 - no update
        userId: Int, wins: Int = 0,
        losses: Int = 0, rWins: Int = 0,
        rLosses: Int = 0, cWins: Int = 0,
        cLosses: Int = 0, sWins: Int = 0,
        sLosses: Int = 0, winnings: Double = 0.0,
        lossAmt: Double = 0.0
    )

    @Query("select cWins from users where user_ID = :userId")
    fun getCWins(userId: Int): Int

    @Query("select cLosses from users where user_ID = :userId")
    fun getCLosses(userId: Int): Int

    @Query("select rWins from users where user_ID = :userId")
    fun getRWins(userId: Int): Int

    @Query("select rLosses from users where user_ID = :userId")
    fun getRLosses(userId: Int): Int

    @Query("select sWins from users where user_ID = :userId")
    fun getSWins(userId: Int): Int

    @Query("select sLosses from users where user_ID = :userId")
    fun getSLosses(userId: Int): Int

    @Query("select total_wins from users where user_ID = :userId")
    fun getNumWins(userId: Int): Int

    @Query("select total_losses from users where user_ID = :userId")
    fun getNumLosses(userId: Int): Int

    @Query("select winnings from users where user_ID = :userId")
    fun getWinnings(userId: Int): Double

    @Query("select losses from users where user_ID = :userId")
    fun getLosses(userId: Int): Double

}