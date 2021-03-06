'use strict'

import { RequestInit } from 'node-fetch'

const SERVER_API_HOST = process.env.CG_SERVER_API_HOST
const SERVER_IM_HOST = process.env.CG_SERVER_IM_HOST != null ? process.env.CG_SERVER_IM_HOST : SERVER_API_HOST

const API_HOST = `${SERVER_API_HOST}/api`
const IM_HOST = `${SERVER_IM_HOST}/im`
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
    getAuthCodeUri: (state: string): string => `${IM_HOST}/oauth/authorize?client_id=${CLIENT_ID}&state=${state}`,
    getAccessTokenUri: `${API_HOST}/oauth/access_token`,
    getRevokeAccessTokenUri: `${API_HOST}/oauth/revoke`,
    getAccessTokenRequestBody: (code: string): string => 
        `client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}&code=${code}`,
    getRevokeAccessTokenRequestBody: (token: string): string => 
        `client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}&token=${token}`
    
}

// Interaction Manager routes
const imRoutes = {
    getInstallOrgUri: `${IM_HOST}/github/install`
}

const userRoutes = {
    getAuthenticatedUserUri: `${API_HOST}/user`,
    getUserByIdUri: (userId: number): string => `${API_HOST}/users/${userId}`,
    getClassroomUsersUri: (orgId: number, classroomNumber: number, page: number, limit: number): string => 
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/users?page=${page}&limit=${limit}`, 
    getClassroomUserUri: (orgId: number, classroomNumber: number, userId: number): string => 
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/users/${userId}`,
    getTeamUsersUri: (orgId: number, classroomNumber: number, teamNumber: number, page: number, limit: number): string =>
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/teams/${teamNumber}/users?page=${page}&limit=${limit}`, 
    getTeamUserUri: (orgId: number, classroomNumber: number, teamNumber: number, userId: number): string =>
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/teams/${teamNumber}/users/${userId}`, 
}

const orgRoutes = {
    getPaginatedOrgsUri: (page: number, limit: number): string => `${API_HOST}/orgs?page=${page}&limit=${limit}`,
    getSearchOrgTemplateReposUri: (orgId: number, searchQuery: string): string => `${API_HOST}/orgs/${orgId}/templaterepos?q=${searchQuery}`
}

const classroomRoutes = {
    getPaginatedClassroomsUri: (orgId: number, page: number, limit: number): string => `${API_HOST}/orgs/${orgId}/classrooms?page=${page}&limit=${limit}`,
    getClassroomsUri: (orgId: number): string => `${API_HOST}/orgs/${orgId}/classrooms`,
    getClassroomUri: (orgId: number, classroomNumber: number): string => `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}`
}

const assignmentRoutes = {
    getPaginatedAssignmentsUri: (orgId: number, classroomNumber: number, page: number, limit: number): string =>
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/assignments?page=${page}&limit=${limit}`,
    getAssignmentsUri: (orgId: number, classroomNumber: number): string =>
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/assignments`,
    getAssignmentUri: (orgId: number, classroomNumber: number, assignmentNumber: number): string =>
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/assignments/${assignmentNumber}`
}

const teamRoutes = {
    getPaginatedTeamsUri: (orgId: number, classroomNumber: number, page: number, limit: number): string =>
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/teams?page=${page}&limit=${limit}`,
    getTeamsUri: (orgId: number, classroomNumber: number): string =>
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/teams`,
    getTeamUri: (orgId: number, classroomNumber: number, teamNumber: number): string => 
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/teams/${teamNumber}`
}

const participationRoutes = {
    getUserParticipationInAssignmentUri: (assignmentId: number): string =>
        `${API_HOST}/user/assignments/${assignmentId}/participation`,
    getUserParticipationInClassroomUri: (classroomId: number): string =>
        `${API_HOST}/user/classrooms/${classroomId}/participation`,
    getPaginatedAssignmentParticipantsUri: (orgId: number, classroomNumber: number, assignmentNumber: number, 
        page: number, limit: number): string =>
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/assignments/${assignmentNumber}/participants?page=${page}&limit=${limit}`,
    getAssignmentParticipantUri: (orgId: number, classroomNumber: number, assignmentNumber: number, participantId: number): string => 
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/assignments/${assignmentNumber}/participants/${participantId}`
}

const deliveryRoutes = {
    getPaginatedDeliveriesUri: (orgId: number, classroomNumber: number, assignmentNumber: number, 
        page: number, limit: number): string =>
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/assignments/${assignmentNumber}/deliveries?page=${page}&limit=${limit}`,
    getDeliveriesUri: (orgId: number, classroomNumber: number, assignmentNumber: number): string =>
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/assignments/${assignmentNumber}/deliveries`,
    getDeliveryUri: (orgId: number, classroomNumber: number, assignmentNumber: number, deliveryNumber: number): string =>
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/assignments/${assignmentNumber}/deliveries/${deliveryNumber}`,
    getPaginatedParticipantDeliveriesUri: (orgId: number, classroomNumber: number, assignmentNumber: number, participantId: number,
        page: number, limit: number): string =>
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/assignments/${assignmentNumber}/participants/${participantId}/deliveries?page=${page}&limit=${limit}`,
    getParticipantDeliveryUri: (orgId: number, classroomNumber: number, assignmentNumber: number, 
        participantId: number, deliveryNumber: number): string =>
        `${API_HOST}/orgs/${orgId}/classrooms/${classroomNumber}/assignments/${assignmentNumber}/participants/${participantId}/deliveries/${deliveryNumber}`
}

const invitationRoutes = {
    getInvitationUri: (invitationId: string): string => `${API_HOST}/user/invites/${invitationId}`,
    getPaginatedInvitationTeamsUri: (invitationId: string, page: number, limit: number): string =>
        `${API_HOST}/user/invites/${invitationId}/classroom/teams?page=${page}&limit=${limit}`
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
    assignmentRoutes,
    teamRoutes,
    participationRoutes,
    deliveryRoutes,
    invitationRoutes
}