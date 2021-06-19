type Repository = {
    id: number,
    name: string,
    description?: string,
    isPrivate: boolean,

    organization: string,
    repoUri: string,
}

type Repositories = {
    repos: Repository[],

    organization: string,
    organizationUri: string,
}