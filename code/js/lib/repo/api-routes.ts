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


type SirenLink = {
    rel: string[],
    href: string
}

function getSirenLink(links: SirenLink[], rel: string): SirenLink {
    return links
        .find(link => {
            const relArr = Array.from(link.rel)
            return relArr.includes(rel)
        })
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
    getOrgsUri: (page: number): string => `${API_HOST}/orgs?page=${page}`,
    getOrgByIdUri: (orgId: number): string => `${API_HOST}/orgs/${orgId}`
}

export {
    getJsonRequestOptions,
    getUrlEncodedRequestOptions,
    SirenLink,
    getSirenLink,
    authRoutes,
    imRoutes,
    userRoutes,
    orgRoutes
}