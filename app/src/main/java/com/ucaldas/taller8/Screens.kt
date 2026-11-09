package com.ucaldas.taller8

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun RickAndMortyApp(viewModel: PersonajesViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "lista") {
        composable("lista") {
            ListaScreen(viewModel = viewModel, onPersonajeClick = { id ->
                navController.navigate("detalle/$id")
            })
        }
        composable("detalle/{id}") { backStackEntry ->
            val idString = backStackEntry.arguments?.getString("id")
            val id = idString?.toIntOrNull()
            if (id != null) {
                DetalleScreen(viewModel = viewModel, id = id)
            }
        }
    }
}

@Composable
fun ListaScreen(viewModel: PersonajesViewModel, onPersonajeClick: (Int) -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = viewModel::cargar, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Text("Recargar lista")
        }

        Button(
            onClick = viewModel::cargarSinManejoDeErrores,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Text("Probar sin manejo de errores")
        }

        when (val state = uiState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.items, key = { it.id }) { personaje ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onPersonajeClick(personaje.id) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Nombre: ${personaje.name}", style = MaterialTheme.typography.titleMedium)
                                Text(text = "Estado: ${personaje.status}")
                            }
                        }
                    }
                }
            }
            is UiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = viewModel::cargar) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}

@Composable
fun DetalleScreen(viewModel: PersonajesViewModel, id: Int) {
    val uiState by viewModel.detalleUiState.collectAsStateWithLifecycle()

    LaunchedEffect(id) {
        viewModel.cargarDetalle(id)
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        when (val state = uiState) {
            is DetalleUiState.Loading -> CircularProgressIndicator()
            is DetalleUiState.Success -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.personaje.name, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Estado: ${state.personaje.status}")
                    Text(text = "Especie: ${state.personaje.species}")
                }
            }
            is DetalleUiState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.cargarDetalle(id) }) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}
