# Users

A user is a person that is allowed to use to API.

## Properties
* `id` - Unique and stable global identifier of a user
    * mandatory
    * non editable, auto-assigned
    * type: **number**
    * example: `1`
* `name` - Unique and short name that defines a user
    * mandatory
    * non editable
    * type: **text**
    * example: `"user1"`
* `gitHubName` - Name of the user's GitHub account
    * mandatory
    * non editable
    * type: **text**
    * example: `"user1"`
* `role` - Role of the user in a classroom
    * mandatory
    * editable
    * type: **text** (Can be one of the following: `["teacher", "student"]`)
    * example: `"student"`

## Link Relations
* [self](#get-user)
* github (User's GitHub Account)
* avatar (User's Avatar in GitHub)
* [team](teams.md#get-team)
* [classroom](classrooms.md#get-classroom)
* [organization](organizations.md#get-organization)

## Actions
* [List Classroom Users](#list-classroom-users)
* [List Team Users](#list-team-users)
* [Get User](#get-user)
* [Get Authenticated User](#get-authenticated-user)
* [Edit User](#edit-user)
* [Delete User](#delete-user)
* [Edit Classroom User Membership](#edit-classroom-user-membership)
* [Delete Classroom User](#delete-classroom-user)
* [Add Team User](#add-team-user)
* [Delete Team User](#delete-team-user)


------
### List Classroom Users
List all users present in the classroom.

```http
GET /api/orgs/{orgId}/classrooms/{classroomNumber}/users
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
  "class": ["user", "collection"],
  "properties": {
    "collectionSize": 2,
    "pageIndex": 0,
    "pageSize": 2
  },
  "entities": [
    {
      "class": ["user"],
      "rel": ["item"],
      "properties": {
        "id": 1,
        "name": "codegartenStudent1",
        "gitHubId": 76118444,
        "role": "student"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "/api/users/1"
        },
        {
          "rel": ["avatar"],
          "href": "https://avatars.githubusercontent.com/u/76118444"
        },
        {
          "rel": ["classroom"],
          "href": "/api/orgs/80703382/classrooms/1"
        },
        {
          "rel": ["organization"],
          "href": "/api/orgs/80703382"
        }
      ]
    },
    {
      "class": ["user"],
      "rel": ["item"],
      "properties": {
        "id": 2,
        "name": "codegartenTeacher",
        "gitHubId": 83508555,
        "role": "teacher"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "/api/users/2"
        },
        {
          "rel": ["avatar"],
          "href": "https://avatars.githubusercontent.com/u/83508555"
        },
        {
          "rel": ["classroom"],
          "href": "/api/orgs/80703382/classrooms/1"
        },
        {
          "rel": ["organization"],
          "href": "/api/orgs/80703382"
        }
      ]
    }
  ],
  "actions": [
    {
      "name": "add-user-to-classroom",
      "title": "Add User To Classroom",
      "method": "PUT",
      "hrefTemplate": "/api/orgs/80703382/classrooms/1/users/{userId}",
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
          "name": "userId",
          "type": "number"
        },
        {
          "name": "role",
          "type": "text",
          "value": {
            "oneOf": [
              "student",
              "teacher"
            ]
          }
        }
      ]
    },
    {
      "name": "remove-user-from-classroom",
      "title": "Remove User From Classroom",
      "method": "DELETE",
      "hrefTemplate": "/api/orgs/80703382/classrooms/1/users/{userId}",
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
          "name": "userId",
          "type": "number"
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "/api/orgs/80703382/classrooms/1/users?page=0&limit=10"
    },
    {
      "rel": ["page"],
      "hrefTemplate": "/api/orgs/80703382/classrooms/1/users{?page,limit}"
    },
    {
      "rel": ["classroom"],
      "href": "/api/orgs/80703382/classrooms/1"
    },
    {
      "rel": ["organization"],
      "href": "/api/orgs/80703382"
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
### List Team Users
List all users present in the team.

```http
GET /api/orgs/{orgId}/classrooms/{classroomNumber}/teams/{teamNumber}/users
```

#### Parameters
| Name                  | Type        | In         | Description                                                                           |
| --------------------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| orgId                 | integer     | path       | The organization's unique identifier                                                  |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                               |
| teamNumber            | integer     | path       | The team's identifier relative to the classroom                                       |
| page                  | integer     | query      | Specifies the current page of the list                                                |
| limit                 | integer     | query      | Specifies the number of results per page (max. 100)                                   |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["user", "collection"],
  "properties": {
    "collectionSize": 1,
    "pageIndex": 0,
    "pageSize": 1
  },
  "entities": [
    {
      "class": ["user"],
      "rel": ["item"],
      "properties": {
        "id": 1,
        "name": "codegartenStudent1"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "/api/users/1"
        },
        {
          "rel": ["avatar"],
          "href": "https://avatars.githubusercontent.com/u/76118444"
        },
        {
          "rel": ["team"],
          "href": "/api/orgs/80703382/classrooms/1/teams/1"
        },
        {
          "rel": ["classroom"],
          "href": "/api/orgs/80703382/classrooms/1"
        },
        {
          "rel": ["organization"],
          "href": "/api/orgs/80703382"
        }
      ]
    }
  ],
  "actions": [
    {
      "name": "add-user-to-team",
      "title": "Add User To Team",
      "method": "PUT",
      "hrefTemplate": "/api/orgs/80703382/classrooms/1/teams/1/users/{userId}",
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
          "name": "userId",
          "type": "number"
        }
      ]
    },
    {
      "name": "remove-user-from-team",
      "title": "Remove User From Team",
      "method": "DELETE",
      "hrefTemplate": "/api/orgs/80703382/classrooms/1/teams/1/users/{userId}",
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
          "name": "userId",
          "type": "number"
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "/api/orgs/80703382/classrooms/1/teams/1/users?page=0&limit=10"
    },
    {
      "rel": ["page"],
      "hrefTemplate": "/api/orgs/80703382/classrooms/1/teams/1/users{?page,limit}"
    },
    {
      "rel": ["team"],
      "href": "/api/orgs/80703382/classrooms/1/teams/1"
    },
    {
      "rel": ["classroom"],
      "href": "/api/orgs/80703382/classrooms/1"
    },
    {
      "rel": ["organization"],
      "href": "/api/orgs/80703382"
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
### Get User
Get a user.

```http
GET /api/users/{userId}
```

#### Parameters
| Name                  | Type        | In         | Description                                                                           |
| --------------------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| userId                | integer     | path       | The user's unique identifier                                                          |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["user"],
  "properties": {
    "id": 1,
    "name": "codegartenStudent1",
    "gitHubName": "octocat"
  },
  "links": [
    {
      "rel": ["self"],
      "href": "/api/users/1"
    },
    {
      "rel": ["github"],
      "href": "https://github.com/octocat"
    },
    {
      "rel": ["avatar"],
      "href": "https://avatars.githubusercontent.com/u/76118444?v=4"
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
### Get Authenticated User
Get the user that's currently authenticated.

```http
GET /api/user
```

#### Parameters
| Name                  | Type        | In         | Description                                                                           |
| --------------------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["user"],
  "properties": {
    "id": 1,
    "name": "codegartenStudent1",
    "gitHubName": "octocat"
  },
  "links": [
    {
      "rel": ["self"],
      "href": "/api/users/1"
    },
    {
      "rel": ["github"],
      "href": "https://github.com/octocat"
    },
    {
      "rel": ["avatar"],
      "href": "https://avatars.githubusercontent.com/u/76118444?v=4"
    }
  ]
}
```

#### Requires Authentication
```
Status: 401 Unauthorized
```

#### Forbidden
```
Status: 403 Forbidden
```


------
### Edit User
Edit the user that's currently authenticated.

```http
PUT /api/user
```

#### Parameters
| Name                  | Type        | In         | Description                                                                           |
| --------------------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| content-type          | string      | header     | Should be set to `application/json`                                                   |
| name                  | string      | body       | **Required**. Short name that defines the user                                        |

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


------
### Delete User
Delete the user that's currently authenticated.

```http
DELETE /api/user
```

#### Parameters
| Name                  | Type        | In         | Description                                                                           |
| --------------------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |

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


------
### Edit Classroom User Membership
Edit the membership of the user in the classroom. If the user is not in the classroom, it will be added with the specified role. The authenticated user must be a Teacher in the classroom in order to perform this action.

```http
PUT /api/orgs/{orgId}/classrooms/{classroomNumber}/users/{userId}
```

#### Parameters
| Name                  | Type        | In         | Description                                                                                            |
| --------------------- | ----------- | ---------- | ------------------------------------------------------------------------------------------------------ |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`                             |
| content-type          | string      | header     | Should be set to `application/json`                                                                    |
| orgId                 | integer     | path       | The organization's unique identifier                                                                   |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                                                |
| userId                | integer     | path       | The user's unique identifier                                                                           |
| role                  | string      | body       | **Required**. Defines the role in the classroom. Can be one of the following: `["teacher", "student"]` |

#### Default Response
```
Status: 201 Created
Location: /api/orgs/{orgId}/classrooms/{classroomNumber}
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
### Delete Classroom User
Removes the user from the classroom. The authenticated user must be a Teacher in the classroom in order to perform this action.

```http
DELETE /api/orgs/{orgId}/classrooms/{classroomNumber}/users/{userId}
```

#### Parameters
| Name                  | Type        | In         | Description                                                                                            |
| --------------------- | ----------- | ---------- | ------------------------------------------------------------------------------------------------------ |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`                             |
| orgId                 | integer     | path       | The organization's unique identifier                                                                   |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                                                |
| userId                | integer     | path       | The user's unique identifier                                                                           |

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
### Add Team User
Adds a user to a team. The authenticated user must be a Teacher in the classroom in order to perform this action.

```http
PUT /api/orgs/{orgId}/classrooms/{classroomNumber}/teams/{teamNumber}/users/{userId}
```

#### Parameters
| Name                  | Type        | In         | Description                                                                                            |
| --------------------- | ----------- | ---------- | ------------------------------------------------------------------------------------------------------ |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`                             |
| orgId                 | integer     | path       | The organization's unique identifier                                                                   |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                                                |
| teamNumber            | integer     | path       | The team's identifier relative to the classroom                                                        |
| userId                | integer     | path       | The user's unique identifier                                                                           |

#### Default Response
```
Status: 201 Created
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
### Delete Team User
Removes a user from a team. The authenticated user must be a Teacher in the classroom or the user to be removed in order to perform this action.

```http
DELETE /api/orgs/{orgId}/classrooms/{classroomNumber}/teams/{teamNumber}/users/{userId}
```

#### Parameters
| Name                  | Type        | In         | Description                                                                                            |
| --------------------- | ----------- | ---------- | ------------------------------------------------------------------------------------------------------ |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`                             |
| orgId                 | integer     | path       | The organization's unique identifier                                                                   |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                                                |
| teamNumber            | integer     | path       | The team's identifier relative to the classroom                                                        |
| userId                | integer     | path       | The user's unique identifier                                                                           |

#### Default Response
```
Status: 201 Created
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
