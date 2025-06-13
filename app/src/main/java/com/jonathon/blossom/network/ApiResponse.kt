package com.jonathon.blossom.network

import com.squareup.moshi.Json

// This outer class matches the top-level object from the API response.
data class ApiResponse(
    @Json(name = "verse")
    val verse: VerseDetailsContainer
)

// This class represents the "verse" object which contains the "details".
data class VerseDetailsContainer(
    @Json(name = "details")
    val details: BibleVerse
)

// This is our main blueprint, now matching the "details" object.
data class BibleVerse(
    @Json(name = "text")
    val verseText: String,

    @Json(name = "reference")
    val reference: String
)