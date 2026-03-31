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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.safealert.data.PreferencesManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Switch
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
){
    val contact1 = remember {mutableStateOf("")} //nu pierde valoare la rerandare
    val contact2 = remember {mutableStateOf("")}
    val emergencyMessage = remember {mutableStateOf("")}
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    val inactivityEnabled = remember { mutableStateOf(false) }
    val selectedMinutes = remember { mutableStateOf(10) }
    val lowBatteryEnabled = remember { mutableStateOf(false) }
    val weatherAlertsEnabled = remember { mutableStateOf(false) }
    val weatherSmsEnabled = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { //popularea datelor existente in ecranul de setari
        contact1.value = prefs.getContact1()
        contact2.value = prefs.getContact2()
        emergencyMessage.value = prefs.getMessage()
        inactivityEnabled.value = prefs.isInactivityEnabled()
        selectedMinutes.value = prefs.getInactivityMinutes()
        lowBatteryEnabled.value = prefs.isLowBatteryEnabled()
        weatherAlertsEnabled.value = prefs.isWeatherAlertsEnabled()
        weatherSmsEnabled.value = prefs.isWeatherSmsEnabled()
    }

    val scrollState = rememberScrollState() //pt scroll

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
                .verticalScroll(scrollState)
        )
        {
            //camp contact 1
            OutlinedTextField(
                value = contact1.value,
                onValueChange = { contact1.value = it },
                label = { Text("Contact 1") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            //camp contact 2
            OutlinedTextField(
                value = contact2.value,
                onValueChange = { contact2.value = it },
                label = { Text("Contact 2") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            //camp pentru mesaj de urgenta
            OutlinedTextField(
                value = emergencyMessage.value,
                onValueChange = { emergencyMessage.value = it },
                label = { Text("Mesaj de urgență") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))


            //toggle pt feature inactivitate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SOS la inactivitate")

                Switch(
                    checked = inactivityEnabled.value,
                    onCheckedChange = {
                        inactivityEnabled.value = it
                        prefs.setInactivityEnabled(it)
                    }
                )
            }

            //spatiu
            Spacer(modifier = Modifier.height(16.dp))

            //zona de activare si setare inactivitate
            Text(
                text = if (inactivityEnabled.value)
                    "Feature activ"
                else
                    "Feature inactiv"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Alege timpul de inactivitate:")

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(10, 15, 20, 30).forEach { minute ->
                    OutlinedButton(
                        onClick = {
                            selectedMinutes.value = minute
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "$minute",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Selectat: ${selectedMinutes.value} minute")

            Spacer(modifier = Modifier.height(8.dp))

            //zona feature baterie

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Alertă baterie scăzută")

                Switch(
                    checked = lowBatteryEnabled.value,
                    onCheckedChange = {
                        lowBatteryEnabled.value = it
                        prefs.setLowBatteryEnabled(it)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("La 10% se trimite alertă de baterie scăzută.")
            Text("La 5% se trimite locația finală.")

            //zona meteo
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Alerte meteo")

                Switch(
                    checked = weatherAlertsEnabled.value,
                    onCheckedChange = {
                        weatherAlertsEnabled.value = it
                        prefs.setWeatherAlertsEnabled(it)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SMS la alertă severă")

                Switch(
                    checked = weatherSmsEnabled.value,
                    onCheckedChange = {
                        weatherSmsEnabled.value = it
                        prefs.setWeatherSmsEnabled(it)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Verificare meteo la fiecare 15 minute.")
            Text("Trimite SMS doar dacă alerta este severă.")

            Spacer(modifier = Modifier.height(20.dp))

            //buton pentru salvare
            Button(
                onClick = {
                    //salvarea datelor
                    prefs.saveContacts(
                        contact1.value,
                        contact2.value,
                        emergencyMessage.value
                    )

                    prefs.setInactivityEnabled(inactivityEnabled.value)
                    prefs.setInactivityMinutes(selectedMinutes.value)

                    prefs.setLowBatteryEnabled(lowBatteryEnabled.value)

                    prefs.setWeatherAlertsEnabled(weatherAlertsEnabled.value)
                    prefs.setWeatherSmsEnabled(weatherSmsEnabled.value)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Salvează")
            }

            Spacer(modifier = Modifier.height(40.dp))

        }

    }
}