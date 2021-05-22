type Participation = {
    type: string,
    id: number,
    name: string
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