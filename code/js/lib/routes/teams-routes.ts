'use strict'

import {NextFunction, Request, Response, Router as expressRouter} from 'express'
import { INTERNAL_ERROR, requiresAuth } from './common-routes'
import { createTeam, deleteTeam, getTeams } from '../repo/services/teams'

const router = expressRouter()

router.get('/orgs/:orgId/classrooms/:classroomNumber/teams', requiresAuth, handlerGetClassroomTeams)
router.post('/orgs/:orgId/classrooms/:classroomNumber/teams', requiresAuth, handlerCreateTeam)
router.delete('/orgs/:orgId/classrooms/:classroomNumber/teams/:teamNumber', requiresAuth, handlerDeleteTeam)


function handlerGetClassroomTeams(req: Request, res: Response, next: NextFunction) {
    if (!req.xhr) return next()

    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)

    if (isNaN(orgId) || isNaN(classroomNumber)) return next()

    const page = Number(req.query.page) || 0

    getTeams(orgId, classroomNumber, page, req.user.accessToken.token)
        .then(teams => {
            if (!teams) return next()

            res.render('classroom-teams-fragment', {
                layout: false,

                teams: teams.teams,
                isEmpty: teams.teams.length == 0,
                page: teams.page,

                hasPrev: teams.page > 0,
                prevPage: teams.page > 0 ? teams.page - 1 : 0,

                hasNext: !teams.isLastPage,
                nextPage: teams.page + 1,

                classroomNumber: classroomNumber,

                organization: teams.organization,
                orgId: orgId,
                orgUri: teams.organizationUri,
                
                canManage: teams.canManage
            })
        })
        .catch(err => next(INTERNAL_ERROR))

    
}

function handlerCreateTeam(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)

    if (isNaN(orgId) || isNaN(classroomNumber)) return next()

    const name = req.body.name

    if (!name) { 
        return res.send({
            wasCreated: false,
            message: 'Name was not specified'
        })
    }

    createTeam(orgId, classroomNumber, name, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 201: 
                    req.flash('success', 'Team created successfully')
                    break
                case 409:
                    message = 'Team name already exists'
                    break
                default:
                    message = 'Failed to create team'
            }

            res.send({
                wasCreated: result.status == 201,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasCreated: false,
                message: 'Failed to create team'
            })
        })
}

function handlerDeleteTeam(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    const teamNumber = Number(req.params.teamNumber)

    if (isNaN(orgId) || isNaN(classroomNumber) || isNaN(teamNumber)) return next()

    deleteTeam(orgId, classroomNumber, teamNumber, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 200:
                    message = 'Team was deleted successfully'
                    break
                default:
                    message = 'Failed to delete team'
            }

            res.send({
                wasDeleted: result.status == 200,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasDeleted: false,
                message: 'Failed to delete team'
            })
        })
}

export = router