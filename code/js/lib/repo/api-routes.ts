'use strict'

import { RequestInit } from 'node-fetch'

const API_HOST = 'http://localhost:8080/api'
const IM_HOST = 'http://localhost:8080/im'
const CLIENT_ID = process.env.CG_CLIENT_ID
const CLIENT_SECRET = process.env.CG_CLIENT_SECRET

export function getJsonRequestOptions(method: string, accessToken: string, body: string = null): RequestInit {
    return {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'Authorization': `Bearer ${accessToken}`
        },
        body: body
    }
}

export function getUrlEncodedRequestOptions(method: string, body: string = null): RequestInit {
    return {
        method: method,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept': 'application/json',
        },
        body: body
    }
}


export type SirenLink = {
    rel: string[],
    href: string
}

export function getSirenLink(links: SirenLink[], rel: string): SirenLink {
    return links
        .find(link => {
            const relArr = Array.from(link.rel)
            return relArr.includes(rel)
        })
}

export const authRoutes = {
    // TODO: Implement state
    getAuthCode: `${IM_HOST}/oauth/authorize?client_id=${CLIENT_ID}`,
    getAccessToken: `${API_HOST}/oauth/access_token`,
    getAccessTokenRequestBody: (code: string): string => 
        `client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}&code=${code}`
}

export const userRoutes = {
    getAuthenticatedUser: `${API_HOST}/user`
}

