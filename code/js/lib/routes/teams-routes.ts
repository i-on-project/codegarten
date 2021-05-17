'use strict'

import {NextFunction, Request, Response, Router as expressRouter} from 'express'
import { requiresAuth } from './common-routes'

const router = expressRouter()

router.get('/orgs/:orgId/classrooms/:classroomNumber/teams', requiresAuth, handlerGetClassroomTeams)

function handlerGetClassroomTeams(req: Request, res: Response, next: NextFunction) {
    res.render('classroom-teams-fragment', {
        layout: false
    })
}

export = router