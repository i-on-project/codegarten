import { NextFunction, Request, Response } from 'express'

const COOKIE_PREFIX = 'cgflash'

export = function(req: Request, res: Response, next: NextFunction): void {
    req.flash = function(type: string, message: string = null): string {
        const cookie = `${COOKIE_PREFIX}_${type}`

        if (message == null) {
            const msg = req.getCookie(cookie)
            res.expireCookie(cookie)
            return msg
        }
        
        res.setCookie(cookie, message)
        
        return null
    }

    next()
}