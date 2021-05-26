'use strict'

import { classroomRoutes, getJsonRequestOptions, getSirenLink, getSirenAction } from '../api-routes'
import fetch from 'node-fetch'

const CLASSROOM_LIST_LIMIT = 9

function getClassrooms(orgId: number, page: number, accessToken: string): Promise<Classrooms> {
    return fetch(classroomRoutes.getPaginatedClassroomsUri(orgId, page, CLASSROOM_LIST_LIMIT), getJsonRequestOptions('GET', accessToken))
        .then(res => res.status != 403 ? res.json() : null)
        .then(collection => {
            if (!collection) return null

            const entities = Array.from(collection.entities) as any[]
            const sirenActions: SirenAction[] = Array.from(collection.actions || [])
            const links: SirenLink[] = Array.from(collection.links)
            const orgUri = getSirenLink(links, 'organizationGitHub').href

            const classrooms = entities.map(entity => {
                return {
                    id: entity.properties.id,
                    inviteCode: entity.properties.inviteCode,
                    number: entity.properties.number,
                    name: entity.properties.name,
                    description: entity.properties.description,
                    organization: entity.properties.organization,
                    organizationUri: orgUri,
                
                    canManage: null,
                } as Classroom
            })

            return {
                classrooms: classrooms,
                page: page,
                isLastPage: CLASSROOM_LIST_LIMIT * (collection.properties.pageIndex + 1) >= collection.properties.collectionSize,
            
                organization: collection.properties.organization,
                organizationUri: orgUri,
                canCreate: getSirenAction(sirenActions, 'create-classroom') != null,
            } as Classrooms
        })
}

function getClassroom(orgId: number, classroomNumber: number, accessToken: string): Promise<Classroom> {
    return fetch(classroomRoutes.getClassroomUri(orgId, classroomNumber), getJsonRequestOptions('GET', accessToken))
        .then(res => (res.status != 404 && res.status != 403) ? res.json() : null)
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
                organization: entity.properties.organization,
                organizationUri: orgUri,
            
                canManage: getSirenAction(sirenActions, 'edit-classroom') != null,
            } as Classroom
        })
}

function createClassroom(orgId: number, name: string, description: string, accessToken: string): Promise<ApiResponse> {
    return fetch(
        classroomRoutes.getClassroomsUri(orgId), 
        getJsonRequestOptions('POST', accessToken, { 
            name: name, 
            description: description 
        })
    )
        .then(async (res) => {
            return {
                status: res.status,
                json: res.status == 201 ? await res.json() : null
            }
        }).then(res => {
            let content = null
            if (res.status == 201) {
                // Classroom was created
                const entity = res.json

                const links: SirenLink[] = Array.from(entity.links)
                const orgUri = getSirenLink(links, 'organizationGitHub').href

                content = {
                    id: entity.properties.id,
                    inviteCode: entity.properties.inviteCode,
                    number: entity.properties.number,
                    name: entity.properties.name,
                    description: entity.properties.description,
                    organization: entity.properties.organization,
                    organizationUri: orgUri,
                
                    canManage: null,
                } as Classroom
            }

            return {
                status: res.status,
                content: content,
            } as ApiResponse
        })
}

function editClassroom(orgId: number, classroomNumber: number, newName: string, newDescription: string, accessToken: string): Promise<ApiResponse> {
    return fetch(
        classroomRoutes.getClassroomUri(orgId, classroomNumber), 
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

function deleteClassroom(orgId: number, classroomNumber: number, accessToken: string): Promise<ApiResponse> {
    return fetch(classroomRoutes.getClassroomUri(orgId, classroomNumber), getJsonRequestOptions('DELETE', accessToken))
        .then(res => {
            return {
                status: res.status
            } as ApiResponse
        })
}

export {
    getClassrooms,
    getClassroom,
    createClassroom,
    editClassroom,
    deleteClassroom
}
