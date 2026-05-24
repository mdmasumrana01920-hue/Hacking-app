package com.example

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun QuizArenaScreen(
    onXPChange: (Int) -> Unit,
    showXPSnackbar: (String, Int) -> Unit
) {
    var activeQuestionIndex by remember { mutableStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var completedQuiz by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var showsResultReview by remember { mutableStateOf(false) }
    var isAnswerCorrect by remember { mutableStateOf(false) }

    val activeQuestion = CybersecurityData.quizzes[activeQuestionIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Quiz Arena Banner
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = CyberPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CYBER QUIZ ARENA",
                style = MaterialTheme.typography.titleMedium,
                color = CyberPrimary,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f)
            )

            if (!completedQuiz) {
                Text(
                    text = "Q: ${activeQuestionIndex + 1}/${CybersecurityData.quizzes.size}",
                    color = CyberTextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        HorizontalDivider(color = CyberSurfaceVariant, modifier = Modifier.padding(bottom = 16.dp))

        if (!completedQuiz) {
            // Display active quiz box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "QUESTION IN ${activeQuestion.category.uppercase()}:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberSecondary,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = activeQuestion.questionText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    // Options list
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        activeQuestion.options.forEachIndexed { idx, option ->
                            val isSelected = selectedOptionIndex == idx
                            val normalBgColor = CyberCodeBackground
                            val selectedBgColor = CyberSurfaceVariant
                            
                            val isCorrectAnswerIndex = idx == activeQuestion.correctIndex
                            
                            val boxBorderColor = when {
                                showsResultReview && isCorrectAnswerIndex -> CyberPrimary
                                showsResultReview && isSelected && !isAnswerCorrect -> CyberAccent
                                isSelected -> CyberSecondary
                                else -> CyberSurfaceVariant
                            }
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) selectedBgColor else normalBgColor)
                                    .border(width = 1.dp, color = boxBorderColor, shape = RoundedCornerShape(8.dp))
                                    .clickable(enabled = !showsResultReview) { selectedOptionIndex = idx }
                                    .padding(14.dp)
                                    .testTag("quiz_option_$idx")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { if (!showsResultReview) selectedOptionIndex = idx },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = CyberSecondary,
                                            unselectedColor = CyberTextSecondary
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = option,
                                        color = CyberTextPrimary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }

                    // Result validation panel
                    if (showsResultReview) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CyberCodeBackground),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .border(1.dp, if (isAnswerCorrect) CyberPrimary else CyberAccent, RoundedCornerShape(8.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isAnswerCorrect) Icons.Default.CheckCircle else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (isAnswerCorrect) CyberPrimary else CyberAccent
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAnswerCorrect) "ANSWER SECURED (+50 XP)" else "VULNERABLE CHOICE SELECTED",
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (isAnswerCorrect) CyberPrimary else CyberAccent,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = activeQuestion.explanation,
                                    fontSize = 11.sp,
                                    color = CyberTextPrimary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action buttons
                    if (!showsResultReview) {
                        Button(
                            onClick = {
                                if (selectedOptionIndex == null) return@Button
                                showsResultReview = true
                                isAnswerCorrect = selectedOptionIndex == activeQuestion.correctIndex
                                if (isAnswerCorrect) {
                                    score++
                                    onXPChange(50)
                                    showXPSnackbar("Correct! Security concept fully aligned.", 50)
                                }
                            },
                            enabled = selectedOptionIndex != null,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary, contentColor = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quiz_submit_button")
                        ) {
                            Text("SUBMIT AUDIT ANSWER", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                showsResultReview = false
                                selectedOptionIndex = null
                                if (activeQuestionIndex + 1 < CybersecurityData.quizzes.size) {
                                    activeQuestionIndex++
                                } else {
                                    completedQuiz = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary, contentColor = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quiz_next_button")
                        ) {
                            Text(
                                text = if (activeQuestionIndex + 1 < CybersecurityData.quizzes.size) "NEXT CHALLENGE" else "FINISH CLINICAL ASSESSMENT",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // Displays final quiz results score screen
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, CyberPrimary, RoundedCornerShape(12.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(36.dp))
                            .background(CyberGreenGlow)
                            .border(2.dp, CyberPrimary, RoundedCornerShape(36.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = CyberPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "ASSESSMENT CONCLUDED",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Score achieved: $score out of ${CybersecurityData.quizzes.size} correct answers",
                        color = CyberTextSecondary,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Final performance rating feedback
                    val rankEarned = when (score) {
                        CybersecurityData.quizzes.size -> "Hacker Commandant"
                        in 3..4 -> "Security Researcher"
                        else -> "Apprentice Analyst"
                    }

                    Text(
                        text = "CREDITED BADGE RANK:",
                        fontSize = 10.sp,
                        color = CyberTextSecondary,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        text = rankEarned.uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (score == CybersecurityData.quizzes.size) CyberPrimary else CyberSecondary,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            activeQuestionIndex = 0
                            selectedOptionIndex = null
                            completedQuiz = false
                            score = 0
                            showsResultReview = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary, contentColor = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quiz_retry_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RETRY ASSESSMENT MATRIX", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
