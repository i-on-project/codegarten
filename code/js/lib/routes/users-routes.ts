'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'
import { requiresAuth } from './common-routes'
import { deleteUser, editUser } from '../repo/services/users'

const router = expressRouter()

router.get('/user', requiresAuth, handlerUser)
router.put('/user', requiresAuth, handlerEditUser)
router.delete('/user', requiresAuth, handlerDeleteUser)

function handlerUser(req: Request, res: Response, next: NextFunction) {
    res.render('user-profile')
}

function handlerEditUser(req: Request, res: Response, next: NextFunction) {
    const newName = req.body.name
    if (!newName) 
        return res.send({
            wasEdited: false,
            message: 'New username was not specified'
        })

    editUser(newName, req.user.accessToken.accessToken)
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
    deleteUser(req.user.accessToken.accessToken)
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