'use strict'

import {NextFunction, Request, Response, Router as expressRouter} from 'express'

const router = expressRouter()

router.get('/', (req: Request, res: Response, next: NextFunction) => {
    const test = req.user
    res.render('home')
})

export = router