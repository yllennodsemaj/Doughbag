package com.example.doughbag

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doughbag.ui.theme.DoughbagTheme
import java.security.SecureRandom
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoughbagTheme {
                AppRender()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppRender() {
    App()
}

@Composable
fun App() {
    DoughbagNavigation()
}
@Composable
fun DoughbagNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)

    val userViewModel: DoughbagViewModel = viewModel(
        factory = DoughbagViewModelFactory(
            userDao = database.userDao(),
            skinDao = database.skinDao()
        )
    )
    val skinViewModel: SkinViewModel = viewModel(
        factory = SkinViewModel.SkinViewModelFactory(database.skinDao())
    )

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("chooseUser") { ChooseUser(navController, userViewModel) }
        composable("createUser") { CreateUser(navController, userViewModel) }
        composable("deleteUser") { DeleteUser(navController, userViewModel) }
        composable("play") { Play(navController) }
        composable("roulette") { Roulette(navController) }
        composable("craps") { Craps(navController, userViewModel) }
        composable("slots") { Slots(navController, userViewModel) }
        composable("shop") { Shop(navController,userViewModel, skinViewModel) }
        composable("inventory") { Inventory(navController, userViewModel, skinViewModel) }
        composable("stats") { Statistics(navController, userViewModel) }
    }
}
@Composable
fun ForceLandscapeOrientation() {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val activity = context as? Activity
        val originalOrientation = activity?.requestedOrientation
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = originalOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}

@Composable //home page; viewed on load
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Next stop: rock bottom", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.navigate("chooseUser") }) {
            Text("Play")
        }
    }
}

@Composable // create user page to create a new user
fun CreateUser(navController: NavController, viewModel: DoughbagViewModel) {
    var userNameInput by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = userNameInput,
            onValueChange = { userNameInput = it },
            label = { Text("Username") },
            placeholder = { Text("Enter a name") },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (userNameInput.isNotBlank()) {
                viewModel.createNewUser(userNameInput) // save to database
                navController.navigate("chooseUser")
            }
        }) {
            Text("Create!")
        }
    }
}

@Composable
fun DeleteUser(navController: NavController, viewModel: DoughbagViewModel){
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Button(onClick = {
            val getUser = viewModel.activeUser
            if (getUser != null) {
                viewModel.deleteUser(getUser) // delete the active user
                navController.navigate("chooseUser")
            }
        }) {
            Text("Delete")
        }
        Button(onClick = { navController.navigate("chooseUser") }) { Text("Cancel") }
    }
}

@Composable // page with users listed and option to create a new user
fun ChooseUser(navController: NavController, viewModel: DoughbagViewModel) {
    // listens for new users
    val userList by viewModel.userList.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Select a Player", fontSize = 28.sp, modifier = Modifier.padding(bottom = 24.dp))

        // Loop through users and create a button for each one
        userList.forEach { user ->
            Button(
                onClick = {
                    viewModel.setActiveUser(user) // from here on this user is "active"
                    navController.navigate("play")
                },
                modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(0.6f)
            ) {
                Text("${user.user_name} - $${user.balance}")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { navController.navigate("createUser") }) {
            Text("Create New User")
        }
        if(viewModel.activeUser != null) {
            Button(onClick = { navController.navigate("deleteUser") }) {
                Text("Delete User")
            }
        }
    }
}

@Composable // after choosing a user, select a game to play, or shop, inventory, or go back home
fun Play(navController: NavController) {
    ForceLandscapeOrientation()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Roulette button
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { navController.navigate("roulette") },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.roulette_icon),
                    contentDescription = "Play roulette",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Roulette", fontSize = 16.sp)
            }
                // Craps button
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { navController.navigate("craps") },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.craps_icon),
                        contentDescription = "Play craps",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Craps", fontSize = 16.sp)
                }
                    // Slots button
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { navController.navigate("slots") },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.slots_icon),
                            contentDescription = "Play slots",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Slots", fontSize = 16.sp)
                    }
                }



        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { navController.navigate("stats") }) { Text("View Stats") }
            Button(onClick = { navController.navigate("inventory") }) { Text("Inventory") }
            Button(onClick = { navController.navigate("shop") }) { Text("Shop") }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { navController.navigate("chooseUser") }) { Text("Back to Users") }
            Button(onClick = { navController.navigate("deleteUser") }) { Text("Delete User") }
        }
    }
}


@Composable
fun Roulette(navController: NavController){
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Button(onClick = { navController.navigate("play") }) { Text("Exit game") }
        Button(onClick = { println("You spun the roulette wheel") }) { Text("Spin Wheel") }
    }
}

object CasinoRng {
    private val secureRandom = SecureRandom()
    fun nextInt(min: Int, max: Int): Int {
        require(max >= min) { "Max must be greater than or equal to min" }
        val range = max - min
        return secureRandom.nextInt(range) + min
    }
}

class SlotCard(id: Int, res: Int) {
    val myId = id
    val myRes = res
}

fun Bet(bet: Int, viewModel: DoughbagViewModel): Boolean {
    val user: User? = viewModel.activeUser
    var balance:Double? = 0.0
    if(user != null) {
        balance = user.balance
        if(bet <= balance && bet >= 10) {
            viewModel.updateBalance(user.user_ID, ( -1.0 * bet))
            return true
        }
    }
    return false
}

@Composable
fun Slots(navController: NavController, viewModel: DoughbagViewModel) {
    ForceLandscapeOrientation()
    val context = LocalContext.current

    var balance by remember {
        mutableStateOf(viewModel.activeUser?.balance ?: 0.0)
    } // praise AI
    val hearts = SlotCard(1, R.drawable.heart)
    val diamonds = SlotCard(2, R.drawable.diamond)
    val clubs = SlotCard(3, R.drawable.clover)
    val spades = SlotCard(4, R.drawable.spade)
    val seven = SlotCard(5, R.drawable.seven)
    val jackpot = SlotCard(6, R.drawable.jackpot)

    val slots = listOf<SlotCard>(
        hearts, hearts, hearts, hearts, hearts, hearts,
        diamonds, diamonds, diamonds, diamonds, diamonds,
        clubs, clubs, clubs, clubs, spades, spades, spades,
        seven, seven, jackpot
    )

    var slot1 : SlotCard by remember { mutableStateOf(SlotCard(1, R.drawable.clover)) }
    var slot2 : SlotCard by remember { mutableStateOf(SlotCard(1, R.drawable.heart)) }
    var slot3 : SlotCard by remember { mutableStateOf(SlotCard(1, R.drawable.spade)) }

    var myBet by remember { mutableStateOf(10) }

    Box(
        modifier = Modifier
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Balance: ",
                color = Color.Gray,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$balance $",
                color = Color(0xFF2E7D32),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

        OutlinedTextField(
            value = myBet.toString(),
            onValueChange = { myBet = it.toIntOrNull() ?: 0 },
            label = { Text("Bet") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier
                .padding(5.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically)
        {
            Image(
                painter = painterResource(slot1.myRes),
                contentDescription = "slot 1",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Image(
                painter = painterResource(slot2.myRes),
                contentDescription = "slot 2",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Image(
                painter = painterResource(slot3.myRes),
                contentDescription = "slot 3",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = {
                if(!Bet(myBet, viewModel))
                {
                    // change from toast to in game text?
                    Toast.makeText(context, "Can't bet this", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val user: User? = viewModel.activeUser
                if(user != null) {
                    // update balance text to subtract current bet
                    balance = balance.minus(myBet)
                }
                slot1 = slots[CasinoRng.nextInt(0, slots.size)]
                slot2 = slots[CasinoRng.nextInt(0, slots.size)]
                slot3 = slots[CasinoRng.nextInt(0, slots.size)]

                if(slot1.myId == slot2.myId && slot2.myId == slot3.myId)
                {
                    // update db Balance for win
                    val win: Double = (myBet * slot1.myId).toDouble()
                    if(user != null) {
                        viewModel.updateBalance(user.user_ID, win)
                    }
                    // update to in game text
                    Toast.makeText(context, "You won $${myBet * slot1.myId}", Toast.LENGTH_SHORT).show()
                }
            })
            {
                Text(text = stringResource(R.string.spinBtnTxt))
            }
        }
        Button(onClick = { navController.navigate("play") }) { Text("Exit game") }
    }

}

@Composable
fun Craps(navController: NavController, viewModel: DoughbagViewModel){
    ForceLandscapeOrientation()
    val context = LocalContext.current

    var balance by remember {
        mutableStateOf(viewModel.activeUser?.balance ?: 0.0)
    }

    var diceResult1 by remember  { mutableStateOf(1) }
    var diceResult2 by remember  { mutableStateOf(2) }

    val imageResource1 = when (diceResult1) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    val imageResource2 = when (diceResult2) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    var myBet by remember { mutableStateOf(10) }

    Box(
        modifier = Modifier
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Balance: ",
                color = Color.Gray,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$balance $",
                color = Color(0xFF2E7D32),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

        OutlinedTextField(
            value = myBet.toString(),
            onValueChange = { myBet = it.toIntOrNull() ?: 0 },
            label = { Text("Bet") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier
                .padding(5.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically)
        {
            Image(
                painter = painterResource(imageResource1),
                contentDescription = "dice 1",
                modifier = Modifier.size(100.dp)
            )

            Image(
                painter = painterResource(imageResource2),
                contentDescription = "dice 2",
                modifier = Modifier.size(100.dp)
            )

            var isFirstClick by remember { mutableStateOf(true) }
            var pointValue by remember { mutableStateOf(0) }

            Button(onClick = {
                diceResult1 = CasinoRng.nextInt(1, 6)
                println(diceResult1)
                diceResult2 = CasinoRng.nextInt(1, 6)
                println(diceResult2)

                val sum = diceResult1 + diceResult2

                if (isFirstClick) {
                    if (!Bet(myBet, viewModel)) {
                        Toast.makeText(context, "Can't bet this", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val user: User? = viewModel.activeUser
                    if(user != null) {
                        // update balance text to subtract current bet
                        balance = balance.minus(myBet)
                    }

                    when (sum) {
                        7, 11 -> {
                            balance += myBet * 2
                            Toast.makeText(context, "You won $${myBet * 2}", Toast.LENGTH_SHORT)
                                .show()
                            isFirstClick = true
                            return@Button
                        }

                        2, 3, 12 -> {
                            Toast.makeText(context, "You lost", Toast.LENGTH_SHORT).show()
                            isFirstClick = true
                            return@Button
                        }

                        else -> {
                            pointValue = sum
                            Toast.makeText(context, "Keep rolling", Toast.LENGTH_SHORT).show()
                            isFirstClick = false
                        }
                    }
                } else {
                    when (sum) {
                        pointValue -> {
                            balance += myBet * 2
                            Toast.makeText(context, "You won $${myBet * 2}", Toast.LENGTH_SHORT)
                                .show()
                            isFirstClick = true
                            return@Button
                        }

                        7 -> {
                            Toast.makeText(context, "You lost", Toast.LENGTH_SHORT).show()
                            isFirstClick = true
                            return@Button
                        }

                        else -> {
                            Toast.makeText(context, "Keep rolling", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            })
            {
                Text(text = stringResource(R.string.rollBtnTxt))
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Button(onClick = { navController.navigate("play") }) { Text("Exit game") }
        Button(onClick = { println("You rolled the dice") }) { Text("Roll Dice") }
    }
}

    @Composable
    fun Statistics(navController: NavController, viewModel: DoughbagViewModel) {
        val getUser = viewModel.activeUser
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (getUser != null) {
                Text("User statistics for: ${getUser.user_name}")
                Spacer(modifier = Modifier.height(16.dp))
                Text("Total Wins: ${getUser.total_wins}")
                Text("Slot Wins: ${getUser.sWins}")
                Text("Craps Wins: ${getUser.cWins}")
                Text("Roulette Wins: ${getUser.rWins}")
                Text("Total Losses: ${getUser.total_losses}")
                Text("Slot Losses: ${getUser.sLosses}")
                Text("Craps Losses: ${getUser.cLosses}")
                Text("Roulette Losses: ${getUser.rLosses}")
                Text("Total winnings: \$${getUser.winnings}")
                Text("Total losses: \$${getUser.losses}")
            } else {
                Text("No active user!")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("play") }) { Text("Go back") }
        }
    }

    @Composable
    fun Shop(
        navController: NavController,
        userViewModel: DoughbagViewModel,
        skinViewModel: SkinViewModel
    ) {
        val currentUser = userViewModel.activeUser
        LaunchedEffect(currentUser) {
            currentUser?.let { skinViewModel.loadSkinsForUser(it.user_ID) }
        }
        // Observe ONLY the shop skins
        val shopSkins by skinViewModel.shopSkins.collectAsState()
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Skin Shop", fontSize = 24.sp)
            LazyColumn {
                items(shopSkins) { skin ->
                    Button(onClick = {
                        // Trigger the transaction we talked about earlier!
                        println("Triggering purchase logic for ${skin.skinName}")
                    }) {
                        Text("Buy ${skin.skinName} for $${skin.skinPrice}")
                    }
                }

            }
            Button(onClick = { navController.navigate("play") }) { Text("Go back") }
        }
    }

    @Composable
    fun Inventory(
        navController: NavController,
        userViewModel: DoughbagViewModel, // Handles user balance, ID, auth state
        skinViewModel: SkinViewModel  // Handles available skins, active skin
    ) {
        val currentUser = userViewModel.activeUser // Assuming you have access to this

        // Tell ViewModel to load this user's data
        LaunchedEffect(currentUser) {
            currentUser?.let { skinViewModel.loadSkinsForUser(it.user_ID) }
        }

        // Observe ONLY the inventory skins
        val mySkins by skinViewModel.inventorySkins.collectAsState()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("My Inventory", fontSize = 24.sp)

            LazyColumn {
                items(mySkins) { skin ->
                    Button(onClick = {
                        skinViewModel.setActiveSkin(skin)
                        navController.navigate("play")
                    }) {
                        Text("Equip ${skin.skinName}")
                    }
                }
            }
        }
    }
