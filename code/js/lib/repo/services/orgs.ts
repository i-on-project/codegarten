'use strict'

import { orgRoutes, getJsonRequestOptions, getSirenLink } from '../api-routes'
import fetch from 'node-fetch'

const ORG_LIST_LIMIT = 9

function getUserOrgs(page: number, accessToken: string): Promise<Organizations> {
    return fetch(orgRoutes.getPaginatedOrgsUri(page, ORG_LIST_LIMIT), getJsonRequestOptions('GET', accessToken))
        .then(res => res.json())
        .then(collection => {
            const entities = Array.from(collection.entities) as any[]
            const orgs = entities.map(entity => {
                const links: SirenLink[] = Array.from(entity.links)
                
                return {
                    id: entity.properties.id,
                    name: entity.properties.name,
                    description: entity.properties.description,
                    gitHubUri: getSirenLink(links, 'github').href,
                    avatarUri: getSirenLink(links, 'avatar').href,
                } as Organization
            })

            return {
                orgs: orgs,
                page: page,
                isLastPage: collection.properties.pageSize < ORG_LIST_LIMIT
            } as Organizations
        })
}

function searchOrgTemplateRepos(orgId: number, searchQuery: string, accessToken: string): Promise<Repositories> {
    return fetch(orgRoutes.getSearchOrgTemplateReposUri(orgId, searchQuery || ''), getJsonRequestOptions('GET', accessToken))
        .then(res => res.json())
        .then(collection => {
            const entities = Array.from(collection.entities) as any[]
            const links: SirenLink[] = Array.from(collection.links)

            const repos = entities.map(entity => {
                const repoLinks: SirenLink[] = Array.from(entity.links)
                
                return {
                    id: entity.properties.id,
                    name: entity.properties.name,
                    description: entity.properties.description,
                    isPrivate: entity.properties.isPrivate,
                    organization: entity.properties.organization,
                    repoUri: getSirenLink(repoLinks, 'self').href,
                } as Repository
            })

            return {
                repos: repos,
                organization: collection.properties.organization,
                organizationUri: getSirenLink(links, 'github').href,
            } as Repositories
        })
}

export {
    getUserOrgs,
    searchOrgTemplateRepos
}