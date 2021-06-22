import { Response, Request, NextFunction } from 'express'

const PATH = '/'

export = function(req: Request, res: Response, next: NextFunction): void {
    req.getAllCookies = function(): Map<string, string> {
        if (req.headers.cookie) {
            const rawCookies = req.headers.cookie.split('; ')
    
            const parsedCookies = new Map()
            rawCookies.forEach(rawCookie => {
                const parsedCookie = rawCookie.split('=')
                parsedCookies.set(parsedCookie[0], parsedCookies[1])
            })
            return parsedCookies
        }
        return null
    }
    req.getCookie = function(key: string): string {
        if (req.headers.cookie) {
            const rawCookies = req.headers.cookie.split('; ')
    
            for(let i = 0; i < rawCookies.length; ++i) {
                const rawCookie = rawCookies[i]
                const parsedCookie = rawCookie.split('=')
                if (parsedCookie[0] == key) return parsedCookie[1]
            }
        }
    
        return null
    }
    res.expireCookie = function(key: string): void {
        const oldHeader = (res.getHeader('Set-Cookie') || []) as string[]
        oldHeader.push(`${key}=expired; Path=${PATH}; expires=${new Date(0).toUTCString()}`)
        
        res.setHeader('Set-Cookie', oldHeader)
    }
    res.setCookie = function(key: string, value: string, expiresAt: Date = null): void {
        const oldHeader = (res.getHeader('Set-Cookie') || []) as string[]
        oldHeader.push(`${key}=${value}; Path=${PATH}; ${expiresAt == null ? '' : `expires=${expiresAt};`} HttpOnly; SameSite=Lax`)
        
        res.setHeader('Set-Cookie', oldHeader)
    }

    next()
}