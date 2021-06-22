'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'

import { getAuthenticatedUser } from '../repo/services/users'
import { getAccessToken } from '../repo/services/auth'

import { authRoutes } from '../repo/api-routes'

const router = expressRouter()

router.get('/login', handlerLogin)
router.get('/login/cb', handlerLoginCallback)
router.get('/logout', handlerLogout)

function handlerLogin(req: Request, res: Response, next: NextFunction) {
    const state = req.generateRandomState()
    res.redirect(authRoutes.getAuthCodeUri(state))
}

function handlerLoginCallback(req: Request, res: Response, next: NextFunction) {
    const code = req.query.code as string
    const state = req.query.state as string
    if (!code || !state || state != req.getState()) {
        req.flash('error', 'Failed to log in! Please try again.')
        return res.redirect('/')
    }

    getAccessToken(code)
        .then(token => getAuthenticatedUser(token))
        .then(user => req.login(user))
        .then(success => {
            if (!success) throw new Error()
            res.redirect(req.getLoginRedirect() || '/')
        })
        .catch(err => {
            req.flash('error', 'Failed to log in! Please try again.')
            res.redirect('/') 
        })
}

function handlerLogout(req: Request, res: Response, next: NextFunction) {
    if (req.user) {
        req.logout()
            .then(success => {
                if (!success) throw new Error()
            })
            .catch(err => {
                req.flash('error', 'Failed to log out')
            })
            .finally(() => res.redirect('/'))
    } else res.redirect('/')
}

export = router