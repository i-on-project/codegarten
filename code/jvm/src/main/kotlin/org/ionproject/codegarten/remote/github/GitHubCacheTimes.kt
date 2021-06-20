package org.ionproject.codegarten.remote.github

import java.time.Duration

object GitHubCacheTimes {
    val USER_INFO_CACHE = Duration.ofMinutes(2).toMillis()
    val USER_ORGS_CACHE = Duration.ofSeconds(10).toMillis()

    val ORG_MEMBERSHIP_CACHE = Duration.ofMinutes(1).toMillis()
    val ORG_INFO_CACHE = Duration.ofMinutes(10).toMillis()

    val REPO_SEARCH_CACHE = Duration.ofSeconds(10).toMillis()
    val REPO_INFO_CACHE = Duration.ofMinutes(10).toMillis()
    val REPO_TAGS_CACHE = Duration.ofSeconds(10).toMillis()
    val REPO_TAG_CACHE = Duration.ofSeconds(10).toMillis()

    val TEAM_INFO_CACHE = Duration.ofMinutes(10).toMillis()
}