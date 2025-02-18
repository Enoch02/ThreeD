package com.enoch02.threed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.enoch02.threed.ui.theme.ThreeDTheme
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThreeDTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        val engine = rememberEngine()
                        val modelLoader = rememberModelLoader(engine)
                        val environmentLoader = rememberEnvironmentLoader(engine)
                        val view = rememberView(engine)
                        val collisionSystem = rememberCollisionSystem(view)

                        val centerNode = rememberNode(engine)
                        val cameraNode = rememberCameraNode(engine) {
                            position = Position(y = 0.4f, z = 1.3f)
                            lookAt(centerNode)
                            centerNode.addChildNode(this)
                        }

                        Scene(
                            modifier = Modifier.fillMaxSize(),
                            engine = engine,
                            modelLoader = modelLoader,
                            view = view,
                            cameraNode = cameraNode,
                            cameraManipulator = rememberCameraManipulator(
                                orbitHomePosition = cameraNode.worldPosition,
                                targetPosition = centerNode.worldPosition
                            ),
                            childNodes = rememberNodes {
                                add(centerNode)

                                add(
                                    ModelNode(
                                        modelInstance = modelLoader.createModelInstance(
                                            assetFileLocation = "models/floor_material.glb"
                                        )
                                    ).apply {
//                                        setWorldPosition(1f, 1f, 1f)
                                    }
                                )

                                add(
                                    ModelNode(
                                        modelInstance = modelLoader.createModelInstance(
                                            assetFileLocation = "models/male.glb"
                                        ),
                                        scaleToUnits = 0.35f
                                    ).apply {
                                        setWorldPosition(x = -0.15f)
                                        setRotation()
                                    }
                                )

                                add(
                                    ModelNode(
                                        modelInstance = modelLoader.createModelInstance(
                                            assetFileLocation = "models/female.glb"
                                        ),
                                        scaleToUnits = 0.35f
                                    ).apply {
                                        setWorldPosition(x = 0.15f)
                                    }
                                )
                            },
                            collisionSystem = collisionSystem,
                            environment = environmentLoader.createHDREnvironment(
                                assetFileLocation = "environments/sky_2k.hdr"
                            )!!,
                            onGestureListener = rememberOnGestureListener(
                                onDoubleTap = { _, node ->
                                    node?.apply {
                                        scale *= 2.0f
                                    }
                                }
                            ),
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {},
                                content = {
                                    Text("Do Something 1")
                                }
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Button(
                                onClick = {},
                                content = {
                                    Text("Do Something")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

fun ModelNode.setWorldPosition(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
    position = Float3(x, y, z)
}

fun ModelNode.setRotation(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
    rotation = Float3()
}

// Extension function for relative positioning
fun ModelNode.moveBy(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
    position += Float3(x, y, z)
}