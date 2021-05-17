'use strict'

import { assignmentRoutes, getJsonRequestOptions, getSirenLink, getSirenAction } from '../api-routes'
import fetch from 'node-fetch'

const ASSIGNMENT_LIST_LIMIT = 10

function getAssignments(orgId: number, classroomNumber: number, page: number, accessToken: string): Promise<Assignments> {
    return fetch(
        assignmentRoutes.getPaginatedAssignmentsUri(orgId, classroomNumber, page, ASSIGNMENT_LIST_LIMIT), 
        getJsonRequestOptions('GET', accessToken)
    )
        .then(res => (res.status != 404 && res.status != 401) ? res.json() : null)
        .then(collection => {
            if (!collection) return null

            const entities = Array.from(collection.entities) as any[]
            const sirenActions: SirenAction[] = Array.from(collection.actions || [])
            const links: SirenLink[] = Array.from(collection.links)
            const orgUri = getSirenLink(links, 'organizationGitHub').href

            const assignments = entities.map(entity => {
                return {
                    id: entity.properties.id,
                    inviteCode: entity.properties.inviteCode,
                    number: entity.properties.number,
                    name: entity.properties.name,
                    description: entity.properties.description,
                    isGroup: entity.properties.type == 'group',

                    organization: entity.properties.organization,
                    organizationUri: orgUri,

                    classroom: entity.properties.classroom,
                
                    canManage: null,
                } as Assignment
            })

            return {
                assignments: assignments,
                page: page,
                isLastPage: ASSIGNMENT_LIST_LIMIT * (collection.properties.pageIndex + 1) >= collection.properties.collectionSize,
            
                organization: collection.properties.organization,
                organizationUri: orgUri,

                classroom: collection.properties.classroom,
                canCreate: getSirenAction(sirenActions, 'create-assignment') != null,
            } as Assignments
        })
}

export {
    getAssignments,
}