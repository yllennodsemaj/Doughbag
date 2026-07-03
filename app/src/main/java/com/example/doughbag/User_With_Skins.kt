package com.example.doughbag

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

data class User_With_Skins(
    @Embedded val user: User,
    @Relation(
        parentColumn = "user_ID",
        entityColumn = "userOwnerId"
    )
    val skins:List<Owned_Skins>
)
