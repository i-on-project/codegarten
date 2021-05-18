'use strict'

import { NextFunction, Request, Response } from 'express'
import { getAuthenticatedUser } from './repo/services/users'
import { authRoutes } from './repo/api-routes'
import { INTERNAL_ERROR } from './routes/common-routes'
import { revokeAccessToken } from './repo/services/auth'

const ACCESS_TOKEN_VALIDITY_THRESHOLD = 1000 * 60 * 60 * 24 // 1 day

export = function(req: Request, res: Response, next: NextFunction): void {
    req.login = function(user: AuthenticatedUser): Promise<boolean> {
        req.session.accessToken = user.accessToken

        console.log(`[LOGIN] ${user.username}`)
        return Promise.resolve(true)
    }
    req.logout = function(): Promise<boolean> {
        const username = req.user.username
        const token = req.session.accessToken

        return revokeAccessToken(token.token)
            .then(res => {
                if (res.status == 200) { 
                    req.user = null
                    req.session.accessToken = null
                    console.log(`[LOGOUT] ${username}`)
                    return true
                } else {
                    console.log(`[LOGOUT FAIL] ${username}`)
                    return false
                }
            })
    }

    const accessToken = req.session.accessToken
    if (accessToken) {
        const date = new Date(new Date().getTime() + ACCESS_TOKEN_VALIDITY_THRESHOLD)
        const expirDate = new Date(accessToken.expiresAt)
        if (expirDate <= date) {
            req.session.accessToken = null
            req.session.redirectUri = req.url
            return res.redirect(authRoutes.getAuthCodeUri)
        }
        
        getAuthenticatedUser(accessToken)
            .then(user => req.user = user)
            .then(() => next())
            .catch((err) => next(INTERNAL_ERROR))
    } else next()
}