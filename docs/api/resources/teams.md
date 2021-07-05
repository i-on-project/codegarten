# Teams

A team is a group of [users](users.md) in a [classroom](classrooms.md).

## Properties
* `id` - Unique and stable global identifier of a team
    * mandatory
    * non editable, auto-assigned
    * type: **number**
    * example: `1`
* `number` - Stable identifier of a team relative to a classroom
    * mandatory
    * non editable, auto-assigned
    * type: **number**
    * example: `1`
* `name` - Short name that defines an team
    * mandatory
    * editable
    * type: **text**
    * example: `"My Team"`
* `gitHubName` - Name of the associated GitHub Team
    * mandatory
    * non editable
    * type: **text**
    * example: `"CG1-My Team"`
* `isMember` - Indicates wether the authenticated user is a member of the team
    * mandatory
    * non editable
    * type: **boolean**
    * example: `true`
* `classroom` - Name of the classroom where the issue is contained
    * mandatory
    * non editable
    * type: **text**
    * example: `"classroom 1"`
* `organization` - Name of the organization where the issue is contained
    * mandatory
    * non editable
    * type: **text**
    * example: `"organization 1"`

## Link Relations
* [self](#get-team)
* github (Team in GitHub Organization)
* avatar (Team's Avatar in GitHub)
* [users](users.md#get-users-in-users)
* [classroom](classrooms.md#get-classroom)
* [organization](organizations.md#get-organization)
* organizationGitHub (GitHub Organization URL)

## Actions
* [List Teams](#list-teams)
* [Get Team](#get-team)
* [Create Team](#create-team)
* [Edit Team](#edit-team)
* [Delete Team](#delete-team)

------
### List Teams
List all the teams that are present in a classroom. If the authenticated user is a Student in the classroom, only the teams in which the user is in are listed.

```http
GET /api/orgs/{orgId}/classrooms/{classroomNumber}/teams
```

#### Parameters
| Name                  | Type        | In         | Description                                                                           |
| --------------------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| orgId                 | integer     | path       | The organization's unique identifier                                                  |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                               |
| page                  | integer     | query      | Specifies the current page of the list                                                |
| limit                 | integer     | query      | Specifies the number of results per page (max. 100)                                   |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["team", "collection"],
  "properties": {
    "classroom": "Classroom 1",
    "organization": "i-on-project-codegarten-tests",
    "collectionSize": 2,
    "pageIndex": 0,
    "pageSize": 2
  },
  "entities": [
    {
      "class": ["team"],
      "rel": ["item"],
      "properties": {
        "id": 1,
        "number": 1,
        "name": "Team 1",
        "classroom": "Classroom 1",
        "organization": "i-on-project-codegarten-tests"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "/api/orgs/80703382/classrooms/1/teams/1"
        },
        {
          "rel": ["avatar"],
          "href": "https://avatars.githubusercontent.com/t/4794888"
        },
        {
          "rel": ["users"],
          "href": "/api/orgs/80703382/classrooms/1/teams/1/users"
        },
        {
          "rel": ["classroom"],
          "href": "/api/orgs/80703382/classrooms/1"
        },
        {
          "rel": ["organization"],
          "href": "/api/orgs/80703382"
        },
        {
          "rel": ["organizationGitHub"],
          "href": "https://github.com/i-on-project-codegarten-tests"
        }
      ]
    },
    {
      "class": ["team"],
      "rel": ["item"],
      "properties": {
        "id": 2,
        "number": 2,
        "name": "Team 2",
        "classroom": "Classroom 1",
        "organization": "i-on-project-codegarten-tests"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "/api/orgs/80703382/classrooms/1/teams/2"
        },
        {
          "rel": ["avatar"],
          "href": "https://avatars.githubusercontent.com/t/4794889"
        },
        {
          "rel": ["users"],
          "href": "/api/orgs/80703382/classrooms/1/teams/2/users"
        },
        {
          "rel": ["classroom"],
          "href": "/api/orgs/80703382/classrooms/1"
        },
        {
          "rel": ["organization"],
          "href": "/api/orgs/80703382"
        },
        {
          "rel": ["organizationGitHub"],
          "href": "https://github.com/i-on-project-codegarten-tests"
        }
      ]
    }
  ],
  "actions": [
    {
      "name": "create-team",
      "title": "Create Team",
      "method": "POST",
      "href": "/api/orgs/80703382/classrooms/1/teams",
      "type": "application/json",
      "fields": [
        {
          "name": "orgId",
          "type": "hidden",
          "value": 80703382
        },
        {
          "name": "classroomNumber",
          "type": "hidden",
          "value": 1
        },
        {
          "name": "name",
          "type": "text"
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "/api/orgs/80703382/classrooms?page=0&limit=10"
    },
    {
      "rel": ["page"],
      "hrefTemplate": "/api/orgs/80703382/classrooms{?page,limit}"
    },
    {
      "rel": ["classroom"],
      "href": "/api/orgs/80703382/classrooms/1"
    },
    {
      "rel": ["organization"],
      "href": "/api/orgs/80703382"
    },
    {
      "rel": ["organizationGitHub"],
      "href": "https://github.com/i-on-project-codegarten-tests"
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

------
### Get Team
Get a team from the classroom.

```http
GET /api/orgs/{orgId}/classrooms/{classroomNumber}/teams/{teamNumber}
```

#### Parameters
| Name                  | Type        | In         | Description                                                                           |
| --------------------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| orgId                 | integer     | path       | The organization's unique identifier                                                  |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                               |
| teamNumber            | integer     | path       | The team's identifier relative to the classroom                                       |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["team"],
  "properties": {
    "id": 1,
    "number": 1,
    "name": "Team 1",
    "gitHubName": "CG1-Team 1",
    "isMember": false,
    "classroom": "Classroom 1",
    "organization": "i-on-project-codegarten-tests"
  },
  "actions": [
    {
      "name": "edit-team",
      "title": "Edit Team",
      "method": "PUT",
      "href": "/api/orgs/80703382/classrooms/1/teams/1",
      "type": "application/json",
      "fields": [
        {
          "name": "orgId",
          "type": "hidden",
          "value": 80703382
        },
        {
          "name": "classroomNumber",
          "type": "hidden",
          "value": 1
        },
        {
          "name": "teamNumber",
          "type": "hidden",
          "value": 1
        },
        {
          "name": "name",
          "type": "text"
        }
      ]
    },
    {
      "name": "delete-team",
      "title": "Delete Team",
      "method": "DELETE",
      "href": "/api/orgs/80703382/classrooms/1/teams/1",
      "type": "application/json",
      "fields": [
        {
          "name": "orgId",
          "type": "hidden",
          "value": 80703382
        },
        {
          "name": "classroomNumber",
          "type": "hidden",
          "value": 1
        },
        {
          "name": "teamNumber",
          "type": "hidden",
          "value": 1
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "/api/orgs/80703382/classrooms/1/teams/1"
    },
    {
      "rel": ["github"],
      "href": "https://github.com/orgs/i-on-project-codegarten-tests/teams/cg1-team-1"
    },
    {
      "rel": ["avatar"],
      "href": "https://avatars.githubusercontent.com/t/4794888"
    },
    {
      "rel": ["users"],
      "href": "/api/orgs/80703382/classrooms/1/teams/1/users"
    },
    {
      "rel": ["teams"],
      "href": "/api/orgs/80703382/classrooms/1/teams"
    },
    {
      "rel": ["classroom"],
      "href": "/api/orgs/80703382/classrooms/1"
    },
    {
      "rel": ["organization"],
      "href": "/api/orgs/80703382"
    },
    {
      "rel": ["organizationGitHub"],
      "href": "https://github.com/i-on-project-codegarten-tests"
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

------
### Create Team
Create a team in the classroom. A GitHub Team will also be created in the GitHub Organization that'll be associated with this team. The authenticated user must be a Teacher in the classroom in order to perform this action.

```http
POST /api/orgs/{orgId}/classrooms/{classroomNumber}/teams
```

#### Parameters
| Name                  | Type        | In         | Description                                                                           |
| --------------------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| content-type          | string      | header     | Should be set to `application/json`                                                   |
| orgId                 | integer     | path       | The organization's unique identifier                                                  |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                               |
| name                  | string      | body       | **Required**. Short name that defines the team                                        |

#### Default Response
```
Status: 201 Created
Location: /api/orgs/{orgId}/classrooms/{classroomNumber}/teams/{teamNumber}
```
```json
{
  "class": ["team"],
  "properties": {
    "id": 1,
    "number": 1,
    "name": "Team 1",
    "gitHubName": "CG1-Team 1",
    "isMember": false,
    "classroom": "Classroom 1",
    "organization": "i-on-project-codegarten-tests"
  },
  "actions": [
    {
      "name": "edit-team",
      "title": "Edit Team",
      "method": "PUT",
      "href": "/api/orgs/80703382/classrooms/1/teams/1",
      "type": "application/json",
      "fields": [
        {
          "name": "orgId",
          "type": "hidden",
          "value": 80703382
        },
        {
          "name": "classroomNumber",
          "type": "hidden",
          "value": 1
        },
        {
          "name": "teamNumber",
          "type": "hidden",
          "value": 1
        },
        {
          "name": "name",
          "type": "text"
        }
      ]
    },
    {
      "name": "delete-team",
      "title": "Delete Team",
      "method": "DELETE",
      "href": "/api/orgs/80703382/classrooms/1/teams/1",
      "type": "application/json",
      "fields": [
        {
          "name": "orgId",
          "type": "hidden",
          "value": 80703382
        },
        {
          "name": "classroomNumber",
          "type": "hidden",
          "value": 1
        },
        {
          "name": "teamNumber",
          "type": "hidden",
          "value": 1
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "/api/orgs/80703382/classrooms/1/teams/1"
    },
    {
      "rel": ["github"],
      "href": "https://github.com/orgs/i-on-project-codegarten-tests/teams/cg1-team-1"
    },
    {
      "rel": ["avatar"],
      "href": "https://avatars.githubusercontent.com/t/4794888"
    },
    {
      "rel": ["users"],
      "href": "/api/orgs/80703382/classrooms/1/teams/1/users"
    },
    {
      "rel": ["teams"],
      "href": "/api/orgs/80703382/classrooms/1/teams"
    },
    {
      "rel": ["classroom"],
      "href": "/api/orgs/80703382/classrooms/1"
    },
    {
      "rel": ["organization"],
      "href": "/api/orgs/80703382"
    },
    {
      "rel": ["organizationGitHub"],
      "href": "https://github.com/i-on-project-codegarten-tests"
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

------
### Edit Team
Edit an already existing team. The authenticated user must be a Teacher in the classroom in order to perform this action.

```http
PUT /api/orgs/{orgId}/classrooms/{classroomNumber}/teams/{teamNumber}
```

#### Parameters
| Name                  | Type        | In         | Description                                                                           |
| --------------------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| content-type          | string      | header     | Should be set to `application/json`                                                   |
| orgId                 | integer     | path       | The organization's unique identifier                                                  |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                               |
| teamNumber            | integer     | path       | The team's identifier relative to the classroom                                       |
| name                  | string      | body       | **Required**. Short name that defines the team                                        |

#### Default Response
```
Status: 200 OK
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

------
### Delete Team
Delete an existing team from the classroom. The authenticated user must be a Teacher in the classroom in order to perform this action.

```http
DELETE /api/orgs/{orgId}/classrooms/{classroomNumber}/teams/{teamNumber}
```

#### Parameters
| Name                  | Type        | In         | Description                                                                           |
| --------------------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| orgId                 | integer     | path       | The organization's unique identifier                                                  |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                               |
| teamNumber            | integer     | path       | The team's identifier relative to the classroom                                       |

#### Default Response
```
Status: 200 OK
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