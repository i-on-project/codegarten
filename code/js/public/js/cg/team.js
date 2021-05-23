import { alertMsg, fetchXhr, getLocation, hideOverlay, mapEnterToButton, showOverlay, workWithLoading, workWithOverlay } from './common.js'

export function setup() {
    const teamNameForm = $('#editTeamNameForm')[0]
    if (teamNameForm) {
        const newTeamName = $('#editTeamName')[0]
        const button = teamNameForm.querySelector('button')
        button.addEventListener('click', () => editName(button, newTeamName))
        newTeamName.addEventListener('keyup', (event) => {
            newTeamName.classList.remove('is-invalid')
            mapEnterToButton(newTeamName, event, button)
        })
    }

    const deleteOrLeaveOverlay = $('#deleteAndLeaveOverlay')[0]
    const deleteTeamButton = $('#deleteTeamButton')[0]
    const leaveTeamButton = $('#leaveTeamButton')[0]

    const overlayYes = $('#yesDeleteOrLeaveTeam')
    const overlayNo = $('#noDeleteOrLeaveTeam')
    overlayNo.on('click', () => hideOverlay(deleteOrLeaveOverlay))

    if (deleteTeamButton) {
        deleteTeamButton.addEventListener('click', () => {
            $('#overlayMessage').text('Are you sure you want to delete this team?')
            showOverlay(deleteOrLeaveOverlay)
            
            overlayYes.off()
            overlayYes.on('click', () => {
                hideOverlay(deleteOrLeaveOverlay)
                deleteTeam()
            })
        })
    }

    if (leaveTeamButton) {
        leaveTeamButton.addEventListener('click', event => {
            $('#overlayMessage').text('Are you sure you want to leave this team?')
            showOverlay(deleteOrLeaveOverlay)
            
            overlayYes.off()
            overlayYes.on('click', () => {
                hideOverlay(deleteOrLeaveOverlay)
                leaveTeam(event.target.dataset.userId)
            })
        })
    }


    const teamUsers = $('#teamUsers')[0]
    if (teamUsers) {
        const loadingOverlay = $('#loadingTeamUsersOverlay')[0]
        workWithOverlay(loadingOverlay, getTeamUsers(teamUsers, 0))
    }
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


function getTeamUsers(content, page) {
    content.innerHTML = ''
    const promise = fetchXhr(`${getLocation()}/users?page=${page}`)
        .then(res => {
            if (res.status != 200) return alertMsg('Error while getting users')
            return res.text()
        })
        .then(fragment => {
            content.innerHTML = fragment ? fragment : ''
            updatePagination(nextPage => getTeamUsers(content, nextPage))
            $('.removeUserButton').on('click', (event) => {
                const overlay = $(`#removeUser${event.target.dataset.userId}Overlay`)
                showOverlay(overlay[0])
            })
            $('.noRemoveUser').on('click', (event) => {
                hideOverlay(event.target.parentElement.parentElement)
            })
            $('.yesRemoveUser').on('click', (event) => {
                hideOverlay(event.target.parentElement.parentElement)
                removeTeamUser(event.target.dataset.userId)
            })
        })
        .catch((err) => alertMsg('Error while getting users'))

    return promise
}

function editName(button, newName) {
    if (newName.value.length == 0 || newName.value == newName.placeholder) {
        $('#teamNameFeedback').html('Please enter a new name')
        newName.focus()
        return newName.classList.add('is-invalid')
    }
    const name = newName.value
    
    if (name.length > 64) {
        $('#teamNameFeedback').html('Name too long')
        newName.focus()
        return newName.classList.add('is-invalid')
    }

    button.blur()
    workWithLoading(
        fetch(getLocation(), { 
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({name: name})
        })
            .then(res => res.json())
            .then(res => {
                if (!res.wasEdited) alertMsg(res.message)
                else {
                    $('#editTeamNameForm').collapse('hide')
                    alertMsg(res.message, 'success')
                    newName.value = ''
                    newName.placeholder = name
                    $('#teamName').text(name)
                }

            })
            .catch(err => alertMsg('Failed to edit name')))
}

function leaveTeam(userId) {
    workWithLoading(
        fetch(`${getLocation()}/users/${userId}`, { 
            method: 'DELETE'
        })
            .then(res => res.json())
            .then(res => {
                if (!res.wasRemoved) alertMsg(res.message)
                else {
                    window.location.href = './'
                }
            })
            .catch(err => alertMsg('Failed to leave team')))
}

function removeTeamUser(userId) {
    const loadingOverlay = $(`#user${userId}LoadingOverlay`)[0]

    workWithOverlay(loadingOverlay,
        fetch(`${getLocation()}/users/${userId}`, { 
            method: 'DELETE'
        })
            .then(res => res.json())
            .then(res => {
                if (!res.wasRemoved) alertMsg(res.message)
                else {
                    alertMsg(res.message, 'success')
                    loadingOverlay.parentElement.remove()
                }
            })
            .catch(err => alertMsg('Failed to leave team')))
}

function deleteTeam() {
    workWithLoading(
        fetch(getLocation(), { 
            method: 'DELETE'
        })
            .then(res => res.json())
            .then(res => {
                if (!res.wasDeleted) alertMsg(res.message)
                else {
                    window.location.href = './'
                }
            })
            .catch(err => alertMsg('Failed to delete team')))
}