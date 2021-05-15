'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'

import { INTERNAL_ERROR, requiresAuth } from './common-routes'
import { imRoutes } from '../repo/api-routes'
import { getUserOrgs } from '../repo/services/orgs'

const router = expressRouter()

router.get('/orgs', requiresAuth, handlerGetOrgs)

function handlerGetOrgs(req: Request, res: Response, next: NextFunction) {
    const page = Number(req.query.page) || 0

    getUserOrgs(page >= 0 ? page : 0, req.user.accessToken.token)
        .then(orgs => {
            res.render('list-orgs', {
                orgs: orgs.orgs,
                isEmpty: orgs.orgs.length == 0,
                page: orgs.page,

                hasPrev: orgs.page > 0,
                prevPage: orgs.page > 0 ? orgs.page - 1 : 0,

                hasNext: !orgs.isLastPage,
                nextPage: orgs.page + 1,

                orgInstallUri: imRoutes.getInstallOrgUri
            })
        })
        .catch(err => next(INTERNAL_ERROR))
}

export = router