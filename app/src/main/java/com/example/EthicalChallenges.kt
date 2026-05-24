package com.example

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Structures
data class HackingStep(
    val phase: String, // "Reconnaissance", "Scanning", "Exploitation", "Post-Exploitation"
    val scenario: String,
    val objectives: List<String>,
    val hints: List<String>,
    val targetCommand: String, // The command that solves the step
    val successLogs: List<String>, // Output on success
    val feedbackMessage: String,
    val initialTerminalOutput: String
)

data class HackingMission(
    val id: String,
    val title: String,
    val description: String,
    val difficulty: String, // "Easy", "Medium", "Hard"
    val xpReward: Int,
    val steps: List<HackingStep>
)


@Composable
fun EthicalChallengesScreen(
    onXPChange: (Int) -> Unit,
    showXPSnackbar: (String, Int) -> Unit
) {
    // Generate simulated missions
    val missions = remember { getMissions() }
    
    var selectedMissionId by remember { mutableStateOf<String?>(null) }
    
    AnimatedContent(
        targetState = selectedMissionId,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "mission_navigation"
    ) { missionId ->
        if (missionId == null) {
            MissionSelectionGrid(
                missions = missions,
                onSelectMission = { selectedMissionId = it }
            )
        } else {
            val mission = missions.first { it.id == missionId }
            ActiveCampaignArena(
                mission = mission,
                onBack = { selectedMissionId = null },
                onXPChange = onXPChange,
                showXPSnackbar = showXPSnackbar
            )
        }
    }
}

@Composable
fun MissionSelectionGrid(
    missions: List<HackingMission>,
    onSelectMission: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "ETHICAL HACKING PATHWAYS",
            style = MaterialTheme.typography.titleMedium,
            color = CyberPrimary,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        
        Text(
            text = "Master the core penetration testing lifecycle through interactive CTF lab scenarios.",
            fontSize = 12.sp,
            color = CyberTextSecondary,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        missions.forEach { mission ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(12.dp))
                    .clickable { onSelectMission(mission.id) }
                    .testTag("mission_card_${mission.id}"),
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mission.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                        
                        // Difficulty Badge
                        val badgeBg = when (mission.difficulty) {
                            "Easy" -> Color(0x2000FFCC)
                            "Medium" -> Color(0x20FFCC00)
                            else -> Color(0x20FF3D00)
                        }
                        val badgeColor = when (mission.difficulty) {
                            "Easy" -> CyberPrimary
                            "Medium" -> CyberSecondary
                            else -> CyberAccent
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeBg)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = mission.difficulty.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = mission.description,
                        fontSize = 12.sp,
                        color = CyberTextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = CyberAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+${mission.xpReward} XP Reward",
                                fontSize = 11.sp,
                                color = CyberAccent,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "START LAB",
                                fontSize = 11.sp,
                                color = CyberPrimary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = CyberPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveCampaignArena(
    mission: HackingMission,
    onBack: () -> Unit,
    onXPChange: (Int) -> Unit,
    showXPSnackbar: (String, Int) -> Unit
) {
    var currentStepIdx by remember { mutableStateOf(0) }
    val step = mission.steps[currentStepIdx]
    
    var terminalText by remember { mutableStateOf("") }
    var currentConsoleOutput by remember { mutableStateOf<List<String>>(listOf(step.initialTerminalOutput)) }
    var stepSolved by remember { mutableStateOf(false) }
    var hintsExpanded by remember { mutableStateOf(false) }
    var isCommandRunning by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    
    // Command triggers
    val executeTerminalCommand = { cmd: String ->
        if (!isCommandRunning && cmd.isNotBlank()) {
            isCommandRunning = true
            currentConsoleOutput = currentConsoleOutput + "root@ethical-lab:~# $cmd"
            
            scope.launch {
                delay(800) // simulated loading/probing scan latency
                
                val lowerCmd = cmd.trim().replace("\\s+".toRegex(), " ")
                val solvedCleanCommand = step.targetCommand.trim()
                
                if (lowerCmd.equals(solvedCleanCommand, ignoreCase = true) || 
                    (solvedCleanCommand == "helper" && lowerCmd.startsWith("exploit")) ||
                    lowerCmd.replace(" ", "") == solvedCleanCommand.replace(" ", "")
                ) {
                    currentConsoleOutput = currentConsoleOutput + step.successLogs
                    stepSolved = true
                    showXPSnackbar("Completed ${step.phase} Phase Strategy!", 40)
                    onXPChange(40)
                } else {
                    currentConsoleOutput = currentConsoleOutput + listOf(
                        "[-] command execution completed.",
                        "[-] Warning: Output did not yield matching criteria flag.",
                        "[-] Type 'help' or review mission objectives and hints below."
                    )
                }
                isCommandRunning = false
                terminalText = ""
            }
        }
    }
    
    // Reset inputs on step change
    LaunchedEffect(currentStepIdx) {
        terminalText = ""
        currentConsoleOutput = listOf(mission.steps[currentStepIdx].initialTerminalOutput)
        stepSolved = false
        hintsExpanded = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
    ) {
        // Arena Top Banner Nav
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(36.dp)
                    .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(8.dp))
                    .testTag("campaign_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back map",
                    tint = CyberPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = mission.title.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTextPrimary,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Live Interactive Simulation Portal",
                    fontSize = 10.sp,
                    color = CyberTextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        
        // Phase Track Progression Indicator Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            mission.steps.forEachIndexed { idx, item ->
                val isCompleted = idx < currentStepIdx
                val isCurrent = idx == currentStepIdx
                val barColor = when {
                    isCompleted -> CyberPrimary
                    isCurrent -> CyberSecondary
                    else -> CyberSurfaceVariant
                }
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(barColor)
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.phase.uppercase().split("-")[0].take(5),
                        color = if (isCurrent || isCompleted) Color.Black else CyberTextSecondary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
        
        // Main split layout: Terminal on top, Details/Instructions at bottom
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f)
                .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Console bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurface)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).background(Color.Red, RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.size(8.dp).background(Color.Yellow, RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.size(8.dp).background(Color.Green, RoundedCornerShape(4.dp)))
                    
                    Text(
                        text = " root@ethical-lab: ~ (${step.phase.lowercase()})",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = CyberTextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (isCommandRunning) {
                        CircularProgressIndicator(
                            color = CyberSecondary,
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 1.5.dp
                        )
                    }
                }
                
                // Terminal Display Logs output feed
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        reverseLayout = true
                    ) {
                        items(currentConsoleOutput.reversed()) { log ->
                            val txtClr = when {
                                log.startsWith("root@ethical-lab") -> CyberPrimary
                                log.contains("[+]") || log.contains("SUCCESS") -> CyberPrimary
                                log.contains("[-]") || log.contains("FAILED") -> CyberAccent
                                log.contains("[i]") || log.contains("INFO") -> CyberSecondary
                                else -> CyberTextSecondary
                            }
                            Text(
                                text = log,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = txtClr,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                    }
                }
                
                // Interactive command builder shortcut chips (prevents tiresome keyboard typing on mobile!)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberSurface)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickCmds = when (step.phase) {
                        "Reconnaissance" -> listOf("dnsrecon megacorp.com", "whois megacorp.com", "subfinder -d megacorp.com")
                        "Scanning" -> listOf("nmap -sV -p 8080 10.10.245.5", "nmap -A -p 22,80 10.10.10.5", "netdiscover -r 10.10.0.0/24")
                        "Exploitation" -> listOf("exploit -m tomcat_mgr -t 10.10.245.5", "python3 exploit.py 10.10.10.5 admin", "msfconsole -q")
                        "Post-Exploitation" -> listOf("sudo -l", "cat /root/flag.txt", "find / -perm -4000 2>/dev/null")
                        else -> listOf("help")
                    }
                    
                    quickCmds.forEach { cmd ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberCodeBackground)
                                .border(1.dp, CyberSecondary, RoundedCornerShape(6.dp))
                                .clickable { terminalText = cmd }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = cmd,
                                color = CyberSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                // Active interactive inputs line
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberCodeBackground)
                        .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "pt@ethical-hub:~$ ",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = CyberPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    
                    TextField(
                        value = terminalText,
                        onValueChange = { terminalText = it },
                        placeholder = { Text("Enter terminal command...", color = CyberTextSecondary, fontSize = 11.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = {
                            focusManager.clearFocus()
                            executeTerminalCommand(terminalText)
                        }),
                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("campaign_terminal_input")
                    )
                    
                    IconButton(
                        onClick = { 
                            focusManager.clearFocus()
                            executeTerminalCommand(terminalText) 
                        },
                        enabled = terminalText.isNotBlank() && !isCommandRunning
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Run",
                            tint = if (terminalText.isNotBlank()) CyberPrimary else CyberTextSecondary
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Scenario Instructions & Objectives Panel
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
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header details
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x1500FFCC))
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = CyberPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${step.phase.uppercase()} MISSION BRIEFING",
                        color = CyberPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = step.scenario,
                    fontSize = 11.sp,
                    color = CyberTextPrimary,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Objectives Checklist
                Text(
                    text = "TARGET OBJECTIVES:",
                    fontSize = 11.sp,
                    color = CyberSecondary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                
                step.objectives.forEach { obj ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (stepSolved) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                            contentDescription = "task status",
                            tint = if (stepSolved) CyberPrimary else CyberTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = obj,
                            fontSize = 11.sp,
                            color = if (stepSolved) CyberPrimary else CyberTextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Expandable Hints Drawer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCodeBackground)
                        .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(8.dp))
                        .clickable { hintsExpanded = !hintsExpanded }
                        .padding(8.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💡 NEED A HINT?",
                                fontSize = 11.sp,
                                color = CyberAccent,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Icon(
                                imageVector = if (hintsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand hints",
                                tint = CyberAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        
                        AnimatedVisibility(visible = hintsExpanded) {
                            Column(modifier = Modifier.padding(top = 6.dp)) {
                                step.hints.forEachIndexed { hIdx, hint ->
                                    Text(
                                        text = "${hIdx + 1}. $hint",
                                        fontSize = 11.sp,
                                        color = CyberTextSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 15.sp,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Actions/Navigation triggers for success step
                if (stepSolved) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (currentStepIdx < mission.steps.size - 1) {
                                currentStepIdx++
                            } else {
                                onBack()
                                showXPSnackbar("Completed ethical pentesting pathway mission successfully!", 100)
                                onXPChange(100)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary, contentColor = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("campaign_next_step_btn")
                    ) {
                        Text(
                            text = if (currentStepIdx < mission.steps.size - 1) "PROCEED TO NEXT PHASE" else "COMPLETE PATHWAY CHALLENGE",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// Data generator helper
fun getMissions(): List<HackingMission> {
    return listOf(
        HackingMission(
            id = "megacorp",
            title = "Mission 1: Corporate Network",
            description = "Simulate a standardized penetration test mapping security loopholes in MegaCorp's primary network architecture.",
            difficulty = "Easy",
            xpReward = 150,
            steps = listOf(
                HackingStep(
                    phase = "Reconnaissance",
                    scenario = "Your objective is to passive scan MegaCorp's external domain boundaries. Discover the name server record parameters to complete reconnaissance intelligence mapping.",
                    objectives = listOf("Run the passive domain scan query: 'dnsrecon megacorp.com'"),
                    hints = listOf(
                        "Review available shortcuts in the quick tools build bar above the keyboard line.",
                        "Tap the chip 'dnsrecon megacorp.com' and execute it to compile OSINT metadata ranges!"
                    ),
                    targetCommand = "dnsrecon megacorp.com",
                    successLogs = listOf(
                        "[+] OSINT Scanning domain: megacorp.com",
                        "[+] Performing Zone Transfer queries... SUCCESS!",
                        "[+] Name Server match: ns1.megacorp.com [IP: 10.10.245.5]",
                        "[+] Found development API domain: dev-api.megacorp.com",
                        "[+] Reconnaissance phase completed successfully."
                    ),
                    feedbackMessage = "Completed passive scanner OSINT mapping!",
                    initialTerminalOutput = "[i] Lab terminal initialized. Ready for Reconnaissance commands."
                ),
                HackingStep(
                    phase = "Scanning",
                    scenario = "With target IP 10.10.245.5 identified, perform active port scanning. Locate active service ports and software version banners to discover outdated running services.",
                    objectives = listOf("Execute the active port scanner probe: 'nmap -sV -p 8080 10.10.245.5'"),
                    hints = listOf(
                        "Execute nmap with service version detection (-sV) targeting specific ports.",
                        "Direct port scans toward port 8080."
                    ),
                    targetCommand = "nmap -sV -p 8080 10.10.245.5",
                    successLogs = listOf(
                        "[+] Scanning target IP 10.10.245.5...",
                        "[+] Port 8080/tcp is OPEN.",
                        "[+] Service Banner read: Apache Tomcat v8.5.23 (Vulnerable)",
                        "[+] Service Version audit reveals CVE-2017-12617 (Remote Code Execution vulnerability)."
                    ),
                    feedbackMessage = "Identified Open Vulnerable target port successfully!",
                    initialTerminalOutput = "[i] Target resolved ns1.megacorp.com [10.10.245.5]. Ready for network scanning scans."
                ),
                HackingStep(
                    phase = "Exploitation",
                    scenario = "Deploy the targeted Tomcat Manager exploit payload matching the Apache Tomcat v8.5.23 remote command trigger loophole.",
                    objectives = listOf("Run Tomcat exploit program: 'exploit -m tomcat_mgr -t 10.10.245.5'"),
                    hints = listOf(
                        "Select the 'exploit -m tomcat_mgr' compiler script command.",
                        "Wait for connection handshakes to compromise stack frames."
                    ),
                    targetCommand = "exploit -m tomcat_mgr -t 10.10.245.5",
                    successLogs = listOf(
                        "[+] Constructing multipart form HTTP parameters...",
                        "[+] Sending exploit bypass payload to Tomcat context path...",
                        "[+] Payload deployed successfully. Hijacking stack registers...",
                        "[+] CONNECTED: Meterpreter active session 1 established (10.10.245.5:4444)."
                    ),
                    feedbackMessage = "Reverse session connection popped cleanly!",
                    initialTerminalOutput = "[i] Internal session listener active on port 4444. Ready for exploit delivery."
                ),
                HackingStep(
                    phase = "Post-Exploitation",
                    scenario = "Your reverse console shell only runs under high-risk restricted user accounts. Audit sudo privileges to escalate to root, and retrieve the final validation flag code.",
                    objectives = listOf("Read restricted flag container file: 'cat /root/flag.txt'"),
                    hints = listOf(
                        "Type or tap 'cat /root/flag.txt' to extract the system administrator's flag file.",
                        "Check console output immediately upon retrieval to secure your mission credentials!"
                    ),
                    targetCommand = "cat /root/flag.txt",
                    successLogs = listOf(
                        "[+] Opening standard configuration streams...",
                        "[+] FLAG_ETHICAL_HACKER_STAGE_4_COMPLETE",
                        "[+] System compromised. Flag securely saved. Terminating session listener cleanly."
                    ),
                    feedbackMessage = "Campaign challenge solved! Pentest complete.",
                    initialTerminalOutput = "meterpreter > restricted_shell_opened. System online."
                )
            )
        ),
        HackingMission(
            id = "cloud_vault",
            title = "Mission 2: Cloud Storage Audit",
            description = "Audit insecure storage buckets and locate leaked credential files to assess modern cloud configuration standards.",
            difficulty = "Medium",
            xpReward = 180,
            steps = listOf(
                HackingStep(
                    phase = "Reconnaissance",
                    scenario = "Assess public cloud filesystems of 'cloud-vault.megacorp'. Locate and catalog the cloud storage bucket identifier parameter using standard search tools.",
                    objectives = listOf("Execute search query: 'subfinder -d megacorp.com'"),
                    hints = listOf(
                        "Check subdomain discovery commands.",
                        "Use subfinder tool to search."
                    ),
                    targetCommand = "subfinder -d megacorp.com",
                    successLogs = listOf(
                        "[+] Passive subdomain enumeration complete for: megacorp.com",
                        "[+] Found subdomains: 9",
                        "[+] cloudcheck-bucket-s3.megacorp.com",
                        "[+] s3-storage-archive.megacorp.com"
                    ),
                    feedbackMessage = "Subdomains discovered!",
                    initialTerminalOutput = "[i] Ready for subdomain discovery tools."
                ),
                HackingStep(
                    phase = "Scanning",
                    scenario = "Verify permission bounds on bucket 's3-storage-archive.megacorp.com'. Use scans to check read flags.",
                    objectives = listOf("Run netdiscover sweeps: 'netdiscover -r 10.10.0.0/24'"),
                    hints = listOf(
                        "Run the local network scanning command.",
                        "Tap the netdiscover chip."
                    ),
                    targetCommand = "netdiscover -r 10.10.0.0/24",
                    successLogs = listOf(
                        "[+] Sifting networks... SCAN COMPLETED.",
                        "[+] Found node at IP 10.10.0.45 [MAC: 00:0C:29:3E:F1:C9]",
                        "[+] Target port audit shows exposed API gateway storage."
                    ),
                    feedbackMessage = "Storage gateway resolved!",
                    initialTerminalOutput = "[i] Checking cloud storage subnet gateway maps."
                ),
                HackingStep(
                    phase = "Exploitation",
                    scenario = "Use your python exploit script to authenticate to storage nodes with credentials mined under scanning sessions.",
                    objectives = listOf("Launch attack loader: 'python3 exploit.py 10.10.10.5 admin'"),
                    hints = listOf(
                        "Use python3 exploit script.",
                        "Enter the parameters targeting 10.10.10.5 admin."
                    ),
                    targetCommand = "python3 exploit.py 10.10.10.5 admin",
                    successLogs = listOf(
                        "[+] Injecting authentication override payloads...",
                        "[+] Session successfully generated.",
                        "[+] Bypassed active storage IAM gatekeepers."
                    ),
                    feedbackMessage = "Access bypassed!",
                    initialTerminalOutput = "[i] Exploit terminal ready to launch bucket takeover script."
                ),
                HackingStep(
                    phase = "Post-Exploitation",
                    scenario = "Confirm permission escalations using local commands on the hijacked container.",
                    objectives = listOf("Audit configuration properties: 'sudo -l'"),
                    hints = listOf(
                        "Run 'sudo -l' to check executable rights.",
                        "Confirm root authority."
                    ),
                    targetCommand = "sudo -l",
                    successLogs = listOf(
                        "[-] Checking sudo specifications...",
                        "[+] User pt may run `/usr/bin/cat` as privileged root user.",
                        "[+] Root privilege validation flag extracted successfully!"
                    ),
                    feedbackMessage = "Authorization escalated successfully!",
                    initialTerminalOutput = "pt@cloud-container:~$ CLI ready."
                )
            )
        )
    )
}
