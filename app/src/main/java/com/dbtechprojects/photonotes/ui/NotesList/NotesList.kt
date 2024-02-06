package com.dbtechprojects.photonotes.ui.NotesList

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.dbtechprojects.photonotes.Constants
import com.dbtechprojects.photonotes.Constants.orPlaceHolderList
import com.dbtechprojects.photonotes.R
import com.dbtechprojects.photonotes.model.Note
import com.dbtechprojects.photonotes.model.getDay
import com.dbtechprojects.photonotes.ui.GenericAppBar
import com.dbtechprojects.photonotes.ui.NotesViewModel
import com.dbtechprojects.photonotes.ui.theme.PhotoNotesTheme
import com.dbtechprojects.photonotes.ui.theme.noteBGBlue
import com.dbtechprojects.photonotes.ui.theme.noteBGYellow


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NotesList(navController: NavController, viewModel: NotesViewModel) {
    // Estados para manejar el diálogo de eliminación, texto de eliminación, consulta de notas y notas a borrar
    val openDialog = remember { mutableStateOf(false) }
    val deleteText = remember { mutableStateOf("") }
    val notesQuery = remember { mutableStateOf("") }
    val notesToDelete = remember { mutableStateOf(listOf<Note>()) }

    // Observa las notas desde el ViewModel
    val notes = viewModel.notes.observeAsState()

    // Contexto local
    val context = LocalContext.current

    // Theme de la aplicación
    PhotoNotesTheme {
        // Surface: Contenedor principal que ocupa toda la pantalla
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.primary) {
            // Scaffold: Componente para implementar la estructura básica de una pantalla con AppBar y FAB
            Scaffold(
                // topBar: Barra de la parte superior con título y acciones
                topBar = {
                    GenericAppBar(
                        title = stringResource(R.string.photo_notes),  // Título de la pantalla
                        onIconClick = {
                            // Acción al hacer clic en el ícono (Eliminar todas las notas)
                            if (notes.value?.isNotEmpty() == true) {
                                openDialog.value = true
                                deleteText.value = "¿Seguro quieres borrar TODAS las notas?"
                                notesToDelete.value = notes.value ?: emptyList()
                            } else {
                                // Muestra un mensaje si no hay notas para eliminar
                                Toast.makeText(
                                    context,
                                    "No se encontraron notas.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        icon = {
                            // Ícono en la barra superior para eliminar notas
                            Icon(
                                imageVector = ImageVector.vectorResource(
                                    id = R.drawable.note_delete
                                ),
                                contentDescription = stringResource(id = R.string.delete_note),
                                tint = Color.Black
                            )
                        },
                        iconState = remember { mutableStateOf(true) }

                    )
                },
                // floatingActionButton: Botón flotante para crear nuevas notas
                floatingActionButton = {
                    NotesFab(
                        contentDescription = stringResource(R.string.create_note),
                        action = { navController.navigate(Constants.NAVIGATION_NOTES_CREATE) },
                        icon = R.drawable.note_add_icon
                    )
                }
            ) {
                // Column: Contenedor vertical para organizar componentes
                Column() {
                    // Barra de búsqueda para filtrar notas
                    SearchBar(notesQuery)

                    // Lista de notas
                    NotesList(
                        notes = notes.value.orPlaceHolderList(),
                        query = notesQuery,
                        openDialog = openDialog,
                        deleteText = deleteText,
                        navController = navController,
                        notesToDelete = notesToDelete
                    )
                }

                // DeleteDialog: Diálogo para confirmar la eliminación de notas
                DeleteDialog(
                    openDialog = openDialog,
                    text = deleteText,
                    notesToDelete = notesToDelete,
                    action = {
                        // Acción ejecutada al confirmar la eliminación
                        notesToDelete.value.forEach {
                            viewModel.deleteNotes(it)
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun SearchBar(query: MutableState<String>) {
    // Column: Contenedor vertical para organizar la barra de búsqueda
    Column(Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp, bottom = 0.dp)) {
        // TextField: Componente para la entrada de texto de la barra de búsqueda
        TextField(
            value = query.value,  // Valor actual de la barra de búsqueda
            placeholder = { Text("Buscando...") },  // Texto de marcador de posición
            maxLines = 1,  // Número máximo de líneas
            onValueChange = { query.value = it },  // Actualiza el valor de la barra de búsqueda al cambiar el texto
            modifier = Modifier
                .background(Color.White)  // Color de fondo blanco
                .clip(RoundedCornerShape(12.dp))  // Esquinas redondeadas con un radio de 12 dp
                .fillMaxWidth(),  // Ocupa todo el ancho disponible
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Black,  // Color del texto
            ),
            trailingIcon = {
                // AnimatedVisibility: Componente para animar la visibilidad del ícono de eliminación
                AnimatedVisibility(
                    visible = query.value.isNotEmpty(),  // Visible solo si la barra de búsqueda no está vacía
                    enter = fadeIn(),  // Animación de entrada (aparecer)
                    exit = fadeOut()  // Animación de salida (desaparecer)
                ) {
                    // IconButton: Botón con un ícono para borrar el contenido de la barra de búsqueda
                    IconButton(onClick = { query.value = "" }) {
                        // Icon: Ícono de cruz para borrar el contenido de la barra de búsqueda
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.icon_cross),
                            contentDescription = stringResource(
                                R.string.clear_search
                            )
                        )
                    }
                }
            }
        )
    }
}


@Composable
fun NotesList(
    notes: List<Note>,  // Lista de notas a mostrar
    openDialog: MutableState<Boolean>,  // Estado para controlar la apertura del cuadro de diálogo
    query: MutableState<String>,  // Estado para almacenar el texto de búsqueda
    deleteText: MutableState<String>,  // Estado para almacenar el texto a mostrar en el cuadro de diálogo de eliminación
    navController: NavController,  // NavController para la navegación entre destinos
    notesToDelete: MutableState<List<Note>>,  // Lista de notas seleccionadas para eliminar
) {
    var previousHeader = ""  // Almacena la fecha anterior para mostrar encabezados de fecha
    // LazyColumn: Columna perezosa para manejar eficientemente grandes conjuntos de datos
    LazyColumn(
        contentPadding = PaddingValues(12.dp),  // Relleno interior del contenido de la lista
        modifier = Modifier.background(MaterialTheme.colors.primary)  // Fondo de la lista
    ) {
        // Filtrar las notas según la consulta de búsqueda
        val queriedNotes = if (query.value.isEmpty()){
            notes
        } else {
            notes.filter { it.note.contains(query.value) || it.title.contains(query.value) }
        }
        // itemsIndexed: Crea elementos de lista indexados para las notas filtradas
        itemsIndexed(queriedNotes) { index, note ->
            // Verificar si la fecha de la nota es diferente a la fecha anterior
            if (note.getDay() != previousHeader) {
                // Mostrar un nuevo encabezado de fecha
                Column(
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = note.getDay(), color = Color.Black)  // Texto del encabezado de fecha
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                )
                previousHeader =  note.getDay()  // Actualizar la fecha anterior
            }

            // NoteListItem: Componente para mostrar una única nota en la lista
            NoteListItem(
                note,
                openDialog,
                deleteText = deleteText ,
                navController,
                notesToDelete = notesToDelete,
                noteBackGround = if (index % 2 == 0) {
                    noteBGYellow  // Fondo amarillo para índices pares
                } else noteBGBlue  // Fondo azul para índices impares
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteListItem(
    note: Note,  // Objeto Note que contiene la información de la nota a representar
    openDialog: MutableState<Boolean>,  // Estado para controlar la apertura del cuadro de diálogo
    deleteText: MutableState<String>,  // Estado para almacenar el texto a mostrar en el cuadro de diálogo de eliminación
    navController: NavController,  // NavController para la navegación entre destinos
    noteBackGround: Color,  // Color de fondo de la vista de la nota
    notesToDelete: MutableState<List<Note>>  // Lista de notas que se han seleccionado para eliminar
) {

    // Box: Contenedor que puede aplicar restricciones a su contenido
    return Box(modifier = Modifier.height(120.dp).clip(RoundedCornerShape(12.dp))) {
        // Column: Contenedor vertical para organizar el contenido de la nota
        Column(
            modifier = Modifier
                .background(noteBackGround)  // Aplica el color de fondo
                .fillMaxWidth()  // Ocupa todo el ancho disponible
                .height(120.dp)  // Altura fija de 120 dp
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),  // Efecto de onda al hacer clic
                    onClick = {
                        if (note.id != 0) {
                            navController.navigate(Constants.noteDetailNavigation(note.id ?: 0))
                        }
                    },
                    onLongClick = {
                        if (note.id != 0) {
                            openDialog.value = true  // Abre el cuadro de diálogo de eliminación
                            deleteText.value = "¿Estás seguro de que quieres borrar la nota?"
                            notesToDelete.value = mutableListOf(note)  // Agrega la nota actual a la lista de notas para eliminar
                        }
                    }
                )
        ) {
            // Row: Contenedor horizontal para organizar elementos en fila
            Row() {
                if (note.imageUri != null && note.imageUri.isNotEmpty()) {
                    // Image: Componente para cargar y mostrar una imagen
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest
                                .Builder(LocalContext.current)
                                .data(data = Uri.parse(note.imageUri))
                                .build()
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.3f),  // Ocupa el 30% del ancho disponible
                        contentScale = ContentScale.Crop  // Escala de contenido para recortar la imagen
                    )
                }

                // Column: Contenedor vertical para organizar el título, cuerpo y fecha de actualización de la nota
                Column() {
                    // Texto del título de la nota
                    Text(
                        text = note.title,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    // Texto del cuerpo de la nota con un máximo de 3 líneas
                    Text(
                        text = note.note,
                        color = Color.Black,
                        maxLines = 3,
                        modifier = Modifier.padding(12.dp)
                    )

                    // Texto de la fecha de actualización de la nota
                    Text(
                        text = note.dateUpdated,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NotesFab(contentDescription: String, icon: Int, action: () -> Unit) {
    // FloatingActionButton: Botón flotante utilizado para acciones principales
    FloatingActionButton(
        onClick = { action.invoke() },  // Acción a realizar cuando se hace clic en el botón
        backgroundColor = MaterialTheme.colors.primary  // Color de fondo del botón extraído del tema de Material
    ) {
        // Icon: Icono dentro del botón, representado por un recurso de vector
        Icon(
            ImageVector.vectorResource(id = icon),  // ID del recurso de vector que representa el ícono
            contentDescription = contentDescription,  // Descripción del contenido para accesibilidad
            tint = Color.Black  // Tinte del ícono (color)
        )
    }
}


@Composable
fun DeleteDialog(
    openDialog: MutableState<Boolean>,  // Estado que indica si el diálogo está abierto o cerrado
    text: MutableState<String>,  // Estado que almacena el texto a mostrar en el diálogo
    action: () -> Unit,  // Acción a realizar cuando se confirma el borrado
    notesToDelete: MutableState<List<Note>>  // Estado que almacena la lista de notas a borrar
) {
    // Verifica si el diálogo está abierto
    if (openDialog.value) {
        // Crea un cuadro de diálogo de alerta utilizando el componente AlertDialog
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false  // Cierra el diálogo si se cancela
            },
            title = {
                Text(text = "Borrar nota")  // Título del cuadro de diálogo
            },
            text = {
                Column() {
                    Text(text.value)  // Muestra el texto proporcionado en el cuerpo del cuadro de diálogo
                }
            },
            buttons = {
                // Row: Fila que contiene los botones en el cuadro de diálogo
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Column: Columna que contiene los botones "Sí" y "No"
                    Column() {
                        // Botón "Sí" para confirmar el borrado
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Black,
                                contentColor = Color.White
                            ),
                            onClick = {
                                action.invoke()  // Ejecuta la acción de borrado
                                openDialog.value = false  // Cierra el diálogo
                                notesToDelete.value = mutableListOf()  // Limpia la lista de notas a borrar
                            }
                        ) {
                            Text("Si")
                        }

                        // Espaciador para separar los botones
                        Spacer(modifier = Modifier.padding(12.dp))

                        // Botón "No" para cancelar el borrado
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Black,
                                contentColor = Color.White
                            ),
                            onClick = {
                                openDialog.value = false  // Cierra el diálogo
                                notesToDelete.value = mutableListOf()  // Limpia la lista de notas a borrar
                            }
                        ) {
                            Text("No")
                        }
                    }
                }
            }

        )
    }
}


