package com.example.goldparfumadmin.presentation.home.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goldparfumadmin.data.utils.getWidthPercent
import com.example.goldparfumadmin.presentation.theme.Gold

@Composable
fun ActionCard(onClick : () -> Unit, title : String, imageVector : ImageVector, contentColor : Color) {

    val wp = getWidthPercent(context = LocalContext.current)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(width = 2.dp, color = Gold),

        modifier = Modifier
            .width(wp * 30)
            .height(100.dp)
            .clip(RoundedCornerShape(30.dp))
            //.border(border = BorderStroke(width = 2.dp, color = Gold), shape = RoundedCornerShape(30.dp))
            .clickable { onClick() },
        ) {

        Column(
            modifier = Modifier.fillMaxSize().padding(3.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier.padding(5.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = title, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }

}