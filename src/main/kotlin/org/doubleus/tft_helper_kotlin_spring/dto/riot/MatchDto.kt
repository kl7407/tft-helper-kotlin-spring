package org.doubleus.tft_helper_kotlin_spring.dto.riot

import kotlinx.serialization.Serializable

@Serializable
data class MatchDto(
    val info: GameInfoDto,
)
