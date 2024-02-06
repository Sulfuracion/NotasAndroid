package com.dbtechprojects.photonotes.ui.createNote

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.dbtechprojects.photonotes.PhotoNotesApp
import com.dbtechprojects.photonotes.R
import com.dbtechprojects.photonotes.ui.GenericAppBar
import com.dbtechprojects.photonotes.ui.NotesList.NotesFab
import com.dbtechprojects.photonotes.ui.NotesViewModel
import com.dbtechprojects.photonotes.ui.theme.PhotoNotesTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CreateNoteScreen(
    navController: NavController,
    viewModel: NotesViewModel
) {
    // Declaración de estados para el título, el cuerpo, las fotos y el estado del botón de guardar
    val currentNote = remember { mutableStateOf("") }
    val currentTitle = remember { mutableStateOf("") }
    val currentPhotos = remember { mutableStateOf("") }
    val saveButtonState = remember { mutableStateOf(false) }

    // Launcher para obtener una imagen de la galería
    val getImageRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        if (it != null) {
            PhotoNotesApp.getUriPermission(it)
        }
        currentPhotos.value = it.toString()
    }

    // Tema de la aplicación
    PhotoNotesTheme {
        // Contenedor de superficie usando el color 'background' del tema
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.primary) {
            // Scaffold proporciona una estructura básica para la pantalla, incluyendo AppBar y FloatingActionButton
            Scaffold(
                // Barra superior de la aplicación
                topBar = {
                    // Barra de aplicación personalizada
                    GenericAppBar(
                        title = "Crear nota", // Título de la barra de aplicación
                        icon = {
                            // Icono de la barra de aplicación (Guardar)
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.save), // Icono de guardar
                                contentDescription = stringResource(R.string.save_note), // Descripción del contenido para accesibilidad
                                tint = Color.Black, // Tinte del icono (color negro)
                            )
                        },
                        onIconClick = {
                            // Acción al hacer clic en el ícono (Guardar)
                            viewModel.createNote(
                                currentTitle.value,
                                currentNote.value,
                                currentPhotos.value
                            )
                            navController.popBackStack() // Regresa al fragmento anterior en la pila de retroceso
                        },
                        iconState = saveButtonState // Estado del ícono (habilitado o deshabilitado)
                    )
                },
                // Botón flotante para agregar imágenes
                floatingActionButton = {
                    NotesFab(
                        contentDescription = stringResource(R.string.add_image), // Descripción del contenido para accesibilidad
                        action = {
                            // Acción a realizar al hacer clic en el botón flotante (lanzar el selector de imágenes)
                            getImageRequest.launch(arrayOf("image/*"))
                        },
                        icon = R.drawable.camera // Icono del botón flotante (una cámara)
                    )
                },
                content = {
                    // Contenido principal de la pantalla
                    Column(
                        Modifier
                            .padding(12.dp)
                            .fillMaxSize()
                    ) {
                        // Mostrar la imagen seleccionada si la URI de la imagen no está vacía
                        if (currentPhotos.value.isNotEmpty()) {
                            // Utiliza la biblioteca Coil para cargar y mostrar la imagen de manera asincrónica
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest
                                        .Builder(LocalContext.current)
                                        .data(data = Uri.parse(currentPhotos.value)) // Uri de la imagen seleccionada
                                        .build()
                                ),
                                contentDescription = null, // Descripción de contenido nula para una imagen decorativa
                                modifier = Modifier
                                    .fillMaxHeight(0.3f) // Ocupa el 30% de la altura disponible
                                    .padding(6.dp), // Añade relleno alrededor de la imagen
                                contentScale = ContentScale.Crop // Escala de contenido: recorta para adaptarse al contenedor
                            )
                        }


                        // Campo de texto para el título
                        TextField(
                            value = currentTitle.value, // Valor actual del título
                            modifier = Modifier.fillMaxWidth(), // Ocupa el ancho máximo disponible
                            colors = TextFieldDefaults.textFieldColors(
                                cursorColor = Color.Black,
                                focusedLabelColor = Color.Black,
                            ),
                            onValueChange = { value ->
                                currentTitle.value = value // Actualiza el valor del título cuando cambia

                                // Habilita o deshabilita el botón de guardar en función de si hay contenido en el título y el cuerpo de la nota
                                saveButtonState.value = currentTitle.value.isNotBlank() && currentNote.value.isNotBlank()
                            },
                            label = { Text(text = "Titulo") } // Etiqueta del campo de texto
                        )
                        Spacer(modifier = Modifier.padding(12.dp)) // Espaciador vertical para separar los campos de texto


                        // Campo de texto para el cuerpo de la nota
                        TextField(
                            value = currentNote.value, // Valor actual del cuerpo de la nota
                            colors = TextFieldDefaults.textFieldColors(
                                cursorColor = Color.Black,
                                focusedLabelColor = Color.Black,
                            ),
                            modifier = Modifier
                                .fillMaxHeight(0.5f) // Ocupa el 50% de la altura disponible
                                .fillMaxWidth(), // Ocupa el ancho máximo disponible
                            onValueChange = { value ->
                                currentNote.value = value // Actualiza el valor del cuerpo de la nota cuando cambia

                                // Habilita o deshabilita el botón de guardar en función de si hay contenido en el título y el cuerpo de la nota
                                saveButtonState.value = currentTitle.value.isNotBlank() && currentNote.value.isNotBlank()
                            },
                            label = { Text(text = "Cuerpo") } // Etiqueta del campo de texto
                        )

                    }
                }
            )
        }
    }
}
