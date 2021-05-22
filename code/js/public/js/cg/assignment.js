import { mapEnterToButton, alertMsg, workWithLoading, workWithOverlay, getLocation, showOverlay, hideOverlay, getLocationWithoutQuery } from './common.js'
import { getParticipants } from './assignment/assignment-participants.js'
import { getDeliveries } from './assignment/assignment-deliveries.js'

let assignmentUri

export function setup() {
    const content = $('#assignmentContent')[0]
    if (content) {
        const location = getLocationWithoutQuery()
        let currentOption = location.substring(location.lastIndexOf('/') + 1)
        assignmentUri = location.substring(0, location.lastIndexOf('/'))

        const contentOverlay = $('#loadingAssignmentContentOverlay')[0]

        const participantsButton = $('#participantsOption').parent()[0]
        const deliveriesButton = $('#deliveriesOption').parent()[0]

        $('#participantsOption').on('click', (event) => {
            if (participantsButton.classList.contains('active')) return
            history.pushState({}, '', `${assignmentUri}/participants`)
            updateState(content, contentOverlay, 'participants', participantsButton, deliveriesButton)
        })
        $('#deliveriesOption').on('click', (event) => {
            if (deliveriesButton.classList.contains('active')) return
            history.pushState({}, '', `${assignmentUri}/deliveries`)
            updateState(content, contentOverlay, 'deliveries', participantsButton, deliveriesButton)
        })
        setUpEditForm()

        window.onpopstate = () => {
            currentOption = getLocation().substring(getLocation().lastIndexOf('/') + 1)
            updateState(content, contentOverlay, currentOption, participantsButton, deliveriesButton)
        }

        updateState(content, contentOverlay, currentOption, participantsButton, deliveriesButton)
    }
}

function updateState(content, contentOverlay, currentOption, participantsButton, deliveriesButton) {
    switch(currentOption) {
        case 'participants': 
            if (participantsButton.classList.contains('active')) return
            participantsButton.classList.add('active')

            deliveriesButton.classList.add('disabled')
            deliveriesButton.classList.remove('active')

            workWithOverlay(contentOverlay, getParticipants(content, 0, updatePagination))
                .finally(() => {
                    deliveriesButton.classList.remove('disabled')
                })
            break
        case 'deliveries':
            if (deliveriesButton.classList.contains('active')) return
            deliveriesButton.classList.add('active')

            participantsButton.classList.add('disabled')
            participantsButton.classList.remove('active')

            workWithOverlay(contentOverlay, getDeliveries(content, 0, updatePagination))
                .finally(() => {
                    participantsButton.classList.remove('disabled')
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

    $('#deleteAssignmentButton').on('click', (event) => {
        confirmMessage.text('Are you sure you want to delete this assignment')

        yesConfirm.off()
        yesConfirm.on('click', (event) => {
            hideOverlay(overlay)
            deleteAssignment()
        })
        showOverlay(overlay)
    })
    $('#leaveAssignmentButton').on('click', (event) => {
        const participantId = event.target.dataset.participantId

        confirmMessage.text('Are you sure you want to leave this assignment?')
        yesConfirm.off()
        yesConfirm.on('click', (event) => {
            hideOverlay(overlay)
            leaveAssignment(participantId)
        })
        showOverlay(overlay)
    })

    const editButton = $('#editAssignmentButton')
    const assignmentName = $('#editAssignmentName')
    const assignmentDescription = $('#editAssignmentDescription')
    assignmentName.on('keyup', (event) => {
        assignmentName[0].classList.remove('is-invalid')
        assignmentDescription[0].classList.remove('is-invalid')
        mapEnterToButton(event.target, event, editButton[0])
    })
    assignmentDescription.on('keyup', (event) => {
        assignmentName[0].classList.remove('is-invalid')
        assignmentDescription[0].classList.remove('is-invalid')
        mapEnterToButton(event.target, event, editButton[0])
    })
    editButton.on('click', (event) => {
        editAssignment(editButton[0], assignmentName[0], assignmentDescription[0])
    })
}

function updatePagination(cb) {
    $('#prevPage').on('click', (event) => {
        const target = event.target
        if (target.classList.contains('disabled')) return
        workWithOverlay($('#loadingAssignmentContentOverlay')[0], 
            cb($('#prevPage').data('page'))
        )    
    })
    $('#nextPage').on('click', (event) => {
        const target = event.target
        if (target.classList.contains('disabled')) return
        workWithOverlay($('#loadingAssignmentContentOverlay')[0], 
            cb($('#nextPage').data('page'))
        )
    })
}

function editAssignment(editButton, assignmentName, assignmentDescription) {
    if (assignmentName.value.length == 0 && assignmentDescription.value.length == 0) {
        $('#assignmentNameFeedback').html('Please change at least one field')
        assignmentName.classList.add('is-invalid')
        $('#assignmentDescriptionFeedback').html('Please change at least one field')
        return assignmentDescription.classList.add('is-invalid')
    }

    const name = assignmentName.value.length == 0 ? null : assignmentName.value
    const description = assignmentDescription.value.length == 0 ? null : assignmentDescription.value
    
    if (name != null && name.length > 64) {
        $('#assignmentNameFeedback').html('Name too long')
        return assignmentName.classList.add('is-invalid')
    }
    if (description != null && description.length > 256) {
        $('#assignmentDescriptionFeedback').html('Description too long')
        assignmentDescription.classList.add('is-invalid') 
    }

    editButton.blur()
    $('#editAssignmentForm').modal('hide')
    assignmentName.value = ''
    assignmentDescription.value = ''

    workWithLoading(
        fetch(assignmentUri, { 
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
                        assignmentName.placeholder = name
                        $('#assignmentName').text(name)
                    }
                    if (description) {
                        assignmentDescription.placeholder = description
                        $('#assignmentDescription').text(description)
                    }
                }
            })
            .catch(err => alertMsg('Failed to edit assignment')))
}

function deleteAssignment() {
    $('#editAssignmentForm').modal('hide')
    workWithLoading(
        fetch(assignmentUri, { method: 'DELETE' })
            .then(res => res.json())
            .then(res => {
                if (!res.wasDeleted) alertMsg(res.message)
                else {
                    location.href = res.message
                }

            })
            .catch(err => alertMsg('Failed to delete assignment')))
}

function leaveAssignment(participantId) {
    workWithLoading(
        fetch(`${assignmentUri}/participants/${participantId}`, { method: 'DELETE' })
            .then(res => res.json())
            .then(res => {
                if (!res.wasRemoved) alertMsg(res.message)
                else {
                    location.href = res.message
                }
            })
            .catch(err => alertMsg('Failed to leave assignment')))
}