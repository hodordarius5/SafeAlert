package com.example.safealert.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
){
    val contact1 = remember {mutableStateOf("")} //nu pierde valoare la rerandare
    val contact2 = remember {mutableStateOf("")}
    val emergencyMessage = remember {mutableStateOf("")}

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setări") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("Înapoi")
                    }
                }
            )
        }
    ) { paddingValues ->

        //coloana noastră
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        )
        {
            OutlinedTextField(
                value = contact1.value,
                onValueChange = { contact1.value = it },
                label = { Text("Contact 1") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = contact2.value,
                onValueChange = { contact2.value = it },
                label = { Text("Contact 2") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = emergencyMessage.value,
                onValueChange = { emergencyMessage.value = it },
                label = { Text("Mesaj de urgență") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    //aici salvăm mai târziu
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Salvează")
            }

        }

    }
}