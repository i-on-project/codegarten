function alertMsg(message, kind = 'danger') {
    document
        .querySelector('.messages')
        .innerHTML = 
            `<div class="shadow-sm alert alert-${kind} slide-in">
                <button type="button" id="closeAlertButton" class="close">
                    <i class="fas fa-times"></i>
                </button>
                ${message}
            </div>`
    document.querySelector('#closeAlertButton').addEventListener('click', () => dismissAlert())
}

function dismissAlert() {
    const overlay = document.querySelector('.alert')
    overlay.classList.remove('slide-in')
    overlay.classList.add('slide-out')
    setTimeout(() => overlay.style.display = 'none', 200)
}

function workWithLoading(workPromise) {
    const overlay = document.querySelector('#loadingOverlay')
    showOverlay

    workPromise
        .then(finishLoadingMsg)
        .catch(finishLoadingMsg)
}

function showOverlay(overlay) {
    overlay.classList.remove('slide-out')
    overlay.classList.add('slide-in')
    overlay.style.display = 'flex'
}

function hideOverlay(overlay) {
    overlay.classList.remove('slide-in')
    overlay.classList.add('slide-out')
    setTimeout(() => overlay.style.display = 'none', 200)
}

function finishLoadingMsg() {
    const overlay = document.querySelector('#loadingOverlay')
    hideOverlay(overlay)
}

function mapEnterToButton(elem, event, button) {
    if (event.key == 'Enter') {
        elem.blur()
        button.click()
    }
}

function sanitizeInput(input) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        '\'': '&#x27;',
        '/': '&#x2F;',
    }
    const reg = /[&<>"'/]/ig
    return input.replace(reg, (match)=>(map[match]))
}

function getLocation() {
    // Removes trailing '/' and '#'
    return document.location.href.replace(/\/$/, '').replace('#', '')
}

export {
    alertMsg,
    dismissAlert,
    workWithLoading,
    mapEnterToButton,
    sanitizeInput,
    getLocation,
    showOverlay,
    hideOverlay
}