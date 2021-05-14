'use strict'

import { NextFunction, Request, Response } from 'express'
import { getAuthenticatedUser } from './repo/services/users'
import { authRoutes } from './repo/api-routes'

const ACCESS_TOKEN_VALIDITY_THRESHOLD = 1000 * 10 * 60 // 10 minutes

export = function(req: Request, res: Response, next: NextFunction): void {
    req.login = function(user: AuthenticatedUser) {
        req.session.accessToken = user.accessToken

        console.log(`[LOGIN] ${user.username}`)
    }
    req.logout = function() {
        const username = req.user.username
        req.user = null
        req.session.accessToken = null

        console.log(`[LOGOUT] ${username}`)
    }

    const accessToken = req.session.accessToken
    if (accessToken) {
        const date = new Date(new Date().getTime() + ACCESS_TOKEN_VALIDITY_THRESHOLD)
        const expirDate = new Date(accessToken.expiresAt)
        if (expirDate <= date) {
            req.session.accessToken = null
            req.session.redirectUri = req.url
            return res.redirect(authRoutes.getAuthCode)
        }
        
        getAuthenticatedUser(accessToken)
            .then(user => req.user = user)
            .then(() => next())
    } else next()
}