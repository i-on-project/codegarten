import { Response, Request, NextFunction } from 'express'

import crypto from 'crypto'

const COOKIE_NAME = 'state'
const STATE_LENGTH = 20

export = function(req: Request, res: Response, next: NextFunction): void {
    req.generateRandomState = function(): string {
        const state = crypto.randomBytes(STATE_LENGTH).toString('hex')
        res.setCookie(COOKIE_NAME, state)
        return state
    }
    
    req.getState = function(): string {
        const state = req.getCookie(COOKIE_NAME)
        res.expireCookie(COOKIE_NAME)
        return state
    }

    next()
}