package com.example.doughbag

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SkinDao {

    // 1. Give a user a new skin (when they buy it)
    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    //suspend fun insertOwnedSkin(skin: Owned_Skins)
    //@Query("SELECT * FROM owned_skins WHERE user_ID = :user_ID")

    //fun getUserPurchasedSkins(user_ID: Int): Flow<List<Owned_Skins>>
    //@Query("select * from skins where user_ID")

    // buy a skin for a user
    //@Query("update skins set owned = user_ID where skin_ID = :skin_ID")
    //fun buySkin(user_ID: Int, skin_ID: Int)
}