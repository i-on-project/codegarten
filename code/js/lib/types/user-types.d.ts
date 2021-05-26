type User = {
    id: number,
    username: string,
    gitHubName?: string,
    gitHubUri?: string,
    avatarUri: string,
    isTeacher?: boolean,
    isAuthUser?: boolean
}

type Users = {
    users: User[],
    page: number,
    isLastPage: boolean,

    canManage: boolean,
}

type AuthenticatedUser = User & { accessToken: AccessToken }

