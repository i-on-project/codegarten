'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'

import { INTERNAL_ERROR, requiresAuth } from './common-routes'
import { getAssignments, getAssignment, createAssignment } from '../repo/services/assignments'

const router = expressRouter()

router.get('/orgs/:orgId/classrooms/:classroomNumber/assignments', requiresAuth, handlerGetAssignments)
router.get('/orgs/:orgId/classrooms/:classroomNumber/assignments/:assignmentNumber', requiresAuth, handlerGetAssignment)

router.post('/orgs/:orgId/classrooms/:classroomNumber/assignments', requiresAuth, handlerCreateAssignment)

function handlerGetAssignments(req: Request, res: Response, next: NextFunction) {
    if (!req.xhr) return next()

    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)

    if (isNaN(orgId) || isNaN(classroomNumber)) return next()

    const page = Number(req.query.page) || 0
    
    getAssignments(orgId, classroomNumber, page >= 0 ? page : 0, req.user.accessToken.token)
        .then(assignments => {
            if (!assignments) return next()

            res.render('classroom-fragments/classroom-assignments', {
                layout: false,

                assignments: assignments.assignments,
                isEmpty: assignments.assignments.length == 0,
                page: assignments.page,

                hasPrev: assignments.page > 0,
                prevPage: assignments.page > 0 ? assignments.page - 1 : 0,

                hasNext: !assignments.isLastPage,
                nextPage: assignments.page + 1,

                classroomNumber: classroomNumber,

                organization: assignments.organization,
                orgId: orgId,
                orgUri: assignments.organizationUri,
                
                canCreate: assignments.canCreate
            })
        })
        .catch(err => next(INTERNAL_ERROR))
}

function handlerGetAssignment(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    const assignmentNumber = Number(req.params.assignmentNumber)

    if (isNaN(orgId) || isNaN(classroomNumber) || isNaN(assignmentNumber)) return next()

    getAssignment(orgId, classroomNumber, assignmentNumber, req.user.accessToken.token)
        .then(assignment => {
            if (!assignment) return next()

            res.render('assignment', {
                assignment: assignment,

                classroomNumber: classroomNumber,

                organization: assignment.organization,
                orgId: orgId,
                orgUri: assignment.organizationUri,
                
                canManage: assignment.canManage
            })
        })
        .catch(err => next(INTERNAL_ERROR))
}

function handlerCreateAssignment(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)

    if (isNaN(orgId) || isNaN(classroomNumber)) return next()

    const name = req.body.name
    const description = req.body.description
    const type = req.body.type
    const repoPrefix = req.body.repoPrefix
    const repoTemplate = req.body.repoTemplate

    if (!name) return res.send({wasCreated: false, message: 'Name was not specified'})
    if (!type) return res.send({wasCreated: false, message: 'Type was not specified'})
    if (!repoPrefix) return res.send({wasCreated: false, message: 'Repository prefix was not specified'})

    createAssignment(orgId, classroomNumber, name, description, type, repoPrefix, repoTemplate, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 201: 
                    message = `/orgs/${orgId}/classrooms/${classroomNumber}/assignments/${result.content.number}`
                    break
                case 409:
                    message = 'Assignment name already exists'
                    break
                default:
                    message = 'Failed to create assignment'
            }

            res.send({
                wasCreated: result.status == 201,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasCreated: false,
                message: 'Failed to create assignment'
            })
        })
}

export = router