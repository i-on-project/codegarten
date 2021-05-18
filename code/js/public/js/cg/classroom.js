import { mapEnterToButton, alertMsg, workWithLoading, workWithOverlay, getLocation, showOverlay, hideOverlay } from './common.js'
import { getAssignments } from './classroom-assignments.js'
import { getTeams } from './classroom-teams.js'
import { getUsers } from './classroom-users.js'

export function setup() {
    const content = $('#classroomContent')[0]
    if (content) {
        const contentOverlay = $('#loadingClassroomContentOverlay')[0]

        const assignmentsButton = $('#assignmentsOption').parent()[0]
        const usersButton = $('#usersOption').parent()[0]
        const teamsButton = $('#teamsOption').parent()[0]

        $('#assignmentsOption').on('click', (event) => {
            if (event.target.parentElement.classList.contains('active')) return
            usersButton.classList.add('disabled')
            teamsButton.classList.add('disabled')
            workWithOverlay(contentOverlay, getAssignments(content, 0, updatePagination))
                .finally(() => {
                    usersButton.classList.remove('disabled')
                    teamsButton.classList.remove('disabled')
                })
        })
        $('#usersOption').on('click', (event) => {
            if (event.target.parentElement.classList.contains('active')) return
            assignmentsButton.classList.add('disabled')
            teamsButton.classList.add('disabled')
            workWithOverlay(contentOverlay, getUsers(content, 0, updatePagination))
                .finally(() => {
                    assignmentsButton.classList.remove('disabled')
                    teamsButton.classList.remove('disabled')
                })
        })
        $('#teamsOption').on('click', (event) => {
            if (event.target.parentElement.classList.contains('active')) return
            assignmentsButton.classList.add('disabled')
            usersButton.classList.add('disabled')
            workWithOverlay(contentOverlay, getTeams(content, 0, updatePagination))
                .finally(() => {
                    assignmentsButton.classList.remove('disabled')
                    usersButton.classList.remove('disabled')
                })
        })
        setUpEditForm()

        workWithOverlay(contentOverlay, getAssignments(content, 0, updatePagination))
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
        cb($('#prevPage').data('page'))
    })
    $('#nextPage').on('click', (event) => {
        const target = event.target
        if (target.classList.contains('disabled')) return
        cb($('#nextPage').data('page'))
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
        fetch(getLocation(), { 
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
        fetch(getLocation(), { method: 'DELETE' })
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
        fetch(`${getLocation()}/users/${userId}`, { method: 'DELETE' })
            .then(res => res.json())
            .then(res => {
                if (!res.wasRemoved) alertMsg(res.message)
                else {
                    location.href = res.message
                }

            })
            .catch(err => alertMsg('Failed to leave classroom')))
}