'use strict'

import { authRoutes, getUrlEncodedRequestOptions } from '../api-routes'
import fetch from 'node-fetch'

export function getAccessToken(code: string): Promise<AccessToken> {
    const options = getUrlEncodedRequestOptions('POST', authRoutes.getAccessTokenRequestBody(code))
    return fetch(authRoutes.getAccessTokenUri, options)
        .then(res => res.json())
        .then(token => {
            const date = new Date().getTime()
            return {
                token: token.access_token,
                expiresAt: new Date(date + token.expires_in * 1000)
            } as AccessToken
        })
}

export function revokeAccessToken(token: string): Promise<ApiResponse> {
    const options = getUrlEncodedRequestOptions('POST', authRoutes.getRevokeAccessTokenRequestBody(token))
    return fetch(authRoutes.getRevokeAccessTokenUri, options)
        .then(res => {
            return {
                status: res.status
            } as ApiResponse
        })
}