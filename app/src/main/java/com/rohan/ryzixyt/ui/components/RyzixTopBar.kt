package com.rohan.ryzixyt.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RyzixTopBar(title: String, actions: @Composable () -> Unit = {}) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            Icon(
                imageVector = Icons.Outlined.PlayCircle,
                contentDescription = null,
                tint = Color(0xFFFF2541),
            )
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(),
    )
}
