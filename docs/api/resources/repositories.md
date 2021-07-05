# Repositories

A repository is a [GitHub Repository](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/creating-a-repository-on-github/about-repositories).

## Properties
* `id` - Unique and stable global identifier of a GitHub Repository
    * mandatory
    * non editable
    * type: **number**
    * example: `1`
* `name` - Unique and short name that defines the GitHub Repository
    * mandatory
    * non editable
    * type: **text**
    * example: `my-repository`
* `description` - Short description that characterizes the GitHub Repository
    * non mandatory
    * non editable
    * type: **text**
    * example: `This is my repository`
* `organization` - Name of the organization where the repository is contained
    * non mandatory
    * non editable
    * type: **text**
    * example: `My Organization`

## Link Relations
* self (GitHub Repository URL)
* [organization](organizations.md/#get-organization)
* organizationGitHub (Organization GitHub URL)
* avatar (Organization GitHub Avatar URL)

## Actions
* [Search Template Repositories](#search-template-repositories)

------
### Search Template Repositories
Search for template repositories in a given organization.

```http
GET /api/orgs/{orgId}/templaterepos
```

#### Parameters
| Name        | Type        | In         | Description                                                                           |
| ----------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept      | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| orgId       | integer     | path       | The GitHub Organization's unique identifier                                           |
| q           | string      | query      | The name to search for                                                                |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["repository", "collection"],
  "properties": {
    "organization": "i-on-project-codegarten-tests",
    "collectionSize": 2
  },
  "entities": [
    {
      "class": ["repository"],
      "rel": ["item"],
      "properties": {
        "id": 363878129,
        "name": "template-repo",
        "description": null,
        "isPrivate": false,
        "organization": "i-on-project-codegarten-tests"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "https://github.com/i-on-project-codegarten-tests/template-repo"
        },
        {
          "rel": ["organization"],
          "href": "/api/orgs/80703382"
        },
        {
          "rel": ["organizationGitHub"],
          "href": "https://github.com/i-on-project-codegarten-tests"
        },
        {
          "rel": ["avatar"],
          "href": "https://avatars.githubusercontent.com/u/80703382?v=4"
        }
      ]
    },
    {
      "class": ["repository"],
      "rel": ["item"],
      "properties": {
        "id": 348030512,
        "name": "template-repository",
        "description": null,
        "isPrivate": true,
        "organization": "i-on-project-codegarten-tests"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "https://github.com/i-on-project-codegarten-tests/template-repository"
        },
        {
          "rel": ["organization"],
          "href": "/api/orgs/80703382"
        },
        {
          "rel": ["organizationGitHub"],
          "href": "https://github.com/i-on-project-codegarten-tests"
        },
        {
          "rel": ["avatar"],
          "href": "https://avatars.githubusercontent.com/u/80703382?v=4"
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "/api/orgs/80703382/templaterepos"
    },
    {
      "rel": ["organization"],
      "href": "/api/orgs/80703382"
    },
    {
      "rel": ["organizationGitHub"],
      "href": "https://github.com/i-on-project-codegarten-tests"
    },
    {
      "rel": ["avatar"],
      "href": "https://avatars.githubusercontent.com/u/80703382?v=4"
    }
  ]
}
```

#### Bad Request
```
Status: 400 Bad Request
```

#### Requires Authentication
```
Status: 401 Unauthorized
```

#### Forbidden
```
Status: 403 Forbidden
```

#### Resource Not Found
```
Status: 404 Not Found
```