'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'

import { INTERNAL_ERROR, requiresAuth } from './common-routes'
import { getClassrooms, getClassroom, createClassroom, deleteClassroom, editClassroom } from '../repo/services/classrooms'

const router = expressRouter()

router.get('/orgs/:orgId/classrooms', requiresAuth, handlerGetClassrooms)
router.get('/orgs/:orgId/classrooms/:classroomNumber', requiresAuth, handlerGetClassroomDefault)
router.get('/orgs/:orgId/classrooms/:classroomNumber/assignments', requiresAuth, handlerGetClassroom)
router.get('/orgs/:orgId/classrooms/:classroomNumber/users', requiresAuth, handlerGetClassroom)
router.get('/orgs/:orgId/classrooms/:classroomNumber/teams', requiresAuth, handlerGetClassroom)

router.post('/orgs/:orgId/classrooms', requiresAuth, handlerCreateClassroom)

router.put('/orgs/:orgId/classrooms/:classroomNumber', requiresAuth, handlerEditClassroom)

router.delete('/orgs/:orgId/classrooms/:classroomNumber', requiresAuth, handlerDeleteClassroom)

function handlerGetClassrooms(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    if (isNaN(orgId)) return next()

    const page = Number(req.query.page) || 0

    getClassrooms(orgId, page >= 0 ? page : 0, req.user.accessToken.token)
        .then(classrooms => {
            if (!classrooms) return next()

            res.render('list-classrooms', {
                classrooms: classrooms.classrooms,
                isEmpty: classrooms.classrooms.length == 0,
                page: classrooms.page,

                hasPrev: classrooms.page > 0,
                prevPage: classrooms.page > 0 ? classrooms.page - 1 : 0,

                hasNext: !classrooms.isLastPage,
                nextPage: classrooms.page + 1,

                organization: classrooms.organization,
                orgId: orgId,
                orgUri: classrooms.organizationUri,
                canCreate: classrooms.canCreate
            })
        })
        .catch(err => next(INTERNAL_ERROR))
}

function handlerGetClassroom(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)

    if (isNaN(orgId) || isNaN(classroomNumber)) return next()
    
    getClassroom(orgId, classroomNumber, req.user.accessToken.token)
        .then(classroom => {
            if (!classroom) return next()

            res.render('classroom', {
                classroom: classroom,

                organization: classroom.organization,
                orgId: orgId,
                orgUri: classroom.organizationUri,
                
                canManage: classroom.canManage
            })
        })
}

function handlerGetClassroomDefault(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)

    if (isNaN(orgId) || isNaN(classroomNumber)) return next()
    
    res.redirect(`/orgs/${orgId}/classrooms/${classroomNumber}/assignments`)
}

function handlerCreateClassroom(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    if (isNaN(orgId)) return next()

    const name = req.body.name
    const description = req.body.description

    if (!name) { 
        return res.send({
            wasCreated: false,
            message: 'Name was not specified'
        })
    }

    createClassroom(orgId, name, description, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 201: 
                    message = `/orgs/${orgId}/classrooms/${result.content.number}`
                    break
                case 409:
                    message = 'Classroom name already exists'
                    break
                default:
                    message = 'Failed to create classroom'
            }

            res.send({
                wasCreated: result.status == 201,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasCreated: false,
                message: 'Failed to create classroom'
            })
        })
}

function handlerEditClassroom(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    if (isNaN(orgId) || isNaN(classroomNumber)) return next()

    const name = req.body.name
    const description = req.body.description

    if (!name && !description) { 
        return res.send({
            wasEdited: false,
            message: 'You need to change at least one field'
        })
    }

    editClassroom(orgId, classroomNumber, name, description, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 200:
                    message = 'Classroom edited successfully'
                    break
                case 409:
                    message = 'Classroom name already exists'
                    break
                default:
                    message = 'Failed to edit classroom'
            }

            res.send({
                wasEdited: result.status == 200,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasEdited: false,
                message: 'Failed to edit classroom'
            })
        })
}

function handlerDeleteClassroom(req: Request, res: Response, next: NextFunction) {
    const orgId = Number(req.params.orgId)
    const classroomNumber = Number(req.params.classroomNumber)
    if (isNaN(orgId) || isNaN(classroomNumber)) return next()

    deleteClassroom(orgId, classroomNumber, req.user.accessToken.token)
        .then(result => {
            let message: string
            switch(result.status) {
                case 200:
                    req.flash('success', 'Classroom was deleted successfully')
                    message = `/orgs/${orgId}/classrooms`
                    break
                default:
                    message = 'Failed to delete classroom'
            }

            res.send({
                wasDeleted: result.status == 200,
                message: message
            })
        })
        .catch(err => {
            res.send({
                wasDeleted: false,
                message: 'Failed to delete classroom'
            })
        })
}

export = router