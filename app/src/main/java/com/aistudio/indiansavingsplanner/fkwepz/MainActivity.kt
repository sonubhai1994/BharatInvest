package com.aistudio.indiansavingsplanner.fkwepz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.aistudio.indiansavingsplanner.fkwepz.ui.HomeScreen
import com.aistudio.indiansavingsplanner.fkwepz.ui.MainActivityViewModel
import com.aistudio.indiansavingsplanner.fkwepz.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    // Trivial change to force rebuild
    
    // Instantiate our consolidated state manager with the custom application factory
    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                HomeScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
