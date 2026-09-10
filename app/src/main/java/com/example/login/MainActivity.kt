package com.example.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.login.ui.theme.LoginTheme

class MainActivity : ComponentActivity() {
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHelper(this)

        setContent {
            LoginTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigator(db)
                }
            }
        }
    }
}

@Composable
fun AppNavigator(db: DatabaseHelper) {
    var pantallaActual by remember { mutableStateOf("login") }
    var userLogueado by remember { mutableStateOf("") }

    when (pantallaActual) {
        "login" -> LoginView(
            db = db,
            onSuccess = { u ->
                userLogueado = u
                pantallaActual = "welcome"
            },
            goToReg = { pantallaActual = "register" }
        )
        "register" -> RegisterView(
            db = db,
            onRegOk = { pantallaActual = "login" },
            goBack = { pantallaActual = "login" }
        )
        "welcome" -> WelcomeView(
            user = userLogueado,
            onSalir = { pantallaActual = "login" }
        )
    }
}

@Composable
fun LoginView(db: DatabaseHelper, onSuccess: (String) -> Unit, goToReg: () -> Unit) {
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pasanaku App", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Control de rondas y aportes", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = user,
            onValueChange = { user = it },
            label = { Text("Usuario / Participante") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMsg.isNotBlank()) {
            Text(text = errorMsg, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (user.isBlank() || pass.isBlank()) {
                    errorMsg = "Llena todos los campos"
                } else {
                    if (db.login(user, pass)) {
                        onSuccess(user)
                    } else {
                        errorMsg = "Credenciales incorrectas"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Ingresar al Sistema")
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = goToReg) {
            Text("¿Nuevo socio? Regístrate aquí")
        }
    }
}

@Composable
fun RegisterView(db: DatabaseHelper, onRegOk: () -> Unit, goBack: () -> Unit) {
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registro de Socio", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = user,
            onValueChange = { user = it },
            label = { Text("Nombre de Usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (msg.isNotBlank()) {
            Text(text = msg, color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (user.isBlank() || pass.isBlank()) {
                    msg = "Faltan datos"
                } else if (db.existeUsuario(user)) {
                    msg = "El usuario ya existe"
                } else {
                    if (db.addUser(user, pass)) {
                        onRegOk()
                    } else {
                        msg = "No se pudo registrar"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Registrar Socio")
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = goBack) {
            Text("Volver al Login")
        }
    }
}

@Composable
fun WelcomeView(user: String, onSalir: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "¡Hola, $user!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Panel de control del Pasanaku", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "💰 Estado de Cuotas: Al día", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "📅 Próximo turno de cobro: 15 de Octubre", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "👥 Grupo activo: 10 Participantes", fontSize = 14.sp)

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onSalir,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar Sesión")
                }
            }
        }
    }
}