'use strict'

import { invitationRoutes, getJsonRequestOptions, getSirenLink, getSirenAction } from '../api-routes'
import fetch from 'node-fetch'

const INVITATION_TEAM_LIST_LIMIT = 9

function getInvitationInfo(invitationId: string, accessToken: string): Promise<Invitation> {
    return fetch(invitationRoutes.getInvitationUri(invitationId), getJsonRequestOptions('GET', accessToken))
        .then(res => res.status != 404 ? res.json() : null)
        .then(invitation => {
            if (!invitation) return null
            const type = invitation.class[0]

            switch (type) {
                case 'classroomInvitation':
                    return getClassroomInvitationInfo(invitation)
                case 'assignmentInvitation':
                    return getAssignmentInvitationInfo(invitation)
                default:
                    return null
            }
        })
}

function getClassroomInvitationInfo(invitation: any): Invitation {
    const links: SirenLink[] = Array.from(invitation.links)
    const orgUri = getSirenLink(links, 'organizationGitHub').href

    const classroomInvitation = {
        id: invitation.properties.id,
        number: invitation.properties.number,
        name: invitation.properties.name,
        description: invitation.properties.description,
        
        orgId: invitation.properties.orgId,
        organization: invitation.properties.organization,
        orgUri: orgUri
    } as ClassroomInvitation

    return {
        type: 'classroomInvitation',
        invitation: classroomInvitation
    } as Invitation
}

function getAssignmentInvitationInfo(invitation: any): Invitation {
    const links: SirenLink[] = Array.from(invitation.links)
    const orgUri = getSirenLink(links, 'organizationGitHub').href

    const assignmentInvitation = {
        id: invitation.properties.id,
        number: invitation.properties.number,
        name: invitation.properties.name,
        description: invitation.properties.description,
        type: invitation.properties.type,

        classroomId: invitation.properties.classroomId,
        classroomNumber: invitation.properties.classroomNumber,
        classroom: invitation.properties.classroom,
        
        orgId: invitation.properties.orgId,
        organization: invitation.properties.organization,
        orgUri: orgUri
    } as AssignmentInvitation

    return {
        type: 'assignmentInvitation',
        invitation: assignmentInvitation
    } as Invitation
}

function getInvitationTeams(invitationId: string, page: number, accessToken: string): Promise<Teams> {
    return fetch(
        invitationRoutes.getPaginatedInvitationTeamsUri(invitationId, page, INVITATION_TEAM_LIST_LIMIT), 
        getJsonRequestOptions('GET', accessToken)
    )
        .then(res => (res.status != 404) ? res.json() : null)
        .then(collection => {
            if (!collection) return null

            const entities = Array.from(collection.entities) as any[]

            const teams = entities.map(entity => {
                return {
                    id: entity.properties.id,
                    number: entity.properties.number,
                    name: entity.properties.name,
                    classroom: entity.properties.classroom,
                    organization: entity.properties.organization,
                } as Team
            })

            return {
                teams: teams,
                page: page,
                isLastPage: INVITATION_TEAM_LIST_LIMIT * (collection.properties.pageIndex + 1) >= collection.properties.collectionSize,
            
                organization: collection.properties.organization,
            } as Teams
        })
}

function joinInvitation(invitationId: string, teamId: number, accessToken: string): Promise<JoinInvitation> {
    const body = {
        teamId: teamId
    }

    return fetch(invitationRoutes.getInvitationUri(invitationId), getJsonRequestOptions('PUT', accessToken, body))
        .then(async res => {
            return {
                status: res.status,
                content: await res.json()
            } as ApiResponse
        })
        .then(resp => {
            const links: SirenLink[] = Array.from(resp.content.links)
            const orgUri = getSirenLink(links, 'organizationInviteGitHub').href
            const repoUri = getSirenLink(links, 'repositoryInviteGitHub')?.href


            return {
                status: resp.status,
                isOrgInvitePending: resp.content.properties.isOrgInvitePending,
                orgUri: orgUri,
                repoUri: repoUri
            } as JoinInvitation
        })
}

export {
    getInvitationInfo,
    getInvitationTeams,
    joinInvitation,
}