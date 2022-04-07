package com.example.survey.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.survey.ApplicationState
import com.example.survey.R
import com.example.survey.ui.theme.SurveyTheme
import com.example.survey.ui.theme.progressIndicatorBackground


@Composable
fun SurveyScreen() {
    Scaffold(
        topBar = {
            val animatedProgress by animateFloatAsState(
                targetValue = (4 + 1) / 10.toFloat(),
                animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
            )
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                backgroundColor = MaterialTheme.colors.progressIndicatorBackground
            )
        },
        content = {
            var text by remember { mutableStateOf("") }
            Column(modifier = Modifier.fillMaxWidth()) {

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(id = R.string.email)) }
                )

                Spacer(modifier = Modifier.padding(24.dp))

                OutlinedButton(
                    onClick = {
                        ApplicationState.screenState.value = ApplicationState.Screen.HOME
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
fun SignInPreview() {
    SurveyTheme {
        SurveyScreen()
    }
}