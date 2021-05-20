import { alertMsg, fetchXhr, getLocationWithoutQuery } from './common.js'

function getAssignments(content, page, updatePaginationFn) {
    content.innerHTML = ''

    const promise = fetchXhr(`${getLocationWithoutQuery()}?page=${page}`)
        .then(res => {
            if (res.status != 200) return alertMsg('Error while getting assignments')
            return res.text()
        })
        .then(fragment => {
            content.innerHTML = fragment ? fragment : ''
            updatePaginationFn(nextPage => getAssignments(content, nextPage, updatePaginationFn))
        })
        .catch((err) => alertMsg('Error while getting assignments'))
        
    return promise
}

export {
    getAssignments
}