package com.enoch02.threed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.enoch02.threed.navigation.TDNavHost
import com.enoch02.threed.ui.screen.demo.RenderScreen
import com.enoch02.threed.ui.theme.ThreeDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThreeDTheme {
                TDNavHost()
            }
        }
    }
}

/*

// Extension function for relative positioning
fun ModelNode.moveBy(x: Float = 0f, y: Float = 0f, z: Float = 0f) {
    position += Float3(x, y, z)
}
 */