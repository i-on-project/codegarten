'use strict'

import express, { NextFunction, Request, Response } from 'express'
import flash from 'connect-flash'
import session from 'express-session'

import userControl from './user-control'
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

const SESSION_MAX_AGE = 1000 * 60 * 60 * 24 * 365 // 1 year

let PORT = 80
let server

export function init(portArg: number, done: () => void = null): void {
    if(portArg) {
        PORT = portArg
    }

    const app = express()
    app.set('view engine', 'hbs')
    app.set('views', './lib/views')

    app.use(express.static('public'))
    app.use(session({
        secret: 'changeit',
        resave: true,
        saveUninitialized: true,
        cookie: {
            maxAge: SESSION_MAX_AGE,
            sameSite: 'lax' 
        }
    }))

    app.use(flash())
    app.use(userControl)
    
    app.use((req: Request, res: Response, next: NextFunction) => {
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

    app.use((err: Error, req: Request, res: Response, next: NextFunction) => {
        res.status(err.code || 500)
        res.render('error', {
            'status': err.code,
            'message': err.message,
            'user': req.user
        })
    })

    app.use((req: Request, res: Response, next: NextFunction) => {
        res.status(404)
        res.render('error', {
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