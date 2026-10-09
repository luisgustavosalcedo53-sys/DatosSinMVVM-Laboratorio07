
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Registro de Usuarios",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Nombre") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Apellido") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
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
                    }

                    firstName = ""
                    lastName = ""
                }
            }
        ) {
            Text("Agregar Usuario")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val users = dao.getAll()

                    dataUser = users.joinToString("\n") {
                        "${it.uid} - ${it.firstName} ${it.lastName}"
                    }
                }
            }
        ) {
            Text("Listar Usuarios")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = dataUser,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
