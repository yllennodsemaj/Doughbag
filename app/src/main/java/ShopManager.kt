import androidx.room.withTransaction
import com.example.doughbag.AppDatabase
import com.example.doughbag.Owned_Skins
import com.example.doughbag.Skin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShopManager(private val database: AppDatabase) {

    suspend fun buySkinSafely(userId: Int, skin: Skin): Boolean {
        // Switch to a background thread for database operations
        return withContext(Dispatchers.IO) {

            // withTransaction locks the database. If anything inside this block
            // crashes or throws an error, ALL changes are reversed automatically.
            database.withTransaction {
                val userDao = database.userDao()
                val skinDao = database.skinDao()

                // 1. Check current balance
                val currentBalance = userDao.getBalanceSync(userId)

                if (currentBalance >= skin.skinPrice) {
                    // 2. Deduct the dough (pass the price as a negative number)
                    userDao.updateBalance(userId, -skin.skinPrice.toDouble())

                    // 3. Add the skin to the user's inventory
                    val newSkinRecord = Owned_Skins(
                        user_ID = userId,
                        name = skin.skinName
                    )
                    skinDao.insertOwnedSkin(newSkinRecord)

                    // Transaction successful!
                    true
                } else {
                    // Not enough dough, cancel the transaction
                    false
                }
            }
        }
    }
}