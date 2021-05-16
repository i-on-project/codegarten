type Assignment = {
    id: number,
    inviteCode?: string,
    number: number,
    name: string,
    description?: string,
    isGroup: boolean,
    repoPrefix?: string,
    repoTemplate?: string,

    organization: string,
    organizationUri: string,

    classroom: string,

    canManage?: boolean,
}

type Assignments = {
    assignments: Assignment[],
    page: number,
    isLastPage: boolean,

    organization: string,
    organizationUri: string,

    classroom: string,

    canCreate: boolean,
}