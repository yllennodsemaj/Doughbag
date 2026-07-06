package com.example.doughbagwork

import android.os.Bundle
import android.util.Log.i
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.doughbag.R





// Generic Scrollable View
@Composable
fun ScrollPage(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val scrollState = rememberScrollState()

    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = modifier
            .padding(horizontal = 30.dp)
            .verticalScroll(scrollState)) {
            content()
        }
    }
}

// Displays a list of items purchasable from shops
@Composable
fun ShopList(modifier: Modifier = Modifier, navController: NavController) {
    ScrollPage(modifier) {
        ExitButton(navController = navController)
        for ((i, item) in ShopResources.shopItems.withIndex()) {
            ShopEntry(navController = navController, item=item, itemId = i)
        }
    }
}

// Displays a button that is used for the shop menu
@Composable
fun ShopButton(modifier: Modifier = Modifier, buttonLabel: String, onClick: () -> Unit) {
    Button(onClick = onClick,
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        ),
        modifier = Modifier
            .padding(vertical = 15.dp)
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Text(text=buttonLabel, fontSize = 10.em)
    }
}

@Composable
fun ExitButton(modifier: Modifier = Modifier, navController: NavController) {
    ShopButton(modifier, "Exit") {
        navController.popBackStack()
    }
}

// Makes an entry for the shop list view
@Composable
fun ShopEntry(modifier: Modifier = Modifier, navController: NavController, item: ShopItemData, itemId: Int) {
    val itemName: String = stringResource(item.nameRes)

    ShopButton(modifier = modifier, buttonLabel = itemName, onClick = {
        println("purchasePage/$itemName")
        navController.navigate("purchasePage/$itemId")
    })
}


// Shows information about an item before purchasing
// TODO: Add purchase method to button
@Composable
fun PurchasePage(modifier: Modifier = Modifier, itemId: Int, navController: NavController) {
    val itemData: ShopItemData = ShopResources.shopItems[itemId]

    val itemName = stringResource(itemData.nameRes)
    val itemDescription = stringResource(itemData.descriptionRes)
    val image = painterResource(itemData.imageRes)


    ScrollPage(modifier) {
        Text(itemName, fontSize = 13.em, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Image(image, contentDescription = "Test")

        Text(itemDescription, fontSize = 5.em)

        ShopButton(buttonLabel = "Purchase", onClick = {})
        ExitButton(navController = navController)
        //ShopButton(buttonLabel = "Exit", onClick = {navController.navigate("shop")})
    }
}

// TODO: Add more strings to the shopItemStrings.xml file, then reference them here
object ShopResources {
    public val shopItems: List<ShopItemData> = listOf(
        ShopItemData(R.drawable.clover, R.string.item1_name, R.string.item1_description),
        ShopItemData(R.drawable.heart, R.string.item2_name, R.string.item2_description)
    )
}


class ShopItemData {
    @StringRes
    var nameRes: Int = 0
    @StringRes
    var descriptionRes: Int = 0
    @DrawableRes
    var imageRes: Int = 0


    constructor(imageId: Int, nameId: Int, descriptionId: Int) {
        imageRes = imageId
        nameRes = nameId
        descriptionRes = descriptionId
    }
}