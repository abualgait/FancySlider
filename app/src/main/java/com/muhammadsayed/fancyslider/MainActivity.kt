package com.muhammadsayed.fancyslider

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.muhammadsayed.fancyslider.ui.theme.FancySliderTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FancySliderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FancySlider()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FancySlider() {

    // ACTUAL OFFSET
    fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

    val pagesTotal = 12
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val distance = screenWidth / 6 / 2
    val indicatorSize = 40
    val pagerState = rememberPagerState(pageCount = { 12 })

    var offsetX by remember { mutableFloatStateOf(0f) }

    var progress by remember {
        mutableFloatStateOf(0f)
    }

    var isDragging by remember {
        mutableStateOf(false)
    }

    val duration = 100

    val colorRed =
        animateColorAsState(
            targetValue = if (isDragging) Color(0xFFEBEBEB)
            else Color(0xFFFD98C6), label = "Red Color Animation",
            animationSpec = tween(durationMillis = duration)
        )

    val colorGreen =
        animateColorAsState(
            targetValue = if (isDragging) Color(0xFFEBEBEB)
            else Color(0xFF59D0C7), label = "Green Color Animation",
            animationSpec = tween(durationMillis = duration)
        )

    val sizeAnimation = animateDpAsState(
        targetValue = if (isDragging) Dp(35f) else Dp(45f),
        label = "Size Animation",
        animationSpec = tween(durationMillis = duration)
    )

    LaunchedEffect(key1 = progress) {
        pagerState.animateScrollToPage((progress * 12).roundToInt() + 1)
    }


    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF1A8DFB))
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "month",
            fontSize = 30.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center, color = Color.White
        )

        Spacer(modifier = Modifier.height(10.dp))


        HorizontalPager(
            pagerState,
            pageSpacing = -(300.dp),
            modifier = Modifier.weight(1f),
            userScrollEnabled = false,
        ) { page ->

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .zIndex(page.toFloat() * 12)
                    .graphicsLayer {
                        val pageOffset = pagerState.offsetForPage(page)

                        translationX = -pageOffset * 400

                        alpha = lerp(
                            start = 0.1f,
                            stop = 1f,
                            fraction = 0.8f - pageOffset.coerceIn(0f, 1f)
                        )

                        scaleX = 1f + pageOffset * 0.9f
                        scaleY = 1f + pageOffset * 0.9f

                    }
            ) {
                Text(
                    text = "${page + 1}",
                    fontSize = 200.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Icon(
                Icons.Default.Clear,
                tint = Color.White,
                contentDescription = null,
                modifier = Modifier
                    .size(sizeAnimation.value)
                    .drawBehind {
                        drawCircle(color = colorRed.value)

                    })

            Icon(
                Icons.Default.Check,
                tint = Color.White,
                contentDescription = null,
                modifier = Modifier
                    .size(sizeAnimation.value)
                    .drawBehind {
                        drawCircle(color = colorGreen.value)
                    })


        }

        Spacer(modifier = Modifier.height(indicatorSize.dp + 20.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            val numberDuration = 150
            repeat(pagesTotal) {

                val isAroundCurrentPage =
                    ((progress * 12f) + 1 in it - 0.75f..it + 0.75f)

                val animation by animateDpAsState(
                    targetValue =
                    if (isAroundCurrentPage)
                        -Dp(indicatorSize.toFloat() / 2) - Dp(3f)
                    else
                        Dp(0f),
                    label = "Offset Animation", animationSpec = tween(numberDuration)
                )

                val scale by animateFloatAsState(
                    targetValue = if (isAroundCurrentPage) 1.5f else 1f,
                    label = "Scale Animation", animationSpec = tween(numberDuration)
                )

                if (it % 2 == 1) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .offset(y = animation)
                            .graphicsLayer {
                                if (isAroundCurrentPage) {
                                    scaleX = scale
                                    scaleY = scale
                                }
                            }
                    ) {
                        Text(
                            text = "${(it + 1)}",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = if (isAroundCurrentPage) Color.White else Color.White.copy(
                                alpha = 0.5f
                            )
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Spacer(
                            modifier = Modifier
                                .width(1.dp)
                                .height(7.dp)
                                .background(
                                    if (isAroundCurrentPage) Color.White else Color.White.copy(
                                        alpha = 0.5f
                                    )
                                )
                        )

                    }
                }
            }
        }


        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .height(150.dp)

        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = -(1.dp))
                    .height((indicatorSize / 2).dp)
                    .background(Color(0xFF1A8DFB))
            )

            //thumb
            Box(modifier = Modifier
                .offset {
                    IntOffset(
                        offsetX.roundToInt() - (distance.roundToPx() / 2) + 5,
                        30
                    )
                }
                .height(indicatorSize.dp / 2)
                .width(distance * 2)
                .drawBehind {
                    val curveWidth = 60
                    val path = Path().apply {

                        val topRight = Offset(size.width, 0f)
                        val bottomLeft = Offset(0f, size.height)
                        val bottomRight = Offset(size.width, size.height)


                        moveTo(bottomLeft.x - curveWidth, bottomLeft.y)


                        // Draw curve
                        val controlPointX1 = (topRight.x) / 2
                        val controlPointY1 = -(topRight.x) / 2

                        val endPointX = topRight.x + curveWidth
                        val endPointY = bottomRight.y

                        cubicTo(
                            controlPointX1,
                            controlPointY1,
                            controlPointX1,
                            controlPointY1,
                            endPointX,
                            endPointY
                        )

                        lineTo(bottomLeft.x, bottomLeft.y)

                        close()
                    }

                    drawPath(
                        path = path,
                        color = Color.White,
                    )

                })

            // indicator circle
            Box(modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .size(indicatorSize.dp)
                .shadow(5.dp, shape = CircleShape)
                .background(
                    shape = CircleShape,
                    color = Color.White
                )
                .graphicsLayer {
                    //set initial offsetX
                    offsetX = distance.toPx() - (indicatorSize / 2).dp.toPx()

                }

                .pointerInput(Unit) {
                    distance.toPx() - (indicatorSize / 2).dp.toPx()
                    detectDragGestures(onDragEnd = {
                        isDragging = false
                    }, onDrag = { change, dragAmount ->
                        isDragging = true
                        change.consume()
                        offsetX = (offsetX + dragAmount.x)
                            .coerceIn(
                                -indicatorSize.dp.toPx() / 2,//distance.toPx() - (indicatorSize / 2).dp.toPx(),
                                screenWidth.toPx() - indicatorSize.dp.toPx() - distance.toPx() + (indicatorSize / 2).dp.toPx()
                            )

                        progress = (offsetX / screenWidth.toPx()).coerceIn(-0.1f, 1f)
                    })
                }
            )
        }


    }

}

@Preview
@Composable
private fun FancySliderPreview() {
    FancySlider()

}