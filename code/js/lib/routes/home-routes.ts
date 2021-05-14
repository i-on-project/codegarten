'use strict'

import { NextFunction, Request, Response, Router as expressRouter } from 'express'

const router = expressRouter()

router.get('/', handlerHome)

function handlerHome(req: Request, res: Response, next: NextFunction) {
    res.render('homepage')
}

export = router