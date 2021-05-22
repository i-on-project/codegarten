import { mapEnterToButton, workWithLoading, alertMsg, fetchXhr, getLocation, getLocationWithoutQuery, generateSlug } from '../common.js'

function getAssignments(content, page, updatePaginationFn) {
    content.innerHTML = ''

    const promise = fetchXhr(`${getLocationWithoutQuery()}?page=${page}`)
        .then(res => {
            if (res.status != 200) return alertMsg('Error while getting assignments')
            return res.text()
        })
        .then(fragment => {
            content.innerHTML = fragment ? fragment : ''
            setupCreationForm()
            updatePaginationFn(nextPage => getAssignments(content, nextPage, updatePaginationFn))
        })
        .catch((err) => alertMsg('Error while getting assignments'))
        
    return promise
}

function setupCreationForm() {
    const name = $('#assignmentName')
    const description = $('#assignmentDescription')
    const repoPrefix = $('#repoPrefix')
    const repoTemplate = $('#repoTemplate')
    const dropdownButton = $('#assignmentTypeButton')[0]
    const createButton = $('#createAssignmentButton')

    $('.assignment-type a').on('click', (event => {
        const elem = event.target
        const option = elem.tagName == 'I' ? elem.parentElement : elem // Get the anchor element depending on the clicked element

        if (option.classList.contains('active')) return
        
        option.classList.add('active')
        if (option.dataset.type == 'individual') {
            option.nextElementSibling.classList.remove('active')
            dropdownButton.innerHTML = '<i class="fas fa-user mr-2"></i> Individual'
        } else {
            option.previousElementSibling.classList.remove('active')
            dropdownButton.innerHTML = '<i class="fas fa-users mr-2"></i> Group'
        }
    }))

    name.on('input', (event) => {
        name.removeClass('is-invalid')

        // Fill prefix with name
        repoPrefix.val(generateSlug(name.val()))
    })
    name.on('keyup', (event) => mapEnterToButton(name[0], event, createButton[0]))

    description.on('input', (event) => description.removeClass('is-invalid'))
    description.on('keyup', (event) => mapEnterToButton(description[0], event, createButton[0]))

    repoPrefix.on('input', (event) => {
        repoPrefix.removeClass('is-invalid')
        repoPrefix.val(generateSlug(event.target.value))
    })
    repoPrefix.on('keyup', (event) => mapEnterToButton(repoPrefix[0], event, createButton[0]))

    // TODO: Find better way to provide repo template
    repoTemplate.on('keyup', (event) => mapEnterToButton(repoTemplate[0], event, createButton[0]))

    createButton.on('click', (event) => {
        let type
        $('.assignment-type a').each(function() {
            if (this.classList.contains('active')) {
                type = this.dataset.type
                return false
            }
        })

        createAssignment(createButton, name, description, repoPrefix, repoTemplate, type)
    })
}

function createAssignment(createButton, assignmentName, assignmentDescription, assignmentRepoPrefix, assignmentRepoTemplate, assignmentType) {
    if (assignmentName.val().length == 0) {
        $('#assignmentNameFeedback').html('Name can\'t be empty')
        return assignmentName.addClass('is-invalid')
    }
    if (assignmentRepoPrefix.val().length == 0) {
        $('#repoPrefixFeedback').html('Prefix can\'t be empty')
        return assignmentRepoPrefix.addClass('is-invalid')
    }

    const name = assignmentName.val()
    const description = assignmentDescription.val()
    const repoPrefix = assignmentRepoPrefix.val()
    const repoTemplate = assignmentRepoTemplate.val().length == 0 ? null : assignmentRepoTemplate.val() 
    
    if (name.length > 64) {
        $('#assignmentNameFeedback').html('Name too long')
        return assignmentName.addClass('is-invalid')
    }
    if (repoPrefix.length > 64) {
        $('#repoPrefixFeedback').html('Repository prefix too long')
        return assignmentName.addClass('is-invalid')
    }
    if (description.length > 256) return assignmentDescription.addClass('is-invalid')

    createButton.blur()
    $('#createAssignmentForm').modal('hide')

    workWithLoading(
        fetch(getLocation(), { 
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                name: name,
                description: description,
                type: assignmentType,
                repoPrefix: repoPrefix,
                repoTemplate: repoTemplate
            })
        })
            .then(res => res.json())
            .then(res => {
                if (!res.wasCreated) alertMsg(res.message)
                else {
                    location.href = res.message
                }

            })
            .catch(err => alertMsg('Failed to create assignment')))
}

export {
    getAssignments
}