import { alertMsg, workWithLoading, workWithOverlay, getLocation, fetchXhr, getLocationWithoutQuery } from './common.js'

export function setup() {
    const content = $('#invitationTeamsContent')[0]
    const acceptInvite = $('#acceptInvitation')
    const rejectInvite = $('#rejectInvitation')
    if (content) {
        const overlay = $('#loadingInvitationTeamsContentOverlay')[0]
        workWithOverlay(overlay, getInvitationTeams(content, 0, updatePagination))
    } else {
        acceptInvite.removeAttr('disabled')
    }



    acceptInvite.on('click', () => joinInvitation(content != null))
    rejectInvite.on('click', () => location.href = '/')
}

function updatePagination(cb) {
    $('#prevPage').on('click', (event) => {
        const target = event.target
        if (target.classList.contains('disabled')) return
        workWithOverlay($('#loadingInvitationTeamsContentOverlay')[0], 
            cb($('#prevPage').data('page'))
        )
        
    })
    $('#nextPage').on('click', (event) => {
        const target = event.target
        if (target.classList.contains('disabled')) return
        workWithOverlay($('#loadingInvitationTeamsContentOverlay')[0], 
            cb($('#nextPage').data('page'))
        )
    })
}

function joinInvitation(isTeam) {
    const options = {
        method: 'PUT',
    }

    if (isTeam) {
        $('.teamSelector').each((index, elem) => {
            if (elem.checked) {
                options.body = JSON.stringify({
                    teamId: Number(elem.dataset.teamId)
                })
                options.headers = { 'Content-Type': 'application/json' }
                return false
            }
        })
    }

    workWithLoading(
        fetch(getLocation(), options)
            .then(res => res.json())
            .then(res => {
                if (!res.wasAccepted) alertMsg(res.message)
                else {
                    if (res.isOrgInvitePending) {
                        $('#invite').remove()

                        $('#acceptInvites').removeClass('d-none').addClass('slide-in')
                        $('#orgInvite').attr('href', res.orgUri)
                        $('#repoInvite').attr('href', res.repoUri)
                        $('#continueUri').attr('href', res.message)
                    } else {
                        window.location.href = res.message
                    }
                }
            })
            .catch(err => alertMsg('Could not accept invitation')))
}

function getInvitationTeams(content, page, updatePaginationFn) {
    content.innerHTML = ''
    const promise = fetchXhr(`${getLocationWithoutQuery()}/teams?page=${page}`)
        .then(res => {
            if (res.status != 200) return alertMsg('Error while getting teams')
            return res.text()
        })
        .then(fragment => {
            content.innerHTML = fragment ? fragment : ''
            $('.teamSelector').on('change', (event) => {
                if (event.target.checked) {
                    $('#acceptInvitation').removeAttr('disabled')
                }
            })
            updatePaginationFn(nextPage => getInvitationTeams(content, nextPage, updatePaginationFn))
        })
        .catch((err) => alertMsg('Error while getting teams'))

    return promise
}