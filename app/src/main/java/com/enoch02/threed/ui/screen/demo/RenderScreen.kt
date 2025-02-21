package com.enoch02.threed.ui.screen.demo

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import io.github.sceneview.rememberView

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RenderScreen(
    modifier: Modifier = Modifier,
    viewModel: RenderViewModel = viewModel(),
) {
    val context = LocalContext.current
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

    val childNodes = rememberNodes {
        add(centerNode)
        add(
            ModelNode(
                modelInstance = modelLoader.createModelInstance(
                    assetFileLocation = "models/floor_material.glb"
                )
            )
        )
    }
    val characterNodes = remember { mutableStateListOf<ModelNode>() }
    var animationNames by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val maleNode = ModelNode(
            modelInstance = modelLoader.createModelInstance(
                assetFileLocation = "models/male.glb"
            ),
            scaleToUnits = 0.35f,
            autoAnimate = false
        ).apply {
            setWorldPosition(x = -0.15f)
            setRotation(y = 90f)
            playAnimation(4)
        }

        childNodes.add(maleNode)
        characterNodes.add(maleNode)

        val femaleNode = ModelNode(
            modelInstance = modelLoader.createModelInstance(
                assetFileLocation = "models/female.glb"
            ),
            scaleToUnits = 0.35f
        ).apply {
            setWorldPosition(x = 0.15f)
            setRotation(y = -90f)
            playAnimation(4)
        }

        childNodes.add(femaleNode)
        characterNodes.add(femaleNode)
        viewModel.loadAnimationDurations(maleNode, femaleNode)
        //TODO
        animationNames = viewModel.loadAnimationNames(characterNodes[0], characterNodes[1])
    }

    Column(modifier = modifier) {
        Scene(
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .fillMaxWidth(),
            engine = engine,
            modelLoader = modelLoader,
            view = view,
            cameraNode = cameraNode,
            cameraManipulator = rememberCameraManipulator(
                orbitHomePosition = cameraNode.worldPosition,
                targetPosition = centerNode.worldPosition
            ),
            childNodes = childNodes,
            collisionSystem = collisionSystem,
            environment = environmentLoader.createHDREnvironment(
                assetFileLocation = "environments/sky_2k.hdr"
            )!!,
            /*onGestureListener = rememberOnGestureListener(
                onDoubleTap = { _, node ->
                    node?.apply {
                        // doubles the scale of the node being double tapped
                        scale *= 2.0f
                    }
                }
            ),*/
            onFrame = {

            }
        )

        // TODO: create a toggle that switches the character being controlled
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // For Debugging
            if (0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Animation names: $animationNames"
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    content = { Text("Dance") },
                    onClick = {
                        /*viewModel.playDanceAnimation(characterNodes[0], characterNodes[1])*/
                        val maleModel = characterNodes[0]
                        val femaleModel = characterNodes[1]

                        val danceAnimIndexes = (0..3).toList()
                        val maleAnim = danceAnimIndexes.random()
                        val femaleAnim = danceAnimIndexes.random()

                        viewModel.stopAllAnimations(maleModel, femaleModel)

                        maleModel.playAnimation(maleAnim, loop = false)
                        femaleModel.playAnimation(femaleAnim, loop = false)
                    },
                    enabled = viewModel.animButtonsEnabled // TODO: unneeded
                )

                Button(
                    content = { Text("Dance Forever") },
                    onClick = {
                        /*viewModel.playDanceAnimation(
                            characterNodes[0],
                            characterNodes[1],
                            forever = true
                        )*/
                        //TODO: cycle dances
                        val maleModel = characterNodes[0]
                        val femaleModel = characterNodes[1]

                        val danceAnimIndexes = (0..3).toList()
                        val maleAnim = danceAnimIndexes.random()
                        val femaleAnim = danceAnimIndexes.random()

                        viewModel.stopAllAnimations(maleModel, femaleModel)

                        maleModel.playAnimation(maleAnim)
                        femaleModel.playAnimation(femaleAnim)
                    },
                    enabled = viewModel.animButtonsEnabled
                )

                Button(
                    content = { Text("Stop") },
                    onClick = {
//                        viewModel.stopDanceAnimation()
//                        viewModel.stopAnimationsThenIdle()
                        val maleModel = characterNodes[0]
                        val femaleModel = characterNodes[1]

                        viewModel.stopAllAnimations(maleModel, femaleModel)
                    }
                )

                Button(
                    content = { Text("Discuss") },
                    onClick = {
                        /*viewModel.playDiscussionAnimations(characterNodes[0], characterNodes[1])*/

                        val maleModel = characterNodes[0]
                        val femaleModel = characterNodes[1]
                        val discussionIndexes = (5..7).toList()

                        // stop animation in case any one is playing??
                        viewModel.stopAllAnimations(maleModel, femaleModel)

                        maleModel.playAnimation(discussionIndexes.random())
                        femaleModel.playAnimation(discussionIndexes.random())
                    },
                    enabled = viewModel.animButtonsEnabled
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    IconButton(
                        onClick = {},
                        content = {
                            Icon(
                                imageVector = Icons.Default.ZoomIn,
                                contentDescription = "Zoom In",
                                Modifier.size(48.dp)
                            )
                        }
                    )

                    IconButton(
                        onClick = {},
                        content = {
                            Icon(
                                imageVector = Icons.Default.ZoomOut,
                                contentDescription = "Zoom Out",
                                Modifier.size(48.dp)
                            )
                        }
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Up Button
                    OutlinedIconButton(
                        onClick = {},
                        content = {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Up"
                            )
                        }
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Left Button
                        OutlinedIconButton(
                            onClick = {},
                            content = {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Left"
                                )
                            }
                        )

                        // Down Button (Centered Below Up)
                        OutlinedIconButton(
                            onClick = {},
                            content = {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Down"
                                )
                            }
                        )

                        // Right Button
                        OutlinedIconButton(
                            onClick = {},
                            content = {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Right"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}