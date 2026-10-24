package com.example.data

/**
 * Candidate reservation categories for Indian government recruitment exams.
 */
enum class CandidateCategory(val displayName: String, val shortName: String) {
    UR("General (UR)", "UR"),
    EWS("Economically Weaker Section (EWS)", "EWS"),
    OBC("Other Backward Class (OBC)", "OBC"),
    SC("Scheduled Caste (SC)", "SC"),
    ST("Scheduled Tribe (ST)", "ST"),
    PWD("Persons with Benchmark Disabilities (PwD)", "PwD")
}

/**
 * Standard Exam rules and evaluation pattern
 */
data class ExamRule(
    val id: String,
    val name: String,
    val defaultDurationMinutes: Int,
    val targetKeystrokes: Int,
    val targetWords: Int,
    val requiredWpm: Int,
    val requiredKdph: Int,
    val cutoffGeneral: Double,
    val cutoffEws: Double,
    val cutoffObc: Double,
    val cutoffSc: Double,
    val cutoffSt: Double,
    val cutoffPwd: Double,
    val ruleDescription: String
) {
    fun getCutoffForCategory(category: CandidateCategory): Double = when (category) {
        CandidateCategory.UR -> cutoffGeneral
        CandidateCategory.EWS -> cutoffEws
        CandidateCategory.OBC -> cutoffObc
        CandidateCategory.SC -> cutoffSc
        CandidateCategory.ST -> cutoffSt
        CandidateCategory.PWD -> cutoffPwd
    }
}

object ExamRegistry {
    val SSC_CGL = ExamRule(
        id = "cgl",
        name = "SSC CGL (DEST)",
        defaultDurationMinutes = 15,
        targetKeystrokes = 2000,
        targetWords = 400,
        requiredWpm = 27,
        requiredKdph = 8000,
        cutoffGeneral = 5.0,
        cutoffEws = 7.0,
        cutoffObc = 7.0,
        cutoffSc = 7.0,
        cutoffSt = 7.0,
        cutoffPwd = 10.0,
        ruleDescription = "15 mins · 2000 Key Depressions (8000 KDPH / ~27 WPM). UR error limit 5%, EWS/OBC/SC/ST 7%, PwD 10%."
    )

    val SSC_CHSL_LDC = ExamRule(
        id = "chsl_ldc",
        name = "SSC CHSL (LDC/JSA)",
        defaultDurationMinutes = 10,
        targetKeystrokes = 1750,
        targetWords = 350,
        requiredWpm = 35,
        requiredKdph = 10500,
        cutoffGeneral = 7.0,
        cutoffEws = 10.0,
        cutoffObc = 10.0,
        cutoffSc = 10.0,
        cutoffSt = 10.0,
        cutoffPwd = 10.0,
        ruleDescription = "10 mins · 35 WPM English (10500 KDPH). UR error limit 7%, EWS/OBC/SC/ST/PwD 10%."
    )

    val SSC_CHSL_DEO = ExamRule(
        id = "chsl_deo",
        name = "SSC CHSL (DEO)",
        defaultDurationMinutes = 15,
        targetKeystrokes = 2000,
        targetWords = 400,
        requiredWpm = 27,
        requiredKdph = 8000,
        cutoffGeneral = 5.0,
        cutoffEws = 7.0,
        cutoffObc = 7.0,
        cutoffSc = 7.0,
        cutoffSt = 7.0,
        cutoffPwd = 10.0,
        ruleDescription = "15 mins · 8000 KDPH (approx 27 WPM). Standard DEO posts: UR 5%, reserved 7-10%."
    )

    val RRB_NTPC = ExamRule(
        id = "ntpc",
        name = "RRB NTPC (CBTST)",
        defaultDurationMinutes = 10,
        targetKeystrokes = 1500,
        targetWords = 300,
        requiredWpm = 30,
        requiredKdph = 9000,
        cutoffGeneral = 5.0,
        cutoffEws = 5.0,
        cutoffObc = 5.0,
        cutoffSc = 5.0,
        cutoffSt = 5.0,
        cutoffPwd = 5.0,
        ruleDescription = "10 mins · 300 words min (passage provides 1500+ words). 30 Net WPM required. 5% mistakes allowed without penalty; excess mistakes deducted 10x from words typed."
    )

    val DSSSB = ExamRule(
        id = "dsssb",
        name = "DSSSB (JA / LDC)",
        defaultDurationMinutes = 10,
        targetKeystrokes = 1750,
        targetWords = 350,
        requiredWpm = 35,
        requiredKdph = 10500,
        cutoffGeneral = 3.0,
        cutoffEws = 5.0,
        cutoffObc = 5.0,
        cutoffSc = 5.0,
        cutoffSt = 5.0,
        cutoffPwd = 7.0,
        ruleDescription = "10 mins · 35 WPM in English. High accuracy requirement: UR 3% error limit, EWS/OBC/SC/ST 5%."
    )

    val KVS_NVS = ExamRule(
        id = "kvs_nvs",
        name = "KVS / NVS (JSA)",
        defaultDurationMinutes = 10,
        targetKeystrokes = 1750,
        targetWords = 350,
        requiredWpm = 35,
        requiredKdph = 10500,
        cutoffGeneral = 7.0,
        cutoffEws = 10.0,
        cutoffObc = 10.0,
        cutoffSc = 10.0,
        cutoffSt = 10.0,
        cutoffPwd = 10.0,
        ruleDescription = "10 mins · 35 WPM (KVS) / 30 WPM (NVS) in English. CBSE pattern mistake deduction."
    )

    val HINDI = ExamRule(
        id = "hindi",
        name = "Hindi Typing (Inscript)",
        defaultDurationMinutes = 10,
        targetKeystrokes = 1500,
        targetWords = 300,
        requiredWpm = 30,
        requiredKdph = 9000,
        cutoffGeneral = 7.0,
        cutoffEws = 10.0,
        cutoffObc = 10.0,
        cutoffSc = 10.0,
        cutoffSt = 10.0,
        cutoffPwd = 10.0,
        ruleDescription = "10 mins · 30 WPM in Hindi (Mangal Inscript). UR 7%, reserved 10% error limit."
    )

    val allRules = listOf(SSC_CGL, SSC_CHSL_LDC, SSC_CHSL_DEO, RRB_NTPC, DSSSB, KVS_NVS, HINDI)

    fun findRule(id: String): ExamRule {
        return allRules.find { it.id.equals(id, ignoreCase = true) } ?: SSC_CGL
    }
}
