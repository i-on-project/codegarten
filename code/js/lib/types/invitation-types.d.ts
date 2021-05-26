type ClassroomInvitation = {
    id: number,
    number: number,
    name: string,
    description?: string,
    
    orgId: number,
    organization: string,
    orgUri: string
}

type AssignmentInvitation = {
    id: number,
    number: number,
    name: string,
    description?: string,
    type: 'individual' | 'group'

    classroomId: number,
    classroomNumber: number,
    classroom: string,

    orgId: number,
    organization: string,
    orgUri: string,
}

type JoinInvitation = {
    status: number,
    isOrgInvitePending: boolean,
    orgUri: string,
    repoUri?: string
}

type Invitation = {
    type: 'classroomInvitation' | 'assignmentInvitation'
    invitation: ClassroomInvitation | AssignmentInvitation
}