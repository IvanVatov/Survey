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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.survey.ApplicationState
import com.example.survey.R
import com.example.survey.ui.theme.Purple500
import com.example.survey.ui.theme.SurveyTheme


@Composable
fun StartScreen() {
    Scaffold(
        content = {
            var text by remember { mutableStateOf("") }
            Column(modifier = Modifier.fillMaxWidth()) {

                Text(
                    text = stringResource(id = R.string.start_description),
                    color = Purple500,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.padding(24.dp))

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(id = R.string.user_name)) }
                )

                Spacer(modifier = Modifier.padding(24.dp))

                OutlinedButton(
                    onClick = {
                        ApplicationState.startSurvey(text)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.start))
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
fun StartPreview() {
    SurveyTheme {
        HomeScreen()
    }
}