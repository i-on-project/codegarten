'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'
import { createDelivery, deleteDelivery, deleteParticipantDelivery, editDelivery, getDeliveries, getParticipantDeliveries, submitDelivery } from '../repo/services/deliveries'
import { INTERNAL_ERROR, requiresAuth } from './common-routes'

const router = expressRouter()

router.get('/orgs/:orgId/classrooms/:classroomNumber/assignments/:assignmentNumber/deliveries', requiresAuth, handlerGetDeliveries)
router.get('/orgs/:orgId/classrooms/:classroomNumber/assignments/:assignmentNumber/participants/:participantId/deliveries', requiresAuth, handlerGetParticipantDeliveries)

router.post('/orgs/:orgId/classrooms/:classroomNumber/assignments/:assignmentNumber/deliveries', requiresAuth, handlerCreateDelivery)
router.put('/orgs/:orgId/classrooms/:classroomNumber/assignments/:assignmentNumber/deliveries/:deliveryNumber', requiresAuth, handlerEditDelivery)
router.put('/orgs/:orgId/classrooms/:classroomNumber/assignments/:assignmentNumber/participants/:participantId/deliveries/:deliveryNumber', requiresAuth, handlerSubmitDelivery)

router.delete('/orgs/:orgId/classrooms/:classroomNumber/assignments/:assignmentNumber/deliveries/:deliveryNumber', requiresAuth, handlerDeleteDelivery)
router.delete('/orgs/:orgId/classrooms/:classroomNumber/assignments/:assignmentNumber/participants/:participantId/deliveries/:deliveryNumber', requiresAuth, handlerDeleteParticipantDelivery)

function handlerGetDeliveries(req: Request, res: Response, next: NextFunction) {
    if (!req.xhr) return next()

    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    const assignmentNumber = Number(req.params.assignmentNumber)

    if (isNaN(orgId) || isNaN(classroomNumber) || isNaN(assignmentNumber)) return next()

    const page = Number(req.query.page) || 0

    getDeliveries(orgId, classroomNumber, assignmentNumber, page, req.user.accessToken.token)
        .then(deliveries => {
            if (!deliveries) return next()

            res.render('assignment-fragments/assignment-deliveries', {
                layout: false,

                deliveries: deliveries.deliveries,
                isEmpty: deliveries.deliveries.length == 0,
                page: deliveries.page,

                hasPrev: deliveries.page > 0,
                prevPage: deliveries.page > 0 ? deliveries.page - 1 : 0,

                hasNext: !deliveries.isLastPage,
                nextPage: deliveries.page + 1,

                assignmentNumber: assignmentNumber,
                classroomNumber: classroomNumber,
                orgId: orgId,
                
                canManage: deliveries.canManage
            })
        })
        .catch(err => next(INTERNAL_ERROR))
}

function handlerGetParticipantDeliveries(req: Request, res: Response, next: NextFunction) {
    if (!req.xhr) return next()

    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    const assignmentNumber = Number(req.params.assignmentNumber)
    const participantId = Number(req.params.participantId)

    if (isNaN(orgId) || isNaN(classroomNumber) || isNaN(assignmentNumber) || isNaN(participantId)) return next()

    const page = Number(req.query.page) || 0

    getParticipantDeliveries(orgId, classroomNumber, assignmentNumber, participantId, page, req.user.accessToken.token)
        .then(deliveries => {
            if (!deliveries) return next()

            res.render('assignment-fragments/assignment-deliveries', {
                layout: false,

                deliveries: deliveries.deliveries,
                isEmpty: deliveries.deliveries.length == 0,
                page: deliveries.page,

                hasPrev: deliveries.page > 0,
                prevPage: deliveries.page > 0 ? deliveries.page - 1 : 0,

                hasNext: !deliveries.isLastPage,
                nextPage: deliveries.page + 1,

                assignmentNumber: assignmentNumber,
                classroomNumber: classroomNumber,
                orgId: orgId,
                participantId: participantId,
                
                canManage: deliveries.canManage
            })
        })
        .catch(err => next(INTERNAL_ERROR))
}


function handlerCreateDelivery(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    const assignmentNumber = Number(req.params.assignmentNumber)

    if (isNaN(orgId) || isNaN(classroomNumber) || isNaN(assignmentNumber)) return next()

    const tag = req.body.tag

    if (!tag) { 
        return res.send({
            wasCreated: false,
            message: 'Tag was not specified'
        })
    }

    // Assuming body date is in ISO format
    const dueDate = req.body.dueDate == null ? null : new Date(req.body.dueDate)

    createDelivery(orgId, classroomNumber, assignmentNumber, tag, dueDate, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 201: 
                    req.flash('success', 'Delivery created successfully')
                    break
                case 409:
                    message = 'Delivery tag already exists'
                    break
                default:
                    message = 'Failed to create delivery'
            }

            res.send({
                wasCreated: result.status == 201,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasCreated: false,
                message: 'Failed to create delivery'
            })
        })
}

function handlerSubmitDelivery(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    const assignmentNumber = Number(req.params.assignmentNumber)
    const participantId = Number(req.params.participantId)
    const deliveryNumber = Number(req.params.deliveryNumber)

    if (isNaN(orgId) || isNaN(classroomNumber) || isNaN(assignmentNumber) || isNaN(participantId)|| isNaN(deliveryNumber)) return next()

    submitDelivery(orgId, classroomNumber, assignmentNumber, participantId, deliveryNumber, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 201:
                    message = 'Delivery submitted successfully'
                    req.flash('success', message)
                    break
                case 403:
                    message = 'Delivery has already been submitted or repo is empty'
                    break
                default:
                    message = 'Failed to submit delivery'
            }

            res.send({
                wasSubmitted: result.status == 201,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasSubmitted: false,
                message: 'Failed to submit delivery'
            })
        })
}

function handlerEditDelivery(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    const assignmentNumber = Number(req.params.assignmentNumber)
    const deliveryNumber = Number(req.params.deliveryNumber)

    if (isNaN(orgId) || isNaN(classroomNumber) || isNaN(assignmentNumber) || isNaN(deliveryNumber)) return next()

    const newTag = req.body.tag
    const newDueDate = req.body.dueDate

    if (!newTag && !newDueDate) { 
        return res.send({
            wasEdited: false,
            message: 'You need to change at least one field'
        })
    }

    // Assuming body date is in ISO format
    const newParsedDueDate = newDueDate == null ? null : new Date(newDueDate)

    editDelivery(orgId, classroomNumber, assignmentNumber, deliveryNumber, newTag, newParsedDueDate, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 200:
                    message = 'Delivery edited successfully'
                    req.flash('success', message)
                    break
                case 409:
                    message = 'Delivery tag already exists'
                    break
                default:
                    message = 'Failed to edit delivery'
            }

            res.send({
                wasEdited: result.status == 200,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasEdited: false,
                message: 'Failed to edit delivery'
            })
        })
}

function handlerDeleteDelivery(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    const assignmentNumber = Number(req.params.assignmentNumber)
    const deliveryNumber = Number(req.params.deliveryNumber)

    if (isNaN(orgId) || isNaN(classroomNumber) || isNaN(assignmentNumber) || isNaN(deliveryNumber)) return next()

    deleteDelivery(orgId, classroomNumber, assignmentNumber, deliveryNumber, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 200:
                    message = 'Delivery was deleted successfully'
                    break
                default:
                    message = 'Failed to delete delivery'
            }

            res.send({
                wasDeleted: result.status == 200,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasDeleted: false,
                message: 'Failed to delete delivery'
            })
        })
}

function handlerDeleteParticipantDelivery(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    const assignmentNumber = Number(req.params.assignmentNumber)
    const participantId = Number(req.params.participantId)
    const deliveryNumber = Number(req.params.deliveryNumber)

    if (isNaN(orgId) || isNaN(classroomNumber) || isNaN(assignmentNumber) || isNaN(participantId)|| isNaN(deliveryNumber)) return next()

    deleteParticipantDelivery(orgId, classroomNumber, assignmentNumber, participantId, deliveryNumber, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 200:
                    message = 'Delivery submission deleted successfully'
                    req.flash('success', message)
                    break
                case 403:
                    message = 'No permission to delete a delivery for another participant'
                    break
                default:
                    message = 'Failed to submit delivery'
            }

            res.send({
                wasDeleted: result.status == 200,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasDeleted: false,
                message: 'Failed to delete delivery submission'
            })
        })
}

export = router