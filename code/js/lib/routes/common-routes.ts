'use strict'

import { NextFunction, Request, Response } from 'express'
import { Error } from '../types/error-types'

const INTERNAL_ERROR: Error = {
    status: 500,
    message: 'Internal server error' 
}

function requiresAuth(req: Request, res: Response, next: NextFunction): void {
    if (req.user) {
        req.redirectLoginTo(null)
        return next()
    }

    req.redirectLoginTo(req.url)
    res.redirect('/login')
}

export {
    INTERNAL_ERROR,
    requiresAuth
}