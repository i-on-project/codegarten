'use strict'

import { orgRoutes, getJsonRequestOptions, SirenLink, getSirenLink } from '../api-routes'
import fetch from 'node-fetch'

const ORG_LIST_LIMIT = 10

function getUserOrgs(page: number, accessToken: string): Promise<Organizations> {
    return fetch(orgRoutes.getOrgsUri(page), getJsonRequestOptions('GET', accessToken))
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

export {
    getUserOrgs,
}