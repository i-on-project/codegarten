'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'

import { INTERNAL_ERROR, requiresAuth } from './common-routes'
import { getClassrooms, createClassroom } from '../repo/services/classrooms'

const router = expressRouter()

router.get('/orgs/:orgId/classrooms', requiresAuth, handlerGetClassrooms)
router.post('/orgs/:orgId/classrooms', requiresAuth, handlerCreateClassroom)

function handlerGetClassrooms(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    if (isNaN(orgId)) return next()

    const page = Number(req.query.page) || 0

    getClassrooms(orgId, page >= 0 ? page : 0, req.user.accessToken.token)
        .then(classrooms => {
            if (!classrooms) return next()

            res.render('list-classrooms', {
                classrooms: classrooms.classrooms,
                isEmpty: classrooms.classrooms.length == 0,
                page: classrooms.page,

                hasPrev: classrooms.page > 0,
                prevPage: classrooms.page > 0 ? classrooms.page - 1 : 0,

                hasNext: !classrooms.isLastPage,
                nextPage: classrooms.page + 1,

                organization: classrooms.organization,
                orgId: orgId,
                orgUri: classrooms.organizationUri,
                canCreate: classrooms.canCreate
            })
        })
        .catch(err => next(INTERNAL_ERROR))
}

function handlerCreateClassroom(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    if (isNaN(orgId)) return next()

    const name = req.body.name
    const description = req.body.description

    if (!name) { 
        return res.send({
            wasCreated: false,
            message: 'Name was not specified'
        })
    }

    createClassroom(orgId, name, description, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 201: 
                    message = `/orgs/${orgId}/classrooms/${result.content.number}`
                    break
                case 409:
                    message = 'Classroom name already exists'
                    break
                default:
                    message = 'Failed to create classroom'
            }

            res.send({
                wasCreated: result.status == 201,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasCreated: false,
                message: 'Failed to create classroom'
            })
        })
}

export = router