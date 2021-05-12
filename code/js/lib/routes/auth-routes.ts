'use strict'

import {Router as expressRouter} from 'express'
import passport from 'passport'

const router = expressRouter()


passport.serializeUser((user, done) =>  {
    // TODO:
})
  
passport.deserializeUser((username, done) => {
    // TODO:
})

export = router