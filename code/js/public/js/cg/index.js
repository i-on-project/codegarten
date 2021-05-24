window.onload = setup

import { dismissAlert } from './common.js'
import * as userProfile from './user-profile.js'
import * as orgs from './orgs.js'
import * as classrooms from './classrooms.js'
import * as classroom from './classroom.js'
import * as team from './team.js'
import * as assignment from './assignment.js'
import * as invitations from './invitations.js'

function setup() {
    // Close alert
    const closeAlertButton = document.querySelector('#closeAlertButton')
    if (closeAlertButton) {
        closeAlertButton.addEventListener('click', () => dismissAlert())
    }

    userProfile.setup()
    orgs.setup()
    classrooms.setup()
    classroom.setup()
    team.setup()
    assignment.setup()
    invitations.setup()
}