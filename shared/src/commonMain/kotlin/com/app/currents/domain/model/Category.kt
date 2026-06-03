package com.app.currents.domain.model


sealed interface Category {
    val label: String
    val apiValue: String

    data object Top           : Category { override val label = "Top";           override val apiValue = "top" }
    data object Technology    : Category { override val label = "Tech";          override val apiValue = "technology" }
    data object Business      : Category { override val label = "Business";      override val apiValue = "business" }
    data object Sports        : Category { override val label = "Sports";        override val apiValue = "sports" }
    data object Health        : Category { override val label = "Health";        override val apiValue = "health" }
    data object Science       : Category { override val label = "Science";       override val apiValue = "science" }
    data object Entertainment : Category { override val label = "Entertainment"; override val apiValue = "entertainment" }
    data object Politics      : Category { override val label = "Politics";      override val apiValue = "politics" }
    data object World         : Category { override val label = "World";         override val apiValue = "world" }
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
            else            -> Unknown(value, value)
        }

        val all: List<Category> = listOf(
            Top, Technology, Business, Sports,
            Health, Science, Entertainment, Politics, World
        )
    }
}