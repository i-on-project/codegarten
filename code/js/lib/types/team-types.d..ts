type Team = {
    id: number,
    number: number,
    name: string,
    classroom: string,
    organization: string,
    organizationUri: string,
    avatarUri: string,
    gitHubUri?: string,

    canManage?: boolean,
}

type Teams = {
    teams: Team[],
    page: number,
    isLastPage: boolean,

    classroom: string,
    organization: string,
    organizationUri: string,
    
    canManage: boolean,
}