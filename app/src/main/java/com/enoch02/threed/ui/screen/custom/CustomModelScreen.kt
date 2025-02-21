package com.enoch02.threed.ui.screen.custom

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import io.github.sceneview.Scene
import io.github.sceneview.animation.Transition.animateRotation
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import io.github.sceneview.rememberNodes
import java.io.File
import java.io.FileOutputStream
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModelScreen(navController: NavController) {
    val context = LocalContext.current
    var fileUri: Uri? by rememberSaveable { mutableStateOf(null) }
    var file: File? by rememberSaveable { mutableStateOf(null) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            fileUri = it.data?.data
            file = it.data?.data?.let { it1 -> copyFileToCache(context, it1) }
        }
    val intent = Intent(
        Intent.ACTION_OPEN_DOCUMENT,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )
        .apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf("model/gltf-binary", "application/octet-stream")
            )
        }

    LaunchedEffect(Unit) {
        launcher.launch(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Custom Model") },
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
                },
                actions = {
                    IconButton(
                        onClick = {
                            fileUri = null
                            file = null
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null
                            )
                        }
                    )
                }
            )
        },
        content = { innerPadding ->
            if (fileUri != null) {
                val engine = rememberEngine()
                val modelLoader = rememberModelLoader(engine)
                val environmentLoader = rememberEnvironmentLoader(engine)

                val centerNode = rememberNode(engine)

                val cameraNode = rememberCameraNode(engine) {
                    position = Position(z = 2.0f)
                    lookAt(centerNode)
                    centerNode.addChildNode(this)
                }

                val cameraTransition = rememberInfiniteTransition(label = "CameraTransition")
                val cameraRotation by cameraTransition.animateRotation(
                    initialValue = Rotation(y = 0.0f),
                    targetValue = Rotation(y = 360.0f),
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 7.seconds.toInt(DurationUnit.MILLISECONDS))
                    )
                )
                val childNodes = rememberNodes {
                    add(centerNode)
                }

                if (file != null) {
                    childNodes.add(
                        ModelNode(
                            modelInstance = modelLoader.createModelInstance(file!!),
                            scaleToUnits = 0.35f
                        )
                    )
                }

                Box(modifier = Modifier.padding(innerPadding)) {
                    Scene(
                        modifier = Modifier.fillMaxSize(),
                        engine = engine,
                        modelLoader = modelLoader,
                        cameraNode = cameraNode,
                        cameraManipulator = rememberCameraManipulator(
                            orbitHomePosition = cameraNode.worldPosition,
                            targetPosition = if (file != null) {
                                childNodes[1].worldPosition
                            } else {
                                centerNode.worldPosition
                            }
                        ),
                        childNodes = childNodes,
                        environment = environmentLoader.createHDREnvironment(
                            assetFileLocation = "environments/sky_2k.hdr"
                        )!!,
                        onFrame = {
                            centerNode.rotation = cameraRotation
                            cameraNode.lookAt(centerNode)
                        }
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                    content = {
                        Button(
                            onClick = { launcher.launch(intent) },
                            content = { Text("Load Model from Storage") }
                        )
                    }
                )
            }
        }
    )
}

fun copyFileToCache(context: Context, uri: Uri): File? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val tempFile = File(context.cacheDir, "temp_model.glb")

    FileOutputStream(tempFile).use { output ->
        inputStream.copyTo(output)
    }

    return tempFile
}