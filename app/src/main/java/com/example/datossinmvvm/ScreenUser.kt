
package com.example.datossinmvvm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenUser() {

    val context = LocalContext.current

    val db = remember {
        Room.databaseBuilder(
            context.applicationContext,
            UserDatabase::class.java,
            "user_db"
        ).build()
    }

    val dao = remember(db) { db.userDao() }
    val coroutineScope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dataUser by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    // Función para actualizar la lista
    suspend fun actualizarUsuarios() {
        val users = dao.getAll()
        dataUser = users.joinToString("\n") {
            "${it.uid} - ${it.firstName} ${it.lastName}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Registro de Usuarios")
                },
                actions = {

                    TextButton(
                        onClick = {
                            if (firstName.isNotBlank() &&
                                lastName.isNotBlank()
                            ) {
                                val user = User(
                                    firstName = firstName.trim(),
                                    lastName = lastName.trim()
                                )

                                coroutineScope.launch {
                                    dao.insert(user)
                                    actualizarUsuarios()
                                    mensaje = "Usuario agregado"
                                    firstName = ""
                                    lastName = ""
                                }
                            } else {
                                mensaje = "Complete los campos"
                            }
                        }
                    ) {
                        Text("Agregar")
                    }

                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                actualizarUsuarios()
                                mensaje = "Lista actualizada"
                            }
                        }
                    ) {
                        Text("Listar")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val ultimo = dao.getLastUser()

                        if (ultimo != null) {
                            dao.deleteUserById(ultimo.uid)
                            actualizarUsuarios()
                            mensaje = "Último usuario eliminado"
                        } else {
                            mensaje = "No hay usuarios para eliminar"
                        }
                    }
                }
            ) {
                Text("Eliminar último usuario")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Usuarios registrados:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = dataUser,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
