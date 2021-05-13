'use strict'

import {Router as expressRouter} from 'express'

const router = expressRouter()

router.get('/', (req, res, next) => {
    const test = req.user
    res.render('layout')
})

export = router