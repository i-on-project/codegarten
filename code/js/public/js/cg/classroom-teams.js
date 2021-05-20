import { alertMsg, getLocationWithoutQuery, hideOverlay, showOverlay, mapEnterToButton, workWithLoading, workWithOverlay, fetchXhr } from './common.js'

function getTeams(content, page, updatePaginationFn) {
    content.innerHTML = ''
    const promise = fetchXhr(`${getLocationWithoutQuery()}?page=${page}`)
        .then(res => {
            if (res.status != 200) return alertMsg('Error while getting teams')
            return res.text()
        })
        .then(fragment => {
            content.innerHTML = fragment ? fragment : ''
            updatePaginationFn(nextPage => getTeams(content, nextPage, updatePaginationFn))
            const createTeamName = $('#teamName')[0]
            const createTeamButton = $('#createTeamButton')

            createTeamName.addEventListener('keyup', (event) => {
                createTeamName.classList.remove('is-invalid')
                mapEnterToButton(createTeamName, event, createTeamButton[0])
            })

            $('.deleteTeamButton').on('click', (event) => {
                const overlay = $(`#deleteTeam${event.target.dataset.teamNumber}Overlay`)
                showOverlay(overlay[0])
            })
            $('.noDeleteTeam').on('click', (event) => {
                hideOverlay(event.target.parentElement.parentElement)
            })
            $('.yesDeleteTeam').on('click', (event) => {
                hideOverlay(event.target.parentElement.parentElement)
                deleteTeam(event.target.dataset.teamNumber)
            })
            createTeamButton.on('click', (event => {
                $('#createTeamForm').modal('hide')
                createTeam(createTeamName, createTeamButton)
            }))
        })
        .catch((err) => alertMsg('Error while getting teams'))

    return promise
}

function createTeam(teamName, createTeamButton) {
    if (teamName.value.length == 0) {
        $('#teamNameFeedback').html('Name can\'t be empty')
        return teamName.classList.add('is-invalid')
    }

    const name = teamName.value
    
    if (name.length > 64) {
        $('#teamNameFeedback').html('Name too long')
        return teamName.classList.add('is-invalid')
    }

    createTeamButton.blur()
    $('#createTeamForm').modal('hide')
    teamName.value = ''

    workWithLoading(
        fetch(getLocationWithoutQuery(), { 
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                name: name,
            })
        })
            .then(res => res.json())
            .then(res => {
                if (!res.wasCreated) alertMsg(res.message)
                else {
                    location.reload()
                }
            })
            .catch(err => alertMsg('Failed to create team')))
}

function deleteTeam(teamNumber) {
    const loadingOverlay = $(`#team${teamNumber}LoadingOverlay`)[0]
    workWithOverlay(loadingOverlay, fetch(`${getLocationWithoutQuery()}/${teamNumber}`, { method: 'DELETE' })
        .then(res => res.json())
        .then(res => {
            hideOverlay(loadingOverlay)
            
            if (res.wasDeleted) {
                alertMsg(res.message, 'success')
                loadingOverlay.parentElement.remove()
            } else alertMsg(res.message)
        })
        .catch(err => {
            hideOverlay(loadingOverlay)
            alertMsg('Failed to delete team')
        }))
}

export {
    getTeams
}