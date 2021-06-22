'use strict'

import { NextFunction, Request, Response } from 'express'
import { getAuthenticatedUser } from './repo/services/users'
import { authRoutes } from './repo/api-routes'
import { revokeAccessToken } from './repo/services/auth'
import { Error } from './types/error-types'
import * as crypto from 'crypto'
import * as fs from 'fs'

const ACCESS_TOKEN_VALIDITY_THRESHOLD = 1000 * 60 * 60 * 24 // 1 day
const ACCESS_TOKEN_COOKIE = 'tok'

const SECRET_KEY = fs.readFileSync(`${process.env.CG_WEB_CIPHER_KEY_PATH}`)
const CIPHER_ALGORITHM = 'aes-256-cbc'

export = function(req: Request, res: Response, next: NextFunction): void {
    req.login = function(user: AuthenticatedUser): Promise<boolean> {
        setAccessToken(res, user.accessToken)

        console.log(`[LOGIN] ${user.username}`)
        return Promise.resolve(true)
    }
    req.logout = async function(): Promise<boolean> {
        const username = req.user?.username
        const token = getAccessToken(req)

        const apiRes = await revokeAccessToken(token.token)
        if (apiRes.status == 200) {
            req.user = null
            removeAccessToken(res)
            console.log(`[LOGOUT] ${username}`)
            return true
        } else {
            console.log(`[LOGOUT FAIL] ${username}`)
            return false
        }
    }

    const accessToken = getAccessToken(req)
    if (accessToken) {
        const date = new Date(new Date().getTime() + ACCESS_TOKEN_VALIDITY_THRESHOLD)
        const expirDate = new Date(accessToken.expiresAt)
        if (expirDate <= date) {
            removeAccessToken(res)
            req.redirectLoginTo(req.url)
            return res.redirect('/login')
        }
        
        getAuthenticatedUser(accessToken)
            .then(user => req.user = user)
            .then(() => next())
            .catch((err) => {
                req.logout()
                    .then(() => next())
                    .catch(err => {
                        const error: Error = {
                            status: 502,
                            message: 'Cannot establish connection to the API' 
                        }
                        next(error)
                    })
            })
    } else next()
}

function getAccessToken(req: Request): AccessToken {
    const accessToken: string = req.getCookie(ACCESS_TOKEN_COOKIE)
    if (accessToken != null) {
        const iv = accessToken.slice(0, 16)

        try {
            const decipher = crypto.createDecipheriv(CIPHER_ALGORITHM, SECRET_KEY, iv)
            const decrypted = decipher.update(accessToken.slice(16), 'base64', 'utf8')
            return JSON.parse(decrypted + decipher.final('utf8'))
        } catch(error) {
            return null
        }
    }
    return null
}

function setAccessToken(res: Response, accessToken: AccessToken): void {
    const iv = crypto.randomBytes(8).toString('hex')
    const cipher = crypto.createCipheriv(CIPHER_ALGORITHM, SECRET_KEY, iv)
    let encrypted = cipher.update(JSON.stringify(accessToken), 'utf8', 'base64')
    encrypted += cipher.final('base64')
    res.setCookie(ACCESS_TOKEN_COOKIE, iv + encrypted, accessToken.expiresAt)
}

function removeAccessToken(res: Response) {
    res.expireCookie(ACCESS_TOKEN_COOKIE)
}
