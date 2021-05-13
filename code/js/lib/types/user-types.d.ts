type User = {
    id: number,
    username: string,
    gitHubName?: string,
    gitHubUri?: string,
    avatarUri: string
}

type AuthenticatedUser = User & { accessToken: AccessToken }
