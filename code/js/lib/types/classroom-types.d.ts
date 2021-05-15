type Classroom = {
    id: number,
    inviteCode?: string,
    number: number,
    name: string,
    description?: string,
    organization: string,
    organizationUri: string,

    canManage?: boolean,
}

type Classrooms = {
    classrooms: Classroom[],
    page: number,
    isLastPage: boolean,

    organization: string,
    organizationUri: string,
    canCreate: boolean,
}