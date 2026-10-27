package com.atom.vpn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.AndroidEntryPoint
import com.atom.vpn.ui.navigation.AtomNavHost
import com.atom.vpn.ui.theme.AtomTheme
import com.atom.vpn.ui.theme.AtomColors

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtomTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AtomColors.Background
                ) {
                    AtomNavHost()
                }
            }
        }
    }
}
