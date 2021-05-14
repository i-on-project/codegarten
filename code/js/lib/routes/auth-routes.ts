'use strict'

import {NextFunction, Request, Response, Router as expressRouter} from 'express'

import { getAuthenticatedUser } from '../repo/services/users'
import { getAccessToken } from '../repo/services/auth'

import { authRoutes } from '../repo/api-routes'

const router = expressRouter()

router.get('/login', handlerLogin)
router.get('/login/cb', handlerLoginCallback)
router.get('/logout', handlerLogout)

function handlerLogin(req: Request, res: Response, next: NextFunction) {
    res.redirect(authRoutes.getAuthCode)
}

function handlerLoginCallback(req: Request, res: Response, next: NextFunction) {
    const code = req.query.code as string
    if (!code) {
        req.flash('error', 'Failed to log in! Please try again.')
        return res.redirect('/')
    }

    getAccessToken(code)
        .then(token => getAuthenticatedUser(token))
        .then(user => {
            req.login(user)
            res.redirect(req.session.redirectUri || '/')
        })
        .catch(err => {
            req.flash('error', 'Failed to log in! Please try again.')
            res.redirect('/') 
        })
}

function handlerLogout(req: Request, res: Response, next: NextFunction) {
    if (req.user) req.logout()
    res.redirect('/')
}

export = router