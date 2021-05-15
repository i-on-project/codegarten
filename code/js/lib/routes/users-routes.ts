'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'
import { INTERNAL_ERROR, requiresAuth } from './common-routes'
import { getUserById, deleteUser, editUser } from '../repo/services/users'
import { Error } from '../types/error-types'

const router = expressRouter()

router.get('/user', requiresAuth, handlerGetAuthenticatedUser)
router.get('/users/:userId', requiresAuth, handlerGetUserById)

router.put('/user', requiresAuth, handlerEditUser)
router.delete('/user', requiresAuth, handlerDeleteUser)

function handlerGetAuthenticatedUser(req: Request, res: Response, next: NextFunction) {
    res.render('auth-user-profile')
}

function handlerGetUserById(req: Request, res: Response, next: NextFunction) {
    const userId = Number(req.params.userId)
    if (isNaN(userId)) return res.redirect('/')
    
    getUserById(userId, req.user.accessToken.token)
        .then(user => {
            if (!user) {
                const err: Error = {
                    status: 404,
                    message: 'User does not exist'
                }
                return next(err)
            }

            res.render('user-profile', {
                userById: user
            })
        })
        .catch(err => next(INTERNAL_ERROR))
}

function handlerEditUser(req: Request, res: Response, next: NextFunction) {
    const newName = req.body.name
    if (!newName) 
        return res.send({
            wasEdited: false,
            message: 'New username was not specified'
        })

    editUser(newName, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result) {
                case 200: 
                    message = 'Username was successfully edited'
                    break
                case 409:
                    message = 'Username already exists'
                    break
                default:
                    message = 'Failed to edit username'
            }

            res.send({
                wasEdited: result == 200,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasEdited: false,
                message: 'Failed to edit username'
            })
        })
}

function handlerDeleteUser(req: Request, res: Response, next: NextFunction) {
    // TODO secure this route against CSRF
    deleteUser(req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result) {
                case 200: 
                    message = 'User was successfully deleted'
                    req.logout()
                    req.flash('success', message)
                    break
                default:
                    message = 'Failed to delete user'
            }

            res.send({
                wasDeleted: result == 200,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasDeleted: false,
                message: 'Failed to delete user'
            })
        })
}

export = router