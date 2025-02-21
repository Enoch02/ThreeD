package com.enoch02.threed.ui.screen.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RenderViewModel : ViewModel() {
    private val maleAnimDurations = mutableMapOf<Int, Float>()
    private val femaleAnimDurations = mutableMapOf<Int, Float>()

    private var animationJob1: Job? = null
    private var animationJob2: Job? = null


    fun loadAnimationDurations(maleModel: ModelNode, femaleModel: ModelNode) {
        for (i in 0..maleModel.animationCount) {
            maleAnimDurations[i] = maleModel.animator.getAnimationDuration(i)
        }

        for (i in 0..femaleModel.animationCount) {
            femaleAnimDurations[i] = femaleModel.animator.getAnimationDuration(i)
        }
    }

    fun stopAllAnimations(maleModel: ModelNode, femaleModel: ModelNode) {
        for (i in 0..11) {
            maleModel.stopAnimation(i)
        }

        for (i in 0..11) {
            femaleModel.stopAnimation(i)
        }

        maleModel.playAnimation(4)
        femaleModel.playAnimation(4)
    }

    private fun stopAllAnimations(node: ModelNode) {
        for (i in 0..11) {
            node.stopAnimation(i)
        }

        node.playAnimation(4)
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

    fun moveForward(model: ModelNode, direction: Float) {
        val duration = maleAnimDurations[9] ?: 0f
        if (duration <= 0f) return

        val startPosition = model.position.x
        val targetPosition = if (direction > 0) {
            startPosition + 0.5f
        } else {
            startPosition - 0.5f
        }

        model.playAnimation(9, loop = false)

        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val endTime = startTime + (duration * 1000).toLong()

            while (System.currentTimeMillis() < endTime) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress =
                    (elapsed / (duration * 1000)).coerceIn(0f, 1f)

                model.setPosition(x = startPosition + (targetPosition - startPosition) * progress)

                delay(16)
            }

            model.setPosition(x = targetPosition)

            stopAllAnimations(model)
        }
    }

    fun moveBackward(model: ModelNode, direction: Float) {
        val duration = maleAnimDurations[8] ?: 0f
        if (duration <= 0f) return

        val startPosition = model.position.x
        val targetPosition = if (direction > 0) {
            startPosition - 0.5f
        } else {
            startPosition + 0.5f
        }

        model.playAnimation(8, loop = false)
        model.playAnimation(8, loop = false)

        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val endTime = startTime + (duration * 1000).toLong()

            while (System.currentTimeMillis() < endTime) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress =
                    (elapsed / (duration * 1000)).coerceIn(0f, 1f)

                model.setPosition(x = startPosition + (targetPosition - startPosition) * progress)

                delay(16)
            }

            model.setPosition(x = targetPosition)

            stopAllAnimations(model)
        }
    }

    fun moveLeft(model: ModelNode, direction: Float) {
        val duration = if (direction > 0) {
            maleAnimDurations[10] ?: 0f
        } else {
            femaleAnimDurations[10] ?: 0f
        }
        if (duration <= 0f) return

        val startPosition = model.position.z
        val targetPosition = if (direction > 0) {
            startPosition - 0.5f
        } else {
            startPosition + 0.5f
        }

        model.playAnimation(10, loop = false)

        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val endTime = startTime + (duration * 1000).toLong()

            while (System.currentTimeMillis() < endTime) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress =
                    (elapsed / (duration * 1000)).coerceIn(0f, 1f)

                model.setPosition(z = startPosition + (targetPosition - startPosition) * progress)

                delay(16)
            }

            model.setPosition(z = targetPosition)

            stopAllAnimations(model)
        }
    }

    fun moveRight(model: ModelNode, direction: Float) {
        val duration = if (direction > 0) {
            maleAnimDurations[11] ?: 0f
        } else {
            femaleAnimDurations[11] ?: 0f
        }
        if (duration <= 0f) return

        val startPosition = model.position.z
        val targetPosition = if (direction > 0) {
            startPosition + 0.5f
        } else {
            startPosition - 0.5f
        }

        model.playAnimation(11, loop = false)

        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val endTime = startTime + (duration * 1000).toLong()

            while (System.currentTimeMillis() < endTime) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress =
                    (elapsed / (duration * 1000)).coerceIn(0f, 1f)

                model.setPosition(z = startPosition + (targetPosition - startPosition) * progress)

                delay(16)
            }

            model.setPosition(z = targetPosition)

            stopAllAnimations(model)
        }
    }

    fun cycleDanceAnimationsMale(model: ModelNode, animationIndices: List<Int>) {
        animationJob1?.cancel()

        animationJob1 = viewModelScope.launch {
            while (true) {
                for (animIndex in animationIndices) {
                    val duration = maleAnimDurations[animIndex] ?: 0f

                    model.playAnimation(animIndex, loop = false)
                    delay((duration * 1000).toLong() + 1000)
                }
            }
        }
    }

    fun cycleDanceAnimationsFemale(model: ModelNode, animationIndices: List<Int>) {
        animationJob2?.cancel()

        animationJob2 = viewModelScope.launch {
            while (true) {
                for (animIndex in animationIndices) {
                    val duration = femaleAnimDurations[animIndex] ?: 0f

                    model.playAnimation(animIndex, loop = false)
                    delay((duration * 1000).toLong() + 1000)
                }
            }
        }
    }

    fun stopCycling() {
        animationJob1?.cancel()
        animationJob2?.cancel()
    }
}

fun ModelNode.setPosition(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
    val mX = if (x == 0f) this.position.x else x
    val mY = if (y == 0f) this.position.y else y
    val mZ = if (z == 0f) this.position.z else z

    position = Float3(mX, mY, mZ)
}

fun ModelNode.setRotation(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
    rotation = Float3(x, y, z)
}