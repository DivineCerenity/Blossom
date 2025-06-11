package com.example.blossom.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 🎵 MEDITATION SOUNDS DATA MODEL 🎵
 * Beautiful sound categories and individual sounds for meditation
 */

enum class SoundCategory(
    val displayName: String,
    val icon: ImageVector,
    val description: String
) {
    NATURE(
        displayName = "Nature",
        icon = Icons.Default.Favorite,
        description = "Peaceful sounds from nature"
    ),
    AMBIENT(
        displayName = "Ambient",
        icon = Icons.Default.Star,
        description = "Calming ambient soundscapes"
    ),
    WATER(
        displayName = "Water",
        icon = Icons.Default.Circle,
        description = "Flowing water and ocean sounds"
    ),
    FIRE(
        displayName = "Fire",
        icon = Icons.Default.Home,
        description = "Crackling fire and warmth"
    )
}

data class MeditationSound(
    val id: String,
    val name: String,
    val category: SoundCategory,
    val fileName: String,
    val duration: Int? = null, // Duration in seconds, null for looping sounds
    val isLooping: Boolean = true,
    val icon: ImageVector,
    val description: String,
    val volume: Float = 0.7f // Default volume (0.0 to 1.0)
)

/**
 * 🌿 BEAUTIFUL MEDITATION SOUNDS COLLECTION 🌿
 */
object MeditationSounds {
    
    val allSounds = listOf(
        // 🌿 NATURE SOUNDS
        MeditationSound(
            id = "rain_gentle",
            name = "Gentle Rain",
            category = SoundCategory.NATURE,
            fileName = "rain_gentle.wav",
            icon = Icons.Default.Favorite,
            description = "Soft rainfall for deep relaxation",
            isLooping = true
        ),
        MeditationSound(
            id = "rain_heavy",
            name = "Heavy Rain",
            category = SoundCategory.NATURE,
            fileName = "rain_heavy.wav",
            icon = Icons.Default.Star,
            description = "Intense rainfall with thunder",
            isLooping = true
        ),
        MeditationSound(
            id = "forest_birds",
            name = "Forest Birds",
            category = SoundCategory.NATURE,
            fileName = "forest_birds.wav",
            icon = Icons.Default.Circle,
            description = "Peaceful bird songs in the forest",
            isLooping = true
        ),
        MeditationSound(
            id = "wind_trees",
            name = "Wind in Trees",
            category = SoundCategory.NATURE,
            fileName = "wind_trees.wav",
            icon = Icons.Default.Home,
            description = "Gentle wind through leaves",
            isLooping = true
        ),
        
        // 🌊 WATER SOUNDS
        MeditationSound(
            id = "ocean_waves",
            name = "Ocean Waves",
            category = SoundCategory.WATER,
            fileName = "ocean_waves.wav",
            icon = Icons.Default.Circle,
            description = "Rhythmic ocean waves on the shore",
            isLooping = true
        ),
        MeditationSound(
            id = "stream_flowing",
            name = "Flowing Stream",
            category = SoundCategory.WATER,
            fileName = "stream_flowing.wav",
            icon = Icons.Default.Favorite,
            description = "Gentle babbling brook",
            isLooping = true
        ),

        // 🔥 FIRE SOUNDS
        MeditationSound(
            id = "fireplace",
            name = "Fireplace",
            category = SoundCategory.FIRE,
            fileName = "fireplace.wav",
            icon = Icons.Default.Home,
            description = "Cozy crackling fireplace",
            isLooping = true
        ),

        // 🎵 AMBIENT SOUNDS
        MeditationSound(
            id = "white_noise",
            name = "White Noise",
            category = SoundCategory.AMBIENT,
            fileName = "white_noise.wav",
            icon = Icons.Default.Star,
            description = "Pure white noise for focus",
            isLooping = true
        ),
        MeditationSound(
            id = "pink_noise",
            name = "Pink Noise",
            category = SoundCategory.AMBIENT,
            fileName = "pink_noise.wav",
            icon = Icons.Default.Circle,
            description = "Balanced pink noise for relaxation",
            isLooping = true
        ),
        MeditationSound(
            id = "singing_bowls",
            name = "Singing Bowls",
            category = SoundCategory.AMBIENT,
            fileName = "singing_bowls.wav",
            icon = Icons.Default.Favorite,
            description = "Tibetan singing bowls",
            isLooping = true
        ),
        MeditationSound(
            id = "cafe_ambience",
            name = "Cafe Ambience",
            category = SoundCategory.AMBIENT,
            fileName = "cafe_ambience.wav",
            icon = Icons.Default.Home,
            description = "Soft background cafe chatter",
            isLooping = true
        )
    )
    
    fun getSoundsByCategory(category: SoundCategory): List<MeditationSound> {
        return allSounds.filter { it.category == category }
    }
    
    fun getSoundById(id: String): MeditationSound? {
        return allSounds.find { it.id == id }
    }
}
