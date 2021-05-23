import { alertMsg, getLocationWithoutQuery, hideOverlay, showOverlay, mapEnterToButton, workWithLoading, workWithOverlay, fetchXhr, generateSlug, getLocation, validateTag } from '../common.js'

function getDeliveries(content, assignmentUri, page, updatePaginationFn) {
    content.innerHTML = ''
    const participantId = $('#deliveriesOption')[0].dataset.participantId

    const location = participantId.length == 0 ? getLocationWithoutQuery() : `${assignmentUri}/participants/${participantId}/deliveries`

    const promise = fetchXhr(`${location}?page=${page}`)
        .then(res => {
            if (res.status != 200) return alertMsg('Error while getting deliveries')
            return res.text()
        })
        .then(fragment => {
            content.innerHTML = fragment ? fragment : ''
            setupCreationForm()
            updatePaginationFn(nextPage => getDeliveries(content, nextPage, updatePaginationFn))

            $('.deleteDeliveryButton').on('click', (event) => {
                const overlay = $(`#deleteDelivery${event.target.dataset.deliveryNumber}Overlay`)
                showOverlay(overlay[0])
            })
            $('.noDeleteDelivery').on('click', (event) => {
                hideOverlay(event.target.parentElement.parentElement)
            })
            $('.yesDeleteDelivery').on('click', (event) => {
                hideOverlay(event.target.parentElement.parentElement)
                deleteDelivery(event.target.dataset.deliveryNumber)
            })
        })
        .catch((err) => alertMsg('Error while getting deliveries'))
        
    return promise
}

function setupCreationForm() {
    const tag = $('#deliveryTag')
    const dueDate = $('#deliveryDueDate')
    const dueTime = $('#deliveryDueTime')
    const createButton = $('#createDeliveryButton')

    tag.on('input', (event) => {
        tag.removeClass('is-invalid')
        tag.val(tag.val().replace(' ', '-'))
    })
    tag.on('keyup', (event) => mapEnterToButton(tag[0], event, createButton[0]))

    dueDate.on('input', (event) => dueDate.removeClass('is-invalid'))
    dueDate.on('keyup', (event) => mapEnterToButton(dueDate[0], event, createButton[0]))
    dueTime.on('keyup', (event) => mapEnterToButton(dueTime[0], event, createButton[0]))

    

    createButton.on('click', (event) => {
        createDelivery(createButton, tag, dueDate, dueTime)
    })
}

function createDelivery(createButton, deliveryTag, deliveryDueDate, deliveryDueTime) {
    if (deliveryTag.val().length == 0) {
        $('#deliveryTagFeedback').html('Tag can\'t be empty')
        return deliveryTag.addClass('is-invalid')
    }

    const tag = deliveryTag.val()
    const dueDate = deliveryDueDate.val()
    const dueTime = deliveryDueTime.val()
    
    if (tag.length > 64) {
        $('#deliveryTagFeedback').html('Tag too long')
        return deliveryTag.addClass('is-invalid')
    }
    if (!validateTag(tag)) {
        $('#deliveryTagFeedback').html('Invalid tag')
        return deliveryTag.addClass('is-invalid')
    }
    if (dueTime.length > 0 && dueDate.length == 0) {
        $('#deliveryDueDateFeedback').html('Can\'t set a time without a date')
        return deliveryDueDate.addClass('is-invalid')
    }

    createButton.blur()
    $('#createDeliveryForm').modal('hide')

    workWithLoading(
        fetch(getLocation(), { 
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                tag: tag,
                dueDate: dueDate.length == 0 ? null : `${dueDate}T${dueTime.length == 0 ? '00:00' : dueTime}`
            })
        })
            .then(res => res.json())
            .then(res => {
                if (!res.wasCreated) return alertMsg(res.message)
                location.reload()
            })
            .catch(err => alertMsg('Failed to create delivery')))
}

function deleteDelivery(deliveryNumber) {
    const loadingOverlay = $(`#delivery${deliveryNumber}LoadingOverlay`)[0]
    
    workWithOverlay(loadingOverlay, fetch(`${getLocationWithoutQuery()}/${deliveryNumber}`, { method: 'DELETE' })
        .then(res => res.json())
        .then(res => {
            if (res.wasDeleted) {
                alertMsg('Delivery deleted successfully', 'success')
                loadingOverlay.parentElement.remove()
            } else alertMsg(res.message)
        })
        .catch(err => {
            alertMsg('Failed to delete delivery')
        }))
}

export {
    getDeliveries
}