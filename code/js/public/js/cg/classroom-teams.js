import { alertMsg, getLocation } from './common.js'

function getTeams(content, page, updatePaginationFn) {
    content.innerHTML = ''
    const promise = fetch(`${getLocation()}/teams?page=${page}`)
        .then(res => {
            if (res.status != 200) return alertMsg('Error while getting teams')
            return res.text()
        })
        .then(fragment => {
            content.innerHTML = fragment ? fragment : ''
            updatePaginationFn(nextPage => getTeams(content, nextPage, updatePaginationFn))
        })
        .catch((err) => alertMsg('Error while getting teams'))

    return promise
}

export {
    getTeams
}