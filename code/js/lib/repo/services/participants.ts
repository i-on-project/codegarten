'use strict'

import fetch from 'node-fetch'
import { getJsonRequestOptions, participationRoutes } from '../api-routes'

// TODO: Change once API changes go through (when teacher has participation)
function getUserParticipationInAssignment(assignmentId: number, accessToken: string): Promise<Participation> {
    return fetch(
        participationRoutes.getUserParticipationInAssignmentUri(assignmentId), 
        getJsonRequestOptions('GET', accessToken)
    ).then(res => {
        if (res.status == 401) return null
        if (res.status == 404) {
            // hard-coded, to be changed later...
            return {
                properties: {
                    type: 'teacher'
                }  
            }
        }
        if (res.status == 200) return res.json()
        return null
    })
        .then(entity => {
            if (!entity) return null

            return {
                type: entity.properties.type,
                id: entity.properties.id,
                name: entity.properties.name
            } as Participation
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
    removeParticipantFromAssignment
}