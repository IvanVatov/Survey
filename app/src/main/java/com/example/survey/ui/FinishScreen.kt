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
import com.example.survey.ApplicationState
import com.example.survey.R
import com.example.survey.ui.theme.SurveyTheme


@Composable
fun FinishScreen() {
    Scaffold(
        content = {
            Column(modifier = Modifier.fillMaxWidth()) {


                Spacer(modifier = Modifier.padding(24.dp))

                OutlinedButton(
                    onClick = {
                        ApplicationState.sendResult()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.send_result))
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
fun FinishPreview() {
    SurveyTheme {
        FinishScreen()
    }
}