type Participation = {
    type: 'user' | 'teacher' | 'team' | 'notMember',
    id?: number,
    name?: string,
    repoUri?: string
}

type Participant = {
    id: number,
    name: string,
    avatarUri: string
}

type Participants = {
    type: string,
    participants: Participant[],

    page: number,
    isLastPage: boolean,

    canManage: boolean
}