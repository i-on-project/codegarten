'use strict'

let port: number
if(process.argv.length > 2) {
    port = Number(process.argv[2])
}

import { init as initServer } from './lib/server'
initServer(port || Number(process.env.PORT))
