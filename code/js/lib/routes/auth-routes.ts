'use strict'

import {Router as expressRouter} from 'express'
import passport from 'passport'

import { getAuthenticatedUser } from '../repo/services/users'
import { getAccessToken} from '../repo/services/auth'

import { authRoutes, getUrlEncodedRequestOptions } from '../repo/api-routes'

const router = expressRouter()

router.get('/login', handlerLogin)
router.get('/login/cb', handlerLoginCallback)
router.get('/logout', handlerLogout)

function handlerLogin(req, res, next) {
    res.redirect(authRoutes.getAuthCode)
}

function handlerLoginCallback(req, res, next) {
    const code = req.query.code
    if (!code) {
        req.flash('error', 'Failed to log in! Please try again.')
        res.redirect('/')
    }

    getAccessToken(code)
        .then(token => getAuthenticatedUser(token))
        .then(user => {
            req.logIn(user, (err) => {
                if(err) return next(err)
                res.redirect('/')
            })
        })
        .catch(err => {
            req.flash('error', 'Failed to log in! Please try again.')
            res.redirect('/') 
        })
}

function handlerLogout(req, res, next) {
    req.logout()
    res.redirect('/')
}

/*
passport.serializeUser((user: AuthenticatedUser, done) =>  {
    done(null, JSON.stringify(user.accessToken))
})
  
passport.deserializeUser((accessToken: string, done) => {
    getAuthenticatedUser(JSON.parse(accessToken))
        .then(user => done(null, user))
        .catch(err => {
            done(err)
        })
})
*/

export = router