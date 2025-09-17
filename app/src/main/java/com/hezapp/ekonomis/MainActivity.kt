package com.hezapp.ekonomis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hezapp.ekonomis.koin_module.KoinProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val koinApp = (application as KoinProvider).koinApp

        setContent {
            MainComposable(koinApp)
        }
    }
}