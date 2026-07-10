package com.example.doughbag

//sort of like nested classes when they have differing properties
sealed class Skin(val skinID: Int, val skinName: String, val skinPrice: Int, var owned: Boolean = false) {
    class WheelSkin(
        skinID: Int, skinName: String, skinPrice: Int,
        val wheelRes: Int
    ) : Skin(skinID, skinName, skinPrice)

    class DiceSkin(
        skinID: Int, skinName: String, skinPrice: Int,
        val dice1: Int, val dice2: Int, val dice3: Int,
        val dice4: Int, val dice5: Int, val dice6: Int
    ) : Skin(skinID, skinName, skinPrice)

    class SlotSkin(
        skinID: Int, skinName: String, skinPrice: Int,
        val clover: Int, val club: Int, val heart: Int,
        val diamond: Int, val jack: Int, val seven: Int
    ) : Skin(skinID, skinName, skinPrice)

// create the skins from a list
object masterSkinList {
    val masterSkinList = listOf<Skin>(
    Skin.WheelSkin(0, "Default Wheel", 0,
        R.drawable.pretty_roulette),
    Skin.DiceSkin(1, "Default Dice", 0,
        R.drawable.dice_1, R.drawable.dice_2,R.drawable.dice_3,
        R.drawable.dice_4, R.drawable.dice_5, R.drawable.dice_6),
    Skin.SlotSkin(2, "Default Slots", 0,
        R.drawable.spade, R.drawable.clover, R.drawable.heart,
        R.drawable.diamond, R.drawable.jackpot, R.drawable.seven),
    Skin.DiceSkin(skinID = 3, skinName = "White Dice", skinPrice = 1000,
        dice1 = R.drawable.white_dice_1, dice2 = R.drawable.white_dice_2,
        dice3 = R.drawable.white_dice_3, dice4 = R.drawable.white_dice_4,
        dice5 = R.drawable.white_dice_5, dice6 = R.drawable.white_dice_6),
    Skin.DiceSkin(skinID = 4, skinName = "Neon Dice", skinPrice = 10000,
        dice1 = R.drawable.neon_dice_1, dice2 = R.drawable.neon_dice_2,
        dice3 = R.drawable.neon_dice_3, dice4 = R.drawable.neon_dice_4,
        dice5 = R.drawable.neon_dice_5, dice6 = R.drawable.neon_dice_6),
    Skin.SlotSkin(5, "Neon Slots", 10000,
        R.drawable.neon_spade, R.drawable.neon_club, R.drawable.neon_heart,
        R.drawable.neon_diamond, R.drawable.neon_jackpot, R.drawable.neon_seven),
    Skin.WheelSkin(6, "Pretty Wheel", 10000,
        R.drawable.pretty_roulette)
)}
}