'use strict'

import { userRoutes, getJsonRequestOptions, getSirenLink, getSirenAction } from '../api-routes'
import fetch from 'node-fetch'

const CLASSROOM_USER_LIST_LIMIT = 10
const TEAM_USER_LIST_LIMIT = 8

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

function getClassroomUsers(orgId: number, classroomNumber: number, authUser: AuthenticatedUser, page: number, accessToken: string): Promise<Users>  {
    return fetch(userRoutes.getClassroomUsersUri(orgId, classroomNumber, page, CLASSROOM_USER_LIST_LIMIT), getJsonRequestOptions('GET', accessToken))
        .then(res => (res.status != 404 && res.status != 403) ? res.json() : null)
        .then(collection => {
            if (!collection) return null

            const entities = Array.from(collection.entities) as any[]
            const sirenActions: SirenAction[] = Array.from(collection.actions || [])

            const users = entities.map(entity => {
                const userLinks: SirenLink[] = Array.from(entity.links)

                return {
                    id: entity.properties.id,
                    username: entity.properties.name,
                    role: entity.properties.role,
                    isTeacher: entity.properties.role == 'teacher',
                    avatarUri: getSirenLink(userLinks, 'avatar').href,
                    isAuthUser: entity.properties.id == authUser.id
                } as User
            })

            return {
                users: users,
                page: page,
                isLastPage: CLASSROOM_USER_LIST_LIMIT * (collection.properties.pageIndex + 1) >= collection.properties.collectionSize,

                canManage: getSirenAction(sirenActions, 'add-user-to-classroom') != null,
            } as Users
        })
}

function getTeamUsers(orgId: number, classroomNumber: number, teamNumber: number, authUser: AuthenticatedUser, page: number, accessToken: string): Promise<Users>  {
    return fetch(userRoutes.getTeamUsersUri(orgId, classroomNumber, teamNumber, page, TEAM_USER_LIST_LIMIT), getJsonRequestOptions('GET', accessToken))
        .then(res => (res.status != 404 && res.status != 403) ? res.json() : null)
        .then(collection => {
            if (!collection) return null

            const entities = Array.from(collection.entities) as any[]
            const sirenActions: SirenAction[] = Array.from(collection.actions || [])

            const users = entities.map(entity => {
                const userLinks: SirenLink[] = Array.from(entity.links)

                return {
                    id: entity.properties.id,
                    username: entity.properties.name,
                    avatarUri: getSirenLink(userLinks, 'avatar').href,
                    isAuthUser: entity.properties.id == authUser.id
                } as User
            })

            return {
                users: users,
                page: page,
                isLastPage: TEAM_USER_LIST_LIMIT * (collection.properties.pageIndex + 1) >= collection.properties.collectionSize,

                canManage: getSirenAction(sirenActions, 'add-user-to-team') != null,
            } as Users
        })
}

function editUser(newName: string, accessToken: string): Promise<ApiResponse> {
    return fetch(userRoutes.getAuthenticatedUserUri, getJsonRequestOptions('PUT', accessToken, { name: newName }))
        .then(res => {
            return { status: res.status } as ApiResponse
        })
}

function editClassroomUserMembership(orgId: number, classroomNumber: number, userId: number, role: string, accessToken: string): Promise<ApiResponse> {
    return fetch(userRoutes.getClassroomUserUri(orgId, classroomNumber, userId), getJsonRequestOptions('PUT', accessToken, { role: role }))
        .then(res => {
            return { status: res.status } as ApiResponse
        })
}

function deleteUser(accessToken: string) : Promise<ApiResponse> {
    return fetch(userRoutes.getAuthenticatedUserUri, getJsonRequestOptions('DELETE', accessToken))
        .then(res => {
            return { status: res.status } as ApiResponse
        })
}

function removeUserFromClassroom(orgId: number, classroomNumber: number, userId: number, accessToken: string) : Promise<ApiResponse> {
    return fetch(userRoutes.getClassroomUserUri(orgId, classroomNumber, userId), getJsonRequestOptions('DELETE', accessToken))
        .then(async res => {
            return { 
                status: res.status,
                content: await res.json()
            } as ApiResponse
        })
}

function removeUserFromTeam(orgId: number, classroomNumber: number, teamNumber: number, userId: number, accessToken: string) : Promise<ApiResponse> {
    return fetch(userRoutes.getTeamUserUri(orgId, classroomNumber, teamNumber, userId), getJsonRequestOptions('DELETE', accessToken))
        .then(res => {
            return { 
                status: res.status,
            } as ApiResponse
        })
}

export {
    getAuthenticatedUser,
    getUserById,
    getClassroomUsers,
    getTeamUsers,
    editUser,
    editClassroomUserMembership,
    deleteUser,
    removeUserFromClassroom,
    removeUserFromTeam
}