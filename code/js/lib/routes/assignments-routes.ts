'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'

import { INTERNAL_ERROR, requiresAuth } from './common-routes'
import { getAssignments } from '../repo/services/assignments'

const router = expressRouter()

router.get('/orgs/:orgId/classrooms/:classroomNumber/assignments', requiresAuth, handlerGetAssignments)

function handlerGetAssignments(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)

    if (isNaN(orgId) || isNaN(classroomNumber)) return next()

    const page = Number(req.query.page) || 0
    
    getAssignments(orgId, classroomNumber, page >= 0 ? page : 0, req.user.accessToken.token)
        .then(assignments => {
            if (!assignments) return next()

            res.render('assignments-fragment', {
                layout: false,

                assignments: assignments.assignments,
                isEmpty: assignments.assignments.length == 0,
                page: assignments.page,

                hasPrev: assignments.page > 0,
                prevPage: assignments.page > 0 ? assignments.page - 1 : 0,

                hasNext: !assignments.isLastPage,
                nextPage: assignments.page + 1,

                organization: assignments.organization,
                orgId: orgId,
                orgUri: assignments.organizationUri,
                
                canCreate: assignments.canCreate
            })
        })
        .catch(err => next(INTERNAL_ERROR))
}

export = router