package com.example.blossom.data

/**
 * 🌬️ BREATHING PATTERN SYSTEM 🌬️
 * Beautiful, customizable breathing techniques for meditation
 */

data class BreathingPattern(
    val id: String,
    val name: String,
    val description: String,
    val inhaleSeconds: Int,
    val holdInSeconds: Int = 0,
    val exhaleSeconds: Int,
    val holdOutSeconds: Int = 0,
    val isCustom: Boolean = false
) {
    /**
     * Total cycle duration in seconds
     */
    val totalCycleSeconds: Int
        get() = inhaleSeconds + holdInSeconds + exhaleSeconds + holdOutSeconds
    
    /**
     * Get the current phase based on elapsed time in cycle
     */
    fun getCurrentPhase(elapsedInCycle: Int): BreathingPhase {
        return when {
            elapsedInCycle < inhaleSeconds -> BreathingPhase.INHALE
            elapsedInCycle < inhaleSeconds + holdInSeconds -> BreathingPhase.HOLD_IN
            elapsedInCycle < inhaleSeconds + holdInSeconds + exhaleSeconds -> BreathingPhase.EXHALE
            else -> BreathingPhase.HOLD_OUT
        }
    }
    
    /**
     * Get progress within current phase (0.0 to 1.0)
     */
    fun getPhaseProgress(elapsedInCycle: Int): Float {
        val phase = getCurrentPhase(elapsedInCycle)
        return when (phase) {
            BreathingPhase.INHALE -> {
                (elapsedInCycle.toFloat() / inhaleSeconds).coerceIn(0f, 1f)
            }
            BreathingPhase.HOLD_IN -> {
                val holdStart = inhaleSeconds
                ((elapsedInCycle - holdStart).toFloat() / holdInSeconds).coerceIn(0f, 1f)
            }
            BreathingPhase.EXHALE -> {
                val exhaleStart = inhaleSeconds + holdInSeconds
                ((elapsedInCycle - exhaleStart).toFloat() / exhaleSeconds).coerceIn(0f, 1f)
            }
            BreathingPhase.HOLD_OUT -> {
                val holdOutStart = inhaleSeconds + holdInSeconds + exhaleSeconds
                ((elapsedInCycle - holdOutStart).toFloat() / holdOutSeconds).coerceIn(0f, 1f)
            }
        }
    }
}

enum class BreathingPhase(val displayName: String, val instruction: String) {
    INHALE("Inhale", "Breathe in slowly"),
    HOLD_IN("Hold", "Hold your breath"),
    EXHALE("Exhale", "Breathe out slowly"),
    HOLD_OUT("Hold", "Hold empty")
}

/**
 * 🌟 PRESET BREATHING PATTERNS
 */
object BreathingPatterns {
    
    /**
     * 📦 Box Breathing (Your favorite 5-5-5-5!)
     * Perfect for stress relief and focus
     */
    val BOX_BREATHING = BreathingPattern(
        id = "box_5555",
        name = "Box Breathing",
        description = "Equal timing for all phases - perfect for balance and calm",
        inhaleSeconds = 5,
        holdInSeconds = 5,
        exhaleSeconds = 5,
        holdOutSeconds = 5
    )
    
    /**
     * 🌙 4-7-8 Breathing
     * Excellent for sleep and deep relaxation
     */
    val BREATHING_478 = BreathingPattern(
        id = "breathing_478",
        name = "4-7-8 Breathing",
        description = "Powerful technique for deep relaxation and sleep",
        inhaleSeconds = 4,
        holdInSeconds = 7,
        exhaleSeconds = 8,
        holdOutSeconds = 0
    )
    
    /**
     * 🔺 Triangle Breathing
     * Simple and effective for beginners
     */
    val TRIANGLE_BREATHING = BreathingPattern(
        id = "triangle_444",
        name = "Triangle Breathing",
        description = "Simple three-phase breathing for beginners",
        inhaleSeconds = 4,
        holdInSeconds = 4,
        exhaleSeconds = 4,
        holdOutSeconds = 0
    )
    
    /**
     * ⚡ Energizing Breath
     * Quick inhale, longer exhale for energy
     */
    val ENERGIZING_BREATH = BreathingPattern(
        id = "energizing_36",
        name = "Energizing Breath",
        description = "Quick inhale, longer exhale for natural energy",
        inhaleSeconds = 3,
        holdInSeconds = 0,
        exhaleSeconds = 6,
        holdOutSeconds = 0
    )
    
    /**
     * 🧘‍♀️ Deep Meditation
     * Longer cycles for deep states
     */
    val DEEP_MEDITATION = BreathingPattern(
        id = "deep_meditation",
        name = "Deep Meditation",
        description = "Extended breathing for profound meditation states",
        inhaleSeconds = 6,
        holdInSeconds = 6,
        exhaleSeconds = 8,
        holdOutSeconds = 2
    )
    
    /**
     * 💨 Quick Calm
     * Fast technique for immediate stress relief
     */
    val QUICK_CALM = BreathingPattern(
        id = "quick_calm",
        name = "Quick Calm",
        description = "Fast stress relief technique",
        inhaleSeconds = 3,
        holdInSeconds = 3,
        exhaleSeconds = 3,
        holdOutSeconds = 0
    )
    
    /**
     * Get all preset patterns
     */
    fun getAllPresets(): List<BreathingPattern> = listOf(
        BOX_BREATHING,
        BREATHING_478,
        TRIANGLE_BREATHING,
        ENERGIZING_BREATH,
        DEEP_MEDITATION,
        QUICK_CALM
    )
    
    /**
     * Create a custom breathing pattern
     */
    fun createCustomPattern(
        name: String,
        description: String,
        inhaleSeconds: Int,
        holdInSeconds: Int = 0,
        exhaleSeconds: Int,
        holdOutSeconds: Int = 0
    ): BreathingPattern {
        return BreathingPattern(
            id = "custom_${System.currentTimeMillis()}",
            name = name,
            description = description,
            inhaleSeconds = inhaleSeconds,
            holdInSeconds = holdInSeconds,
            exhaleSeconds = exhaleSeconds,
            holdOutSeconds = holdOutSeconds,
            isCustom = true
        )
    }
}

/**
 * 🎯 BREATHING GUIDE STATE
 */
data class BreathingGuideState(
    val isActive: Boolean = false,
    val currentPattern: BreathingPattern? = null,
    val elapsedSeconds: Int = 0,
    val currentPhase: BreathingPhase = BreathingPhase.INHALE,
    val phaseProgress: Float = 0f,
    val cycleCount: Int = 0,
    val isGuideVisible: Boolean = false
) {
    /**
     * Get the current instruction text
     */
    val currentInstruction: String
        get() = currentPhase.instruction
    
    /**
     * Get the current phase display name
     */
    val currentPhaseDisplay: String
        get() = currentPhase.displayName
}
