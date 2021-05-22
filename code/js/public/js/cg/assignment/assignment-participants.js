import { alertMsg, getLocationWithoutQuery, hideOverlay, showOverlay, mapEnterToButton, workWithLoading, workWithOverlay, fetchXhr } from '../common.js'

function getParticipants(content, page, updatePaginationFn) {
    content.innerHTML = ''
    const promise = fetchXhr(`${getLocationWithoutQuery()}?page=${page}`)
        .then(res => {
            if (res.status != 200) return alertMsg('Error while getting participants')
            return res.text()
        })
        .then(fragment => {
            content.innerHTML = fragment ? fragment : ''
            updatePaginationFn(nextPage => getParticipants(content, nextPage, updatePaginationFn))

            $('.removeParticipantButton').on('click', (event) => {
                const overlay = $(`#removeParticipant${event.target.dataset.participantId}Overlay`)
                showOverlay(overlay[0])
            })
            $('.noRemoveParticipant').on('click', (event) => {
                hideOverlay(event.target.parentElement.parentElement)
            })
            $('.yesRemoveParticipant').on('click', (event) => {
                hideOverlay(event.target.parentElement.parentElement)
                removeParticipant(event.target.dataset.participantId)
            })
        })
        .catch((err) => alertMsg('Error while getting participants'))

    return promise
}

function removeParticipant(participantId) {
    const loadingOverlay = $(`#participant${participantId}LoadingOverlay`)[0]
    
    workWithOverlay(loadingOverlay, fetch(`${getLocationWithoutQuery()}/${participantId}`, { method: 'DELETE' })
        .then(res => res.json())
        .then(res => {
            if (res.wasRemoved) {
                alertMsg('Participant removed successfully', 'success')
                loadingOverlay.parentElement.remove()
            } else alertMsg(res.message)
        })
        .catch(err => {
            alertMsg('Failed to remove participant')
        }))
}

export {
    getParticipants
}