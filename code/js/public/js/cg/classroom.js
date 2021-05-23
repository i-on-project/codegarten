import { mapEnterToButton, alertMsg, workWithLoading, workWithOverlay, getLocation, showOverlay, hideOverlay, getLocationWithoutQuery } from './common.js'
import { getAssignments } from './classroom/classroom-assignments.js'
import { getTeams } from './classroom/classroom-teams.js'
import { getUsers } from './classroom/classroom-users.js'

let classroomUri

export function setup() {
    const content = $('#classroomContent')[0]
    if (content) {
        const location = getLocationWithoutQuery()
        let currentOption = location.substring(location.lastIndexOf('/') + 1)
        classroomUri = location.substring(0, location.lastIndexOf('/'))

        const contentOverlay = $('#loadingClassroomContentOverlay')[0]

        const assignmentsButton = $('#assignmentsOption').parent()[0]
        const usersButton = $('#usersOption').parent()[0]
        const teamsButton = $('#teamsOption').parent()[0]

        $('#assignmentsOption').on('click', (event) => {
            if (assignmentsButton.classList.contains('active')) return
            history.pushState({}, '', `${classroomUri}/assignments`)
            updateState(content, contentOverlay, 'assignments', assignmentsButton, usersButton, teamsButton)
        })
        $('#usersOption').on('click', (event) => {
            if (usersButton.classList.contains('active')) return
            history.pushState({}, '', `${classroomUri}/users`)
            updateState(content, contentOverlay, 'users', assignmentsButton, usersButton, teamsButton)
        })
        $('#teamsOption').on('click', (event) => {
            if (teamsButton.classList.contains('active')) return
            history.pushState({}, '', `${classroomUri}/teams`)
            updateState(content, contentOverlay, 'teams', assignmentsButton, usersButton, teamsButton)
        })
        const inviteUri = $('#inviteUri')[0]
        $('#inviteButton').on('click', (event) => {
            inviteUri.select()
            inviteUri.setSelectionRange(0, 99999)
            document.execCommand('copy')
            alertMsg('Invite link copied to clipboard', 'success')
        })
        setUpEditForm()

        window.onpopstate = () => {
            currentOption = getLocation().substring(getLocation().lastIndexOf('/') + 1)
            updateState(content, contentOverlay, currentOption, assignmentsButton, usersButton, teamsButton)
        }

        updateState(content, contentOverlay, currentOption, assignmentsButton, usersButton, teamsButton)
    }
}

function updateState(content, contentOverlay, currentOption, assignmentsButton, usersButton, teamsButton) {
    switch(currentOption) {
        case 'assignments':
            if (assignmentsButton.classList.contains('active')) return 
            assignmentsButton.classList.add('active')
            
            usersButton.classList.add('disabled')
            usersButton.classList.remove('active')
            teamsButton.classList.add('disabled')
            teamsButton.classList.remove('active')

            workWithOverlay(contentOverlay, getAssignments(content, 0, updatePagination))
                .finally(() => {
                    usersButton.classList.remove('disabled')
                    teamsButton.classList.remove('disabled')
                })
            break
        case 'users': 
            if (usersButton.classList.contains('active')) return
            usersButton.classList.add('active')

            assignmentsButton.classList.add('disabled')
            assignmentsButton.classList.remove('active')
            teamsButton.classList.add('disabled')
            teamsButton.classList.remove('active')

            workWithOverlay(contentOverlay, getUsers(content, 0, updatePagination))
                .finally(() => {
                    assignmentsButton.classList.remove('disabled')
                    teamsButton.classList.remove('disabled')
                })
            break
        case 'teams':
            if (teamsButton.classList.contains('active')) return
            teamsButton.classList.add('active')

            assignmentsButton.classList.add('disabled')
            assignmentsButton.classList.remove('active')
            usersButton.classList.add('disabled')
            usersButton.classList.remove('active')

            workWithOverlay(contentOverlay, getTeams(content, 0, updatePagination))
                .finally(() => {
                    assignmentsButton.classList.remove('disabled')
                    usersButton.classList.remove('disabled')
                })
            break
    }
}

function setUpEditForm() {
    const overlay = $('#confirmOverlay')[0]
    const yesConfirm = $('#yesConfirm')
    const confirmMessage = $('#confirmOverlayMessage')

    $('#noConfirm').on('click', (event) => {
        hideOverlay(overlay)
    })

    $('#deleteClassroomButton').on('click', (event) => {
        confirmMessage.text('Are you sure you want to delete this classroom?')

        yesConfirm.off()
        yesConfirm.on('click', (event) => {
            hideOverlay(overlay)
            deleteClassroom()
        })
        showOverlay(overlay)
    })
    $('#leaveClassroomButton').on('click', (event) => {
        const userId = event.target.dataset.userId

        confirmMessage.text('Are you sure you want to leave this classroom?')
        yesConfirm.off()
        yesConfirm.on('click', (event) => {
            hideOverlay(overlay)
            leaveClassroom(userId)
        })
        showOverlay(overlay)
    })

    const editButton = $('#editClassroomButton')
    const classroomName = $('#editClassroomName')
    const classroomDescription = $('#editClassroomDescription')
    classroomName.on('keyup', (event) => {
        classroomName[0].classList.remove('is-invalid')
        classroomDescription[0].classList.remove('is-invalid')
        mapEnterToButton(event.target, event, editButton[0])
    })
    classroomDescription.on('keyup', (event) => {
        classroomName[0].classList.remove('is-invalid')
        classroomDescription[0].classList.remove('is-invalid')
        mapEnterToButton(event.target, event, editButton[0])
    })
    editButton.on('click', (event) => {
        editClassroom(editButton[0], classroomName[0], classroomDescription[0])
    })
}

function updatePagination(cb) {
    $('#prevPage').on('click', (event) => {
        const target = event.target
        if (target.classList.contains('disabled')) return
        workWithOverlay($('#loadingClassroomContentOverlay')[0], 
            cb($('#prevPage').data('page'))
        )
        
    })
    $('#nextPage').on('click', (event) => {
        const target = event.target
        if (target.classList.contains('disabled')) return
        workWithOverlay($('#loadingClassroomContentOverlay')[0], 
            cb($('#nextPage').data('page'))
        )
    })
}

function editClassroom(editButton, classroomName, classroomDescription) {
    if (classroomName.value.length == 0 && classroomDescription.value.length == 0) {
        $('#classroomNameFeedback').html('Please change at least one field')
        classroomName.classList.add('is-invalid')
        $('#classroomDescriptionFeedback').html('Please change at least one field')
        return classroomDescription.classList.add('is-invalid')
    }

    const name = classroomName.value.length == 0 ? null : classroomName.value
    const description = classroomDescription.value.length == 0 ? null : classroomDescription.value
    
    if (name != null && name.length > 64) {
        $('#classroomNameFeedback').html('Name too long')
        return classroomName.classList.add('is-invalid')
    }
    if (description != null && description.length > 256) {
        $('#classroomDescriptionFeedback').html('Description too long')
        classroomDescription.classList.add('is-invalid') 
    }

    editButton.blur()
    $('#editClassroomForm').modal('hide')
    classroomName.value = ''
    classroomDescription.value = ''

    workWithLoading(
        fetch(classroomUri, { 
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({name: name, description: description})
        })
            .then(res => res.json())
            .then(res => {
                if (!res.wasEdited) alertMsg(res.message)
                else {
                    alertMsg(res.message, 'success')
                    if (name) {
                        classroomName.placeholder = name
                        $('#classroomName').text(name)
                    }
                    if (description) {
                        classroomDescription.placeholder = description
                        $('#classroomDescription').text(description)
                    }
                }
            })
            .catch(err => alertMsg('Failed to edit classroom')))
}

function deleteClassroom() {
    $('#editClassroomForm').modal('hide')
    workWithLoading(
        fetch(classroomUri, { method: 'DELETE' })
            .then(res => res.json())
            .then(res => {
                if (!res.wasDeleted) alertMsg(res.message)
                else {
                    location.href = res.message
                }

            })
            .catch(err => alertMsg('Failed to delete classroom')))
}

function leaveClassroom(userId) {
    workWithLoading(
        fetch(`${classroomUri}/users/${userId}`, { method: 'DELETE' })
            .then(res => res.json())
            .then(res => {
                if (!res.wasRemoved) alertMsg(res.message)
                else {
                    location.href = res.message
                }
            })
            .catch(err => alertMsg('Failed to leave classroom')))
}