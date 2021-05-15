'use strict'

import { userRoutes, getJsonRequestOptions, getSirenLink } from '../api-routes'
import fetch from 'node-fetch'

function getAuthenticatedUser(accessToken: AccessToken): Promise<AuthenticatedUser> {
    return fetch(userRoutes.getAuthenticatedUserUri, getJsonRequestOptions('GET', accessToken.token))
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

function getUserById(userId: number, accessToken: string): Promise<User>  {
    return fetch(userRoutes.getUserByIdUri(userId), getJsonRequestOptions('GET', accessToken))
        .then(res => res.status != 404 ? res.json() : null)
        .then(user => {
            if (!user) return null

            const links: SirenLink[] = Array.from(user.links)

            return {
                id: user.properties.id,
                username: user.properties.name,
                gitHubName: user.properties.gitHubName,
                gitHubUri: getSirenLink(links, 'github').href,
                avatarUri: getSirenLink(links, 'avatar').href,
            } as User
        })
}

function editUser(newName: string, accessToken: string): Promise<number> {
    return fetch(userRoutes.getAuthenticatedUserUri, getJsonRequestOptions('PUT', accessToken, { name: newName }))
        .then(res => res.status)
}

function deleteUser(accessToken: string) : Promise<number> {
    return fetch(userRoutes.getAuthenticatedUserUri, getJsonRequestOptions('DELETE', accessToken))
        .then(res => res.status)
}

export {
    getAuthenticatedUser,
    getUserById,
    editUser,
    deleteUser
}