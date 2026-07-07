package com.example.doughbag

//sort of like nested classes when they have differing properties
sealed class Skin(val skinID: Int, val skinName: String, val skinPrice: Int) {
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
    companion object {
        fun createFromList(id: Int, name: String, price: Int, skinList: List<Int>): Skin {
            return when (skinList[0]) {
                0 -> WheelSkin(id, name, price, skinList[1])
                1 -> DiceSkin(
                    id, name, price,
                    skinList[1], skinList[2], skinList[3],
                    skinList[4], skinList[5], skinList[6]
                )
                else -> SlotSkin(
                    id, name, price,
                    skinList[1], skinList[2], skinList[3],
                    skinList[4], skinList[5], skinList[6]
                )
            }
        }
    }
}