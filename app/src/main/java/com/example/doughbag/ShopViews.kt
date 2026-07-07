package com.example.doughbagwork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
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
@Preview(showBackground = true)
@Composable
fun Shop(modifier: Modifier = Modifier) {
    ScrollPage(modifier) {
        for (i in 0..20) {
            ShopEntry(itemName = "Test $i")
        }
    }
}

// Displays a button that is used for the shop menu
@Composable
fun ShopButton(modifier: Modifier = Modifier, buttonLabel: String, onClick: () -> Unit) {
    Button(onClick = {},
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

// Makes an entry for the shop list view
// TODO: Implement onClick to open a PurchasePage view with the proper item
@Composable
fun ShopEntry(modifier: Modifier = Modifier, itemName: String) {
    ShopButton(modifier = modifier, buttonLabel = itemName, onClick = {})
}


// Shows information about an item before purchasing
// TODO: Add purchase method to button
// TODO: Load the currently viewed item's information
// TODO: Replace the goat image with the item's image
@Composable
fun PurchasePage(modifier: Modifier = Modifier, itemName: String) {
    //val image = painterResource(R.drawable.goat)
    val image = painterResource(R.drawable.clover)

    ScrollPage(modifier) {
        Text(itemName, fontSize = 13.em, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Image(image, contentDescription = "Test")

        Text("Description", fontSize = 5.em)

        ShopButton(buttonLabel = "Purchase", onClick = {})
    }
}

// Preview for the purchase page
@Preview(showBackground = true, name = "test")
@Composable
fun PurchasePagePreview() {
    PurchasePage(itemName = "Test 1")
}