'use strict'

export function authMiddleware(req, res, next): void {
    if(req.user) next()
    else res.redirect('/login')
}

export function userMiddleware(req, res, next): void {
    res.locals.user = req.user
    next()
}