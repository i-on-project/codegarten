type SirenLink = {
    rel: string[],
    href: string
}

type SirenAction = {
    name: string,
    title: string,
    method: string,
    href: string,
    type: string,
    fields: SirenActionField[]
}

type SirenActionField = {
    name: string,
    type: string,
    value?: any,
}