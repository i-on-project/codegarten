import { alertMsg, getLocation, workWithLoading } from './common.js'

export function setup() {
    const acceptInvite = $('#acceptInvitation')
    const rejectInvite = $('#rejectInvitation')

    acceptInvite.on('click', () => joinInvitation())
    rejectInvite.on('click', () => location.href = '/')

}

function joinInvitation() {
    workWithLoading(
        fetch(getLocation(), { 
            method: 'PUT'
        })
            .then(res => res.json())
            .then(res => {
                if (!res.wasAccepted) alertMsg(res.message)
                else {
                    window.location.href = res.message
                }
            })
            .catch(err => alertMsg('Could not accept invitation')))
}