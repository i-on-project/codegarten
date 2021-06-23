import { NextFunction, Request, Response } from 'express'

const COOKIE_NAME = 'flash'

export = function(req: Request, res: Response, next: NextFunction): void {
    req.flash = function(type: string, message: string = null): string {
        const msg = JSON.parse(req.getCookie(COOKIE_NAME)) || {}

        if (message == null) {
            const value = msg[type]
            if (value != null) {
                delete msg[type]
                res.setCookie(COOKIE_NAME, JSON.stringify(msg))
            }
            return value
        }
        
        msg[type] = message
        res.setCookie(COOKIE_NAME, JSON.stringify(msg))
        return null
    }

    next()
}