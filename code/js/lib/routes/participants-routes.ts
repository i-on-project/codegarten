'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'
import { removeParticipantFromAssignment } from '../repo/services/participants'

import { INTERNAL_ERROR, requiresAuth } from './common-routes'

const router = expressRouter()

router.delete('/orgs/:orgId/classrooms/:classroomNumber/assignments/:assignmentNumber/participants/:participantId', 
    requiresAuth, removeAssignmentParticipant)

function removeAssignmentParticipant(req: Request, res: Response, next: NextFunction) {
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
                    message = 'Failed to remove user'
            }

            res.send({
                wasRemoved: result.status == 200,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasRemoved: false,
                message: 'Failed to remove user'
            })
        })
}

export = router