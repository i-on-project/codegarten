import session from 'express-session'
import express from 'express'

declare module 'express' {
    export interface Request {
        user: AuthenticatedUser
        login: (user: AuthenticatedUser) => Promise<boolean>
        logout: () => Promise<boolean>
    }
}

declare module 'express-session' {
    export interface Session {
      accessToken: AccessToken
      redirectUri: string
    }
  }