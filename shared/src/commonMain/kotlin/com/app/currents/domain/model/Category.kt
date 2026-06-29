package com.app.currents.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private object CategorySerializer : KSerializer<Category> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Category", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Category) = encoder.encodeString(value.apiValue)
    override fun deserialize(decoder: Decoder): Category = Category.fromApiValue(decoder.decodeString())
}

@Serializable(with = CategorySerializer::class)
sealed interface Category {
    val label: String
    val apiValue: String

    // Core categories — direct API support
    data object Top           : Category { override val label = "Top";           override val apiValue = "top" }
    data object Technology    : Category { override val label = "Technology";    override val apiValue = "technology" }
    data object Business      : Category { override val label = "Business";      override val apiValue = "business" }
    data object Sports        : Category { override val label = "Sports";        override val apiValue = "sports" }
    data object Health        : Category { override val label = "Health";        override val apiValue = "health" }
    data object Science       : Category { override val label = "Science";       override val apiValue = "science" }
    data object Entertainment : Category { override val label = "Entertainment"; override val apiValue = "entertainment" }
    data object Politics      : Category { override val label = "Politics";      override val apiValue = "politics" }
    data object World         : Category { override val label = "World";         override val apiValue = "world" }
    data object Food          : Category { override val label = "Food";          override val apiValue = "food" }
    data object Environment   : Category { override val label = "Environment";   override val apiValue = "environment" }
    data object Lifestyle     : Category { override val label = "Lifestyle";     override val apiValue = "lifestyle" }
    data object Tourism       : Category { override val label = "Travel";        override val apiValue = "tourism" }
    data object Crime         : Category { override val label = "Crime";         override val apiValue = "crime" }
    data object Education     : Category { override val label = "Education";     override val apiValue = "education" }
    data object Domestic      : Category { override val label = "Domestic";      override val apiValue = "domestic" }
    data object Other         : Category { override val label = "Other";         override val apiValue = "other" }

    // UI-only categories — mapped to closest real API category
    data object Culture       : Category { override val label = "Culture";       override val apiValue = "entertainment" }
    data object Art           : Category { override val label = "Art";           override val apiValue = "entertainment" }
    data object Music         : Category { override val label = "Music";         override val apiValue = "entertainment" }
    data object Gaming        : Category { override val label = "Gaming";        override val apiValue = "technology" }

    data class Unknown(override val label: String, override val apiValue: String) : Category

    companion object {
        fun fromApiValue(value: String): Category = when (value) {
            "top"           -> Top
            "technology"    -> Technology
            "business"      -> Business
            "sports"        -> Sports
            "health"        -> Health
            "science"       -> Science
            "entertainment" -> Entertainment
            "politics"      -> Politics
            "world"         -> World
            "food"          -> Food
            "environment"   -> Environment
            "lifestyle"     -> Lifestyle
            "tourism"       -> Tourism
            "crime"         -> Crime
            "education"     -> Education
            "domestic"      -> Domestic
            "other"         -> Other
            else            -> Unknown(value, value)
        }

        val all: List<Category> = listOf(
            Top, Technology, Business, Sports, Health,
            Science, Entertainment, Politics, World, Food,
            Environment, Lifestyle, Tourism, Crime, Education,
            Domestic, Other,
        )

        // Onboarding topics — best mix for user interest selection
        val onboardingTopics: List<Category> = listOf(
            Technology, Sports, Health, Business,
            Entertainment, Politics, Food, Science,
            Tourism, Environment, Lifestyle, Crime,
            Education, Art, Music, Gaming,
        )
    }
}