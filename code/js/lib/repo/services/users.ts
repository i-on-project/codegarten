'use strict'

import { userRoutes, getJsonRequestOptions, SirenLink, getSirenLink } from '../api-routes'
import fetch from 'node-fetch'

function getAuthenticatedUser(accessToken: AccessToken): Promise<AuthenticatedUser> {
    return fetch(userRoutes.getAuthenticatedUser, getJsonRequestOptions('GET', accessToken.accessToken))
        .then(res => res.json())
        .then(user => {
            const links: SirenLink[] = Array.from(user.links)

            return {
                id: user.properties.id,
                username: user.properties.name,
                gitHubName: user.properties.gitHubName,
                gitHubUri: getSirenLink(links, 'github').href,
                avatarUri: getSirenLink(links, 'avatar').href,
                accessToken: accessToken
            } as AuthenticatedUser
        })
}

function getUserById() {
    // TODO: 
}

export {
    getAuthenticatedUser,
    getUserById
}