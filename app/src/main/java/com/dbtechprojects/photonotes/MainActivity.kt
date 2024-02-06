package com.dbtechprojects.photonotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dbtechprojects.photonotes.ui.EditNote.NoteEditScreen
import com.dbtechprojects.photonotes.ui.NoteDetail.NoteDetailScreen
import com.dbtechprojects.photonotes.ui.NotesList.NotesList
import com.dbtechprojects.photonotes.ui.NotesViewModel
import com.dbtechprojects.photonotes.ui.NotesViewModelFactory
import com.dbtechprojects.photonotes.ui.createNote.CreateNoteScreen

class MainActivity : ComponentActivity() {
    // Declaración de una propiedad para el ViewModel
    private lateinit var notesViewModel: NotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización del ViewModel utilizando NotesViewModelFactory
        notesViewModel = NotesViewModelFactory(PhotoNotesApp.getDao()).create(NotesViewModel::class.java)

        // Establecer el contenido de la actividad
        setContent {
            // Crear un NavController para gestionar la navegación
            val navController = rememberNavController()

            // Definir el punto de inicio y las rutas de navegación
            NavHost(
                navController = navController,
                startDestination = Constants.NAVIGATION_NOTES_LIST
            ) {
                // Definir la pantalla de lista de notas
                composable(Constants.NAVIGATION_NOTES_LIST) { NotesList(navController, notesViewModel) }

                // Definir la pantalla de detalle de nota con argumento de ID de nota
                composable(
                    Constants.NAVIGATION_NOTE_DETAIL,
                    arguments = listOf(navArgument(Constants.NAVIGATION_NOTE_ID_Argument) {
                        type = NavType.IntType
                    })
                ) { backStackEntry ->
                    backStackEntry.arguments?.getInt(Constants.NAVIGATION_NOTE_ID_Argument)
                        ?.let { NoteDetailScreen(noteId = it, navController, notesViewModel) }
                }

                // Definir la pantalla de edición de nota con argumento de ID de nota
                composable(
                    Constants.NAVIGATION_NOTE_EDIT,
                    arguments = listOf(navArgument(Constants.NAVIGATION_NOTE_ID_Argument) {
                        type = NavType.IntType
                    })
                ) { backStackEntry ->
                    backStackEntry.arguments?.getInt(Constants.NAVIGATION_NOTE_ID_Argument)
                        ?.let { NoteEditScreen(noteId = it, navController, notesViewModel) }
                }

                // Definir la pantalla de creación de nueva nota
                composable(Constants.NAVIGATION_NOTES_CREATE) { CreateNoteScreen(navController, notesViewModel) }
            }
        }
    }
}
