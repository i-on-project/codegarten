import { mapEnterToButton, alertMsg, workWithLoading, getLocation, showOverlay, hideOverlay } from './common.js'

export function setup() {
    const content = $('#classroomContent')[0]
    if (content) {
        getAssignments(content, 0)
        const assignmentsButton = $('#assignmentsOption').parent()[0]
        const usersButton = $('#usersOption').parent()[0]
        const teamsButton = $('#teamsOption').parent()[0]

        // Disable other buttons when clicking
        $('#assignmentsOption').on('click', (event) => {
            if (event.target.parentElement.classList.contains('active')) return
            usersButton.classList.add('disabled')
            teamsButton.classList.add('disabled')
            getAssignments(content, 0)
                .finally(() => {
                    usersButton.classList.remove('disabled')
                    teamsButton.classList.remove('disabled')
                })
        })
        $('#usersOption').on('click', (event) => {
            if (event.target.parentElement.classList.contains('active')) return
            assignmentsButton.classList.add('disabled')
            teamsButton.classList.add('disabled')
            getUsers(content, 0)
                .finally(() => {
                    assignmentsButton.classList.remove('disabled')
                    teamsButton.classList.remove('disabled')
                })
        })
        $('#teamsOption').on('click', (event) => {
            if (event.target.parentElement.classList.contains('active')) return
            assignmentsButton.classList.add('disabled')
            usersButton.classList.add('disabled')
            getTeams(content, 0)
                .finally(() => {
                    assignmentsButton.classList.remove('disabled')
                    usersButton.classList.remove('disabled')
                })
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
    const promise = fetch(`${getLocation()}/assignments?page=${page}`)
        .then(res => {
            if (res.status != 200) return alertMsg('Error while getting assignments')
            return res.text()
        })
        .then(fragment => {
            content.innerHTML = fragment ? fragment : ''
            updatePagination(nextPage => getAssignments(content, nextPage))
        })
        .catch((err) => alertMsg('Error while getting assignments'))
        
    getContentWithLoading(promise)
    return promise
}

function getUsers(content, page) {
    content.innerHTML = ''
    const promise = fetch(`${getLocation()}/users?page=${page}`)
        .then(res => {
            if (res.status != 200) return alertMsg('Error while getting users')
            return res.text()
        })
        .then(fragment => {
            content.innerHTML = fragment ? fragment : ''
            updatePagination(nextPage => getUsers(content, nextPage))
            $('.removeUserButton').on('click', (event) => {
                const overlay = $(`#removeUser${event.target.dataset.userId}Overlay`)
                showOverlay(overlay[0])
            })
            $('.noRemoveUser').on('click', (event) => {
                hideOverlay(event.target.parentElement.parentElement)
            })
            $('.yesRemoveUser').on('click', (event) => {
                const overlay = $(`#removeUser${event.target.dataset.userId}Overlay`)
                removeUser(overlay[0], event.target.dataset.userId)
            })
            $('.userRoleDropdown a').on('click', (event => {
                if (event.target.classList.contains('active')) return
                updateUserRole(event.target)
            }))
        })
        .catch((err) => alertMsg('Error while getting users'))

    getContentWithLoading(promise)
    return promise
}

function getTeams(content, page) {
    content.innerHTML = ''
    const promise = fetch(`${getLocation()}/teams?page=${page}`)
        .then(res => {
            if (res.status != 200) return alertMsg('Error while getting teams')
            return res.text()
        })
        .then(fragment => {
            content.innerHTML = fragment ? fragment : ''
            updatePagination(nextPage => getTeams(content, nextPage))
        })
        .catch((err) => alertMsg('Error while getting teams'))

    getContentWithLoading(promise)
    return promise
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

function removeUser(overlay, userId) {
    showOverlay(overlay)
    const loadingOverlay = $(`#user${userId}LoadingOverlay`)[0]
    showOverlay(loadingOverlay)

    fetch(`${getLocation()}/users/${userId}`, { method: 'DELETE' })
        .then(res => res.json())
        .then(res => {
            hideOverlay(loadingOverlay)
            
            if (res.wasRemoved) {
                alertMsg(res.message, 'success')
                loadingOverlay.parentElement.remove()
            } else alertMsg(res.message)
        })
        .catch(err => {
            hideOverlay(loadingOverlay)
            alertMsg('Failed to remove user')
        })
}

function updateUserRole(elem) {
    const userId = elem.parentElement.dataset.userId
    const role = elem.dataset.role
    const dropdownButton = $(`#user${userId}RoleDropdownButton`)[0]

    const overlay = $(`#user${userId}LoadingOverlay`)[0]
    showOverlay(overlay)
    fetch(`${getLocation()}/users/${userId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            role: role
        })
    })
        .then(res => res.json())
        .then(res => {
            if (!res.wasEdited) alertMsg(res.message)
            else {
                elem.classList.add('active')
                alertMsg(res.message, 'success')
                if (role == 'teacher') {
                    elem.nextElementSibling.classList.remove('active')
                    dropdownButton.classList.remove('btn-outline-secondary')
                    dropdownButton.classList.add('btn-outline-success')
                    dropdownButton.innerHTML = `
                    <i class="fas fa-chalkboard-teacher mr-2"></i>
                    Teacher
                    `
                } else {
                    elem.previousElementSibling.classList.remove('active')
                    dropdownButton.classList.add('btn-outline-secondary')
                    dropdownButton.classList.remove('btn-outline-success')
                    dropdownButton.innerHTML = `
                    <i class="fas fa-user-graduate mr-2"></i>
                    Student
                    `
                }
            }
            hideOverlay(overlay)
        })
        .catch(err => {
            alertMsg('Failed to update user role')
            hideOverlay(overlay)
        })
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