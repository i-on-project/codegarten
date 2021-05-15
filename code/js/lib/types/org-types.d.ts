type Organization = {
    id: number,
    name: string,
    description?: string,
    gitHubUri: string,
    avatarUri: string
}

type Organizations = {
    orgs: Organization[],
    page: number,
    isLastPage: boolean
}