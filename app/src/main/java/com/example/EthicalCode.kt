package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
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
fun EthicalCodeScreen(
    hasSignedOath: Boolean,
    onSignOath: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Screen Header title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = CyberPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ETHICAL CHARTER & LAWS",
                    style = MaterialTheme.typography.titleMedium,
                    color = CyberPrimary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "Cybersecurity exploits are powerful. Without authorized consent, executing scanning or bypass actions violates laws. Read the ethical constraints below.",
                style = MaterialTheme.typography.bodySmall,
                color = CyberTextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )

            HorizontalDivider(color = CyberSurfaceVariant, modifier = Modifier.padding(top = 16.dp))
        }

        item {
            // Legal compliance core block
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberAccent, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = CyberAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CRITICAL LEGAL WARNING",
                            color = CyberAccent,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "1. Computer Fraud and Abuse Act (CFAA):\nIn many jurisdictions, accessing or scanning any computer network resource without explicit, documented authorization is a federal crime carrying severe penal fines and imprisonment.\n\n2. The Authorization Rule:\nNEVER execute tools, scripts, or ping sweeps against IP addresses, ports, APIs, or domains that you do not own, OR do not have written Black-Box/White-Box scoping agreements for.",
                        color = CyberTextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            // Ethical Hacking Rules of Engagement
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberSurfaceVariant, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = null,
                            tint = CyberSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WHITE HAT COMPLIANCE CHECKLIST",
                            color = CyberSecondary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val points = listOf(
                        "Written Consent (SOW/NDA): Secure a signed statement of work before probing.",
                        "Establish Scope: Define exactly which nodes can be assessed to prevent accidental downtime.",
                        "Disclose Promptly: Inform network administrators immediately when finding dangerous flaws.",
                        "Data Privacy: Do not steal, lease, or distribute private customer/client PII fetched during pen-tests."
                    )
                    points.forEach { pt ->
                        Text(
                            text = "• $pt",
                            color = CyberTextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
            }
        }

        item {
            // Interactive signing pledge element
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberCodeBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (hasSignedOath) CyberPrimary else CyberTextSecondary,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SECURE COGNIZANT ETHICAL OATH",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasSignedOath) CyberPrimary else CyberTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "By checking the slider below, I pledge to use my cybersecurity competence strictly for defensive audits, ethical analysis, and secure code fortification, committing never to deploy offensive payloads in unauthorized environments.",
                        color = CyberTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (hasSignedOath) "OATH SIGNED & AUTHORIZED" else "OATH UNSIGNED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasSignedOath) CyberPrimary else CyberAccent,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f)
                        )

                        Switch(
                            checked = hasSignedOath,
                            onCheckedChange = { onSignOath(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberBackground,
                                checkedTrackColor = CyberPrimary,
                                uncheckedThumbColor = CyberTextSecondary,
                                uncheckedTrackColor = CyberSurfaceVariant
                            ),
                            modifier = Modifier.testTag("oath_signed_switch")
                        )
                    }
                }
            }
        }
    }
}
