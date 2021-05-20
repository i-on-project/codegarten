'use strict'

import { getJsonRequestOptions, getSirenAction, getSirenLink, teamRoutes } from '../api-routes'
import fetch from 'node-fetch'

const TEAM_LIST_LIMIT = 10

function getTeams(orgId: number, classroomNumber: number, page: number, accessToken: string): Promise<Teams> {
    return fetch(teamRoutes.getPaginatedTeamsUri(orgId, classroomNumber, page, TEAM_LIST_LIMIT), getJsonRequestOptions('GET', accessToken))
        .then(res => (res.status != 404 && res.status != 401) ? res.json() : null)
        .then(collection => {
            if (!collection) return null

            const entities = Array.from(collection.entities) as any[]
            const sirenActions: SirenAction[] = Array.from(collection.actions || [])
            const links: SirenLink[] = Array.from(collection.links)
            const orgUri = getSirenLink(links, 'organizationGitHub').href

            const teams = entities.map(entity => {
                const teamLinks: SirenLink[] = Array.from(entity.links)

                return {
                    id: entity.properties.id,
                    number: entity.properties.number,
                    name: entity.properties.name,
                    classroom: entity.properties.classroom,
                    organization: entity.properties.organization,
                    organizationUri: orgUri,
                    avatarUri: getSirenLink(teamLinks, 'avatar').href,
                
                    canManage: null,
                } as Team
            })

            return {
                teams: teams,
                page: page,
                isLastPage: TEAM_LIST_LIMIT * (collection.properties.pageIndex + 1) >= collection.properties.collectionSize,
            
                organization: collection.properties.organization,
                organizationUri: orgUri,
                canManage: getSirenAction(sirenActions, 'create-team') != null,
            } as Teams
        })
}

function getTeam(orgId: number, classroomNumber: number, teamNumber: number, accessToken: string): Promise<Team> {
    return fetch(teamRoutes.getTeamUri(orgId, classroomNumber, teamNumber), getJsonRequestOptions('GET', accessToken))
        .then(res => (res.status != 404 && res.status != 401) ? res.json() : null)
        .then(entity => {
            if (!entity) return null

            const links: SirenLink[] = Array.from(entity.links)
            const sirenActions: SirenAction[] = Array.from(entity.actions || [])
            const orgUri = getSirenLink(links, 'organizationGitHub').href
            const avatarUri = getSirenLink(links, 'avatar').href
            const gitHubUri = getSirenLink(links, 'github').href

            return {
                id: entity.properties.id,
                number: entity.properties.number,
                name: entity.properties.name,
                classroom: entity.properties.classroom,
                organization: entity.properties.organization,
                organizationUri: orgUri,
                avatarUri: avatarUri,
                gitHubUri: gitHubUri,
            
                canManage: getSirenAction(sirenActions, 'edit-team') != null,
            } as Team
        })
}

function createTeam(orgId: number, classroomNumber: number, name: string, accessToken: string): Promise<ApiResponse> {
    //TODO: Change this when API changes for resource creation go through
    return fetch(
        teamRoutes.getTeamsUri(orgId, classroomNumber), 
        getJsonRequestOptions('POST', accessToken, { 
            name: name
        })
    )
        .then(res => {
            if (res.status == 201) {
                // Team was created
                return fetch(res.headers.get('Location'), getJsonRequestOptions('GET', accessToken))
                    .then(res => res.json())
                    .then(entity => {
                        const links: SirenLink[] = Array.from(entity.links)
                        const sirenActions: SirenAction[] = Array.from(entity.actions || [])
                        const orgUri = getSirenLink(links, 'organizationGitHub').href
                        const avatarUri = getSirenLink(links, 'avatar').href

                        const team = {
                            id: entity.properties.id,
                            number: entity.properties.number,
                            name: entity.properties.name,
                            classroom: entity.properties.classroom,
                            organization: entity.properties.organization,
                            organizationUri: orgUri,
                            avatarUri: avatarUri,
                        
                            canManage: getSirenAction(sirenActions, 'edit-team') != null,
                        } as Team

                        return {
                            status: res.status,
                            content: team,
                        } as ApiResponse
                    })
            }

            return {
                status: res.status,
                content: null,
            } as ApiResponse
        })
}

function editTeam(orgId: number, classroomNumber: number, teamNumber: number, newName: string, accessToken: string): Promise<ApiResponse> {
    return fetch(
        teamRoutes.getTeamUri(orgId, classroomNumber, teamNumber), 
        getJsonRequestOptions('PUT', accessToken, {
            name: newName
        }))
        .then(res => {
            return {
                status: res.status
            } as ApiResponse
        })
}

function deleteTeam(orgId: number, classroomNumber: number, teamNumber: number, accessToken: string): Promise<ApiResponse> {
    return fetch(teamRoutes.getTeamUri(orgId, classroomNumber, teamNumber), getJsonRequestOptions('DELETE', accessToken))
        .then(res => {
            return {
                status: res.status
            } as ApiResponse
        })
}

export {
    getTeams,
    getTeam,
    createTeam,
    editTeam,
    deleteTeam
}