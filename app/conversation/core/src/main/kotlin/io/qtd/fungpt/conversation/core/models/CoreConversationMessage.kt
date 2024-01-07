package io.qtd.fungpt.conversation.core.models

import java.time.LocalDateTime

data class CoreConversationMessage(
    val id: String,
    val conversationId: String,
    val userId: String,
    var content: String,
    val role: String,
    var createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime,
    var deletedAt: LocalDateTime?,
) {
    companion object
}
