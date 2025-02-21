package com.enoch02.threed.ui.screen.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.Woman
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
import io.github.sceneview.rememberView

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RenderScreen(
    navController: NavController,
    viewModel: RenderViewModel = viewModel(),
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    val view = rememberView(engine)
    val collisionSystem = rememberCollisionSystem(view)

    val centerNode = rememberNode(engine)
    val cameraNode = rememberCameraNode(engine) {
        position = Position(x = 0.45f, y = 0.97f, z = 1.38f)
        lookAt(centerNode)
        centerNode.addChildNode(this)
    }
    var maleModel by remember { mutableStateOf<ModelNode?>(null) }
    var femaleModel by remember { mutableStateOf<ModelNode?>(null) }
    var moveMaleModel by remember { mutableStateOf(true) }
    var animationNames by remember { mutableStateOf("") }

    LaunchedEffect(maleModel, femaleModel) {
        if (maleModel != null && femaleModel != null) {
            viewModel.loadAnimationDurations(maleModel!!, femaleModel!!)
        }

//        animationNames = viewModel.loadAnimationNames(characterNodes[0], characterNodes[1])
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    )
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
                content = {
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
                        childNodes = listOf(
                            centerNode,
                            rememberNode {
                                ModelNode(
                                    modelInstance = modelLoader.createModelInstance(
                                        assetFileLocation = "models/floor_material.glb"
                                    )
                                )
                            },
                            rememberNode {
                                ModelNode(
                                    modelInstance = modelLoader.createModelInstance(
                                        assetFileLocation = "models/male.glb"
                                    ),
                                    scaleToUnits = 0.35f,
                                    autoAnimate = false
                                ).apply {
                                    setPosition(x = -0.15f)
                                    setRotation(y = 90f)
                                    playAnimation(4)

                                    maleModel = this
                                }
                            },
                            rememberNode {
                                ModelNode(
                                    modelInstance = modelLoader.createModelInstance(
                                        assetFileLocation = "models/female.glb"
                                    ),
                                    scaleToUnits = 0.35f
                                ).apply {
                                    setPosition(x = 0.15f)
                                    setRotation(y = -90f)
                                    playAnimation(4)

                                    femaleModel = this
                                }
                            }
                        ),
                        collisionSystem = collisionSystem,
                        environment = environmentLoader.createHDREnvironment(
                            assetFileLocation = "environments/sky_2k.hdr"
                        )!!,
                        onFrame = {

                        }
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        // For Debugging
                        /*  if (0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
                              Text(
                                  modifier = Modifier
                                      .fillMaxWidth(),
                                  text = "Animation names: $animationNames"
                              )
                          }*/

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                content = { Text("Dance") },
                                onClick = {
                                    val danceAnimIndexes = (0..3).toList()
                                    val maleAnim = danceAnimIndexes.random()
                                    val femaleAnim = danceAnimIndexes.random()

                                    if (maleModel != null && femaleModel != null) {
                                        viewModel.stopAllAnimations(maleModel!!, femaleModel!!)
                                    }

                                    maleModel?.playAnimation(maleAnim, loop = false)
                                    femaleModel?.playAnimation(femaleAnim, loop = false)
                                }
                            )

                            Button(
                                content = { Text("Dance Forever") },
                                onClick = {
                                    //TODO: cycle dances
                                    val danceAnimIndexes = (0..3).toList()
                                    val maleAnim = danceAnimIndexes.random()
                                    val femaleAnim = danceAnimIndexes.random()

                                    if (maleModel != null && femaleModel != null) {
                                        viewModel.stopAllAnimations(maleModel!!, femaleModel!!)
                                    }

                                    /*maleModel?.playAnimation(maleAnim)
                                    femaleModel?.playAnimation(femaleAnim)*/
                                    maleModel?.let {
                                        viewModel.cycleDanceAnimationsMale(
                                            it,
                                            danceAnimIndexes.shuffled()
                                        )
                                    }
                                    femaleModel?.let {
                                        viewModel.cycleDanceAnimationsFemale(
                                            it,
                                            danceAnimIndexes.shuffled()
                                        )
                                    }
                                }
                            )

                            Button(
                                content = { Text("Stop") },
                                onClick = {
                                    if (maleModel != null && femaleModel != null) {
                                        viewModel.stopAllAnimations(maleModel!!, femaleModel!!)
                                    }

                                    viewModel.stopCycling()
                                }
                            )

                            Button(
                                content = { Text("Discuss") },
                                onClick = {
                                    val discussionIndexes = (5..7).toList()

                                    if (maleModel != null && femaleModel != null) {
                                        viewModel.stopAllAnimations(maleModel!!, femaleModel!!)
                                    }

                                    maleModel?.playAnimation(discussionIndexes.random())
                                    femaleModel?.playAnimation(discussionIndexes.random())
                                }
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box {
                                Switch(
                                    checked = moveMaleModel,
                                    onCheckedChange = {
                                        moveMaleModel = it
                                    },
                                    thumbContent = {
                                        val vector = if (moveMaleModel) {
                                            Icons.Default.Man
                                        } else {
                                            Icons.Default.Woman
                                        }

                                        Icon(
                                            imageVector = vector,
                                            tint = if (moveMaleModel) {
                                                androidx.compose.ui.graphics.Color.Blue
                                            } else {
                                                androidx.compose.ui.graphics.Color(255, 105, 180)
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize)
                                        )
                                    }
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                OutlinedIconButton(
                                    onClick = {
                                        if (moveMaleModel) {
                                            maleModel?.let {
                                                viewModel.moveForward(
                                                    it,
                                                    it.rotation.y
                                                )
                                            }
                                        } else {
                                            femaleModel?.let {
                                                viewModel.moveForward(
                                                    it,
                                                    it.rotation.y
                                                )
                                            }
                                        }
                                    },
                                    content = {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowUp,
                                            contentDescription = "Up"
                                        )
                                    }
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedIconButton(
                                        onClick = {
                                            if (moveMaleModel) {
                                                maleModel?.let {
                                                    viewModel.moveLeft(
                                                        it,
                                                        it.rotation.y
                                                    )
                                                }
                                            } else {
                                                femaleModel?.let {
                                                    viewModel.moveLeft(
                                                        it,
                                                        it.rotation.y
                                                    )
                                                }
                                            }
                                        },
                                        content = {
                                            Icon(
                                                imageVector = Icons.Default.ChevronLeft,
                                                contentDescription = "Left"
                                            )
                                        }
                                    )

                                    OutlinedIconButton(
                                        onClick = {
                                            if (moveMaleModel) {
                                                maleModel?.let {
                                                    viewModel.moveBackward(
                                                        it,
                                                        it.rotation.y
                                                    )
                                                }
                                            } else {
                                                femaleModel?.let {
                                                    viewModel.moveBackward(
                                                        it,
                                                        it.rotation.y
                                                    )
                                                }
                                            }
                                        },
                                        content = {
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowDown,
                                                contentDescription = "Down"
                                            )
                                        }
                                    )

                                    OutlinedIconButton(
                                        onClick = {
                                            if (moveMaleModel) {
                                                maleModel?.let {
                                                    viewModel.moveRight(
                                                        it,
                                                        it.rotation.y
                                                    )
                                                }
                                            } else {
                                                femaleModel?.let {
                                                    viewModel.moveRight(
                                                        it,
                                                        it.rotation.y
                                                    )
                                                }
                                            }
                                        },
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
            )
        }
    )
}