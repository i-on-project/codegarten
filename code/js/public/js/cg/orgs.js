import { workWithLoading } from './common.js'

export function setup() {
    const installButton = document.querySelector('#installGitHubAppButton')
    if (installButton) {
        installButton.addEventListener('click', () => handlerClickInstallButton(installButton))
    }
}

function handlerClickInstallButton(button) {
    const installUri = button.getAttribute('href')

    const window = open(installUri)
    workWithLoading(
        new Promise(resolve => {
            const int = setInterval(() => {
                if (window.closed) {
                    clearInterval(int)
                    resolve()
                }
            }, 1000)
        })
    )
}