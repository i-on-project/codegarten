'use strict'

import {NextFunction, Request, Response} from 'express'

export function requiresAuth(req: Request, res: Response, next: NextFunction): void {
    if (req.user) {
        req.session.redirectUri = null
        return next()
    }

    req.session.redirectUri = req.url
    res.redirect('/login')
}