'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'
import { getParticipantsOfAssignment, removeParticipantFromAssignment } from '../repo/services/participants'

import { INTERNAL_ERROR, requiresAuth } from './common-routes'

const router = expressRouter()

router.get('/orgs/:orgId/classrooms/:classroomNumber/assignments/:assignmentNumber/participants',
    requiresAuth, handlerGetAssignmentParticipants)

router.delete('/orgs/:orgId/classrooms/:classroomNumber/assignments/:assignmentNumber/participants/:participantId', 
    requiresAuth, handlerRemoveAssignmentParticipant)

function handlerGetAssignmentParticipants(req: Request, res: Response, next: NextFunction) {
    if (!req.xhr) return next()

    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    const assignmentNumber = Number(req.params.assignmentNumber)

    if (isNaN(orgId) || isNaN(classroomNumber) || isNaN(assignmentNumber)) return next()

    const page = Number(req.query.page) || 0

    getParticipantsOfAssignment(orgId, classroomNumber, assignmentNumber, page, req.user.accessToken.token)
        .then(participants => {
            if (!participants) return next()

            res.render('assignment-fragments/assignment-participants', {
                layout: false,

                isGroup: participants.type == 'team',
                participants: participants.participants,
                isEmpty: participants.participants.length == 0,
                page: participants.page,

                hasPrev: participants.page > 0,
                prevPage: participants.page > 0 ? participants.page - 1 : 0,

                hasNext: !participants.isLastPage,
                nextPage: participants.page + 1,

                assignmentNumber: assignmentNumber,
                classroomNumber: classroomNumber,
                orgId: orgId,
                
                canManage: participants.canManage
            })
        })
        .catch(err => next(INTERNAL_ERROR))
}

function handlerRemoveAssignmentParticipant(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    const assignmentNumber = Number(req.params.assignmentNumber)
    const participantId = Number(req.params.participantId)

    if (isNaN(orgId) || isNaN(classroomNumber) || isNaN(assignmentNumber)|| isNaN(participantId)) return next()

    removeParticipantFromAssignment(orgId, classroomNumber, assignmentNumber, participantId, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 200:
                    message = `/orgs/${orgId}/classrooms/${classroomNumber}/assignments`
                    break
                default:
                    message = 'Failed to remove participant'
            }

            res.send({
                wasRemoved: result.status == 200,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasRemoved: false,
                message: 'Failed to remove participant'
            })
        })
}

export = router