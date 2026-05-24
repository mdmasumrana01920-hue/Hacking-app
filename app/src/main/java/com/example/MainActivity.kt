package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainDashboardScreen()
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainDashboardScreen() {
  var xpPoints by remember { mutableStateOf(0) }
  var hasSignedOath by remember { mutableStateOf(false) }
  var currentTab by remember { mutableStateOf("academy") } // Start on Academy to introduce concepts

  val snackbarHostState = remember { SnackbarHostState() }
  val coroutineScope = rememberCoroutineScope()

  // Dynamic user ranking level thresholds
  val currentRank = when {
    xpPoints >= 300 -> "Cyber Commander"
    xpPoints >= 180 -> "Penetration Tester"
    xpPoints >= 80 -> "SecOps Investigator"
    else -> "Apprentice Analyst"
  }

  val rankColor = when {
    xpPoints >= 300 -> CyberAccent
    xpPoints >= 180 -> CyberSecondary
    xpPoints >= 80 -> CyberPrimary
    else -> CyberTextSecondary
  }

  // Next level progress
  val nextLevelTarget = when {
    xpPoints >= 300 -> 500
    xpPoints >= 180 -> 300
    xpPoints >= 80 -> 180
    else -> 80
  }
  val previousLevelThreshold = when {
    xpPoints >= 300 -> 300
    xpPoints >= 180 -> 180
    xpPoints >= 80 -> 80
    else -> 0
  }
  val progressPercentage = ((xpPoints - previousLevelThreshold).toFloat() / (nextLevelTarget - previousLevelThreshold).toFloat()).coerceIn(0f, 1f)

  // Floating score trigger alerts
  val showXPSnackbar: (String, Int) -> Unit = { message, points ->
    coroutineScope.launch {
      snackbarHostState.showSnackbar(
        message = "+$points XP: $message",
        actionLabel = "ACKNOWLEDGE",
        duration = SnackbarDuration.Short
      )
    }
  }

  val addXPPoints: (Int) -> Unit = { count ->
    xpPoints += count
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = CyberBackground,
    snackbarHost = {
      SnackbarHost(hostState = snackbarHostState) { data ->
        Snackbar(
          snackbarData = data,
          containerColor = CyberSurface,
          contentColor = CyberPrimary,
          actionColor = CyberSecondary,
          modifier = Modifier
            .padding(12.dp)
            .border(1.dp, CyberPrimary, RoundedCornerShape(8.dp))
        )
      }
    },
    bottomBar = {
      NavigationBar(
        containerColor = CyberSurface,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars,
        modifier = Modifier
          .border(1.dp, CyberSurfaceVariant)
          .testTag("main_navigation_bar")
      ) {
        NavigationBarItem(
          selected = currentTab == "academy",
          onClick = { currentTab = "academy" },
          label = { Text("Academy", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Academy") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CyberPrimary,
            unselectedIconColor = CyberTextSecondary,
            selectedTextColor = CyberPrimary,
            unselectedTextColor = CyberTextSecondary,
            indicatorColor = CyberSurfaceVariant
          ),
          modifier = Modifier.testTag("nav_academy_tab")
        )
        NavigationBarItem(
          selected = currentTab == "labs",
          onClick = { currentTab = "labs" },
          label = { Text("Labs", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Default.Build, contentDescription = "Labs") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CyberPrimary,
            unselectedIconColor = CyberTextSecondary,
            selectedTextColor = CyberPrimary,
            unselectedTextColor = CyberTextSecondary,
            indicatorColor = CyberSurfaceVariant
          ),
          modifier = Modifier.testTag("nav_labs_tab")
        )
        NavigationBarItem(
          selected = currentTab == "campaigns",
          onClick = { currentTab = "campaigns" },
          label = { Text("Campaign", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Campaign") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CyberPrimary,
            unselectedIconColor = CyberTextSecondary,
            selectedTextColor = CyberPrimary,
            unselectedTextColor = CyberTextSecondary,
            indicatorColor = CyberSurfaceVariant
          ),
          modifier = Modifier.testTag("nav_campaign_tab")
        )
        NavigationBarItem(
          selected = currentTab == "quiz",
          onClick = { currentTab = "quiz" },
          label = { Text("Quiz", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Quiz") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CyberPrimary,
            unselectedIconColor = CyberTextSecondary,
            selectedTextColor = CyberPrimary,
            unselectedTextColor = CyberTextSecondary,
            indicatorColor = CyberSurfaceVariant
          ),
          modifier = Modifier.testTag("nav_quiz_tab")
        )
        NavigationBarItem(
          selected = currentTab == "laws",
          onClick = { currentTab = "laws" },
          label = { Text("Charter", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          icon = { Icon(Icons.Default.Lock, contentDescription = "Charter") },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CyberPrimary,
            unselectedIconColor = CyberTextSecondary,
            selectedTextColor = CyberPrimary,
            unselectedTextColor = CyberTextSecondary,
            indicatorColor = CyberSurfaceVariant
          ),
          modifier = Modifier.testTag("nav_laws_tab")
        )
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Dynamic Cyber Operator Stats Banner
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
          .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberSurface)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column {
              Text(
                text = "OPERATOR RANK PROFILE:",
                fontSize = 10.sp,
                color = CyberTextSecondary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = currentRank.uppercase(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = rankColor,
                fontFamily = FontFamily.Monospace
              )
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(CyberCodeBackground)
                .border(1.dp, CyberPrimary, RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = null,
                  tint = CyberPrimary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "$xpPoints XP",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = CyberPrimary,
                  fontFamily = FontFamily.Monospace
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Level saturating linear indicators
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            LinearProgressIndicator(
              progress = progressPercentage,
              color = CyberPrimary,
              trackColor = CyberCodeBackground,
              modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "$xpPoints/$nextLevelTarget",
              fontSize = 10.sp,
              color = CyberTextSecondary,
              fontFamily = FontFamily.Monospace
            )
          }

          if (!hasSignedOath) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(CyberRedGlow)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = CyberAccent,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Ethical Oath Unsigned (Check Legal Charter tab to solve rules)",
                color = CyberAccent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }

      // Display selected active layout content with fading animation transitions
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
      ) {
        AnimatedContent(
          targetState = currentTab,
          transitionSpec = {
            fadeIn() togetherWith fadeOut()
          },
          label = "tab_navigation"
        ) { targetTab ->
          when (targetTab) {
            "academy" -> AcademyModulesScreen(
              onXPChange = addXPPoints,
              showXPSnackbar = showXPSnackbar
            )
            "labs" -> LabSimulatorScreen(
              onXPChange = addXPPoints,
              showXPSnackbar = showXPSnackbar
            )
            "campaigns" -> EthicalChallengesScreen(
              onXPChange = addXPPoints,
              showXPSnackbar = showXPSnackbar
            )
            "quiz" -> QuizArenaScreen(
              onXPChange = addXPPoints,
              showXPSnackbar = showXPSnackbar
            )
            "laws" -> EthicalCodeScreen(
              hasSignedOath = hasSignedOath,
              onSignOath = {
                hasSignedOath = it
                if (it) {
                  addXPPoints(30)
                  showXPSnackbar("Signed white hat commitment oath! System authorized.", 30)
                }
              }
            )
          }
        }
      }
    }
  }
}
