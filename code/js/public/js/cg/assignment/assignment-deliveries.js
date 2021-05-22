import { alertMsg, getLocationWithoutQuery, hideOverlay, showOverlay, mapEnterToButton, workWithLoading, workWithOverlay, fetchXhr, generateSlug, getLocation } from '../common.js'

function getDeliveries(content, page, updatePaginationFn) {
    content.innerHTML = ''

    const promise = fetchXhr(`${getLocationWithoutQuery()}?page=${page}`)
        .then(res => {
            if (res.status != 200) return alertMsg('Error while getting deliveries')
            return res.text()
        })
        .then(fragment => {
            content.innerHTML = fragment ? fragment : ''
            setupCreationForm()
            updatePaginationFn(nextPage => getDeliveries(content, nextPage, updatePaginationFn))
        })
        .catch((err) => alertMsg('Error while getting deliveries'))
        
    return promise
}

function setupCreationForm() {
    const tag = $('#deliveryTag')
    const dueDate = $('#deliveryDuedate')
    const createButton = $('#createDeliveryButton')

    // TODO: Slugify is not entirely correct (v0.1 should be a valid tag)
    tag.on('input', (event) => {
        tag.removeClass('is-invalid')
        tag.val(generateSlug(event.target.value))
    })

    tag.on('keyup', (event) => mapEnterToButton(tag[0], event, createButton[0]))

    dueDate.on('input', (event) => dueDate.removeClass('is-invalid'))
    dueDate.on('keyup', (event) => mapEnterToButton(dueDate[0], event, createButton[0]))

    createButton.on('click', (event) => {
        createDelivery(createButton, tag, dueDate)
    })
}

function createDelivery(createButton, deliveryTag, deliveryDueDate) {
    if (deliveryTag.val().length == 0) {
        $('#deliveryTagFeedback').html('Tag can\'t be empty')
        return deliveryTag.addClass('is-invalid')
    }

    const tag = deliveryTag.val()
    const dueDate = deliveryDueDate.val()
    
    if (tag.length > 64) {
        $('#deliveryTagFeedback').html('Tag too long')
        return deliveryTag.addClass('is-invalid')
    }

    createButton.blur()
    $('#createDeliveryForm').modal('hide')

    workWithLoading(
        fetch(getLocation(), { 
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                tag: tag,
                dueDate: dueDate
            })
        })
            .then(res => res.json())
            .then(res => {
                if (!res.wasCreated) return alertMsg(res.message)
                location.reload()
            })
            .catch(err => alertMsg('Failed to create delivery')))
}

export {
    getDeliveries
}