package com.enoch02.threed.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RenderViewModel : ViewModel() {
    var childNodes = mutableStateListOf<Node>()

    private var maleModelInstance by mutableStateOf<ModelInstance?>(null)
    private var femaleModelInstance by mutableStateOf<ModelInstance?>(null)
    private val characterNodes = mutableStateListOf<ModelNode>()
    var animationNames by mutableStateOf("")

    fun loadModels(loader: ModelLoader) {
        maleModelInstance = loader.createModelInstance(
            assetFileLocation = "models/male.glb"
        )

        //TODO
        femaleModelInstance = loader.createModelInstance(
            assetFileLocation = "models/female.glb"
        )

        val floor = ModelNode(
            modelInstance = loader.createModelInstance(
                assetFileLocation = "models/floor_material.glb"
            )
        )
        childNodes.add(floor)

        if (maleModelInstance != null) {
            val maleNode = ModelNode(
                modelInstance = maleModelInstance!!,
                scaleToUnits = 0.35f,
                autoAnimate = false
            ).apply {
                setWorldPosition(x = -0.15f)
                setRotation(y = 90f)
                playAnimation(1)
            }

            childNodes.add(maleNode)
            characterNodes.add(maleNode) // 0
        }

        if (femaleModelInstance != null) {
            val femaleNode = ModelNode(
                modelInstance = femaleModelInstance!!,
                scaleToUnits = 0.35f
            ).apply {
                setWorldPosition(x = 0.15f)
                setRotation(y = -90f)
                playAnimation(1)
            }

            childNodes.add(femaleNode)
            characterNodes.add(femaleNode) // 1
        }

        loadAnimationNames()
    }

    fun playDanceAnimation(forever: Boolean = false) {
        val maleNode = characterNodes[0]
        val femaleNode = characterNodes[1]

        stopAllAnimations(maleNode, femaleNode)

//        maleNode.stopAnimation(1) // Idle animation
        maleNode.playAnimation(0, loop = forever) // Dance
//        femaleNode.stopAnimation(1)
        femaleNode.playAnimation(0, loop = forever)
    }

    fun stopDanceAnimation() {
        val maleNode = characterNodes[0]
        val femaleNode = characterNodes[1]
        /*maleNode.stopAnimation(0)
        maleNode.playAnimation(1)*/

        //TODO: not smooth enough
        transitionAnimations(maleNode, 0, 1, 1000L)
        transitionAnimations(femaleNode, 0, 1, 1000L)
    }

    fun transitionAnimations(modelNode: ModelNode, fromAnim: Int, toAnim: Int, duration: Long) {
        modelNode.playAnimation(fromAnim, loop = false)

        CoroutineScope(Dispatchers.Main).launch {
            val stepTime = 50L
            val steps = (duration / stepTime).toInt()

            for (i in 0..steps) {
                val progress = i.toFloat() / steps
                modelNode.setAnimationSpeed(fromAnim, 1f - progress) // slow down
                modelNode.setAnimationSpeed(toAnim, progress)  // speed up

                delay(stepTime)
            }

            modelNode.stopAnimation(fromAnim)
            modelNode.playAnimation(toAnim, loop = false)
        }
    }

    fun playDiscussionAnimations() {
        val discussionIndexes = listOf(2, 3, 4)
        discussionIndexes.random()

        val maleNode = characterNodes[0]
        val femaleNode = characterNodes[1]

        // stop animation in case any one is playing??
        stopAllAnimations(maleNode, femaleNode)
        maleNode.playAnimation(discussionIndexes.random())
        femaleNode.playAnimation(discussionIndexes.random())
    }

    /**
     * Stop animations that might be playing
     *
     * @param nodes nodes to stop whatever animation they might be playing
     */
    fun stopAllAnimations(vararg nodes: ModelNode) {
        nodes.forEach { node ->
            node.stopAnimation(0)
            node.stopAnimation(1)
        }
    }

    private fun loadAnimationNames() {
        val mCount = maleModelInstance?.animator?.animationCount ?: 0
        val mNames = (0 until mCount).mapNotNull { index ->
            "Male ${maleModelInstance?.animator?.getAnimationName(index)}"
        }
        animationNames += mNames

        val fCount = femaleModelInstance?.animator?.animationCount ?: 0
        val fNames = (0 until fCount).mapNotNull { index ->
            "Female ${maleModelInstance?.animator?.getAnimationName(index)}"
        }
        animationNames += fNames
    }
}

fun ModelNode.setWorldPosition(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
    position = Float3(x, y, z)
}

fun ModelNode.setRotation(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
    rotation = Float3(x, y, z)
}