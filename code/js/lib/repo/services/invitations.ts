'use strict'

import { invitationRoutes, getJsonRequestOptions, getSirenLink, getSirenAction } from '../api-routes'
import fetch from 'node-fetch'

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
    const classroomInvitation = {
        id: invitation.properties.id,
        name: invitation.properties.name,
        description: invitation.properties.description,
        
        organization: invitation.properties.organization,
        orgUri: 'TBA' //TODO
    } as ClassroomInvitation

    return {
        type: 'classroomInvitation',
        invitation: classroomInvitation
    } as Invitation
}

function getAssignmentInvitationInfo(invitation: any): Invitation {
    const assignmentInvitation = {
        id: invitation.properties.id,
        name: invitation.properties.name,
        description: invitation.properties.description,
        type: invitation.properties.type,

        classroom: invitation.properties.classroom,
        
        organization: invitation.properties.organization,
        orgUri: 'TBA' //TODO
    } as AssignmentInvitation

    return {
        type: 'assignmentInvitation',
        invitation: assignmentInvitation
    } as Invitation
}

function joinInvitation(invitationId: string, accessToken: string): Promise<ApiResponse> {
    return fetch(invitationRoutes.getInvitationUri(invitationId), getJsonRequestOptions('PUT', accessToken))
        .then(res => {
            return {
                status: res.status
            } as ApiResponse
        })
}

export {
    getInvitationInfo,
    joinInvitation,
}