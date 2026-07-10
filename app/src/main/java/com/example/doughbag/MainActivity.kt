package com.example.doughbag

import android.R.attr.orientation
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ListItemDefaults.contentColor

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
            userDao = database.userDao()
        )
    )

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("chooseUser") { ChooseUser(navController, userViewModel) }
        composable("createUser") { CreateUser(navController, userViewModel) }
        composable("deleteUser") { DeleteUser(navController, userViewModel) }
        composable("play") { Play(navController) }
        composable("roulette") { Roulette(navController, userViewModel) }
        composable("craps") { Craps(navController, userViewModel) }
        composable("slots") { Slots(navController, userViewModel) }
        composable("shop") { Shop(navController, userViewModel) }
        composable("inventory") { Inventory(navController, userViewModel) }
        composable("stats") { Statistics(navController, userViewModel) }
    }
}
// helper function for screen orientation
fun Context.findActivity(): Activity? {
    var currentContext = this // this ia AI generated
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}
@Composable
fun SetScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity()
        activity?.requestedOrientation = orientation

        onDispose {
            // do nothing, let next screen choose its own orientation
        }
    }
}

@Composable //home page; viewed on load
fun HomeScreen(navController: NavController) {
    SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
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
    SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
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
    SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val user = viewModel.activeUser
            if(user != null) {
                Text("Delete ${user.user_name}?")
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = {
                        viewModel.deleteUser(user) // delete the active user
                        navController.navigate("chooseUser")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White)
                ) {
                    Text("Delete")
                }
            }

            else {
                Text("No active user!")
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { navController.navigate("chooseUser") }) { Text("Cancel") }
        }
}


@Composable // page with users listed and option to create a new user
fun ChooseUser(navController: NavController, viewModel: DoughbagViewModel) {
    SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
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
                Text("${user.user_name}  $${user.balance}")
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
    SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
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
class Pocket(id: Int, color: String)
{
    val myId = id
    val myColor = color
}

@Composable
fun Roulette(navController: NavController, viewModel: DoughbagViewModel){
    val view = LocalView.current
    val keyboardController = LocalSoftwareKeyboardController.current
    SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    DisposableEffect(key1 = view) {
        val window = (view.context as? android.app.Activity)?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, view)
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            // Allow user to swipe from edge to bring them back temporarily
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            // Restore bars when leaving this specific game view screen
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.show(WindowInsetsCompat.Type.statusBars() or
                        WindowInsetsCompat.Type.navigationBars())
            }
        }
    }
    val context = LocalContext.current
    val allUsers by viewModel.userList.collectAsState()
    val liveUser = allUsers.find { it.user_ID == viewModel.activeUser?.user_ID }
    var thisGameBalance = liveUser?.balance ?: 0.0 // best way to get live db balance
    var myNumber by remember { mutableStateOf("") } //  Changed to String
    var betNumber by remember { mutableStateOf("") } // to solve stuttering
    var betRed by remember { mutableStateOf("") } //    issues during input
    var betBlack by remember { mutableStateOf("") }
    var betOdd by remember { mutableStateOf("") }
    var betEven by remember { mutableStateOf("") }
    var betHigh by remember { mutableStateOf("") }
    var betLow by remember { mutableStateOf("") }
    var spinResult by remember { mutableStateOf("")}
    var winStatusText by remember { mutableStateOf("")}
    val wheelNumbers = listOf(0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8,
        23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26)
    val wheel = mutableListOf<Pocket>(Pocket(0, "Green"))

    for(i in 1..wheelNumbers.size step 2)
    {
        wheel += Pocket(i, "Red")
        wheel += Pocket(i + 1, "Black")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp) // Gives safe margins on all edges of the screen
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Balance: ",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp)) // Changed from height to width for horizontal alignment
                Text(
                    text = "$$thisGameBalance",
                    color = Color(0xFF2E7D32),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Button(
            onClick = { navController.navigate("play") },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Text("Exit game")
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1.1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("$spinResult")
                Text("$winStatusText")
                Image(
                    painter = painterResource(R.drawable.roulette),
                    contentDescription = "wheel",
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = {
                    // type checks all user inputs and converts strings to number or null
                    val numTarget = myNumber.toIntOrNull() ?: 0
                    val bet = betNumber.toDoubleOrNull() ?: 0.0
                    val redBet = betRed.toDoubleOrNull() ?: 0.0
                    val blackBet = betBlack.toDoubleOrNull() ?: 0.0
                    val oddBet = betOdd.toDoubleOrNull() ?: 0.0
                    val evenBet = betEven.toDoubleOrNull() ?: 0.0
                    val highBet = betHigh.toDoubleOrNull() ?: 0.0
                    val lowBet = betLow.toDoubleOrNull() ?: 0.0
                    val myBet = (bet + redBet + blackBet + highBet + lowBet + oddBet + evenBet) * 1.0
                    spinResult = "Spin to win"
                    winStatusText = ""
                    // bet too low
                    if (myBet < 10.0) {
                        Toast.makeText(context, "Minimum bet is $10!",
                            Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    // bet more than balance
                    if (!bet(myBet, viewModel)) {
                        Toast.makeText(context, "Insufficient funds!",
                            Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    // if there's a number bet and num not between 1 and 36 cant bet
                    if ( bet > 0 && numTarget !in 0..36 ) {
                        Toast.makeText(context, "Invalid roulette number!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    thisGameBalance -= myBet
                        // first subtract total bets from balance
                    val winningPocket = wheel[CasinoRng.nextInt(0, wheel.size)]
                    spinResult = "The ball landed on: ${winningPocket.myId} ${winningPocket.myColor}"
                        // get winning result
                    if(liveUser != null) {
                        val user = liveUser
                        var win = 0.0

                    if (winningPocket.myId == numTarget) { //
                        win += bet * 36
                        viewModel.updateStats(user.user_ID, wins = 1, rWins = 1,
                            winnings = bet * 36)}
                    else if(bet > 0){
                        viewModel.updateStats(user.user_ID, losses = 1, rLosses = 1,
                            lossAmt = bet)
                    }
                    if (winningPocket.myColor == "Red") {
                        win += redBet * 2
                        viewModel.updateStats(user.user_ID, wins = 1, rWins = 1,
                            winnings = redBet * 2)}
                    else if(redBet > 0){
                        viewModel.updateStats(user.user_ID, losses = 1, rLosses = 1,
                            lossAmt = redBet)
                    }
                    if (winningPocket.myColor == "Black") {
                        win += blackBet * 2
                        viewModel.updateStats(user.user_ID, wins = 1, rWins = 1,
                            winnings = blackBet * 2)}
                    else if(blackBet > 0){
                        viewModel.updateStats(user.user_ID, losses = 1, rLosses = 1,
                            lossAmt = blackBet)
                    }
                    // Zero is not even, odd, high, or low in roulette
                    if (winningPocket.myId == 0){
                        winStatusText = "You lost"
                        viewModel.updateStats(user.user_ID, losses = 1, rLosses = 1, lossAmt = myBet)
                    }
                    else {
                        if (winningPocket.myId % 2 == 1) {
                            win += oddBet * 2
                            viewModel.updateStats(
                                user.user_ID, wins = 1, rWins = 1,
                                winnings = oddBet * 2
                            )
                        } else if(oddBet > 0){
                            viewModel.updateStats(
                                user.user_ID, losses = 1, rLosses = 1,
                                lossAmt = oddBet
                            )
                        }
                        if (winningPocket.myId % 2 == 0) {
                            win += evenBet * 2
                            viewModel.updateStats(
                                user.user_ID, wins = 1, rWins = 1,
                                winnings = evenBet * 2
                            )
                        } else if(evenBet > 0){
                            viewModel.updateStats(
                                user.user_ID, losses = 1, rLosses = 1,
                                lossAmt = evenBet
                            )
                        }
                        if (winningPocket.myId in 1..18) {
                            win += lowBet * 2
                            viewModel.updateStats(
                                user.user_ID, wins = 1, rWins = 1,
                                winnings = lowBet * 2
                            )
                        } else if(lowBet > 0){
                            viewModel.updateStats(
                                user.user_ID, losses = 1, rLosses = 1,
                                lossAmt = lowBet
                            )
                        }
                        if (winningPocket.myId in 19..36) {
                            win += highBet * 2
                            viewModel.updateStats(
                                user.user_ID, wins = 1, rWins = 1,
                                winnings = highBet * 2
                            )
                        } else if(highBet > 0){
                            viewModel.updateStats(
                                user.user_ID, losses = 1, rLosses = 1,
                                lossAmt = highBet
                            )
                        }
                        if(win > 0) {
                            winStatusText = "You won $$win!"
                        }
                        else{
                            winStatusText = "You lost."
                        }
                        thisGameBalance += win
                        viewModel.updateBalance(user.user_ID, thisGameBalance)
                    }
                    }

                }) {
                    Text(text = stringResource(R.string.spinBtnTxt))
                }
            }

            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val fieldModifier = Modifier
                    .height(70.dp)
                    .fillMaxWidth()

                val textColors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )

                val textColors2 = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Gray,
                    unfocusedContainerColor = Color.Gray,
                    focusedTextColor = Color.Red,
                    unfocusedTextColor = Color.Red
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f),verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = myNumber,
                            onValueChange = { myNumber = it },
                            label = { Text("My Number", fontSize = 12.sp) },
                            colors = textColors2,
                            modifier = fieldModifier,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                }
                            )

                        )
                        OutlinedTextField(
                            value = betNumber,
                            onValueChange = { betNumber = it },
                            label = { Text("Bet on Number", fontSize = 12.sp) },
                            colors = textColors,
                            modifier = fieldModifier,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                }
                            )
                        )
                        OutlinedTextField(
                            value = betRed,
                            onValueChange = { betRed = it },
                            label = { Text("Bet on Red", fontSize = 12.sp) },
                            colors = textColors,
                            modifier = fieldModifier,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                }
                            )
                        )
                        OutlinedTextField(
                            value = betBlack,
                            onValueChange = { betBlack = it },
                            label = { Text("Bet on Black", fontSize = 12.sp) },
                            colors = textColors,
                            modifier = fieldModifier,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                }
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f),verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = betOdd,
                            onValueChange = { betOdd = it },
                            label = { Text("Bet on Odd", fontSize = 12.sp) },
                            colors = textColors,
                            modifier = fieldModifier,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                }
                            )
                        )
                        OutlinedTextField(
                            value = betEven,
                            onValueChange = { betEven = it },
                            label = { Text("Bet on Even", fontSize = 12.sp) },
                            colors = textColors,
                            modifier = fieldModifier,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                }
                            )
                        )
                        OutlinedTextField(
                            value = betLow,
                            onValueChange = { betLow = it },
                            label = { Text("Bet on 1 to 18", fontSize = 12.sp) },
                            colors = textColors,
                            modifier = fieldModifier,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                }
                            )
                        )
                        OutlinedTextField(
                            value = betHigh,
                            onValueChange = { betHigh = it },
                            label = { Text("Bet on 19 to 36", fontSize = 12.sp) },
                            colors = textColors,
                            modifier = fieldModifier,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                }
                            )
                        )
                    }
                }
            }
        }
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

fun bet(bet: Double, viewModel: DoughbagViewModel): Boolean {
    val user = viewModel.userList.value.find { it.user_ID == viewModel.activeUser?.user_ID }
    // pulling from userList forces most up to date balance
    if(user != null) {
        val balance = user.balance
        return bet in 10.0..balance
    }
    return false
}

@Composable
fun Slots(navController: NavController, viewModel: DoughbagViewModel) {
    SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    val view = LocalView.current
    val keyboardController = LocalSoftwareKeyboardController.current // need this to force num pad for bets
    DisposableEffect(key1 = view) {
        val window = (view.context as? android.app.Activity)?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, view)
            controller.hide(WindowInsetsCompat.Type.statusBars() or
                    WindowInsetsCompat.Type.navigationBars())
            // Allow user to swipe from edge to bring them back temporarily
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            // Restore bars when leaving this specific game view screen
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            }
        }
    }
    val context = LocalContext.current
    val allUsers by viewModel.userList.collectAsState()
    val user = allUsers.find { it.user_ID == viewModel.activeUser?.user_ID }
    var balance = user?.balance ?: 0.0
    if(user == null) {
        println("no active user")
        navController.navigate("chooseUser")
    }

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

    var bet by remember { mutableStateOf("10") }
    var winStatusText by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp) // Gives safe margins on all edges of the screen
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "  Balance: ",
                    color = Color.Gray,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$$balance",
                    color = Color(0xFF2E7D32),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }
        Button( // back to play screen
            onClick = { navController.navigate("play") },
            modifier = Modifier.align(Alignment.TopEnd)
        ) { Text("Exit game") }
        Column(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = winStatusText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                modifier = Modifier.height(30.dp)
            )
            OutlinedTextField(
                value = bet,
                onValueChange = { bet = it },
                label = { Text("Bet") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal, // Allows decimals like 10.50
                    imeAction = ImeAction.Done
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                modifier = Modifier.padding(5.dp),
                // collapse keyboard on done click
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
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
                    winStatusText = ""
                    var myBet = bet.toDoubleOrNull()
                    if(myBet == null) myBet = 0.0
                    if (myBet < 10.0) {
                        Toast.makeText(context, "Minimum bet $10",
                            Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (!bet(myBet, viewModel)) {
                        Toast.makeText(context, "Insufficient funds",
                            Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (user != null) {
                        // update balance text to subtract current bet
                        var newBalance = balance - myBet
                        slot1 = slots[CasinoRng.nextInt(0, slots.size)]
                        slot2 = slots[CasinoRng.nextInt(0, slots.size)]
                        slot3 = slots[CasinoRng.nextInt(0, slots.size)]

                        if (slot1.myId == slot2.myId && slot2.myId == slot3.myId) {
                            // update db balance and win stats
                            val win: Double = (myBet * slot1.myId)
                            newBalance += win // update game balance on win
                            viewModel.updateStats(
                                userId = user.user_ID,
                                wins = 1,
                                sWins = 1,
                                winnings = win
                            )
                            winStatusText = "You won: $win!"

                        } else { // update db loss stats
                            viewModel.updateStats(
                                userId = user.user_ID,
                                lossAmt = myBet,
                                sLosses = 1,
                                losses = 1
                            )
                        }
                        balance = newBalance
                        viewModel.updateBalance(user.user_ID, balance)
                        // update db balance for bet and win ^
                    }

                })
                {
                    Text(text = stringResource(R.string.spinBtnTxt))
                }
            }
        }
    }
}

@Composable
fun Craps(navController: NavController, viewModel: DoughbagViewModel) {
    SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    val view = LocalView.current
    val keyboardController =
        LocalSoftwareKeyboardController.current // need this to force num pad for bets
    DisposableEffect(key1 = view) {
        val window = (view.context as? android.app.Activity)?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, view)
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            // Allow user to swipe from edge to bring them back temporarily
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            // Restore bars when leaving this specific game view screen
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            }
        }
    }
    val context = LocalContext.current
    val allUsers by viewModel.userList.collectAsState()
    val user = allUsers.find { it.user_ID == viewModel.activeUser?.user_ID }
    var balance = user?.balance ?: 0.0
    if(user == null) {
        println("no active user")
        navController.navigate("chooseUser")
    }

    var diceResult1 by remember { mutableStateOf(1) }
    var diceResult2 by remember { mutableStateOf(2) }

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

    var bet by remember { mutableStateOf("10") }
    var winStatusText by remember { mutableStateOf("7 or 11 wins on first roll!") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp) // Gives safe margins on all edges of the screen
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "  Balance: ",
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
        Button(
            onClick = { navController.navigate("play") },
            modifier = Modifier.align(Alignment.TopEnd)
        ) { Text("Exit game") }
    }

        Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = winStatusText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50),
            modifier = Modifier.height(30.dp)
        )

        OutlinedTextField(
            value = bet,
            onValueChange = { bet = it },
            label = { Text("Bet") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier.padding(5.dp),
            // force number pad
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done // bottom right key = done
            ),
            // collapse keyboard on done click
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            )
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
                if (user == null) return@Button
                else {
                    winStatusText = ""
                    diceResult1 = CasinoRng.nextInt(1, 6)
                    diceResult2 = CasinoRng.nextInt(1, 6)
                    val myBet = bet.toDoubleOrNull() ?: 0.0
                    val sum = diceResult1 + diceResult2

                    if (isFirstClick) { // initial roll
                        if (myBet < 10.0) {
                            Toast.makeText(context, "Minimum bet $10",
                                Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (myBet > balance) {
                            Toast.makeText(context, "Insufficient funds",
                                Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (!bet(myBet, viewModel)) {
                            Toast.makeText(
                                context, "Can't bet this",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        balance -= myBet * 1.0
                        viewModel.updateBalance(user.user_ID, balance)
                        when (sum) {
                            7, 11 -> {
                                val win: Double = myBet * 2.0
                                balance += win
                                viewModel.updateBalance(user.user_ID, balance)
                                viewModel.updateStats(
                                    userId = user.user_ID, wins = 1, winnings = win,
                                    cWins = 1
                                )
                                winStatusText = "You won: $win! Play again?"
                                isFirstClick = true
                            }

                            2, 3, 12 -> {
                                winStatusText = "Craps! Play again?"
                                viewModel.updateStats(
                                    userId = user.user_ID, losses = 1, lossAmt = myBet,
                                    cLosses = 1
                                )
                                viewModel.updateBalance(user.user_ID, balance)
                                isFirstClick = true
                            }

                            else -> {
                                pointValue = sum
                                winStatusText = "$sum wins, 7 loses. Roll again!"
                                isFirstClick = false
                            }
                        }
                    } else {
                        when (sum) {
                            pointValue -> {
                                val win = myBet * 2.0
                                balance += win
                                viewModel.updateBalance(user.user_ID, balance)
                                viewModel.updateStats(
                                    userId = user.user_ID, wins = 1,
                                    winnings = win, cWins = 1
                                )
                                winStatusText = "You won $win! Play again?"
                                isFirstClick = true
                            }

                            7 -> {
                                winStatusText = "7 loses. Play again?"
                                viewModel.updateStats(
                                    userId = user.user_ID, losses = 1, cLosses = 1,
                                    lossAmt = myBet
                                )
                                viewModel.updateBalance(user.user_ID, balance)
                                isFirstClick = true
                            }

                            else -> {
                                winStatusText = "Roll again"
                            }
                        }
                    }
                }
            }) {
                Text(text = stringResource(R.string.rollBtnTxt))
            }
        }
    }
}


@Composable
fun Statistics(navController: NavController, viewModel: DoughbagViewModel) {
    SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val allUsers by viewModel.userList.collectAsState() // collect as state pulls most up to date db info
    val user = allUsers.find { it.user_ID == viewModel.activeUser?.user_ID } // pick active user from user list
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (user != null) {
            Text("User statistics for: ${user.user_name}")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Total Wins: ${user.total_wins}")
            Text("Total Losses: ${user.total_losses}")
            Text("Slot Wins: ${user.sWins}")
            Text("Slot Losses: ${user.sLosses}")
            Text("Craps Wins: ${user.cWins}")
            Text("Craps Losses: ${user.cLosses}")
            Text("Roulette Wins: ${user.rWins}")
            Text("Roulette Losses: ${user.rLosses}")
            Text("Total winnings: \$${user.winnings}")
            Text("Total losses: \$${user.losses}")
        } else {
            Text("No active user!")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("play") }) { Text("Go back") }
    }
}

@Composable
fun Shop(navController: NavController, userViewModel: DoughbagViewModel,
    skinViewModel: SkinViewModel) {
    SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val currentUser = userViewModel.activeUser
    LaunchedEffect(currentUser) {
        currentUser?.let { skinViewModel.loadSkinsForUser(it.user_ID) }
    }
    // Observe ONLY the shop skins
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Skin Shop", fontSize = 24.sp)
        LazyColumn {
            items(shopSkins) { skin ->
                Button(onClick = {
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
fun Inventory(navController: NavController, viewModel: DoughbagViewModel) {
    SetScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val user = viewModel.activeUser
    val allSkins: List<Skin>
    val userOwnedIds: List<Int>
    val myOwnedSkins: List<Skin>
    if(user != null) {
        allSkins = Skin.masterSkinList.masterSkinList
        userOwnedIds = viewModel.getOwnedIds(user)
        myOwnedSkins = allSkins.filter { it.skinID in userOwnedIds }
        // filters user owned skins for inventory view
    }
    else {
        navController.navigate("chooseUser")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("My Inventory", fontSize = 24.sp)
        userList.forEach { user ->
            Button(
                onClick = {
                    viewModel.setActiveUser(user) // from here on this user is "active"
                    navController.navigate("play")
                },
                modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(0.6f)
            ) {
                Text("${user.user_name}  $${user.balance}")
            }
        }
        LazyColumn {
            myOwnedSkins.forEach { skin ->
                Button(onClick = {
                    viewModel.setActiveSkin(skin)
                    navController.navigate("play")
                }) {
                    Text("Equip ${skin.skinName}")
                }
            }
        }
    }
}
