import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goldparfumadmin.ui.theme.Gold


@Composable
fun ValueButton(text : String, clicked : MutableState<Boolean>) {

    Button(
        onClick = { clicked.value = !clicked.value },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        border = BorderStroke(width = 3.dp, color = if (clicked.value) Gold else Color.LightGray),
        contentPadding = ButtonDefaults.ContentPadding,
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
    ) {

        Text(text = text, fontSize = 10.sp, color = Color.Black)
    }
}

@Composable
fun OptionsList(
    oneLine : Boolean = true,
    values : List<String>,
    listOfButtonsStates : List<MutableState<Boolean>>
) {
    var rowsAmount = if (values.size % 3 == 0) values.size / 3 else values.size / 3 + 1
    if (oneLine) rowsAmount = 1
    Column(modifier = Modifier.wrapContentSize()) {
        for (i in 0 until rowsAmount)
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
            ) {
                for (j in 0 until values.size / rowsAmount) {
                    if (i * 3 + j < values.size)
                        ValueButton(
                            text = values[i * 3 + j],
                            clicked = listOfButtonsStates[i * 3 + j]
                        )
                }
            }
    }
}