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

function getAssignment(orgId: number, classroomNumber: number, assignmentNumber: number, accessToken: string): Promise<Assignment> {
    return fetch(assignmentRoutes.getAssignmentUri(orgId, classroomNumber, assignmentNumber), getJsonRequestOptions('GET', accessToken))
        .then(res => (res.status != 404 && res.status != 401) ? res.json() : null)
        .then(entity => {
            if (!entity) return null

            const links: SirenLink[] = Array.from(entity.links)
            const sirenActions: SirenAction[] = Array.from(entity.actions || [])
            const orgUri = getSirenLink(links, 'organizationGitHub').href

            return {
                id: entity.properties.id,
                inviteCode: entity.properties.inviteCode,
                number: entity.properties.number,
                name: entity.properties.name,
                description: entity.properties.description,
                isGroup: entity.properties.type == 'group',
                repoPrefix: entity.properties.repoPrefix,
                repoTemplate: entity.properties.repoTemplate,

                organization: entity.properties.organization,
                organizationUri: orgUri,

                classroom: entity.properties.classroom,
            
                canManage: getSirenAction(sirenActions, 'edit-assignment') != null,
            } as Assignment
        })
}

function createAssignment(orgId: number, classroomNumber: number, name: string, description: string, 
    type: string, repoPrefix: string, repoTemplate: string, accessToken: string): Promise<ApiResponse> {
        
    //TODO: Change this when API changes for resource creation go through
    return fetch(
        assignmentRoutes.getAssignmentsUri(orgId, classroomNumber), 
        getJsonRequestOptions('POST', accessToken, { 
            name: name, 
            description: description,
            type: type,
            repoPrefix: repoPrefix,
            repoTemplate: repoTemplate
        })
    )
        .then(res => {
            if (res.status == 201) {
                // Classroom was created
                return fetch(res.headers.get('Location'), getJsonRequestOptions('GET', accessToken))
                    .then(res => res.json())
                    .then(entity => {
                        const links: SirenLink[] = Array.from(entity.links)
                        const orgUri = getSirenLink(links, 'organizationGitHub').href

                        const assignment =  {
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

                        return {
                            status: res.status,
                            content: assignment,
                        } as ApiResponse
                    })
            }

            return {
                status: res.status,
                content: null,
            } as ApiResponse
        })
}

function editAssignment(orgId: number, classroomNumber: number, assignmentNumber: number, 
    newName: string, newDescription: string, accessToken: string): Promise<ApiResponse> {
    
    return fetch(
        assignmentRoutes.getAssignmentUri(orgId, classroomNumber, assignmentNumber), 
        getJsonRequestOptions('PUT', accessToken, {
            name: newName,
            description: newDescription
        }))
        .then(res => {
            return {
                status: res.status
            } as ApiResponse
        })
}

function deleteAssignment(orgId: number, classroomNumber: number, assignmentNumber: number, accessToken: string): Promise<ApiResponse> {
    return fetch(assignmentRoutes.getAssignmentUri(orgId, classroomNumber, assignmentNumber), getJsonRequestOptions('DELETE', accessToken))
        .then(res => {
            return {
                status: res.status
            } as ApiResponse
        })
}


export {
    getAssignments,
    getAssignment,
    createAssignment,
    editAssignment,
    deleteAssignment
}