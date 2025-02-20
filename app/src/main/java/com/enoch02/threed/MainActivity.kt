package com.enoch02.threed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.enoch02.threed.ui.screen.home.RenderScreen
import com.enoch02.threed.ui.theme.ThreeDTheme
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.ModelNode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThreeDTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RenderScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )

                    /*Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        var info by remember { mutableStateOf("") }
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            text = info
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    info = "${maleModelInstance?.animator?.animationCount}"

                                    val count = maleModelInstance?.animator?.animationCount ?: 0
                                    val names = (0 until count).mapNotNull { index ->
                                        maleModelInstance?.animator?.getAnimationName(index)
                                    }
                                    info += names
                                },
                                content = {
                                    Text("Do Something 1")
                                }
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Button(
                                onClick = {
                                    playAnimation(maleModelInstance, 0)
                                },
                                content = {
                                    Text("Play Animation")
                                }
                            )
                        }
                    }*/
                }
            }
        }
    }
}

/*

// Extension function for relative positioning
fun ModelNode.moveBy(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
    position += Float3(x, y, z)
}

// TODO: the animation does not play
fun playAnimation(modelInstance: ModelInstance?, animationIndex: Int) {
    if (modelInstance != null && animationIndex <= modelInstance.animator.animationCount) {
        modelInstance.animator.applyAnimation(animationIndex, 10f)
    }
}*/
