import { mapEnterToButton, alertMsg, workWithLoading } from './common.js'

export function setup() {
    const content = document.querySelector('#classroomContent')
    if (content) {
        fillContent(content)
        //TODO: Handlers for next page
    }
}
function fillContent(content) {
    const loc = document.location.href
    const assignments = 
    getContentWithLoading(
        fetch(`${loc}/assignments`)
            .then(res => res.text())
            .then(fragment => content.innerHTML = fragment)
    )
}

function getContentWithLoading(workPromise) {
    const overlay = document.querySelector('#loadingClassroomContentOverlay')
    overlay.classList.remove('slide-out')
    overlay.classList.add('slide-in')
    overlay.style.display = 'flex'

    workPromise
        .then(finishLoadingMsg)
        .catch(finishLoadingMsg)
}

function finishLoadingMsg() {
    const overlay = document.querySelector('#loadingClassroomContentOverlay')
    overlay.classList.remove('slide-in')
    overlay.classList.add('slide-out')
    setTimeout(() => overlay.style.display = 'none', 200)
}

