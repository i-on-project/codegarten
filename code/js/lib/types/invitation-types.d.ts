type ClassroomInvitation = {
    id: number,
    name: string,
    description?: string,
    
    organization: string,
    orgUri: string
}

type AssignmentInvitation = {
    id: number,
    name: string,
    description?: string,
    type: 'individual' | 'group'

    classroom: string,

    organization: string,
    orgUri: string,
}

type Invitation = {
    type: 'classroomInvitation' | 'assignmentInvitation'
    invitation: ClassroomInvitation | AssignmentInvitation
}