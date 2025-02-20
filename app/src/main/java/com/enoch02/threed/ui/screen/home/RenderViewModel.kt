package com.enoch02.threed.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RenderViewModel : ViewModel() {
    val maleAnimDurations = mutableMapOf<Int, Float>()
    val femaleAnimDurations = mutableMapOf<Int, Float>()

    //    val animationDurations = MutabStateMap
    var animButtonsEnabled by mutableStateOf(true)

    private fun disableButtonFor(seconds: Float) {
        viewModelScope.launch {
            animButtonsEnabled = false
            delay((seconds * 1000).toLong())
            animButtonsEnabled = true
        }
    }

    fun loadAnimationDurations(maleModel: ModelNode, femaleModel: ModelNode) {
        for (i in 0..maleModel.animationCount) {
            maleAnimDurations[i] = maleModel.animator.getAnimationDuration(i)
        }

        for (i in 0..femaleModel.animationCount) {
            femaleAnimDurations[i] = femaleModel.animator.getAnimationDuration(i)
        }
    }

    /**
     * Play random dance animation from the set of animations
     *
     * @param maleModel male
     * @param femaleModel female
     * @param forever repeat the animations
     */
    fun playDanceAnimation(maleModel: ModelNode, femaleModel: ModelNode, forever: Boolean = false) {
        viewModelScope.launch(Dispatchers.Default) {
            val danceAnimIndexes = (0..3).toList()
            val maleAnim = danceAnimIndexes.random()
            val femaleAnim = danceAnimIndexes.random()

            val delay = maxOf(
                maleAnimDurations[maleAnim] ?: 0f,
                femaleAnimDurations[femaleAnim] ?: 0f
            )

            withContext(Dispatchers.Main) {
                stopAllAnimations(maleModel, femaleModel)

                maleModel.playAnimation(maleAnim, loop = forever)
                femaleModel.playAnimation(femaleAnim, loop = forever)
            }

            disableButtonFor(delay)
        }
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

    fun playDiscussionAnimations(maleModel: ModelNode, femaleModel: ModelNode) {
        val discussionIndexes = listOf(2, 3, 4)

        // stop animation in case any one is playing??
        stopAllAnimations(maleModel, femaleModel)

        maleModel.playAnimation(discussionIndexes.random())
        femaleModel.playAnimation(discussionIndexes.random())
    }

    /**
     * Stop animations that might be playing
     *
     * @param nodes nodes to stop whatever animation they might be playing
     */
    fun stopAllAnimations(maleModel: ModelNode, femaleModel: ModelNode) {
        for (i in 0..15) {
            maleModel.stopAnimation(i)
        }

        for (i in 0..15) {
            femaleModel.stopAnimation(i)
        }

        maleModel.playAnimation(4)
        femaleModel.playAnimation(4)

        //TODO: there are more animations to stop
        /*maleModel.stopAnimation(0)
        maleModel.stopAnimation(1)

        femaleModel.stopAnimation(0)
        femaleModel.stopAnimation(1)*/
    }

    fun loadAnimationNames(vararg models: ModelNode): String {
        var animationNames = ""

        for (model in models) {
            val mCount = model.animator.animationCount
            val mNames = (0 until mCount).mapNotNull { index ->
                model.animator.getAnimationName(index)
            }
            animationNames += mNames
        }

        return animationNames
    }
}

fun ModelNode.setWorldPosition(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
    position = Float3(x, y, z)
}

fun ModelNode.setRotation(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
    rotation = Float3(x, y, z)
}