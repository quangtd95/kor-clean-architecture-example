package io.qtd.fungpt.conversation.core.models

import java.time.LocalDateTime

data class CoreConversation(
    val id: String,
    var model: String,
    var title: String,
    val userId: String,
    val createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime,
    var deletedAt: LocalDateTime?,
) {
    companion object
}