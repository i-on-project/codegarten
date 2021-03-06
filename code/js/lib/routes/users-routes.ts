'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'
import { INTERNAL_ERROR, requiresAuth } from './common-routes'
import { getUserById, getClassroomUsers, deleteUser, editUser ,removeUserFromClassroom, editClassroomUserMembership, getTeamUsers, removeUserFromTeam } from '../repo/services/users'

const router = expressRouter()

router.get('/user', requiresAuth, handlerGetAuthenticatedUser)
router.get('/users/:userId', requiresAuth, handlerGetUserById)
router.get('/orgs/:orgId/classrooms/:classroomNumber/users', requiresAuth, handlerGetClassroomUsers)
router.get('/orgs/:orgId/classrooms/:classroomNumber/teams/:teamNumber/users', requiresAuth, handlerGetTeamUsers)

router.put('/user', requiresAuth, handlerEditUser)
router.put('/orgs/:orgId/classrooms/:classroomNumber/users/:userId', requiresAuth, handlerEditClassroomUserMembership)

router.delete('/user', requiresAuth, handlerDeleteUser)
router.delete('/orgs/:orgId/classrooms/:classroomNumber/users/:userId', requiresAuth, handlerRemoveClassroomUser)
router.delete('/orgs/:orgId/classrooms/:classroomNumber/teamS/:teamNumber/users/:userId', requiresAuth, handlerRemoveTeamUser)

function handlerGetAuthenticatedUser(req: Request, res: Response, next: NextFunction) {
    res.render('auth-user-profile')
}

function handlerGetUserById(req: Request, res: Response, next: NextFunction) {
    const userId = Number(req.params.userId)
    if (isNaN(userId)) return next()
    
    getUserById(userId, req.user.accessToken.token)
        .then(user => {
            if (!user) return next()

            // Render the auth profile page if the user is the authenticated user
            if (user.id == req.user.id)
                return res.render('auth-user-profile')

            res.render('user-profile', {
                userById: user
            })
        })
        .catch(err => next(INTERNAL_ERROR))
}

function handlerGetClassroomUsers(req: Request, res: Response, next: NextFunction) {
    if (!req.xhr) return next()

    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)

    if (isNaN(orgId) || isNaN(classroomNumber)) return next()

    const page = Number(req.query.page) || 0
    
    getClassroomUsers(orgId, classroomNumber, req.user, page >= 0 ? page : 0, req.user.accessToken.token)
        .then(users => {
            if (!users) return next()

            res.render('classroom-fragments/classroom-users', {
                layout: false,

                users: users.users,
                isEmpty: users.users.length == 0,
                page: users.page,

                hasPrev: users.page > 0,
                prevPage: users.page > 0 ? users.page - 1 : 0,

                hasNext: !users.isLastPage,
                nextPage: users.page + 1,
                
                canManage: users.canManage
            })
        })
        .catch(err => next(INTERNAL_ERROR))
}

function handlerGetTeamUsers(req: Request, res: Response, next: NextFunction) {
    if (!req.xhr) return next()

    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    const teamNumber = Number(req.params.teamNumber)

    if (isNaN(orgId) || isNaN(classroomNumber) || isNaN(teamNumber)) return next()

    const page = Number(req.query.page) || 0
    
    getTeamUsers(orgId, classroomNumber, teamNumber, req.user, page >= 0 ? page : 0, req.user.accessToken.token)
        .then(users => {
            if (!users) return next()

            res.render('team-fragments/team-users', {
                layout: false,

                users: users.users,
                isEmpty: users.users.length == 0,
                page: users.page,

                hasPrev: users.page > 0,
                prevPage: users.page > 0 ? users.page - 1 : 0,

                hasNext: !users.isLastPage,
                nextPage: users.page + 1,
                
                canManage: users.canManage
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
            switch(result.status) {
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
                wasEdited: result.status == 200,
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

function handlerEditClassroomUserMembership(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    const userId = Number(req.params.userId)

    if (isNaN(orgId) || isNaN(classroomNumber) || isNaN(userId)) return next()
    
    const role = req.body.role
    if (!role) 
        return res.send({
            wasEdited: false,
            message: 'New role was not specified'
        })

    editClassroomUserMembership(orgId, classroomNumber, userId, role, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 200: 
                    message = 'User role was updated successfully'
                    break
                default:
                    message = 'Failed to update user role'
            }

            res.send({
                wasEdited: result.status == 200,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasEdited: false,
                message: 'Failed to update user role'
            })
        })
}

function handlerDeleteUser(req: Request, res: Response, next: NextFunction) {
    deleteUser(req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 200: 
                    message = 'User was successfully deleted'
                    req.logout()
                    req.flash('success', message)
                    break
                default:
                    message = 'Failed to delete user'
            }

            res.send({
                wasDeleted: result.status == 200,
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

function handlerRemoveClassroomUser(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    const userId = Number(req.params.userId)

    if (isNaN(orgId) || isNaN(classroomNumber) || isNaN(userId)) return next()

    removeUserFromClassroom(orgId, classroomNumber, userId, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 200:
                    if (userId == req.user.id) {
                        message = `/orgs/${orgId}/classrooms`
                        req.flash('success', 'Classroom was successfully left')
                    } else {
                        message = 'User was successfully removed'
                    }
                    break
                default:
                    message = result.content.detail
            }

            res.send({
                wasRemoved: result.status == 200,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasRemoved: false,
                message: userId == req.user.id ? 'Failed to leave classroom' : 'Failed to remove user'
            })
        })
}

function handlerRemoveTeamUser(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    const teamNumber = Number(req.params.teamNumber)
    const userId = Number(req.params.userId)

    if (isNaN(orgId) || isNaN(classroomNumber) || isNaN(teamNumber) || isNaN(userId)) return next()

    removeUserFromTeam(orgId, classroomNumber, teamNumber, userId, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 200:
                    if (userId == req.user.id) {
                        req.flash('success', 'Team was successfully left')
                    } else {
                        message = 'User was successfully removed'
                    }
                    break
                default:
                    message = userId == req.user.id ? 'Failed to leave team' : 'Failed to remove user'
            }

            res.send({
                wasRemoved: result.status == 200,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasRemoved: false,
                message: userId == req.user.id ? 'Failed to leave team' : 'Failed to remove user'
            })
        })
}

export = router
