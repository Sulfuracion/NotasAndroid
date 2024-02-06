package com.dbtechprojects.photonotes.ui.EditNote

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.dbtechprojects.photonotes.Constants
import com.dbtechprojects.photonotes.PhotoNotesApp
import com.dbtechprojects.photonotes.R
import com.dbtechprojects.photonotes.model.Note
import com.dbtechprojects.photonotes.ui.GenericAppBar
import com.dbtechprojects.photonotes.ui.NotesList.NotesFab
import com.dbtechprojects.photonotes.ui.NotesViewModel
import com.dbtechprojects.photonotes.ui.theme.PhotoNotesTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NoteEditScreen(noteId: Int, navController: NavController, viewModel: NotesViewModel) {
    // Alcance de la coroutine para lanzar operaciones en un hilo de fondo
    val scope = rememberCoroutineScope()

    // Estado para la nota actual y sus propiedades (título, cuerpo, fotos, estado del botón de guardar)
    val note = remember {
        mutableStateOf(Constants.noteDetailPlaceHolder)
    }
    // Estado del cuerpo de la nota actual
    val currentNote = remember { mutableStateOf(note.value.note) }

    // Estado del título actual
    val currentTitle = remember { mutableStateOf(note.value.title) }

    // Estado de la URI de la imagen actual
    val currentPhotos = remember { mutableStateOf(note.value.imageUri) }

    // Estado del botón de guardar (inicialmente deshabilitado)
    val saveButtonState = remember { mutableStateOf(false) }


    // Lanzador para obtener una imagen de la galería
    val getImageRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            PhotoNotesApp.getUriPermission(uri)
        }
        currentPhotos.value = uri.toString()
        if (currentPhotos.value != note.value.imageUri) {
            saveButtonState.value = true
        }
    }

    // Efecto de lanzamiento para cargar la nota actual al iniciarse la pantalla
    LaunchedEffect(true) {
        scope.launch(Dispatchers.IO) {
            note.value = viewModel.getNote(noteId) ?: Constants.noteDetailPlaceHolder
            currentNote.value = note.value.note
            currentTitle.value = note.value.title
            currentPhotos.value = note.value.imageUri
        }
    }

    PhotoNotesTheme {
        // Contenedor de superficie utilizando el color de fondo del tema
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.primary) {
            // Scaffold proporciona una estructura básica para la pantalla, incluyendo AppBar y FloatingActionButton
            Scaffold(
                topBar = {
                    // Barra de aplicación personalizada
                    GenericAppBar(
                        title = "Editar nota",
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.save),
                                contentDescription = stringResource(R.string.save_note),
                                tint = Color.Black,
                            )
                        },
                        onIconClick = {
                            // Acción al hacer clic en el ícono (Guardar)
                            viewModel.updateNote(
                                Note(
                                    id = note.value.id,
                                    note = currentNote.value,
                                    title = currentTitle.value,
                                    imageUri = currentPhotos.value
                                )
                            )
                            navController.popBackStack()
                        },
                        iconState = saveButtonState
                    )
                },
                floatingActionButton = {
                    // Botón flotante para agregar imágenes
                    NotesFab(
                        contentDescription = stringResource(R.string.add_photo), // Descripción del contenido para accesibilidad
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
                        // Mostrar la imagen seleccionada
                        if (currentPhotos.value != null && currentPhotos.value!!.isNotEmpty()) {
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
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.3f) // Ocupa el 30% de la altura disponible
                                    .padding(6.dp), // Añade relleno alrededor de la imagen
                                contentScale = ContentScale.Crop // Escala de contenido: recorta para adaptarse al contenedor
                            )
                        }

                        // Campo de texto para el título
                        TextField(
                            value = currentTitle.value, // Valor actual del título
                            colors = TextFieldDefaults.textFieldColors(
                                cursorColor = Color.Black,
                                focusedLabelColor = Color.Black,
                            ),
                            onValueChange = { value ->
                                currentTitle.value = value // Actualiza el valor del título cuando cambia

                                // Verifica si el nuevo valor del título es diferente al valor original de la nota
                                if (currentTitle.value != note.value.title) {
                                    saveButtonState.value = true // Habilita el estado del botón de guardar
                                } else if (currentNote.value == note.value.note &&
                                    currentTitle.value == note.value.title
                                ) {
                                    saveButtonState.value = false // Deshabilita el estado del botón de guardar
                                }
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
                            onValueChange = { value ->
                                currentNote.value = value // Actualiza el valor del cuerpo de la nota cuando cambia

                                // Verifica si el nuevo valor del cuerpo es diferente al valor original de la nota
                                if (currentNote.value != note.value.note) {
                                    saveButtonState.value = true // Habilita el estado del botón de guardar
                                } else if (currentNote.value == note.value.note &&
                                    currentTitle.value == note.value.title
                                ) {
                                    saveButtonState.value = false // Deshabilita el estado del botón de guardar
                                }
                            },
                            label = { Text(text = "Cuerpo") } // Etiqueta del campo de texto
                        )

                    }
                }

            )
        }
    }
}
