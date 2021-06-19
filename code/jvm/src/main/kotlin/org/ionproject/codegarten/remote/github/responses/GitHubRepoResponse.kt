package org.ionproject.codegarten.remote.github.responses

data class GitHubRepoResponse (
    val id: Int,
    val name: String,
    val description: String?,
    val private: Boolean,
    val html_url: String,
    val is_template: Boolean,
    val owner: GitHubLoginResponse
)

data class GitHubRepoSearchResponse (
    val total_count: Int,
    val items: List<GitHubRepoResponse>
)