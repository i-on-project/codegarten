'use strict'

import cookieParser from 'cookie-parser'
import expressSession from 'express-session'

import express from 'express'
import flash from 'connect-flash'
import passport from 'passport'

import homeRoutes from './routes/home-routes'
import orgsRoutes from './routes/orgs-routes'
import classroomsRoutes from './routes/classrooms-routes'
import teamsRoutes from './routes/teams-routes'
import assignmentsRoutes from './routes/assignments-routes'
import deliveriesRoutes from './routes/deliveries-routes'
import usersRoutes from './routes/users-routes'
import invitationsRoutes from './routes/invitations-routes'
import participantsRoutes from './routes/participants-routes'
import authRoutes from './routes/auth-routes'

import { Error } from './types/error-types'

let PORT = 80
let server

export function init(portArg: number, done: Function = null): void {
    if(portArg) {
        PORT = portArg
    }

    const app = express()
    app.set('view engine', 'hbs')
    app.set('views', './lib/views')

    app.use(express.static('public'))
    app.use(cookieParser())
    app.use(expressSession({ 
        secret: 'change it', 
        resave: true, 
        saveUninitialized: true, 
        cookie: {
            sameSite: 'lax'
        }
    }))
    app.use(flash())

    //TODO: implement better session control

    //app.use(passport.initialize())
    //app.use(passport.session()) 
    
    app.use((req, res, next) => {
        res.locals.user = req.user
        res.locals.messages = {
            error: req.flash('error'),
            success: req.flash('success')
        }
        next()
    })

    app.use(authRoutes)
    app.use(homeRoutes)
    app.use(invitationsRoutes)
    app.use(participantsRoutes)
    app.use(deliveriesRoutes)
    app.use(assignmentsRoutes)
    app.use(usersRoutes)
    app.use(teamsRoutes)
    app.use(classroomsRoutes)
    app.use(orgsRoutes)

    app.use((err: Error, req, resp, next) => {
        resp.status(err.code || 500)
        resp.render('error', {
            'status': err.code,
            'message': err.message,
            'user': req.user
        })
    })

    app.use((req, resp, next) => {
        resp.status(404)
        resp.render('error', {
            'status': 404,
            'message': 'Sorry, that page does not exist!',
            'user': req.user
        })
    })

    server = app.listen(PORT, () => {
        console.log(`Listening for HTTP requests on port ${PORT}`)
        if (done) done()
    })
}

export function close(): void {
    server.close()
}