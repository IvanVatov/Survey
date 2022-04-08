package com.example.survey.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.survey.ApplicationState
import com.example.survey.R
import com.example.survey.model.Question
import com.example.survey.ui.theme.SurveyTheme
import com.example.survey.ui.theme.progressIndicatorBackground


@Composable
fun SurveyScreen() {

    var currentQuestion by remember {
        ApplicationState.currentQuestion
    }

    val question = currentQuestion ?: throw NullPointerException("Current question is null")

    Scaffold(
        topBar = {
            var currentIndex by remember { ApplicationState.currentQuestionIndex }

            val animatedProgress by animateFloatAsState(
                targetValue = currentIndex / (ApplicationState.survey.value?.questions?.size
                    ?: 0 - 1).toFloat(),
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
        content = { paddingValues ->
            QuestionContent(
                question = question, modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        },
        bottomBar = { SurveyBottomBar() },

        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    )

}


@Composable
private fun QuestionContent(
    question: Question,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))
            QuestionTitle(question.question)
            Spacer(modifier = Modifier.height(24.dp))
            when (question.isSingle) {
                true -> SingleChoiceQuestion(
                    question = question,
                    modifier = Modifier.fillParentMaxWidth()
                )
                false -> MultipleChoiceQuestion(
                    question = question,
                    modifier = Modifier.fillParentMaxWidth()
                )
            }
        }
    }
}


@Composable
private fun SingleChoiceQuestion(
    question: Question,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        var checkedState by remember { ApplicationState.questionAnswers }

        question.answers.forEach { answer ->

            val onClickHandle = {
                ApplicationState.questionAnswers.value = listOf(answer.id)
            }

            val checked = checkedState.contains(answer.id)

            val answerBorderColor = if (checked) {
                MaterialTheme.colors.primary.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
            }
            val answerBackgroundColor = if (checked) {
                MaterialTheme.colors.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colors.background
            }
            Surface(
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = 1.dp,
                    color = answerBorderColor
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = checked,
                            onClick = onClickHandle
                        )
                        .background(answerBackgroundColor)
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = answer.answer
                    )

                    RadioButton(
                        selected = checked,
                        onClick = onClickHandle,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colors.primary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun MultipleChoiceQuestion(
    question: Question,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        var checkedState by remember { ApplicationState.questionAnswers }

        for (answer in question.answers) {

            var checked = checkedState.contains(answer.id)

            val answerBorderColor = if (checked) {
                MaterialTheme.colors.primary.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
            }
            val answerBackgroundColor = if (checked) {
                MaterialTheme.colors.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colors.background
            }
            Surface(
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = 1.dp,
                    color = answerBorderColor
                ),
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(answerBackgroundColor)
                        .clickable(
                            onClick = {
                                checked = !checked
                                check(checked, answer.id)
                            }
                        )
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = answer.answer)

                    Checkbox(
                        checked = checked,
                        onCheckedChange = { selected ->
                            checked = selected
                            check(checked, answer.id)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colors.primary
                        )
                    )
                }
            }
        }
    }
}

private fun check(checked: Boolean, id: Int) {
    val list = ApplicationState.questionAnswers.value.toMutableList()

    if (checked)
        list.add(id)
    else
        list.remove(id)

    ApplicationState.questionAnswers.value = list
}

@Composable
fun SurveyBottomBar() {
    Surface(
        elevation = 7.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            if (ApplicationState.currentQuestionIndex.value > 0) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = {
                        ApplicationState.previewsQuestion()
                    }
                ) {
                    Text(text = stringResource(id = R.string.previous))
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                onClick = {
                    ApplicationState.nextQuestion()
                },
                enabled = ApplicationState.nextEnabled()
            ) {
                Text(text = stringResource(id = R.string.next))
            }

        }
    }
}


@Composable
private fun QuestionTitle(title: String) {
    val backgroundColor = if (MaterialTheme.colors.isLight) {
        MaterialTheme.colors.onSurface.copy(alpha = 0.04f)
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.06f)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.small
            )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp)
        )
    }
}

@Preview
@Composable
fun SignInPreview() {
    SurveyTheme {
        SurveyScreen()
    }
}