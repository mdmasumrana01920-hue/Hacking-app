package com.example

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AcademyModulesScreen(
    onXPChange: (Int) -> Unit,
    showXPSnackbar: (String, Int) -> Unit
) {
    var activeLessonId by remember { mutableStateOf<String?>(null) }

    AnimatedContent(
        targetState = activeLessonId,
        transitionSpec = {
            slideInHorizontally { width -> if (targetState != null) width else -width } + fadeIn() togetherWith
                    slideOutHorizontally { width -> if (targetState != null) -width else width } + fadeOut()
        },
        label = "lesson_navigation"
    ) { targetLessonId ->
        if (targetLessonId == null) {
            LessonsListScreen(
                onLessonSelect = { activeLessonId = it }
            )
        } else {
            val lesson = CybersecurityData.lessons.first { it.id == targetLessonId }
            LessonDetailedView(
                lesson = lesson,
                onBack = { activeLessonId = null },
                onXPChange = onXPChange,
                showXPSnackbar = showXPSnackbar
            )
        }
    }
}

@Composable
fun LessonsListScreen(onLessonSelect: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
    ) {
        // Academy Title Header
        Text(
            text = "CYBER DETECTIVE ACADEMY",
            style = MaterialTheme.typography.titleMedium,
            color = CyberPrimary,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Learn theoretical exploits, read real-world forensic breaches, and solve interactive defensive code review challenges.",
            style = MaterialTheme.typography.bodySmall,
            color = CyberTextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(CybersecurityData.lessons) { lesson ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(12.dp))
                        .clickable { onLessonSelect(lesson.id) }
                        .testTag("academy_lesson_card_${lesson.id}"),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = lesson.category.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CyberSurfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = lesson.difficulty.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (lesson.difficulty) {
                                        "Beginner" -> CyberPrimary
                                        "Intermediate" -> CyberSecondary
                                        else -> CyberAccent
                                    },
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = lesson.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyberTextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = lesson.summary,
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberTextSecondary,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.List,
                                    contentDescription = null,
                                    tint = CyberTextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${lesson.durationMin} MIN READ",
                                    fontSize = 10.sp,
                                    color = CyberTextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "START TRAINING",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = CyberPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonDetailedView(
    lesson: AcademyLesson,
    onBack: () -> Unit,
    onXPChange: (Int) -> Unit,
    showXPSnackbar: (String, Int) -> Unit
) {
    // Find matching code challenge
    val relatedChallenge = CybersecurityData.codeChallenges.firstOrNull { it.id == "sc1" && lesson.id == "sqli" || it.id == "sc2" && lesson.id == "xss" }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var reviewSubmitted by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
    ) {
        item {
            // Navigation Back Link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(vertical = 8.dp)
                    .testTag("academy_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CyberPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "BACK TO ACADEMY",
                    color = CyberPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Topic Banner heading
            Text(
                text = lesson.category.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CyberSecondary,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = lesson.title,
                style = MaterialTheme.typography.headlineSmall,
                color = CyberTextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberSurfaceVariant)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = lesson.difficulty.uppercase(),
                        fontSize = 9.sp,
                        color = if (lesson.difficulty == "Beginner") CyberPrimary else CyberSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${lesson.durationMin} MIN READ WALKTHROUGH",
                    fontSize = 10.sp,
                    color = CyberTextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Body content parsing simple formatting markers
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(8.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "THEORY & MECHANOLOGY:",
                        fontSize = 11.sp,
                        color = CyberPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    lesson.content.split("\n\n").forEach { section ->
                        if (section.startsWith("###")) {
                            Text(
                                text = section.replace("###", "").trim().uppercase(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberSecondary,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                            )
                        } else if (section.startsWith("-") || section.startsWith("1.")) {
                            Text(
                                text = section,
                                fontSize = 13.sp,
                                color = CyberTextPrimary,
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
                            )
                        } else {
                            Text(
                                text = section,
                                fontSize = 13.sp,
                                color = CyberTextPrimary,
                                lineHeight = 19.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                }
            }

            // Real world forensic case study Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberCodeBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(1.dp, CyberAccent, RoundedCornerShape(8.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = CyberAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "FORENSIC CASE CASE:",
                            fontSize = 11.sp,
                            color = CyberAccent,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = lesson.realWorldBreach,
                        fontSize = 12.sp,
                        color = CyberTextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Active Interactive Spot-the-Vulnerability Panel
        if (relatedChallenge != null) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "DEFENSIVE AUDITING CHALLENGE",
                    style = MaterialTheme.typography.titleMedium,
                    color = CyberPrimary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "Review the vulnerability below. Select the correct secure coding pattern adjustment to fix it.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberTextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberSecondary, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "VULNERABLE SOURCE CODE (${relatedChallenge.language}):",
                            fontSize = 11.sp,
                            color = CyberAccent,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberCodeBackground)
                                .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(6.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = relatedChallenge.vulnerableCode,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = CyberTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "SELECT CODE SECURE RE-FACTOR:",
                            fontSize = 11.sp,
                            color = CyberTextSecondary,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Choice selections
                        relatedChallenge.optionsToFix.forEachIndexed { index, option ->
                            val isChecked = selectedOptionIndex == index
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isChecked) CyberSurfaceVariant else CyberCodeBackground)
                                    .border(
                                        width = 1.dp,
                                        color = if (isChecked) CyberSecondary else CyberSurfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable(enabled = !reviewSubmitted) { selectedOptionIndex = index }
                                    .padding(12.dp)
                                    .testTag("academy_audit_option_$index")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = isChecked,
                                        onClick = { if (!reviewSubmitted) selectedOptionIndex = index },
                                        colors = RadioButtonDefaults.colors(selectedColor = CyberSecondary, unselectedColor = CyberTextSecondary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = option,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = CyberTextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (!reviewSubmitted) {
                            Button(
                                onClick = {
                                    if (selectedOptionIndex == null) return@Button
                                    reviewSubmitted = true
                                    isCorrect = selectedOptionIndex == relatedChallenge.correctFixIndex
                                    if (isCorrect) {
                                        onXPChange(50)
                                        showXPSnackbar("Defensive code audit solved! Excellent analysis.", 50)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary, contentColor = Color.Black),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("academy_audit_submit_button")
                            ) {
                                Text("SUBMIT SECURITY AUDIT", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        } else {
                            // Review results summary module
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (isCorrect) CyberPrimary else CyberAccent
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isCorrect) "SECURE AUDIT APPROVED (+50 XP)" else "AUDIT DEFICIENCY DETECTED",
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (isCorrect) CyberPrimary else CyberAccent,
                                        fontSize = 12.sp
                                    )
                                }

                                Text(
                                    text = relatedChallenge.flawExplanation,
                                    fontSize = 12.sp,
                                    color = CyberTextPrimary,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                Text(
                                    text = "SECURED REFERENCE CODE IMPLEMENTATION:",
                                    fontSize = 11.sp,
                                    color = CyberPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyberCodeBackground)
                                        .border(1.dp, CyberPrimary, RoundedCornerShape(6.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = relatedChallenge.secureCode,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = CyberTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
