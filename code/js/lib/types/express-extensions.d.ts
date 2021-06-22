import express from 'express'

declare module 'express' {
    export interface Request {
        user: AuthenticatedUser
        login: (user: AuthenticatedUser) => Promise<boolean>
        logout: () => Promise<boolean>
        flash(type: string, message: string): void
        flash(type: string): string
        redirectLoginTo(uri: string): void
        getLoginRedirect(): string
        getState(): string
        generateRandomState(): string
        getAllCookies(): Map<string, string>
        getCookie(key: string): string
    }

    export interface Response {
        expireCookie(key: string): void
        setCookie(key: string, value: string, expiresAt: Date): void
        setCookie(key: string, value: string): void
    }
}