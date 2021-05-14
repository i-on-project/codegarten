window.onload = setup

import { dismissAlert } from './common.js'
import * as userProfile from './user-profile.js'

function setup() {
    // Close alert
    const closeAlertButton = document.querySelector('#closeAlertButton')
    if (closeAlertButton) {
        closeAlertButton.addEventListener('click', () => dismissAlert())
    }

    userProfile.setup()
}