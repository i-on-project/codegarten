import { alertMsg, showOverlay, hideOverlay, workWithOverlay, fetchXhr, getLocationWithoutQuery } from './common.js'

function getUsers(content, page, updatePaginationFn) {
    content.innerHTML = ''
    const promise = fetchXhr(`${getLocationWithoutQuery()}?page=${page}`)
        .then(res => {
            if (res.status != 200) return alertMsg('Error while getting users')
            return res.text()
        })
        .then(fragment => {
            content.innerHTML = fragment ? fragment : ''
            updatePaginationFn(nextPage => getUsers(content, nextPage, updatePaginationFn))
            $('.removeUserButton').on('click', (event) => {
                const overlay = $(`#removeUser${event.target.dataset.userId}Overlay`)
                showOverlay(overlay[0])
            })
            $('.noRemoveUser').on('click', (event) => {
                hideOverlay(event.target.parentElement.parentElement)
            })
            $('.yesRemoveUser').on('click', (event) => {
                hideOverlay(event.target.parentElement.parentElement)
                removeUser(event.target.dataset.userId)
            })
            $('.userRoleDropdown a').on('click', (event => {
                if (event.target.classList.contains('active')) return
                updateUserRole(event.target)
            }))
        })
        .catch((err) => alertMsg('Error while getting users'))

    return promise
}

function removeUser(userId) {
    const loadingOverlay = $(`#user${userId}LoadingOverlay`)[0]
    
    workWithOverlay(loadingOverlay, fetch(`${getLocationWithoutQuery()}/${userId}`, { method: 'DELETE' })
        .then(res => res.json())
        .then(res => {
            if (res.wasRemoved) {
                alertMsg(res.message, 'success')
                loadingOverlay.parentElement.remove()
            } else alertMsg(res.message)
        })
        .catch(err => {
            alertMsg('Failed to remove user')
        }))
    
}

function updateUserRole(elem) {
    const userId = elem.parentElement.dataset.userId
    const role = elem.dataset.role
    const dropdownButton = $(`#user${userId}RoleDropdownButton`)[0]

    const overlay = $(`#user${userId}LoadingOverlay`)[0]
    workWithOverlay(overlay, fetch(`${getLocationWithoutQuery()}/${userId}`, {
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
        }))
}

export {
    getUsers,
    removeUser,
    updateUserRole
}