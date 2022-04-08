package com.example.survey.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.survey.ApplicationState
import com.example.survey.Configuration
import com.example.survey.R
import com.example.survey.api.httpClient
import com.example.survey.model.Survey
import com.example.survey.ui.theme.Purple200
import com.example.survey.ui.theme.Purple500
import com.example.survey.ui.theme.SurveyTheme
import io.ktor.client.features.*
import io.ktor.client.features.get
import io.ktor.client.request.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Composable
fun HomeScreen() {
    Scaffold(
        content = {
            var text by remember { mutableStateOf("") }
            Column(modifier = Modifier.fillMaxWidth()) {

                Text(
                    text = stringResource(id = R.string.home_label),
                    color = Purple200,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.padding(24.dp))

                Text(
                    text = stringResource(id = R.string.home_description),
                    color = Purple500,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.padding(24.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(id = R.string.survey_id)) }
                )

                Spacer(modifier = Modifier.padding(24.dp))

                OutlinedButton(
                    onClick = {
                        val textToInt = text.toInt()
                        if (textToInt > 0)
                            ApplicationState.loadSurvey(textToInt)

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.load))
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    )

}

@Preview
@Composable
fun HomePreview() {
    SurveyTheme {
        HomeScreen()
    }
}