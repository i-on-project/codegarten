'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'

import { INTERNAL_ERROR, requiresAuth } from './common-routes'
import { imRoutes } from '../repo/api-routes'
import { getUserOrgs, searchOrgTemplateRepos } from '../repo/services/orgs'

const router = expressRouter()

router.get('/orgs', requiresAuth, handlerGetOrgs)
router.get('/orgs/:orgId/templaterepos', requiresAuth, handlerGetOrgTemplateRepos)

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

function handlerGetOrgTemplateRepos(req: Request, res: Response, next: NextFunction) {
    if (!req.xhr) return next()

    const orgId = Number(req.params.orgId)
    if (isNaN(orgId)) return next()

    const searchQuery = req.query.q as string
    searchOrgTemplateRepos(orgId, searchQuery, req.user.accessToken.token)
        .then(repos => {
            res.render('org-fragments/org-template-repos', {
                layout: false,
                
                repos: repos.repos,
                isEmpty: repos.repos.length == 0,

                org: repos.organization,
                orgUri: repos.organizationUri
            })
        })
        .catch(err => next(INTERNAL_ERROR))
}

export = router