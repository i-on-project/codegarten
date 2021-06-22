import { NextFunction, Request, Response } from 'express'

const COOKIE_NAME = 'redirectAfterLogin'

export = function(req: Request, res: Response, next: NextFunction): void {
    req.redirectLoginTo = function(uri: string): void {
        if (uri == null) {
            res.expireCookie(COOKIE_NAME)
            return
        }
        res.setCookie(COOKIE_NAME, uri)
        return
    }
    req.getLoginRedirect = function(): string {
        return req.getCookie(COOKIE_NAME)
    }

    next()
}