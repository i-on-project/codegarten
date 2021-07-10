# Organizations

An organization is a [GitHub Organization](https://docs.github.com/en/organizations).

## Properties
* `id` - Unique and stable global identifier of a GitHub Organization
    * mandatory
    * non editable
    * type: **number**
    * example: `1`
* `name` - Unique and short name that defines the GitHub Organization
    * mandatory
    * non editable
    * type: **text**
    * example: `My Organization`
* `description` - Short description that characterizes the GitHub Organization
    * non mandatory
    * non editable
    * type: **text**
    * example: `This is my organization`

## Link Relations
* [self](#get-organization)
* github (GitHub Organization URL)
* avatar (GitHub Organization Avatar URL)
* [classrooms](classrooms.md#list-classrooms)
* [organizations](#list-organizations)

## Actions
* [List Organizations](#list-organizations)
* [Get Organization](#get-organization)

------
### List Organizations
List all the organizations in which the user is in and the GitHub App has access to.

```http
GET /api/orgs
```

#### Parameters
| Name        | Type        | In         | Description                                                                           |
| ----------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept      | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| page        | integer     | query      | Specifies the current page of the list                                                |
| limit       | integer     | query      | Specifies the number of results per page (max. 100)                                   |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["organization", "collection"],
  "properties": {
    "pageIndex": 0,
    "pageSize": 2
  },
  "entities": [
    {
      "class": ["organization"],
      "rel": ["item"],
      "properties": {
        "id": 80703382,
        "name": "i-on-project-codegarten-tests",
        "description": null
      },
      "links": [
        {
          "rel": ["self"],
          "href": "/api/orgs/80703382"
        },
        {
          "rel": ["github"],
          "href": "https://github.com/i-on-project-codegarten-tests"
        },
        {
          "rel": ["avatar"],
          "href": "https://avatars.githubusercontent.com/u/80703382?v=4"
        },
        {
          "rel": ["classrooms"],
          "href": "/api/orgs/80703382/classrooms"
        },
        {
          "rel": ["organizations"],
          "href": "/api/orgs"
        }
      ]
    },
    {
      "class": ["organization"],
      "rel": ["item"],
      "properties": {
        "id": 2,
        "name": "codegarten-demo-organization",
        "description": "This is a demo organization that does not exist"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "/api/orgs/2"
        },
        {
          "rel": ["github"],
          "href": "https://github.com/codegarten-demo-organization-2"
        },
        {
          "rel": ["avatar"],
          "href": "https://avatars.githubusercontent.com/u/2?v=4"
        },
        {
          "rel": ["classrooms"],
          "href": "/api/orgs/2/classrooms"
        },
        {
          "rel": ["organizations"],
          "href": "/api/orgs"
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "/api/orgs?page=0&limit=10"
    },
    {
      "rel": ["page"],
      "hrefTemplate": "/api/orgs{?page,limit}"
    },
    {
      "rel": ["next"],
      "href": "/api/orgs?page=1&limit=10"
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

------
### Get Organization
Get a single organization. Both the user and the GitHub App need to be in the organization.

```http
GET /api/orgs/{orgId}
```

#### Parameters
| Name        | Type        | In         | Description                                                                           |
| ----------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept      | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| orgId       | integer     | path       | The GitHub Organization's unique identifier                                           |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["organization"],
  "properties": {
    "id": 80703382,
    "name": "i-on-project-codegarten-tests",
    "description": null
  },
  "links": [
    {
      "rel": ["self"],
      "href": "/api/orgs/80703382"
    },
    {
      "rel": ["github"],
      "href": "https://github.com/i-on-project-codegarten-tests"
    },
    {
      "rel": ["avatar"],
      "href": "https://avatars.githubusercontent.com/u/80703382?v=4"
    },
    {
      "rel": ["classrooms"],
      "href": "/api/orgs/80703382/classrooms"
    },
    {
      "rel": ["organizations"],
      "href": "/api/orgs"
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

#### Resource Not Found
```
Status: 404 Not Found
```