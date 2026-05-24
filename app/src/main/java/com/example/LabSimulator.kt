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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LabSimulatorScreen(
    onXPChange: (Int) -> Unit,
    showXPSnackbar: (String, Int) -> Unit
) {
    val labs = listOf(
        LabModule("sqli", "SQL Injection Lab", "Authenticate as admin using SQL code", Icons.Default.Lock),
        LabModule("xss", "Stored XSS Sandbox", "Inject a simulated browser cookie thief", Icons.Default.Warning),
        LabModule("brute", "Brute Force Monitor", "Test API Rate Limiting protection", Icons.Default.Refresh),
        LabModule("buffer", "Buffer Overflow Stack Visualizer", "Corrupt low-level CPU registers", Icons.Default.Build),
        LabModule("scanner", "Network Port Scanner", "Map subnets, sweep active live hosts and audit open TCP ports", Icons.Default.Search)
    )

    var currentLabId by remember { mutableStateOf("sqli") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp)
    ) {
        // Lab Selection Header
        Text(
            text = "VULNERABILITY SANDBOX LABS",
            style = MaterialTheme.typography.titleMedium,
            color = CyberPrimary,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            labs.forEach { lab ->
                val isSelected = currentLabId == lab.id
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CyberSurfaceVariant else CyberSurface)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) CyberPrimary else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { currentLabId = lab.id }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = lab.icon,
                            contentDescription = lab.title,
                            tint = if (isSelected) CyberPrimary else CyberTextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lab.title.split(" ")[0], // abbreviation
                            color = if (isSelected) CyberTextPrimary else CyberTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Display Active Lab Container
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
                // Topic Banner
                val currentLab = labs.first { it.id == currentLabId }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = currentLab.icon,
                        contentDescription = null,
                        tint = CyberSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = currentLab.title.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyberTextPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = currentLab.desc,
                    fontSize = 13.sp,
                    color = CyberTextSecondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                HorizontalDivider(color = CyberSurfaceVariant, modifier = Modifier.padding(bottom = 16.dp))

                Box(modifier = Modifier.weight(1f)) {
                    when (currentLabId) {
                        "sqli" -> SqlInjectionLab(onXPChange, showXPSnackbar)
                        "xss" -> XssSandboxLab(onXPChange, showXPSnackbar)
                        "brute" -> BruteForceLab(onXPChange, showXPSnackbar)
                        "buffer" -> BufferOverflowLab(onXPChange, showXPSnackbar)
                        "scanner" -> NetworkScannerLab(onXPChange, showXPSnackbar)
                    }
                }
            }
        }
    }
}

data class LabModule(
    val id: String,
    val title: String,
    val desc: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

// SQL INJECTION LAB COMPONENT
@Composable
fun SqlInjectionLab(
    onXPChange: (Int) -> Unit,
    showXPSnackbar: (String, Int) -> Unit
) {
    var userIdInput by remember { mutableStateOf("") }
    var mitigationToggled by remember { mutableStateOf(false) }
    var consoleLogs by remember { mutableStateOf(listOf("Database engine online. Ready for queries.", "Input standard user IDs (e.g. user1, user2) to inspect lists.")) }
    var queryOutputList by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    val focusManager = LocalFocusManager.current

    // Fictional DB registers
    val simulatedDatabase = listOf(
        mapOf("ID" to "admin", "User" to "Administrator", "Hash" to "f85d9da7a8d9f430", "Role" to "LEVEL_10_SUPERUSER", "VaultSecret" to "FLAG_SQL_INJECTION_BYPASS_AUTHORIZED"),
        mapOf("ID" to "user1", "User" to "John Doe", "Hash" to "74a3f2d011ff27bc", "Role" to "LEVEL_1_STAFF", "VaultSecret" to "Internal Memo: Restock coffee"),
        mapOf("ID" to "user2", "User" to "Alice Miller", "Hash" to "8ca3e7f0980bb2dc", "Role" to "LEVEL_1_STAFF", "VaultSecret" to "Idea: Add cyber defense mode")
    )

    // Dynamic query string
    val currentQueryText = if (mitigationToggled) {
        "SELECT * FROM tbl_users WHERE id = ? \n[ Bound parameter: '$userIdInput' ]"
    } else {
        "SELECT * FROM tbl_users WHERE id = '$userIdInput'"
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = "MITIGATION: PreparedStatement",
                fontSize = 12.sp,
                color = if (mitigationToggled) CyberPrimary else CyberTextSecondary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = mitigationToggled,
                onCheckedChange = {
                    mitigationToggled = it
                    consoleLogs = consoleLogs + "System: Toggle PreparedStatements security state to ${if (it) "ON" else "OFF"}."
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CyberBackground,
                    checkedTrackColor = CyberPrimary,
                    uncheckedThumbColor = CyberTextSecondary,
                    uncheckedTrackColor = CyberSurfaceVariant
                ),
                modifier = Modifier.testTag("mitigation_sqli_switch")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Query Viewer panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCodeBackground)
                .border(1.dp, if (mitigationToggled) CyberPrimary else CyberAccent, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column {
                Text("DUMPED SQL QUERY INTERPRETER:", fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentQueryText,
                    fontSize = 12.sp,
                    color = if (mitigationToggled) CyberSecondary else CyberAccent,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = userIdInput,
            onValueChange = { userIdInput = it },
            label = { Text("Search User ID", color = CyberTextSecondary, fontFamily = FontFamily.Monospace) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = CyberTextPrimary,
                unfocusedTextColor = CyberTextPrimary,
                focusedBorderColor = CyberPrimary,
                unfocusedBorderColor = CyberSurfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sqli_input_field"),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (userIdInput.isBlank()) {
                        consoleLogs = consoleLogs + "Engine: Empty payload received. Action stopped."
                        return@Button
                    }
                    if (mitigationToggled) {
                        // SECURE PROCESS: Search query executes literal parameter matching
                        val match = simulatedDatabase.firstOrNull { it["ID"] == userIdInput }
                        if (match != null) {
                            queryOutputList = listOf(match)
                            consoleLogs = consoleLogs + listOf(
                                "SQL Executed successfully.",
                                "Bound placeholder resolved parameters securely.",
                                "Filtered Search Record Match: user specified: '$userIdInput'"
                            )
                        } else {
                            queryOutputList = emptyList()
                            consoleLogs = consoleLogs + "SQL Executed successfully. Bounds resolved. Result: 0 rows returned."
                        }
                    } else {
                        // UNSECURE PROCESS: Check for payload
                        if (userIdInput.contains("' OR 1=1") || userIdInput.contains("' OR '1'='1")) {
                            // Successful SQL injection
                            queryOutputList = simulatedDatabase
                            consoleLogs = consoleLogs + listOf(
                                "CRITICAL EXPLOIT DETECTED!",
                                "Engine evaluation: OR 1=1 translates to logic TRUE.",
                                "Payload parsed effectively: bypassing standard access checks.",
                                "Authentication Overridden. Outputting full user credentials library!"
                            )
                            onXPChange(25)
                            showXPSnackbar("Resolved SQL Bypass: Leaked Admin secret", 25)
                        } else {
                            // Standard scan
                            val match = simulatedDatabase.firstOrNull { it["ID"] == userIdInput }
                            if (match != null) {
                                queryOutputList = listOf(match)
                                consoleLogs = consoleLogs + "Query completed. Standard record fetched."
                            } else {
                                queryOutputList = emptyList()
                                consoleLogs = consoleLogs + "Query executed. Database returned empty array."
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary, contentColor = Color.Black),
                modifier = Modifier
                    .weight(1f)
                    .testTag("sqli_run_exploit_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("RUN COMMAND", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = {
                    userIdInput = "' OR 1=1 --"
                    consoleLogs = consoleLogs + "Terminal: Loaded exploit payload: ' OR 1=1 --"
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant, contentColor = CyberTextPrimary),
                modifier = Modifier
                    .weight(1f)
                    .testTag("sqli_load_exploit_button")
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = CyberAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("LOAD EXPLOIT", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Simulated DB display table
        Text("DUMPED DATABASE RECORD MATCHES:", fontSize = 11.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCodeBackground)
                .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(8.dp)),
            contentAlignment = if (queryOutputList.isEmpty()) Alignment.Center else Alignment.TopStart
        ) {
            if (queryOutputList.isEmpty()) {
                Text(
                    "No Active Queries Transacted / Database Empty",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = CyberTextSecondary
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    items(queryOutputList) { row ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("User: ${row["User"]}", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = CyberPrimary, fontWeight = FontWeight.Bold)
                                    Text("Role: ${row["Role"]}", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = CyberSecondary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("PW hash: ${row["Hash"]}", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = CyberTextSecondary)
                                Text("Vault Key: ${row["VaultSecret"]}", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = CyberAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Console logger
        Text("SYSTEM COMPILER DEBUGLOG:", fontSize = 11.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
                .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), reverseLayout = true) {
                items(consoleLogs.reversed()) { log ->
                    Text(
                        text = ">> $log",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = if (log.contains("CRITICAL") || log.contains("EXPLOIT")) CyberAccent else if (log.contains("success") || log.contains("secure")) CyberPrimary else CyberTextSecondary,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

// STORED XSS SANDBOX COMPONENT
@Composable
fun XssSandboxLab(
    onXPChange: (Int) -> Unit,
    showXPSnackbar: (String, Int) -> Unit
) {
    var feedbackInput by remember { mutableStateOf("") }
    var sanitizeToggled by remember { mutableStateOf(false) }
    var submittedFeedbacks by remember { mutableStateOf(listOf("Awesome cybersecurity lab tutorial!", "Does anyone know how to solve Level 3?")) }
    var activeTriggeredCookieStagingAlert by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = "SANITY CHECK: HTML Entity Encoder",
                fontSize = 11.sp,
                color = if (sanitizeToggled) CyberPrimary else CyberTextSecondary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = sanitizeToggled,
                onCheckedChange = { sanitizeToggled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CyberBackground,
                    checkedTrackColor = CyberPrimary,
                    uncheckedThumbColor = CyberTextSecondary,
                    uncheckedTrackColor = CyberSurfaceVariant
                ),
                modifier = Modifier.testTag("mitigation_xss_switch")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = feedbackInput,
            onValueChange = { feedbackInput = it },
            label = { Text("Write board comment...", color = CyberTextSecondary, fontFamily = FontFamily.Monospace) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = CyberTextPrimary,
                unfocusedTextColor = CyberTextPrimary,
                focusedBorderColor = CyberPrimary,
                unfocusedBorderColor = CyberSurfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("xss_input_field"),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (feedbackInput.isBlank()) return@Button
                    
                    if (sanitizeToggled) {
                        // Safe sanitization
                        val safeOutput = feedbackInput
                            .replace("&", "&amp;")
                            .replace("<", "&lt;")
                            .replace(">", "&gt;")
                            .replace("\"", "&quot;")
                            .replace("'", "&#x27;")
                        submittedFeedbacks = submittedFeedbacks + safeOutput
                    } else {
                        // Vulnerable inclusion
                        submittedFeedbacks = submittedFeedbacks + feedbackInput
                        if (feedbackInput.contains("<script>") && feedbackInput.contains("</script>")) {
                            activeTriggeredCookieStagingAlert = "XSS ATTACK SUCCESSFUL!\n\nPayload executed inside browser window environment. Attacker successfully read Session Tokens:\n\nCOOKIE: user_session_auth=a98s7df98a7sd8f9a"
                            onXPChange(25)
                            showXPSnackbar("Executed Stored XSS Script Bypass", 25)
                        }
                    }
                    feedbackInput = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary, contentColor = Color.Black),
                modifier = Modifier
                    .weight(1.0f)
                    .testTag("xss_post_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("POST COMMENTS", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    feedbackInput = "<script>alert('Cookie stolen: ' + document.cookie)</script>"
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant, contentColor = CyberTextPrimary),
                modifier = Modifier
                    .weight(1.0f)
                    .testTag("xss_load_button")
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = CyberAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("XSS PAYLOAD", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Web representation rendering comments mockup
        Text("WEBSITE COMMENT BOARD SIMULATOR:", fontSize = 11.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCodeBackground)
                .border(2.dp, CyberSecondary, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Box(modifier = Modifier.size(10.dp).background(Color.Red, RoundedCornerShape(5.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.size(10.dp).background(Color.Yellow, RoundedCornerShape(5.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.size(10.dp).background(Color.Green, RoundedCornerShape(5.dp)))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "https://safe-merchandise.cyberlaboratory/guestbook",
                        color = CyberTextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider(color = CyberSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(submittedFeedbacks) { comment ->
                        val isScriptPayload = comment.contains("<script>") && !comment.contains("&lt;script&gt;")
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = if (isScriptPayload) CyberAccent else CyberSurfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isScriptPayload) Color(0x25FF3D00) else CyberSurface
                            )
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = if (isScriptPayload) CyberAccent else CyberPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isScriptPayload) "INTRUDER_SCRIPT" else "Guest_User",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = CyberTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = comment,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = if (isScriptPayload) CyberAccent else CyberTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dialog alert emulator
        if (activeTriggeredCookieStagingAlert != null) {
            AlertDialog(
                onDismissRequest = { activeTriggeredCookieStagingAlert = null },
                confirmButton = {
                    Button(
                        onClick = { activeTriggeredCookieStagingAlert = null },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberAccent)
                    ) {
                        Text("CLOSE INTERCEPT PANEL", fontFamily = FontFamily.Monospace)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = CyberAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("XSS TRIGGERED", color = CyberAccent, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Text(
                        activeTriggeredCookieStagingAlert ?: "",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = CyberTextPrimary
                    )
                },
                containerColor = CyberSurface,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.border(2.dp, CyberAccent, RoundedCornerShape(12.dp))
            )
        }
    }
}

// BRUTE FORCE MONITOR COMPONENT
@Composable
fun BruteForceLab(
    onXPChange: (Int) -> Unit,
    showXPSnackbar: (String, Int) -> Unit
) {
    var rateLimitToggled by remember { mutableStateOf(false) }
    var connectionLogs by remember { mutableStateOf(listOf("Automated API Gateway operational.", "Rate limiting rules configured: None (Defaults to unlimited requests per IP)")) }
    var isSimulating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val wordlist = listOf(
        "admin123", "password", "superman", "cyberlabs", "hacking1", "admin"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = "PROTECTION: Toggle API Rate Limiting",
                fontSize = 11.sp,
                color = if (rateLimitToggled) CyberPrimary else CyberTextSecondary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = rateLimitToggled,
                onCheckedChange = {
                    rateLimitToggled = it
                    connectionLogs = connectionLogs + "API Router: Rate limiting configured to ${if (it) "ENABLED (max 3 tries/ip/min)" else "DISABLED (infinite attempts allowed)"}"
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CyberBackground,
                    checkedTrackColor = CyberPrimary,
                    uncheckedThumbColor = CyberTextSecondary,
                    uncheckedTrackColor = CyberSurfaceVariant
                ),
                modifier = Modifier.testTag("mitigation_bruteforce_switch")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (isSimulating) return@Button
                isSimulating = true
                scope.launch {
                    connectionLogs = connectionLogs + "Launcher: Initiating automated password attack simulation against gateway..."
                    delay(800)
                    var index = 0
                    var successful = false
                    for (i in 0 until wordlist.size * 2) {
                        if (!isSimulating) break
                        val password = wordlist[index % wordlist.size]
                        index++

                        if (rateLimitToggled && i >= 3) {
                            connectionLogs = connectionLogs + "[ALERT] POST /api/login?u=admin&p=$password -> HTTP 429 Too Many Requests (Blocked IP)"
                            connectionLogs = connectionLogs + "System: Connection dropped. Rate limiter neutralized automated attack."
                            successful = false
                            break
                        } else {
                            if (password == "cyberlabs") {
                                connectionLogs = connectionLogs + "[SUCCESS] POST /api/login?u=admin&p=$password -> HTTP 200 OK (JWT Session Token Returned!)"
                                successful = true
                                break
                            } else {
                                connectionLogs = connectionLogs + "POST /api/login?u=admin&p=$password -> HTTP 401 Unauthorized (Auth failed)"
                            }
                        }
                        delay(600)
                    }
                    if (successful && !rateLimitToggled) {
                        connectionLogs = connectionLogs + "Simulation: Attacker recovered valid credential 'cyberlabs' via brute-force stuffing!"
                        onXPChange(25)
                        showXPSnackbar("Completed simulated brute-force extraction", 25)
                    }
                    isSimulating = false
                }
            },
            enabled = !isSimulating,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSimulating) CyberSurfaceVariant else CyberPrimary,
                contentColor = if (isSimulating) CyberTextSecondary else Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("brute_attack_button")
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isSimulating) "ATTACK ACTIVE..." else "LAUNCH DICTIONARY ATTACK", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }

        if (isSimulating) {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = { isSimulating = false },
                colors = ButtonDefaults.buttonColors(containerColor = CyberAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("HALT SIMULATION", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Monitor logs scrolling feed
        Text("WEB ROUTER PORT LOGS FILTER:", fontSize = 11.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
                .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), reverseLayout = true) {
                items(connectionLogs.reversed()) { log ->
                    val clr = when {
                        log.contains("SUCCESS") -> CyberPrimary
                        log.contains("ALERT") || log.contains("HTTP 429") -> CyberAccent
                        log.contains("HTTP 401") -> CyberTextSecondary
                        else -> CyberSecondary
                    }
                    Text(
                        text = ">> $log",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = clr,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }
        }
    }
}

// BUFFER OVERFLOW SCREEN COMPONENT
@Composable
fun BufferOverflowLab(
    onXPChange: (Int) -> Unit,
    showXPSnackbar: (String, Int) -> Unit
) {
    var bufferInput by remember { mutableStateOf("") }
    var safeBoundsCheckToggled by remember { mutableStateOf(false) }
    var consoleMessages by remember { mutableStateOf(listOf("System Kernel V2.4 initialized.", "Buffer stack pointer established at 0x7FFF0044.", "Input max 16 characters string to fill char register.")) }

    val maxBufferBytes = 16

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = "SAFEGUARD: strcpy -> strncpy Bounds Checking",
                fontSize = 11.sp,
                color = if (safeBoundsCheckToggled) CyberPrimary else CyberTextSecondary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = safeBoundsCheckToggled,
                onCheckedChange = { safeBoundsCheckToggled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CyberBackground,
                    checkedTrackColor = CyberPrimary,
                    uncheckedThumbColor = CyberTextSecondary,
                    uncheckedTrackColor = CyberSurfaceVariant
                ),
                modifier = Modifier.testTag("mitigation_buffer_switch")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid showing memory sectors
        Text("Visual Stack Frame Registers (allocated 16 bytes)", fontSize = 11.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(6.dp))

        // Visual Grid of Bytes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 0 until maxBufferBytes) {
                val hasChar = i < bufferInput.length
                val textChar = if (hasChar) bufferInput[i].toString() else ""
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (hasChar) CyberPrimary else CyberSurface)
                        .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = textChar,
                        color = Color.Black,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // EIP register box
            val isOverflown = bufferInput.length > maxBufferBytes
            val overflowChar = if (isOverflown) bufferInput.getOrNull(maxBufferBytes)?.toString() ?: "?" else ""
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isOverflown) CyberAccent else CyberSurfaceVariant)
                    .border(2.dp, if (isOverflown) CyberAccent else Color.Transparent, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("EIP", fontSize = 8.sp, color = if (isOverflown) Color.White else CyberTextSecondary, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text(text = if (isOverflown) overflowChar else "0x44", color = if (isOverflown) Color.White else CyberSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = bufferInput,
            onValueChange = {
                if (safeBoundsCheckToggled) {
                    // Pre-vent modification over capacity strings
                    if (it.length <= maxBufferBytes) {
                        bufferInput = it
                    } else {
                        consoleMessages = consoleMessages + "STRNCPY PREVENTED: Blocked write beyond safe 16-byte bounds configuration limits!"
                    }
                } else {
                    bufferInput = it
                }
            },
            label = { Text("Input data payload", color = CyberTextSecondary, fontFamily = FontFamily.Monospace) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = CyberTextPrimary,
                unfocusedTextColor = CyberTextPrimary,
                focusedBorderColor = CyberPrimary,
                unfocusedBorderColor = CyberSurfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("buffer_input_field"),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (bufferInput.length > maxBufferBytes) {
                        consoleMessages = consoleMessages + listOf(
                            "Copied ${bufferInput.length} bytes into 16-byte buffer register.",
                            "CRITICAL BOUNCE FAULT: Memory registers corrupted!",
                            "Memory Address Instruction EIP overwritten effectively to '${bufferInput.substring(maxBufferBytes)}'.",
                            "Kernel warning: ILLEGAL INSTRUCTION EXECUTED (Stack Hijack achieved)"
                        )
                        onXPChange(25)
                        showXPSnackbar("Resolved Memory Stack Counter Overflows", 25)
                    } else {
                        consoleMessages = consoleMessages + "Execution safe. Saved ${bufferInput.length} bytes cleanly."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary, contentColor = Color.Black),
                modifier = Modifier
                    .weight(1f)
                    .testTag("buffer_run_button")
            ) {
                Text("WRITE MEMORY CODE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }

            Button(
                onClick = {
                    bufferInput = "OVERFLOWSTACK_EIP1"
                    consoleMessages = consoleMessages + "Terminal loaded crash bypass: 18-characters shell string."
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant, contentColor = CyberTextPrimary),
                modifier = Modifier
                    .weight(1f)
                    .testTag("buffer_overflow_load_button")
            ) {
                Text("STUFF PAYLOAD", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("MEMORY REGISTER STATUS DEBUGLOG:", fontSize = 11.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
                .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), reverseLayout = true) {
                items(consoleMessages.reversed()) { log ->
                    val colorLog = when {
                        log.contains("CRITICAL") || log.contains("ILLEGAL") -> CyberAccent
                        log.contains("PREVENTED") || log.contains("STRNCPY") || log.contains("safe") -> CyberPrimary
                        else -> CyberTextSecondary
                    }
                    Text(
                        text = ">> $log",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = colorLog,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

// NETWORK PORT SCANNER SANDBOX COMPONENT
@Composable
fun NetworkScannerLab(
    onXPChange: (Int) -> Unit,
    showXPSnackbar: (String, Int) -> Unit
) {
    var targetIpInput by remember { mutableStateOf("10.10.45.0/24") }
    var scanType by remember { mutableStateOf("SYN Scan (Stealth)") }
    var firewallToggled by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }
    var progressVal by remember { mutableStateOf(0f) }
    var selectedHostIndex by remember { mutableStateOf<Int?>(null) }
    
    var consoleLogs by remember { mutableStateOf(listOf(
        "Network discovery engine initialized.",
        "Select a subnet/host target and configure scanning profiles below."
    )) }

    // Simulating hosts generated per configuration
    val standardHosts = remember {
        listOf(
            SimulatedHost("10.10.45.1", "Enterprise Firewalled Gateway", "Cisco CSR1000V", "UP", listOf(
                SimulatedPort(80, "HTTP", "Apache httpd 2.4.41", "Secure"),
                SimulatedPort(443, "HTTPS", "Apache httpd 2.4.41", "Secure"),
                SimulatedPort(22, "SSH", "OpenSSH 8.2p1", "Secure")
            )),
            SimulatedHost("10.10.45.15", "Corporate Web Server", "Ubuntu Server LTS", "UP", listOf(
                SimulatedPort(80, "HTTP", "Apache Tomcat 8.5.23", "Vulnerable (CVE-2017-12617 - Remote Code Execution)"),
                SimulatedPort(8080, "HTTP-Proxy", "Squid Proxy v4.10", "Secure")
            )),
            SimulatedHost("10.10.45.32", "Restricted Database Backup File", "Windows Server 2012", "UP", listOf(
                SimulatedPort(3306, "MySQL", "MySQL 5.5.0", "Vulnerable (Unauthorized root passwordless credential leak)"),
                SimulatedPort(445, "SMB", "Microsoft-DS Active Directory", "Vulnerable (MS17-010 EternalBlue Exploit target)")
            )),
            SimulatedHost("10.10.45.109", "Standard User Workstation", "Windows 11 Client", "UP", listOf(
                SimulatedPort(139, "NetBIOS", "Windows SAM daemon", "Secure")
            ))
        )
    }

    var scannedHostsList by remember { mutableStateOf<List<SimulatedHost>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Firewall Switch row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = "FIREWALL RULES: Toggle IDS Active Dropping",
                fontSize = 11.sp,
                color = if (firewallToggled) CyberPrimary else CyberTextSecondary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = firewallToggled,
                onCheckedChange = {
                    firewallToggled = it
                    consoleLogs = consoleLogs + "System: Security Firewall rules set to ${if (it) "ENABLED (Filters incoming stealth packets and blocks sweeps)" else "DISABLED (Open network ports fully visible)"}"
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CyberBackground,
                    checkedTrackColor = CyberPrimary,
                    uncheckedThumbColor = CyberTextSecondary,
                    uncheckedTrackColor = CyberSurfaceVariant
                ),
                modifier = Modifier.testTag("mitigation_network_switch")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Target Subnet Input and Scan selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = targetIpInput,
                onValueChange = { targetIpInput = it },
                label = { Text("Subnet Target", color = CyberTextSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CyberTextPrimary,
                    unfocusedTextColor = CyberTextPrimary,
                    focusedBorderColor = CyberPrimary,
                    unfocusedBorderColor = CyberSurfaceVariant
                ),
                modifier = Modifier
                    .weight(1.3f)
                    .testTag("network_target_input"),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
            )

            // Select Scan Style Dropdown (using basic Row selector buttons for simplicity & reliability vs messy anchor dropdown menus)
            Column(modifier = Modifier.weight(1.7f)) {
                Text(
                    text = "Scan Mode:",
                    color = CyberTextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurfaceVariant)
                        .padding(2.dp)
                ) {
                    listOf("SYN", "Connect", "Ping").forEach { mode ->
                        val isSel = (mode == "SYN" && scanType.startsWith("SYN")) ||
                                (mode == "Connect" && scanType.startsWith("Connect")) ||
                                (mode == "Ping" && scanType.startsWith("Ping"))
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) CyberPrimary else Color.Transparent)
                                .clickable {
                                    scanType = when (mode) {
                                        "SYN" -> "SYN Scan (Stealth)"
                                        "Connect" -> "Connect Scan (Full)"
                                        else -> "Ping Sweep Only"
                                    }
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode,
                                color = if (isSel) Color.Black else CyberTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Trigger scan action
        Button(
            onClick = {
                if (isScanning) return@Button
                isScanning = true
                scannedHostsList = emptyList()
                selectedHostIndex = null
                focusManager.clearFocus()
                
                scope.launch {
                    consoleLogs = consoleLogs + listOf(
                        "Starting Nmap network simulation target audit...",
                        "Command Line: nmap ${if (scanType.startsWith("SYN")) "-sS" else if (scanType.startsWith("Connect")) "-sT" else "-sn"} $targetIpInput"
                    )
                    
                    for (step in 1..5) {
                        progressVal = step * 0.2f
                        delay(500)
                        when (step) {
                            1 -> consoleLogs = consoleLogs + "ARP Sweeping network coordinates..."
                            2 -> consoleLogs = consoleLogs + "Evaluating ICMP Echo discovery handshakes..."
                            3 -> {
                                if (firewallToggled) {
                                    consoleLogs = consoleLogs + "[ALERT] Remote IDS security gateway detected SYN port-scan signatures!"
                                    consoleLogs = consoleLogs + "[!] Active dropping rules applied. Heavy packet filtering encountered."
                                } else {
                                    consoleLogs = consoleLogs + "Hosts identified online. Performing port level SYN responses audit..."
                                }
                            }
                            4 -> {
                                if (!firewallToggled) {
                                    consoleLogs = consoleLogs + "Resolving banners. Probing service versions..."
                                } else {
                                    consoleLogs = consoleLogs + "Filtering packets: Ports returning no response (FILTERED)"
                                }
                            }
                            5 -> {
                                if (firewallToggled) {
                                    consoleLogs = consoleLogs + "Scan completed! Result: 0 hosts mapped. Firewall dropped active scan inquiries."
                                    scannedHostsList = emptyList()
                                } else {
                                    consoleLogs = consoleLogs + "SUCCESS: Network discovery audit successfully resolved 4 hosts."
                                    scannedHostsList = standardHosts
                                    onXPChange(30)
                                    showXPSnackbar("Completed Network Discovery Scans!", 30)
                                }
                            }
                        }
                    }
                    progressVal = 0f
                    isScanning = false
                }
            },
            enabled = !isScanning,
            colors = ButtonDefaults.buttonColors(
                containerColor = CyberPrimary,
                contentColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("launch_network_scan_button")
        ) {
            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isScanning) "SCANNING TARGET NETWORK..." else "LAUNCH NETWORK RECONNAISSANCE SCAN",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }

        if (isScanning) {
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = progressVal,
                color = CyberSecondary,
                trackColor = CyberCodeBackground,
                modifier = Modifier.fillMaxWidth().height(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Split view: Discovered hosts panel OR console logger status
        Text("DISCOVERED HOSTS ON SUBNET RANGE:", fontSize = 11.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f)
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCodeBackground)
                .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (scannedHostsList.isEmpty()) {
                Text(
                    text = if (firewallToggled && !isScanning && targetIpInput.isNotBlank()) 
                        "Hosts Unreachable / Blocked by IDS Firewall Filter Rules" 
                        else "No live scan data. Launch active search scan targets.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = CyberTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    items(scannedHostsList.size) { index ->
                        val host = scannedHostsList[index]
                        val isSel = selectedHostIndex == index
                        val hasVulnerability = host.ports.any { it.vulnerabilityStatus.contains("Vulnerable") }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(
                                    width = 1.dp,
                                    color = if (isSel) CyberPrimary else if (hasVulnerability) CyberAccent.copy(alpha = 0.5f) else CyberSurfaceVariant,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedHostIndex = if (isSel) null else index },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSel) CyberSurfaceVariant else CyberSurface
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = if (hasVulnerability) CyberAccent else CyberPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = host.ip,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            color = CyberTextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (hasVulnerability) Color(0x20FF3D00) else Color(0x2000FFCC))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (hasVulnerability) "AUDIT ALERT" else "SECURE HOST",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            color = if (hasVulnerability) CyberAccent else CyberPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                
                                Text(
                                    text = "Name: ${host.hostname} (${host.os})",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = CyberTextSecondary,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                AnimatedVisibility(visible = isSel) {
                                    Column(modifier = Modifier.padding(top = 8.dp)) {
                                        HorizontalDivider(color = CyberSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
                                        Text(
                                            text = "EXPOSED TCP / UDP PORTS DETECTED:",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            color = CyberSecondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        
                                        host.ports.forEach { port ->
                                            val isPortVuln = port.vulnerabilityStatus.contains("Vulnerable")
                                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = "Port ${port.number}/TCP: ${port.service}",
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 11.sp,
                                                        color = if (isPortVuln) CyberAccent else CyberTextPrimary,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = if (isPortVuln) "WEAK PORT" else "OPEN",
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 10.sp,
                                                        color = if (isPortVuln) CyberAccent else CyberPrimary
                                                    )
                                                }
                                                Text(
                                                    text = " Banner: ${port.banner}",
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 10.sp,
                                                    color = CyberTextSecondary
                                                )
                                                if (isPortVuln) {
                                                    Text(
                                                        text = " Warning: ${port.vulnerabilityStatus}",
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 10.sp,
                                                        color = CyberAccent,
                                                        fontWeight = FontWeight.Bold
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
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Console logger status window
        Text("SCANNER DEBUG OUTPUT CONSOLE:", fontSize = 11.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
                .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), reverseLayout = true) {
                items(consoleLogs.reversed()) { log ->
                    val clr = when {
                        log.contains("[SUCCESS]") || log.contains("SUCCESS") -> CyberPrimary
                        log.contains("[ALERT]") || log.contains("[!]") -> CyberAccent
                        log.contains("System:") -> CyberSecondary
                        else -> CyberTextSecondary
                    }
                    Text(
                        text = ">> $log",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = clr,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

data class SimulatedHost(
    val ip: String,
    val hostname: String,
    val os: String,
    val status: String,
    val ports: List<SimulatedPort>
)

data class SimulatedPort(
    val number: Int,
    val service: String,
    val banner: String,
    val vulnerabilityStatus: String
)
