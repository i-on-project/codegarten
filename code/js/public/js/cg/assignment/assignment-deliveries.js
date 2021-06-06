import { alertMsg, getLocationWithoutQuery, hideOverlay, showOverlay, mapEnterToButton, workWithLoading, workWithOverlay, fetchXhr, generateSlug, getLocation, validateTag } from '../common.js'

function getDeliveries(content, assignmentUri, page, updatePaginationFn) {
    content.innerHTML = ''
    const participantId = $('#deliveriesOption')[0].dataset.participantId

    const location = participantId == null ? getLocationWithoutQuery() : `${assignmentUri}/participants/${participantId}/deliveries`

    const promise = fetchXhr(`${location}?page=${page}`)
        .then(res => {
            if (res.status != 200) return alertMsg('Error while getting deliveries')
            return res.text()
        })
        .then(fragment => {
            content.innerHTML = fragment ? fragment : ''
            setupCreationForm()
            $('.editDeliveryButton').on('click', (event) => {
                setupEditForm(event.target.dataset.deliveryNumber, event.target.dataset.tag, event.target.dataset.dueDate)
                $('#editDeliveryForm').modal('show')
            })

            updatePaginationFn('prevPageDeliveries', 'nextPageDeliveries', 
                nextPage => getDeliveries(content, assignmentUri, nextPage, updatePaginationFn)
            )

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

            $('.submitDeliveryButton').on('click', (event) => {
                submitDelivery(event.target.dataset.participantId, event.target.dataset.deliveryNumber, assignmentUri)
            })
            $('.deleteDeliverySubmissionButton').on('click', (event) => {
                deleteParticipantDelivery(event.target.dataset.participantId, event.target.dataset.deliveryNumber, assignmentUri)
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

function submitDelivery(participantId, deliveryNumber, assignmentUri) {
    const loadingOverlay = $(`#delivery${deliveryNumber}LoadingOverlay`)[0]
    workWithOverlay(loadingOverlay, fetch(`${assignmentUri}/participants/${participantId}/deliveries/${deliveryNumber}`, { method: 'PUT' })
        .then(res => res.json())
        .then(res => {
            if (!res.wasSubmitted) return alertMsg(res.message)
            location.reload()
        })
        .catch(err => {
            alertMsg('Failed to submit delivery')
        }))
}

function setupEditForm(deliveryNumber, oldTag, oldDueDate) {
    const tag = $('#editDeliveryTag')
    const dueDate = $('#editDeliveryDueDate')
    const dueTime = $('#editDeliveryDueTime')
    const editButton = $('#editDeliveryButton')

    tag.val('')
    tag.removeClass('is-invalid')
    dueDate.val('')
    dueDate.removeClass('is-invalid')
    dueTime.val('')

    let oldDate = ''
    let oldTime = ''

    tag.attr('placeholder', oldTag)
    if (oldDueDate.length != 0) {
        const date = new Date(oldDueDate)
        oldDate = `${date.getFullYear()}-${getNumberString(date.getMonth() + 1)}-${getNumberString(date.getDate())}`
        oldTime = `${getNumberString(date.getHours())}:${getNumberString(date.getMinutes())}`
        dueDate.val(oldDate)
        dueTime.val(oldTime)
    }

    tag.on('input', (event) => {
        tag.removeClass('is-invalid')
        tag.val(tag.val().replace(' ', '-'))
    })
    tag.on('keyup', (event) => mapEnterToButton(tag[0], event, editButton[0]))

    dueDate.on('input', (event) => dueDate.removeClass('is-invalid'))
    dueDate.on('keyup', (event) => mapEnterToButton(dueDate[0], event, editButton[0]))
    dueTime.on('keyup', (event) => mapEnterToButton(dueTime[0], event, editButton[0]))

    editButton.on('click', (event) => {
        editDelivery(deliveryNumber, editButton, tag, dueDate, dueTime, oldDate, oldTime)
    })
}

function editDelivery(deliveryNumber, editButton, deliveryTag, deliveryDueDate, deliveryDueTime, oldDate, oldTime) {
    const tag = deliveryTag.val()
    const dueDate = deliveryDueDate.val()
    const dueTime = deliveryDueTime.val()

    if (tag.length == 0 && dueDate == oldDate && dueTime == oldTime) {
        $('#editDeliveryTagFeedback').html('Please change at least one field')
        deliveryTag.addClass('is-invalid')
        $('#editDeliveryDueDateFeedback').html('Please change at least one field')
        return deliveryDueDate.addClass('is-invalid')
    }
    
    if (tag.length > 64) {
        $('#editDeliveryTagFeedback').html('Tag too long')
        return deliveryTag.addClass('is-invalid')
    }
    if (tag.length != 0 && !validateTag(tag)) {
        $('#editDeliveryTagFeedback').html('Invalid tag')
        return deliveryTag.addClass('is-invalid')
    }
    if (dueTime.length > 0 && dueDate.length == 0) {
        $('#editDeliveryDueDateFeedback').html('Can\'t set a time without a date')
        return deliveryDueDate.addClass('is-invalid')
    }

    editButton.blur()
    $('#editDeliveryForm').modal('hide')

    workWithLoading(
        fetch(`${getLocation()}/${deliveryNumber}`, { 
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                tag: tag.length == 0 ? null : tag,
                dueDate: dueDate.length == 0 ? null : `${dueDate}T${dueTime.length == 0 ? '00:00' : dueTime}`
            })
        })
            .then(res => res.json())
            .then(res => {
                if (!res.wasEdited) return alertMsg(res.message)
                location.reload() // TODO: Maybe make this dynamic instead of forcing a reload
            })
            .catch(err => alertMsg('Failed to edit delivery')))
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

function deleteParticipantDelivery(participantId, deliveryNumber, assignmentUri) {
    const loadingOverlay = $(`#delivery${deliveryNumber}LoadingOverlay`)[0]
    workWithOverlay(loadingOverlay, fetch(`${assignmentUri}/participants/${participantId}/deliveries/${deliveryNumber}`, { method: 'DELETE' })
        .then(res => res.json())
        .then(res => {
            if (!res.wasDeleted) return alertMsg(res.message)
            location.reload()
        })
        .catch(err => {
            alertMsg('Failed to delete delivery submission')
        }))
}

function getNumberString(number) {
    return `${(number < 10 ? '0' : '')}${number}`
}

export {
    getDeliveries
}