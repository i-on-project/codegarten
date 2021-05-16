'use strict'

import { RequestInit } from 'node-fetch'

const API_HOST = 'http://localhost:8080/api'
const IM_HOST = 'http://localhost:8080/im'
const CLIENT_ID = process.env.CG_CLIENT_ID
const CLIENT_SECRET = process.env.CG_CLIENT_SECRET

function getJsonRequestOptions(method: string, accessToken: string, body: any = null): RequestInit {
    return {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'Authorization': `Bearer ${accessToken}`
        },
        body: body ? JSON.stringify(body) : null
    }
}

function getUrlEncodedRequestOptions(method: string, body: string = null): RequestInit {
    return {
        method: method,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept': 'application/json',
        },
        body: body
    }
}

function getSirenLink(links: SirenLink[], rel: string): SirenLink {
    return links
        .find(link => {
            const relArr = Array.from(link.rel)
            return relArr.includes(rel)
        })
}

function getSirenAction(actions: SirenAction[], name: string): SirenAction {
    return actions
        .find(action => action.name == name)
}

const authRoutes = {
    // TODO: Implement state
    getAuthCodeUri: `${IM_HOST}/oauth/authorize?client_id=${CLIENT_ID}`,
    getAccessTokenUri: `${API_HOST}/oauth/access_token`,
    getAccessTokenRequestBody: (code: string): string => 
        `client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}&code=${code}`
}

// Interaction Manager routes
const imRoutes = {
    getInstallOrgUri: `${IM_HOST}/github/install`
}

const userRoutes = {
    getAuthenticatedUserUri: `${API_HOST}/user`,
    getUserByIdUri: (userId: number): string => `${API_HOST}/users/${userId}`
}

const orgRoutes = {
    getPaginatedOrgsUri: (page: number, limit: number): string => `${API_HOST}/orgs?page=${page}&limit=${limit}`,
}

const classroomRoutes = {
    getPaginatedClassroomsUri: (orgId: number, page: number, limit: number): string => `${API_HOST}/orgs/${orgId}/classrooms?page=${page}&limit=${limit}`,
    getClassroomsUri: (orgId: number): string => `${API_HOST}/orgs/${orgId}/classrooms`,
    getClassroomUri: (orgId: number, classroomNumber: number): string => `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}`
}

const assignmentRoutes = {
    getPaginatedAssignmentsUri: (orgId: number, classroomNumber: number, page: number, limit: number): string =>
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/assignments?page=${page}&limit=${limit}`,
    getAssignmentsUri: (orgId: number, classroomNumber: number) =>
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/assignments`
}

export {
    getJsonRequestOptions,
    getUrlEncodedRequestOptions,
    getSirenLink,
    getSirenAction,
    authRoutes,
    imRoutes,
    userRoutes,
    orgRoutes,
    classroomRoutes,
    assignmentRoutes
}