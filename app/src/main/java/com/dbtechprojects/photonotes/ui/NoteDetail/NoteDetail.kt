package com.dbtechprojects.photonotes.ui.NoteDetail

import android.annotation.SuppressLint
import android.net.Uri
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.dbtechprojects.photonotes.Constants
import com.dbtechprojects.photonotes.Constants.noteDetailPlaceHolder
import com.dbtechprojects.photonotes.R
import com.dbtechprojects.photonotes.ui.GenericAppBar
import com.dbtechprojects.photonotes.ui.NotesViewModel
import com.dbtechprojects.photonotes.ui.theme.PhotoNotesTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NoteDetailScreen(noteId: Int, navController: NavController, viewModel: NotesViewModel) {
    // Alcance de la corrutina para realizar operaciones asincrónicas
    val scope = rememberCoroutineScope()

    // Estado mutable que almacena la información de la nota
    val note = remember {
        mutableStateOf(Constants.noteDetailPlaceHolder)
    }

    // Efecto lanzado cuando el componente es creado (LaunchedEffect)
    LaunchedEffect(true) {
        // Lanzar una corrutina en el hilo de IO para obtener la información de la nota
        scope.launch(Dispatchers.IO) {
            // Obtener la nota con el ID proporcionado desde el ViewModel
            note.value = viewModel.getNote(noteId) ?: Constants.noteDetailPlaceHolder
        }
    }

    // Comienza la composición de la interfaz de usuario utilizando el tema de la aplicación
    PhotoNotesTheme {
        // Contenedor de superficie utilizando el color de fondo del tema
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            // Scaffold proporciona una estructura básica de la pantalla, incluyendo una barra superior
            Scaffold(
                topBar = {
                    // Barra superior personalizada utilizando el componente GenericAppBar
                    GenericAppBar(
                        title = note.value.title, // Título de la barra superior basado en el título de la nota
                        onIconClick = {
                            // Navegar a la pantalla de edición de la nota al hacer clic en el ícono
                            navController.navigate(Constants.noteEditNavigation(note.value.id ?: 0))
                        },
                        icon = {
                            // Ícono de edición en la barra superior
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.edit_note),
                                contentDescription = stringResource(R.string.edit_note),
                                tint = Color.Black,
                            )
                        },
                        iconState = remember { mutableStateOf(true) } // Estado del ícono (puede ser ignorado)
                    )
                },
            ) {
                // Contenido principal de la pantalla (Columna)
                Column(
                    Modifier.fillMaxSize()
                ) {
                    // Mostrar la imagen de la nota si existe
                    if (note.value.imageUri != null && note.value.imageUri!!.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest
                                    .Builder(LocalContext.current)
                                    .data(data = Uri.parse(note.value.imageUri))
                                    .build()
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxHeight(0.3f)
                                .fillMaxWidth()
                                .padding(6.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Título de la nota
                    Text(
                        text = note.value.title,
                        modifier = Modifier.padding(top = 24.dp, start = 12.dp, end = 24.dp),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Fecha de actualización de la nota
                    Text(text = note.value.dateUpdated, Modifier.padding(12.dp), color = Color.Gray)

                    // Cuerpo de la nota
                    Text(text = note.value.note, Modifier.padding(12.dp))
                }
            }
        }
    }
}
