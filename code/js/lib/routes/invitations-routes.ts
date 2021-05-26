'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'

import { INTERNAL_ERROR, requiresAuth } from './common-routes'
import { getInvitationInfo, getInvitationTeams, joinInvitation } from '../repo/services/invitations'
import { getUserParticipationInClassroom, getUserParticipationInAssignment } from '../repo/services/participants'

const router = expressRouter()

router.get('/i/:invitationId', requiresAuth, handlerGetInvitationInformation)
router.get('/i/:invitationId/teams', requiresAuth, handlerGetInvitationTeams)

router.put('/i/:invitationId', requiresAuth, handlerJoinInvitation)

function handlerGetInvitationInformation(req: Request, res: Response, next: NextFunction) {
    const invitationId = req.params.invitationId

    getInvitationInfo(invitationId, req.user.accessToken.token)
        .then(async (invitation) => {
            if (!invitation) return null

            const inv = invitation.invitation
            switch (invitation.type) {
                case 'classroomInvitation':
                    return {
                        invitation: invitation,
                        membership: await getUserParticipationInClassroom(inv.id, req.user.accessToken.token)
                    }
                case 'assignmentInvitation':
                    return {
                        invitation: invitation,
                        membership: await getUserParticipationInAssignment(inv.id, req.user.accessToken.token)
                    }
                default:
                    return null
            }
        })
        .then(invitationMembership => {
            if (!invitationMembership) return next()

            const isMember = invitationMembership.membership.type != 'notMember'
            const invitation = invitationMembership.invitation.invitation
            switch (invitationMembership.invitation.type) {
                case 'classroomInvitation':
                    if (isMember)
                        return res.redirect(`/orgs/${invitation.orgId}/classrooms/${invitation.number}`)
                    
                    return res.render('invitations/classroom-invitation', {
                        invitation: invitation,
                        isTeam: false
                    })
                case 'assignmentInvitation':
                    if (isMember)
                        return res.redirect(`/orgs/${invitation.orgId}/classrooms/${(invitation as AssignmentInvitation).classroomNumber}/assignments/${invitation.number}`)
                    
                    return res.render('invitations/assignment-invitation', {
                        invitation: invitation,
                        isTeam: (invitation as AssignmentInvitation).type == 'group'
                    })
                default:
                    return next()
            }
        })
        .catch(err => next(INTERNAL_ERROR))
}

function handlerGetInvitationTeams(req: Request, res: Response, next: NextFunction) {
    if (!req.xhr) return next()

    const invitationId = req.params.invitationId
    if (!invitationId) return next()

    const page = Number(req.query.page) || 0

    getInvitationTeams(invitationId, page, req.user.accessToken.token)
        .then(teams => {
            if (!teams) return next()

            res.render('invitations/teams-fragment', {
                layout: false,

                teams: teams.teams,
                isEmpty: teams.teams.length == 0,
                page: teams.page,

                hasPrev: teams.page > 0,
                prevPage: teams.page > 0 ? teams.page - 1 : 0,

                hasNext: !teams.isLastPage,
                nextPage: teams.page + 1,
            })
        })
        .catch(err => next(INTERNAL_ERROR))
}

function handlerJoinInvitation(req: Request, res: Response, next: NextFunction) {
    const invitationId = req.params.invitationId
    const teamId = req.body.teamId

    getInvitationInfo(invitationId, req.user.accessToken.token)
        .then(async (inviteInfo) => {
            if (!inviteInfo) return null

            const invitation = inviteInfo.invitation
            let redirectUri
            switch (inviteInfo.type) {
                case 'classroomInvitation':
                    redirectUri = `/orgs/${invitation.orgId}/classrooms/${invitation.number}`
                    break
                case 'assignmentInvitation':
                    redirectUri = `/orgs/${invitation.orgId}/classrooms/${(invitation as AssignmentInvitation).classroomNumber}/assignments/${invitation.number}`
                    break
                default:
                    redirectUri = '/'
            }

            return {
                redirectUri: redirectUri,
                joinRes: await joinInvitation(invitationId, teamId, req.user.accessToken.token)
            }
        })
        .then(joinInvite => {
            if (!joinInvite) return next()

            let message: string
            switch(joinInvite.joinRes.status) {
                case 201: 
                    message = joinInvite.redirectUri
                    break
                case 403:
                    message = 'Could not accept invitation'
                    break
                default:
                    message = 'Could not accept invitation'
            }
            res.send({
                wasAccepted: joinInvite.joinRes.status == 201,
                message: message,
                isOrgInvitePending: joinInvite.joinRes.isOrgInvitePending,
                orgUri: joinInvite.joinRes.orgUri,
                repoUri: joinInvite.joinRes.repoUri
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