package com.enoch02.threed.navigation

sealed class Destination(val route: String) {

    data object Home: Destination("home")
    data object LoadModel : Destination("load_model")
    data object DemoScene : Destination("demo_scene")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}