package com.dbtechprojects.photonotes.ui

import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun GenericAppBar(
    title: String,
    onIconClick: (() -> Unit)?,
    icon: @Composable() (() -> Unit)?,
    iconState: MutableState<Boolean>
) {
    // Barra superior de la aplicación
    TopAppBar(
        // Título de la barra de aplicación
        title = { Text(title) },
        // Color de fondo de la barra de aplicación obtenido del tema Material
        backgroundColor = MaterialTheme.colors.primary,
        // Acciones en la barra de aplicación (puede contener iconos, botones, etc.)
        actions = {
            // Icono de botón (derecha)
            IconButton(
                onClick = {
                    // Invoca la acción asociada al clic en el icono
                    onIconClick?.invoke()
                },
                content = {
                    // Comprobación del estado del icono antes de mostrarlo
                    if (iconState.value) {
                        // Invoca la función que representa el icono (si está presente)
                        icon?.invoke()
                    }
                }
            )
        }
    )
}
