'use strict'

import fetch from 'node-fetch'
import { getJsonRequestOptions, getSirenAction, getSirenLink, deliveryRoutes } from '../api-routes'

const DELIVERIES_LIST_LIMIT = 10

function getDeliveries(orgId: number, classroomNumber: number, assignmentNumber: number, 
    page: number, accessToken: string): Promise<Deliveries> {
    return fetch(
        deliveryRoutes.getPaginatedDeliveriesUri(orgId, classroomNumber, assignmentNumber, page, DELIVERIES_LIST_LIMIT),
        getJsonRequestOptions('GET', accessToken)
    )   
        .then(res => (res.status != 404 && res.status != 403) ? res.json() : null)
        .then(collection => {
            if (!collection) return null

            const entities = Array.from(collection.entities) as any[]
            const sirenActions: SirenAction[] = Array.from(collection.actions || [])

            const deliveries = entities.map(entity => {
                const dueDate = entity.properties.dueDate == null ? null : new Date(entity.properties.dueDate)
                const dueDateString = dueDate == null ? null : dueDate.toLocaleString()

                return {
                    id: entity.properties.id,
                    number: entity.properties.number,
                    tag: entity.properties.tag,
                    dueDate: dueDateString,
                    isDue: dueDate == null ? false : new Date() > dueDate,

                    canManage: false,
                } as Delivery
            })

            return {
                deliveries: deliveries,
                page: page,
                isLastPage: DELIVERIES_LIST_LIMIT * (collection.properties.pageIndex + 1) >= collection.properties.collectionSize,

                canManage: getSirenAction(sirenActions, 'create-delivery') != null,
            } as Deliveries
        })
}

function getParticipantDeliveries(orgId: number, classroomNumber: number, assignmentNumber: number, participantId: number,
    page: number, accessToken: string): Promise<Deliveries> {
    return fetch(
        deliveryRoutes.getPaginatedParticipantDeliveriesUri(orgId, classroomNumber, assignmentNumber, participantId, page, DELIVERIES_LIST_LIMIT),
        getJsonRequestOptions('GET', accessToken)
    )   
        .then(res => (res.status != 404 && res.status != 403) ? res.json() : null)
        .then(collection => {
            if (!collection) return null

            const entities = Array.from(collection.entities) as any[]
            const sirenActions: SirenAction[] = Array.from(collection.actions || [])

            const deliveries = entities.map(entity => {
                const dueDate = entity.properties.dueDate == null ? null : new Date(entity.properties.dueDate)
                const dueDateString = dueDate == null ? null : dueDate.toLocaleString()

                return {
                    id: entity.properties.id,
                    number: entity.properties.number,
                    tag: entity.properties.tag,
                    dueDate: dueDateString,
                    isDue: dueDate == null ? false : new Date() > dueDate,
                    isDelivered: entity.properties.isDelivered,

                    canManage: false,
                } as Delivery
            })

            return {
                deliveries: deliveries,
                page: page,
                isLastPage: DELIVERIES_LIST_LIMIT * (collection.properties.pageIndex + 1) >= collection.properties.collectionSize,

                canManage: getSirenAction(sirenActions, 'create-delivery') != null,
            } as Deliveries
        })
}

function createDelivery(orgId: number, classroomNumber: number, assignmentNumber: number, 
    tag: string, dueDate: Date, accessToken: string): Promise<ApiResponse> {

    return fetch(
        deliveryRoutes.getDeliveriesUri(orgId, classroomNumber, assignmentNumber), 
        getJsonRequestOptions('POST', accessToken, { 
            tag: tag,
            dueDate: dueDate == null ? null : dueDate.toISOString()
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
                // Delivery was created
                const entity = res.json

                const sirenActions: SirenAction[] = Array.from(entity.actions || [])
                content = {
                    id: entity.properties.id,
                    number: entity.properties.number,
                    tag: entity.properties.tag,
                    dueDate: entity.properties.dueDate,
                
                    canManage: getSirenAction(sirenActions, 'edit-delivery') != null,
                } as Delivery
            }

            return {
                status: res.status,
                content: content,
            } as ApiResponse
        })
}

function editDelivery(orgId: number, classroomNumber: number, assignmentNumber: number, deliveryNumber: number,
    newTag: string, newDueDate: Date, accessToken: string): Promise<ApiResponse> {
    return fetch(
        deliveryRoutes.getDeliveryUri(orgId, classroomNumber, assignmentNumber, deliveryNumber), 
        getJsonRequestOptions('PUT', accessToken, {
            tag: newTag,
            dueDate: newDueDate == null ? null : newDueDate.toISOString()
        }))
        .then(res => {
            return {
                status: res.status
            } as ApiResponse
        })
}

function deleteDelivery(orgId: number, classroomNumber: number, assignmentNumber: number, 
    deliveryNumber: number, accessToken: string): Promise<ApiResponse> {
    return fetch(deliveryRoutes.getDeliveryUri(orgId, classroomNumber, assignmentNumber, deliveryNumber), getJsonRequestOptions('DELETE', accessToken))
        .then(res => {
            return {
                status: res.status
            } as ApiResponse
        })
}

export {
    getDeliveries,
    getParticipantDeliveries,
    createDelivery,
    editDelivery,
    deleteDelivery
}