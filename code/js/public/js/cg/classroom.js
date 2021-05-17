import { mapEnterToButton, alertMsg, workWithLoading, getLocation } from './common.js'

export function setup() {
    const content = $('#classroomContent')[0]
    if (content) {
        getAssignments(content, 0)

        // Disable other buttons when clicking
        $('#assignmentsOption').on('click', (event) => {
            if (event.target.parentElement.classList.contains('active')) return
            getAssignments(content, 0)
        })
        $('#usersOption').on('click', (event) => {
            if (event.target.parentElement.classList.contains('active')) return
            getUsers(content, 0)
        })
        $('#teamsOption').on('click', (event) => {
            if (event.target.parentElement.classList.contains('active')) return
            getTeams(content, 0)
        })
        setUpEditForm()
    }
}

function setUpEditForm() {
    $('#deleteClassroomButton').on('click', event => {
        deleteClassroomShow(event)
    })
    $('#noDeleteClassroom').on('click', (event => {
        deleteClassroomHide(event)
    }))
    $('#yesDeleteClassroom').on('click', (event => {
        deleteClassroom(event)
    }))

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

function getAssignments(content, page) {
    content.innerHTML = ''
    getContentWithLoading(
        fetch(`${getLocation()}/assignments?page=${page}`)
            .then(res => {
                if (res.status != 200) return alertMsg('Error while getting assignments')
                return res.text()
            })
            .then(fragment => {
                content.innerHTML = fragment ? fragment : ''
                updatePagination(nextPage => getAssignments(content, nextPage))
            })
            .catch((err) => alertMsg('Error while getting assignments'))
    )
}

function getUsers(content, page) {
    content.innerHTML = ''
    getContentWithLoading(
        fetch(`${getLocation()}/users?page=${page}`)
            .then(res => {
                if (res.status != 200) return alertMsg('Error while getting users')
                return res.text()
            })
            .then(fragment => {
                content.innerHTML = fragment ? fragment : ''
                updatePagination(nextPage => getUsers(content, nextPage))
            })
            .catch((err) => alertMsg('Error while getting users'))
    )
}

function getTeams(content, page) {
    content.innerHTML = ''
    getContentWithLoading(
        fetch(`${getLocation()}/teams?page=${page}`)
            .then(res => {
                if (res.status != 200) return alertMsg('Error while getting teams')
                return res.text()
            })
            .then(fragment => {
                content.innerHTML = fragment ? fragment : ''
                updatePagination(nextPage => getTeams(content, nextPage))
            })
            .catch((err) => alertMsg('Error while getting teams'))
    )
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

function deleteClassroomShow(event) {
    const overlay = $('#deleteClassroomOverlay')[0]
    overlay.classList.remove('slide-out')
    overlay.classList.add('slide-in')
    overlay.style.display = 'flex'
}

function deleteClassroomHide(event) {
    const overlay = $('#deleteClassroomOverlay')[0]
    overlay.classList.remove('slide-in')
    overlay.classList.add('slide-out')
    setTimeout(() => overlay.style.display = 'none', 200)
}

function deleteClassroom(event) {
    deleteClassroomHide()
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

function getContentWithLoading(workPromise) {
    const overlay = $('#loadingClassroomContentOverlay')[0]
    overlay.classList.remove('slide-out')
    overlay.classList.add('slide-in')
    overlay.style.display = 'flex'

    workPromise
        .then(finishLoadingMsg)
        .catch(finishLoadingMsg)
}

function finishLoadingMsg() {
    const overlay = $('#loadingClassroomContentOverlay')[0]
    overlay.classList.remove('slide-in')
    overlay.classList.add('slide-out')
    setTimeout(() => overlay.style.display = 'none', 200)
}