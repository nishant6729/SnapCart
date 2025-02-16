package com.example.snapcart

import android.graphics.DashPathEffect
import android.graphics.Paint
import android.widget.Button
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckOut(navController: NavController){
    var cart by remember{ mutableStateOf<List<Carted>>(emptyList()) }
    var Subcategories by remember { mutableStateOf<List<SubCategory>>(emptyList()) }
    var totalprice by remember{ mutableStateOf(0) }
    var grandtotal by remember{ mutableStateOf(0) }
    var currentlocation by remember {
        mutableStateOf("")
    }
    var context= LocalContext.current
    var LocationViewModel :LocationViewModel= viewModel()
    LaunchedEffect(Unit){
        cart=fetchCartFromFirebase()
        Subcategories= fetchSubCategories()
        currentlocation= LocationViewModel.address.toString()

    }
    totalprice = cart.sumOf { it.item.price.toInt() * it.quantity }
    if(totalprice>=59){
        grandtotal=totalprice+2
    }
    else{
        grandtotal=totalprice+2+25
    }
    Scaffold(
        topBar = {
            TopAppBar(title = { Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically){
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24), contentDescription =null )
                        }
                        Text(text = "Checkout", fontSize = 18.sp,color= Color.DarkGray, fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier=Modifier.padding(16.dp)) {
                        Icon(painter = painterResource(id = R.drawable.outline_shopping_cart_24), contentDescription =null, tint = Color(50,134,22,255), modifier = Modifier.size(20.dp) )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Share",color=Color(50,134,22,255), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
            }})
        }, bottomBar = {
            Row(modifier= Modifier
                .fillMaxWidth()
                .background(Color.White)){
                Button(onClick = { navController.navigate("AddressPage") },modifier= Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(55.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(Color(50,134,22,255))) {
                    Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Choose address at next step",color=Color.White, textAlign = TextAlign.Center,fontSize = 17.sp, fontWeight = FontWeight.Light)
                    }
                }
            }
        }
    ) {
        LazyColumn(modifier= Modifier
            .fillMaxSize()
            .padding(it)
            .background(Color(239, 239, 241, 255))){
            item{
                if (cart.isEmpty() && Subcategories.isNotEmpty()) {
                    Card(colors = CardDefaults.cardColors(Color.White), shape = RoundedCornerShape(8.dp),modifier=Modifier.padding(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                        Row(modifier=Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                            Text(text = "Your Cart is Empty ", fontWeight = FontWeight.ExtraBold,modifier=Modifier.padding(16.dp))
                            Text(text = "Purchase", fontWeight = FontWeight.Bold, color =Color(50,134,22,255),modifier=Modifier.clickable {
                                navController.popBackStack()
                            } )
                        }
                        

                        }
                    }
                else if (cart.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                else{
                    Cartlist(cart,onCartUpdate = { updatedCart ->
                        cart = updatedCart
                    })
                }

            }

            item{
                FancyCard(cart)
            }
            item{
                CouponCard()
            }
            item{
                BillingCard(totalprice,grandtotal)

            }
            item{
                TermsCard()
            }
        }
    }

}

@Composable
fun TermsCard() {
    Card(modifier= Modifier
        .wrapContentSize()
        .padding(16.dp)
        , colors = CardDefaults.cardColors(Color.White), shape = RoundedCornerShape(8.dp),elevation = CardDefaults.cardElevation(4.dp)
    ){
        Column(modifier= Modifier
            .wrapContentSize()
            .padding(10.dp)){
            Text(text = "Cancellation Policy", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Orders cannot be cancelled once packed for delivery.In case of unexpected delays,a refund will be provided,if applicable.", fontSize = 12.sp, fontWeight = FontWeight.Light)
        }
    }
}

@Composable
fun BillingCard(total:Int,grandtotal:Int) {
    Card(modifier= Modifier
        .wrapContentSize()
        .padding(16.dp)
        , colors = CardDefaults.cardColors(Color.White), shape = RoundedCornerShape(8.dp),elevation = CardDefaults.cardElevation(4.dp)
    ){
        Column(modifier= Modifier
            .wrapContentHeight()
            .padding(8.dp)) {
            Text(text = "Bill details", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Row(modifier=Modifier.wrapContentWidth()){
                    Icon(painter = painterResource(id = R.drawable.receipt), contentDescription =null,modifier=Modifier.size(20.dp) )
                    Spacer(modifier=Modifier.width(2.dp))
                    Text(text = "Items total",fontSize = 14.sp, fontWeight = FontWeight.Normal)
                }
                Text(text = total.toString(), fontWeight = FontWeight.Normal, fontSize = 14.sp)

            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Row(modifier=Modifier.wrapContentWidth()){
                    Icon(painter = painterResource(id = R.drawable.bag), contentDescription =null,modifier=Modifier.size(20.dp) )
                    Spacer(modifier=Modifier.width(2.dp))
                    Text(
                        text = "Handling charges",
                        modifier = Modifier.drawBehind {
                            val paint = Paint().apply {
                                color = android.graphics.Color.BLACK
                                pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f) // Adjust dot size and spacing
                                strokeWidth = 3f // Thickness of the underline
                                style = android.graphics.Paint.Style.STROKE
                            }
                            drawContext.canvas.nativeCanvas.drawLine(
                                0f,
                                size.height,
                                size.width,
                                size.height,
                                paint
                            )
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                Text(text = "₹2", fontWeight = FontWeight.Normal, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Row(modifier=Modifier.wrapContentWidth()){
                    Icon(painter = painterResource(id = R.drawable.fastdelivery), contentDescription =null,modifier=Modifier.size(20.dp) )
                    Spacer(modifier=Modifier.width(2.dp))
                    Text(
                        text = "Delivery charges",
                        modifier = Modifier.drawBehind {
                            val paint = Paint().apply {
                                color = android.graphics.Color.BLACK
                                pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f) // Adjust dot size and spacing
                                strokeWidth = 3f // Thickness of the underline
                                style = android.graphics.Paint.Style.STROKE
                            }
                            drawContext.canvas.nativeCanvas.drawLine(
                                0f,
                                size.height,
                                size.width,
                                size.height,
                                paint
                            )
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                Text(text = if(total>=59){
                                  "FREE"
                                         }
                    else{
                        "₹25"
                        }, fontWeight = FontWeight.Normal, fontSize = 14.sp, color = if(total>=59){
                            Color(9, 111, 214, 255)
                        }else{
                            Color.Black
                        })
            }
            if (total<59){
                Text(text = "Shop for ₹59 or more to get FREE delivery", fontSize = 14.sp, fontWeight = FontWeight.Normal, color = Color(
                    220,
                    134,
                    6,
                    255
                )
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                Text(text = "Grand total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "₹"+grandtotal.toString(), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)

            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CouponCard() {
    Card(modifier= Modifier
        .wrapContentSize()
        .padding(16.dp)
        .clickable {
            //Click Event 
        }, colors = CardDefaults.cardColors(Color.White), shape = RoundedCornerShape(8.dp),elevation = CardDefaults.cardElevation(4.dp)
    ){
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(end = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
            Row(modifier= Modifier
                .wrapContentWidth()
                .padding(16.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                Image(painter = painterResource(id = R.drawable.discount), contentDescription =null,modifier=Modifier.size(30.dp) )
                Spacer(modifier=Modifier.width(8.dp))
                Text("Use Coupons", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }
            Icon(painter = painterResource(id = R.drawable.baseline_arrow_forward_ios_24), contentDescription =null ,modifier=Modifier.padding(end=4.dp))

        }
    }
}

@Composable
fun Cartlist(cart:List<Carted>,onCartUpdate:(List<Carted>)->Unit){
        
        Card(colors = CardDefaults.cardColors(Color.White), shape = RoundedCornerShape(8.dp),modifier=Modifier.padding(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
            Text(text = "Delivery in 8 minutes", fontWeight = FontWeight.ExtraBold,modifier=Modifier.padding(16.dp))
            cart.forEach(){carted->
                Column(modifier= Modifier
                    .wrapContentSize()
                    .padding(8.dp)) {
                    Spacer(modifier=Modifier.height(16.dp))
                    ItemCardHorizontal(carted, cart = cart,onCartUpdate)
                }
            }
        }


}

@Composable
fun ItemCardHorizontal(carted: Carted,cart:List<Carted>,onCartUpdate:(List<Carted>)->Unit) {
    Row(modifier= Modifier
        .fillMaxWidth()
        .padding(4.dp)) {
        Box(modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(233, 233, 243, 255))
            .size(80.dp)){
            Image(painter = rememberImagePainter(data =carted.item.image), contentDescription =null, alignment = Alignment.Center,modifier= Modifier
                .size(60.dp)
                .align(Alignment.Center))
        }
        Column(modifier= Modifier
            .padding(8.dp)
            .width(120.dp)) {
            Text(text = carted.item.desc, fontWeight = FontWeight.SemiBold)
            Spacer(modifier=Modifier.height(4.dp))
            Text(text = carted.item.weight, fontWeight = FontWeight.Light)
            Spacer(modifier=Modifier.height(0.dp))
            Text(text = "Save for later", fontWeight = FontWeight.Light)
        }
        Spacer(modifier=Modifier.width(16.dp))
        Column(modifier=Modifier.fillMaxHeight()){
            CustomAddButtonNewt(
                item = carted.item,
                cart = cart,
                onCartUpdate = onCartUpdate,
                carted = carted
            )
//            Text(carted.quantity.toString(), fontWeight = FontWeight.Bold)
            Text(text = "₹ "+carted.item.price, fontWeight = FontWeight.Bold, textAlign = TextAlign.Right,modifier=Modifier.offset(50.dp))
        }
    }
}
@Composable
fun CustomAddButtonNewt(
    item: Items,
    cart: List<Carted>,
    onCartUpdate: (List<Carted>) -> Unit,
    carted: Carted
) {
    var qnty by remember(carted) { mutableStateOf(carted.quantity) }

    Row(
        modifier = Modifier
            .width(120.dp)
            .height(65.dp)
            .padding(12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(70, 167, 31, 255)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            if (qnty > 0) {
                updateQuantity(
                    cart,
                    carted,
                    qnty - 1,
                    onCartUpdate = { updatedCart ->
                        qnty = qnty - 1
                        onCartUpdate(updatedCart)
                    }
                )
            }
        },modifier=Modifier.offset(-4.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_remove_24),
                contentDescription = null,
                tint = Color.White
            )
        }

        Text(
            text = qnty.toString(),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 19.sp,modifier=Modifier.offset(-6.dp)
        )

        IconButton(onClick = {
            updateQuantity(
                cart,
                carted,
                qnty + 1,
                onCartUpdate = { updatedCart ->
                    qnty = qnty + 1
                    onCartUpdate(updatedCart)
                }
            )
        }) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_add_24),
                contentDescription = null,
                tint = Color.White
            )
        }
        Spacer(modifier=Modifier.width(3.dp))
    }
}
private fun updateQuantity(
    cart: List<Carted>,
    carted: Carted,
    newQuantity: Int,
    onCartUpdate: (List<Carted>) -> Unit
) {
    val updatedCart = cart.map {
        if (it.item.desc == carted.item.desc) it.copy(quantity = newQuantity) else it
    }.filter { it.quantity > 0 }

    updateCartedInFirebase(carted.copy(quantity = newQuantity), onSuccess = {
        onCartUpdate(updatedCart)
    }, onFailure = { e ->
        println("Error updating Firebase: ${e.message}")
    })
}
@Composable
fun FancyCard(cart:List<Carted>) {
    Card(
        shape = RoundedCornerShape(8.dp),modifier= Modifier
            .wrapContentSize()
            .padding(16.dp), colors = CardDefaults.cardColors(
            Color.Transparent), border = BorderStroke(2.dp,Color.White)
    )
     {
        Image(painter = painterResource(id = R.drawable.fancycard), contentDescription = null,modifier= Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)))
    }
}