package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExamFailRed
import com.example.ui.theme.ExamPassGreen
import com.example.ui.theme.ExamWarningAmber

@Composable
fun LevelBadge(level: Int, modifier: Modifier = Modifier) {
    val (label, bg, fg) = when (level) {
        1 -> Triple("Level 1: Easy", Color(0xFFECFDF5), Color(0xFF047857))
        2 -> Triple("Level 2: Moderate", Color(0xFFFFFBEB), Color(0xFFB45309))
        3 -> Triple("Level 3: Hard", Color(0xFFFEF2F2), Color(0xFFB91C1C))
        4 -> Triple("Level 4: Advanced", Color(0xFFF5F3FF), Color(0xFF6D28D9))
        else -> Triple("Level $level", Color(0xFFF1F5F9), Color(0xFF334155))
    }

    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(6.dp))
            .border(1.dp, fg.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ExamTagBadge(tag: String, modifier: Modifier = Modifier) {
    val display = when (tag.lowercase()) {
        "ntpc" -> "RRB NTPC (10m · 1500w)"
        "cgl" -> "SSC CGL (15m · 2000kd)"
        "chsl", "chsl_ldc" -> "CHSL LDC (10m · 35wpm)"
        "chsl_deo" -> "CHSL DEO (15m · 8000kd)"
        "dsssb" -> "DSSSB (10m · 35wpm)"
        "kvs", "kvs_nvs" -> "KVS/NVS (10m · 35wpm)"
        "hindi" -> "हिंदी (Inscript)"
        else -> "All Exams"
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = display,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
