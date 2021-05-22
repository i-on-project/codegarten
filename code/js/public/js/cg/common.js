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
    showOverlay(overlay)

    workPromise
        .finally(finishLoadingMsg)
}

function workWithOverlay(overlay, workPromise) {
    showOverlay(overlay)

    workPromise
        .finally(() => hideOverlay(overlay))

    return workPromise
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

function getLocation() {
    // Removes trailing '/' and '#'
    return document.location.href.replace(/\/$/, '').replace('#', '')
}

function getLocationWithoutQuery() {
    return getLocation().split('?')[0]
}

function fetchXhr(uri) {
    return fetch(uri, {
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
}

function generateSlug(str) {
    if (!str) return ''
    
    let slug = slugify(str)
    if (!slug) {
        slug = '-'
    }
    return slug
}

function slugify(str) {
    return str.toString().toLowerCase().normalize('NFD')
        .replace(/\s+/g, '-')          // Replace spaces with -
        .replace(/[^\w-]+/g, '')       // Remove all non-word chars
}

export {
    alertMsg,
    dismissAlert,
    workWithLoading,
    workWithOverlay,
    mapEnterToButton,
    getLocation,
    getLocationWithoutQuery,
    showOverlay,
    hideOverlay,
    fetchXhr,
    generateSlug
}