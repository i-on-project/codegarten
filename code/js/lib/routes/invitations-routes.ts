'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'

import { INTERNAL_ERROR, requiresAuth } from './common-routes'
import { getInvitationInfo, joinInvitation } from '../repo/services/invitations'

const router = expressRouter()

router.get('/i/:invitationId', requiresAuth, handlerGetInvitationInformation)

router.put('/i/:invitationId', requiresAuth, handlerJoinInvitation)

function handlerGetInvitationInformation(req: Request, res: Response, next: NextFunction) {
    const invitationId = req.params.invitationId

    getInvitationInfo(invitationId, req.user.accessToken.token)
        .then(invitation => {
            if (!invitation) return next()

            const inv = invitation.invitation
            switch (invitation.type) {
                case 'classroomInvitation':
                    return res.render('invitations/classroom-invitation', {
                        invitation: inv
                    })
                case 'assignmentInvitation':
                    return res.render('invitations/assignment-invitation', {
                        invitation: inv
                    })
                default:
                    return next()
            }
        })
        .catch(err => next(INTERNAL_ERROR))
}

function handlerJoinInvitation(req: Request, res: Response, next: NextFunction) {
    const invitationId = req.params.invitationId

    joinInvitation(invitationId, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 201: 
                    message = '/' // TODO: url of invitation
                    break
                case 403:
                    message = 'Could not accept invitation'
                    break
                default:
                    message = 'Could not accept invitation'
            }

            res.send({
                wasAccepted: result.status == 201,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasAccepted: false,
                message: 'Could not accept invitation'
            })
        })
}

export = router