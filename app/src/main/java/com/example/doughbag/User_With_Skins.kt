package com.example.doughbag

import androidx.room.Embedded
import androidx.room.Relation

data class User_With_Skins(
    @Embedded val user: User,
    @Relation(
        parentColumn = "user_ID",
        entityColumn = "user_ID"
    )
    val skins: List<Owned_Skins>
)