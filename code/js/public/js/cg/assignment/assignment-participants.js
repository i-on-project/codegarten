import { alertMsg, getLocationWithoutQuery, hideOverlay, showOverlay, mapEnterToButton, workWithLoading, workWithOverlay, fetchXhr, getLocation } from '../common.js'

function getParticipants(content, page, updatePaginationFn) {
    content.innerHTML = ''

    const promise = fetchXhr(`${getLocationWithoutQuery()}?page=${page}`)
        .then(res => {
            if (res.status != 200) return alertMsg('Error while getting participants')
            return res.text()
        })
        .then(fragment => {
            content.innerHTML = fragment ? fragment : ''
            updatePaginationFn('prevPageParticipants', 'nextPageParticipants', 
                nextPage => getParticipants(content, nextPage, updatePaginationFn)
            )
            const deliveriesModal = $('#checkDeliveriesModal')
            const deliveriesModalTitle = $('#deliveriesModalTitle')
            const deliveriesLoading = $('#checkDeliveriesLoading')[0]

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
            $('.checkDeliveriesButton').on('click', (event) => {
                deliveriesModalTitle.text(`Deliveries of ${event.target.dataset.participantName}`)
                $('#deliveriesContent').html('')
                deliveriesModal.modal('show')
                getParticipantDeliveries(deliveriesLoading, event.target.dataset.participantId, 0, updatePaginationFn)
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

function getParticipantDeliveries(loadingOverlay, participantId, page, updatePaginationFn) {
    workWithOverlay(loadingOverlay, 
        fetchXhr(`${getLocation()}/${participantId}/deliveries?page=${page}`)
            .then(res => {
                if (res.status != 200) return alertMsg('Error while getting deliveries')
                return res.text()
            })
            .then(res => {
                $('#deliveriesContent').html(res)
                updateDeliveriesPagination(nextPage => getParticipantDeliveries(loadingOverlay, participantId, nextPage, updatePaginationFn))
            })
            .catch((err) => alertMsg('Error while getting deliveries'))
    )
}

function updateDeliveriesPagination(cb) {
    $('#prevPageDeliveries').on('click', (event) => {
        const target = event.target
        if (target.classList.contains('disabled')) return
        cb($('#prevPageDeliveries').data('page'))
    })
    $('#nextPageDeliveries').on('click', (event) => {
        const target = event.target
        if (target.classList.contains('disabled')) return
        cb($('#nextPageDeliveries').data('page'))
    })
}

export {
    getParticipants
}
