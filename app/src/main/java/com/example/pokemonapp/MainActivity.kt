package com.example.pokemonapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokemonapp.utils.Constants
import com.example.pokemonapp.utils.Utils
import com.skydoves.landscapist.glide.GlideImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: PokemonViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val state = viewModel.state
            val context = LocalContext.current
            val internet by remember { mutableStateOf(Utils.isInternetAvailable(context)) }
            if (internet) {
                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(all = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(count = if (state.endReached) Constants.UI_LIST_MAX_SIZE else state.items.size,
                        itemContent = { i ->
                            val item = state.items[i % Constants.PAGINATION_MAX_LIMIT]
                            if (i >= state.items.size - 1 && !state.endReached && !state.isLoading) {
                                viewModel.loadNextItems()
                            }
//                        Log.e("Inside LazyList: ", "Recomposed")
                            Card(elevation = CardDefaults.cardElevation(8.dp)) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    GlideImage(
                                        imageModel = item.url,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(100.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.name,
                                        fontSize = 20.sp,
                                        color = Color.Black,
                                    )
                                }
                            }
                        }
                    )
                    item {
                        if (state.isLoading) {
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    state.error?.let { Text(text = it, color = Color.Red, fontSize = 20.sp) }
                }
            } else {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                    Text(
                        color = Color.Red,
                        fontSize = 20.sp,
                        text = "Internet Not Available"
                    )
                }
            }
        }
    }

//    @Preview(showBackground = true)
//    @Composable
//    fun DefaultPreview() {
//        PokemonAppTheme {
//            Greeting("Android")
//        }
//    }
}