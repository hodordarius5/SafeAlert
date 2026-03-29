package com.example.safealert.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class) //asumare folosire functii experimentale (TopAppBar)

@Composable
fun HomeScreen(
    onSettingsClick: () -> Unit, //definire functii folosite in ecran, se leaga de AppNavGraph
    onSosClick: () -> Unit,
    onVoiceClick: () -> Unit
){
    //Scaffold = scheletul, layout de baza, pentru organizare
    Scaffold(topBar = { TopAppBar(title = {Text("SafeAlert")})})
    {
        paddingValues ->

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) //setari coloana
        { //conținut coloana
            //titlul
            Text(
                text = "Asistență de urgență",
                style = MaterialTheme.typography.headlineMedium
            )
            //spatiu
            Spacer(modifier = Modifier.height(32.dp))

            //buton SOS
            Button(
                onSosClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors()
            ){
                Text(
                    text = "S.O.S",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            //spatiu
            Spacer(modifier = Modifier.height(20.dp))

            //buton voce
            OutlinedButton(
                onVoiceClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp)
            ){
                Text(
                    text = "Comandă vocală"
                )
            }

            //spatiu
            Spacer(modifier = Modifier.height(20.dp))

            //buton setari
            OutlinedButton(
                onSettingsClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ){
                Text(
                    text = "Setări"
                )
            }

        }
    }
}
