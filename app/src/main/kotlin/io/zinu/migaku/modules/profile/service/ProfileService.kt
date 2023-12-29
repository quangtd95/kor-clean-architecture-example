package io.zinu.migaku.modules.profile.service

import io.zinu.migaku.common.BaseService
import io.zinu.migaku.exception.UserDoesNotExistsException
import io.zinu.migaku.modules.auth.model.User
import io.zinu.migaku.modules.auth.service.getUser
import io.zinu.migaku.modules.profile.model.ProfileResponse
import io.zinu.migaku.utils.unless
import org.jetbrains.exposed.sql.SizedCollection

interface IProfileService {
    suspend fun getProfile(username: String, currentUserId: String? = null): ProfileResponse
    suspend fun changeFollowStatus(toUsername: String, fromUserId: String, follow: Boolean): ProfileResponse
}

class ProfileService : BaseService(), IProfileService {
    override suspend fun getProfile(username: String, currentUserId: String?) = dbQuery {
        val toUser = getUserByUsername(username) ?: return@dbQuery getProfileByUser(null, false)
        currentUserId ?: return@dbQuery getProfileByUser(toUser)
        val fromUser = getUser(currentUserId)
        val follows = isFollower(toUser, fromUser)
        getProfileByUser(toUser, follows)
    }

    override suspend fun changeFollowStatus(toUsername: String, fromUserId: String, follow: Boolean): ProfileResponse {
        dbQuery {
            val toUser = getUserByUsername(toUsername) ?: throw UserDoesNotExistsException()
            val fromUser = getUser(fromUserId)
            if (follow) {
                addFollower(toUser, fromUser)
            } else {
                removeFollower(toUser, fromUser)
            }
        }
        return getProfile(toUsername, fromUserId)
    }

    private fun addFollower(user: User, newFollower: User) {
        unless(isFollower(user, newFollower)) {
            user.followers = SizedCollection(user.followers + newFollower)
        }
    }

    private fun removeFollower(user: User, newFollower: User) {
        if (isFollower(user, newFollower)) {
            user.followers = SizedCollection(user.followers - newFollower)
        }
    }
}

fun isFollower(user: User, follower: User?) = if (follower != null) user.followers.any { it == follower } else false

fun getProfileByUser(user: User?, following: Boolean = false) =
    ProfileResponse(profile = user?.run { ProfileResponse.Profile(username, bio, image, following) })