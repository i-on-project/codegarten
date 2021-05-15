import { mapEnterToButton, alertMsg, workWithLoading } from './common.js'

export function setup() {
    const userNameForm = document.querySelector('#editUsernameForm')
    if (userNameForm) {
        const newUsername = userNameForm.querySelector('#editUsername')
        const button = userNameForm.querySelector('button')
        button.addEventListener('click', () => handlerEditName(button, newUsername))
        newUsername.addEventListener('keyup', (event) => mapEnterToButton(newUsername, event, button))
    }

    const deleteAccountButton = document.querySelector('#deleteAccountButton')
    if (deleteAccountButton) {
        deleteAccountButton.addEventListener('click', () => handlerDeleteAccountShow())
        document.querySelector('#noDeleteAccount').addEventListener('click', () => handlerDeleteAccountClose())
        document.querySelector('#yesDeleteAccount').addEventListener('click', () => handlerDeleteAccount())
    }
}

function handlerEditName(elem, newName) {
    if (newName.value.length == 0 || newName.value == newName.placeholder) {
        return alertMsg('Please enter a new name')
    }
    const name = newName.value
    
    if (name.length > 64) {
        return alertMsg('Name too long.')
    }

    elem.blur()
    const loc = document.location.href
    workWithLoading(
        fetch(loc, { 
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({name: name})
        })
            .then(res => res.json())
            .then(res => {
                if (!res.wasEdited) alertMsg(res.message)
                else {
                    $('#editUsernameForm').collapse('hide')
                    alertMsg(res.message, 'success')
                    newName.value = ''
                    newName.placeholder = name
                    document.querySelector('#username').textContent = name
                    document.querySelector('#usernameNavbar').textContent = name
                }

            })
            .catch(err => alertMsg(err)))
}

function handlerDeleteAccountShow() {
    const overlay = document.querySelector('#deleteOverlay')
    overlay.classList.remove('slide-out')
    overlay.classList.add('slide-in')
    overlay.style.display = 'flex'
}

function handlerDeleteAccountClose() {
    const overlay = document.querySelector('#deleteOverlay')
    overlay.classList.remove('slide-in')
    overlay.classList.add('slide-out')
    setTimeout(() => overlay.style.display = 'none', 200)
}

function handlerDeleteAccount() {
    handlerDeleteAccountClose()
    const loc = document.location.href
    workWithLoading(
        fetch(loc, { 
            method: 'DELETE'
        })
            .then(res => res.json())
            .then(res => {
                if (!res.wasDeleted) alertMsg(res.message)
                else {
                    window.location.href = '/'
                }
            })
            .catch(err => alertMsg(err)))
}