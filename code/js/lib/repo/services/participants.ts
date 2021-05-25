'use strict'

import fetch from 'node-fetch'
import { getJsonRequestOptions, getSirenAction, getSirenLink, participationRoutes } from '../api-routes'

const PARTICIPANTS_LIST_LIMIT = 10

function getUserParticipationInAssignment(assignmentId: number, accessToken: string): Promise<Participation> {
    return fetch(
        participationRoutes.getUserParticipationInAssignmentUri(assignmentId), 
        getJsonRequestOptions('GET', accessToken)
    )
        .then(res => res.status != 403 ? res.json() : null)
        .then(entity => {
            if (!entity) return null

            return {
                type: entity.properties.type,
                id: entity.properties.id,
                name: entity.properties.name
            } as Participation
        })
}

function getParticipantsOfAssignment(orgId: number, classroomNumber: number, 
    assignmentNumber: number, page: number, accessToken: string): Promise<Participants> {
    
    return fetch(
        participationRoutes.getPaginatedAssignmentParticipantsUri(orgId, classroomNumber, assignmentNumber, page, PARTICIPANTS_LIST_LIMIT),
        getJsonRequestOptions('GET', accessToken)
    )   
        .then(res => (res.status != 404 && res.status != 403) ? res.json() : null)
        .then(collection => {
            if (!collection) return null

            const entities = Array.from(collection.entities) as any[]
            const sirenActions: SirenAction[] = Array.from(collection.actions || [])

            const participants = entities.map(entity => {
                const participantLinks: SirenLink[] = Array.from(entity.links)

                return {
                    id: entity.properties.id,
                    name: entity.properties.name,
                    avatarUri: getSirenLink(participantLinks, 'avatar').href,
                } as Participant
            })

            return {
                participants: participants,
                type: collection.properties.participantsType,
                
                page: page,
                isLastPage: PARTICIPANTS_LIST_LIMIT * (collection.properties.pageIndex + 1) >= collection.properties.collectionSize,
                
                canManage: getSirenAction(sirenActions, 'add-participant-to-assignment') != null,
            } as Participants
        })
}

function removeParticipantFromAssignment(orgId: number, classroomNumber: number, assignmentNumber: number, 
    participantId: number, accessToken: string): Promise<ApiResponse> {

    return fetch(
        participationRoutes.getAssignmentParticipantUri(orgId, classroomNumber, assignmentNumber, participantId), 
        getJsonRequestOptions('DELETE', accessToken)
    )
        .then(res => {
            return { status: res.status } as ApiResponse
        })
}

export {
    getUserParticipationInAssignment,
    getParticipantsOfAssignment,
    removeParticipantFromAssignment
}