package com.jonathon.blossom.data

/**
 * 🧠 BINAURAL BEATS SYSTEM 🧠
 * Professional brainwave entrainment for meditation
 */

data class BinauralBeat(
    val id: String,
    val name: String,
    val description: String,
    val frequency: Float, // Hz
    val category: BrainwaveCategory,
    val fileName: String, // Audio file name
    val benefits: List<String>,
    val recommendedDuration: Int = 20, // minutes
    val isCustom: Boolean = false
)

enum class BrainwaveCategory(
    val displayName: String,
    val description: String,
    val color: String, // Hex color for UI
    val frequencyRange: String
) {
    DELTA(
        displayName = "Delta",
        description = "Deep sleep, healing, regeneration",
        color = "#8B0000", // Dark red
        frequencyRange = "0.5-4 Hz"
    ),
    THETA(
        displayName = "Theta", 
        description = "Deep meditation, creativity, intuition",
        color = "#4B0082", // Indigo
        frequencyRange = "4-8 Hz"
    ),
    ALPHA(
        displayName = "Alpha",
        description = "Relaxed focus, learning, positive mood",
        color = "#228B22", // Forest green
        frequencyRange = "8-14 Hz"
    ),
    BETA(
        displayName = "Beta",
        description = "Focus, alertness, problem solving",
        color = "#1E90FF", // Dodger blue
        frequencyRange = "14-30 Hz"
    ),
    GAMMA(
        displayName = "Gamma",
        description = "Higher consciousness, peak awareness",
        color = "#FFD700", // Gold
        frequencyRange = "30+ Hz"
    )
}

/**
 * 🌟 PRESET BINAURAL BEATS COLLECTION
 */
object BinauralBeats {
    
    // 🔴 DELTA WAVES - Deep Sleep & Healing
    val DEEP_SLEEP_1HZ = BinauralBeat(
        id = "delta_1hz",
        name = "Deep Sleep (1 Hz)",
        description = "Profound sleep, pain relief, anti-aging",
        frequency = 1f,
        category = BrainwaveCategory.DELTA,
        fileName = "binaural_1hz.wav",
        benefits = listOf("Deep sleep", "Pain relief", "Anti-aging", "Healing")
    )
    
    val HEALING_2HZ = BinauralBeat(
        id = "delta_2hz",
        name = "Nerve Regeneration (2 Hz)",
        description = "Powerful cellular repair and nerve regeneration frequency",
        frequency = 2f,
        category = BrainwaveCategory.DELTA,
        fileName = "binaural_2hz.wav",
        benefits = listOf("Nerve regeneration", "Cellular repair", "Deep healing", "Pain relief")
    )
    
    val GROWTH_HORMONE_3HZ = BinauralBeat(
        id = "delta_3hz",
        name = "Growth Hormone (3 Hz)",
        description = "Natural growth hormone release for deep restoration",
        frequency = 3f,
        category = BrainwaveCategory.DELTA,
        fileName = "binaural_3hz.wav",
        benefits = listOf("Growth hormone release", "Deep restoration", "Physical healing", "Anti-aging")
    )
    
    // 🟣 THETA WAVES - Deep Meditation & Creativity
    val SHAMANIC_4_5HZ = BinauralBeat(
        id = "theta_4_5hz",
        name = "Shamanic Journey (4.5 Hz)",
        description = "Access shamanic consciousness and deep spiritual states",
        frequency = 4.5f,
        category = BrainwaveCategory.THETA,
        fileName = "binaural_4_5hz.wav",
        benefits = listOf("Shamanic consciousness", "Deep meditation", "Spiritual insight", "Astral projection")
    )
    
    val CREATIVITY_6HZ = BinauralBeat(
        id = "theta_6hz",
        name = "Creative Flow (6 Hz)",
        description = "Unlock enhanced creativity and long-term memory formation",
        frequency = 6f,
        category = BrainwaveCategory.THETA,
        fileName = "binaural_6hz.wav",
        benefits = listOf("Enhanced creativity", "Memory formation", "Artistic flow", "Innovation")
    )
    
    val SCHUMANN_7_83HZ = BinauralBeat(
        id = "theta_7_83hz",
        name = "Earth Frequency (7.83 Hz)",
        description = "Schumann resonance - Earth's natural frequency",
        frequency = 7.83f,
        category = BrainwaveCategory.THETA,
        fileName = "binaural_7_83hz.wav",
        benefits = listOf("Natural grounding", "Earth connection", "Balance")
    )
    
    // 🟢 ALPHA WAVES - Relaxed Focus & Learning
    val LEARNING_8HZ = BinauralBeat(
        id = "alpha_8hz",
        name = "Learning Enhancement (8 Hz)",
        description = "Accelerate learning capacity and positive mental states",
        frequency = 8f,
        category = BrainwaveCategory.ALPHA,
        fileName = "binaural_8hz.wav",
        benefits = listOf("Accelerated learning", "Positive thinking", "Mental clarity", "Focus")
    )
    
    val MOOD_BOOST_10HZ = BinauralBeat(
        id = "alpha_10hz",
        name = "Mood Boost (10 Hz)",
        description = "Natural serotonin release for elevated mood and happiness",
        frequency = 10f,
        category = BrainwaveCategory.ALPHA,
        fileName = "binaural_10hz.wav",
        benefits = listOf("Mood elevation", "Serotonin release", "Happiness", "Stress relief")
    )
    
    val HEALING_10_5HZ = BinauralBeat(
        id = "alpha_10_5hz",
        name = "Holistic Healing (10.5 Hz)",
        description = "Complete healing frequency for body, mind, and soul",
        frequency = 10.5f,
        category = BrainwaveCategory.ALPHA,
        fileName = "binaural_10_5hz.wav",
        benefits = listOf("Holistic healing", "Mind-body balance", "Soul restoration", "Energy alignment")
    )
    
    // 🔵 BETA WAVES - Focus & Alertness
    val FOCUS_14HZ = BinauralBeat(
        id = "beta_14hz",
        name = "Alert Focus (14 Hz)",
        description = "Mental awakening and sharp concentration for productivity",
        frequency = 14f,
        category = BrainwaveCategory.BETA,
        fileName = "binaural_14hz.wav",
        benefits = listOf("Alert focus", "Mental awakening", "Concentration", "Productivity")
    )
    
    val MENTAL_CLARITY_18HZ = BinauralBeat(
        id = "beta_18hz",
        name = "Mental Clarity (18 Hz)",
        description = "Experience euphoria and crystal-clear thinking",
        frequency = 18f,
        category = BrainwaveCategory.BETA,
        fileName = "binaural_18hz.wav",
        benefits = listOf("Mental clarity", "Euphoria", "Clear thinking", "Problem solving")
    )
    
    val ENERGY_20HZ = BinauralBeat(
        id = "beta_20hz",
        name = "Energy Boost (20 Hz)",
        description = "Natural energy enhancement and fatigue reduction",
        frequency = 20f,
        category = BrainwaveCategory.BETA,
        fileName = "binaural_20hz.wav",
        benefits = listOf("Energy boost", "Fatigue reduction", "Vitality", "Alertness")
    )
    
    // 🟡 GAMMA WAVES - Higher Consciousness
    val CONSCIOUSNESS_40HZ = BinauralBeat(
        id = "gamma_40hz",
        name = "Higher Consciousness (40 Hz)",
        description = "Access enhanced consciousness and peak mental performance",
        frequency = 40f,
        category = BrainwaveCategory.GAMMA,
        fileName = "binaural_40hz.wav",
        benefits = listOf("Higher consciousness", "Peak awareness", "Problem solving", "Cognitive enhancement")
    )
    
    /**
     * Get all preset binaural beats in numerical order
     */
    fun getAllPresets(): List<BinauralBeat> = listOf(
        // 🔢 PERFECT NUMERICAL ORDER: 1Hz → 40Hz
        DEEP_SLEEP_1HZ,         // 1 Hz
        HEALING_2HZ,            // 2 Hz
        GROWTH_HORMONE_3HZ,     // 3 Hz
        SHAMANIC_4_5HZ,         // 4.5 Hz
        CREATIVITY_6HZ,         // 6 Hz
        SCHUMANN_7_83HZ,        // 7.83 Hz
        LEARNING_8HZ,           // 8 Hz
        MOOD_BOOST_10HZ,        // 10 Hz
        HEALING_10_5HZ,         // 10.5 Hz
        FOCUS_14HZ,             // 14 Hz
        MENTAL_CLARITY_18HZ,    // 18 Hz
        ENERGY_20HZ,            // 20 Hz
        CONSCIOUSNESS_40HZ      // 40 Hz
    )
    
    /**
     * Get beats by category
     */
    fun getBeatsByCategory(category: BrainwaveCategory): List<BinauralBeat> {
        return getAllPresets().filter { it.category == category }
    }
    
    /**
     * Get recommended beats for meditation
     */
    fun getMeditationRecommended(): List<BinauralBeat> = listOf(
        SCHUMANN_7_83HZ,
        CREATIVITY_6HZ,
        SHAMANIC_4_5HZ,
        HEALING_10_5HZ
    )
    
    /**
     * Get recommended beats for focus/productivity
     */
    fun getFocusRecommended(): List<BinauralBeat> = listOf(
        FOCUS_14HZ,
        MENTAL_CLARITY_18HZ,
        LEARNING_8HZ,
        MOOD_BOOST_10HZ
    )
    
    /**
     * Get recommended beats for sleep/relaxation
     */
    fun getSleepRecommended(): List<BinauralBeat> = listOf(
        DEEP_SLEEP_1HZ,
        HEALING_2HZ,
        GROWTH_HORMONE_3HZ
    )
}

/**
 * 🎯 BINAURAL BEAT STATE
 */
data class BinauralBeatState(
    val isActive: Boolean = false,
    val currentBeat: BinauralBeat? = null,
    val volume: Float = 0.5f,
    val isMixedWithNatureSounds: Boolean = false,
    val natureSoundVolume: Float = 0.7f,
    val elapsedSeconds: Int = 0
)
