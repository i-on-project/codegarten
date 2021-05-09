# Classrooms

A classroom is a collections of [users](users.md), [teams](teams.md) and [assignments](assignments.md).

## Properties
* `id` - Unique and stable global identifier of a classroom
    * mandatory
    * non editable, auto-assigned
    * type: **number**
    * example: `1`
* `inviteCode` - Unique code associated with a classroom
    * mandatory
    * non editable
    * type: **text**
    * example: `inv123`
* `number` - Stable identifier of a classroom relative to an organization
    * mandatory
    * non editable, auto-assigned
    * type: **number**
    * example: `1`
* `name` - Unique and short name that defines a classroom
    * mandatory
    * editable
    * type: **text**
    * example: `My Classroom`
* `description` - Short description that characterizes a classroom
    * non mandatory
    * editable
    * type: **text**
    * example: `This is my classroom`
* `organization` - Name of the organization where the classroom is contained
    * mandatory
    * non editable, auto-assigned
    * type: **text**
    * example: `My Organization`

## Link Relations
* [self](#get-classroom)
* [assignments](assignments.md#list-assignments)
* [teams](teams.md#list-teams)
* [users](users.md#list-users)
* [classrooms](#list-classrooms)
* [organization](organizations.md#get-organization)

## Actions
* [List Classrooms](#list-classrooms)
* [Get Classroom](#get-classroom)
* [Create Classroom](#create-classroom)
* [Edit Classroom](#edit-classroom)
* [Delete Classroom](#delete-classroom)

------
### List Classrooms
List all the classrooms of the organization in which the user is in.

```http
GET /api/orgs/{orgId}/classrooms
```

#### Parameters
| Name        | Type        | In         | Description                                                                           |
| ----------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept      | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| orgId       | integer     | path       | The GitHub Organization's unique identifier                                           |
| page        | integer     | query      | Specifies the current page of the list                                                |
| limit       | integer     | query      | Specifies the number of results per page (max. 100)                                   |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["classroom", "collection"],
  "properties": {
    "collectionSize": 2,
    "pageIndex": 0,
    "pageSize": 2
  },
  "entities": [
    {
      "class": ["classroom"],
      "rel": ["item"],
      "properties": {
        "id": 1,
        "inviteCode": "inv1",
        "number": 1,
        "name": "Classroom 1",
        "description": "Description of Classroom 1",
        "organization": "i-on-project-codegarten-tests"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1"
        },
        {
          "rel": ["assignments"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments"
        },
        {
          "rel": ["teams"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/teams"
        },
        {
          "rel": ["users"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/users"
        },
        {
          "rel": ["classrooms"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms"
        },
        {
          "rel": ["organization"],
          "href": "http://localhost:8080/api/orgs/80703382"
        }
      ]
    },
    {
      "class": ["classroom"],
      "rel": ["item"],
      "properties": {
        "id": 2,
        "inviteCode": "inv2",
        "number": 2,
        "name": "Classroom 2",
        "description": "Description of Classroom 2",
        "organization": "i-on-project-codegarten-tests"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/2"
        },
        {
          "rel": ["assignments"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/2/assignments"
        },
        {
          "rel": ["teams"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/2/teams"
        },
        {
          "rel": ["users"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/2/users"
        },
        {
          "rel": ["classrooms"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms"
        },
        {
          "rel": ["organization"],
          "href": "http://localhost:8080/api/orgs/80703382"
        }
      ]
    }
  ],
  "actions": [
    {
      "name": "create-classroom",
      "title": "Create Classroom",
      "method": "POST",
      "href": "http://localhost:8080/api/orgs/80703382/classrooms",
      "type": "application/json",
      "fields": [
        {
          "name": "orgId",
          "type": "hidden",
          "value": 80703382
        },
        {
          "name": "name",
          "type": "text"
        },
        {
          "name": "description",
          "type": "text"
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms?page=0&limit=10"
    },
    {
      "rel": ["page"],
      "hrefTemplate": "http://localhost:8080/api/orgs/80703382/classrooms{?page,limit}"
    },
    {
      "rel": ["organization"],
      "href": "http://localhost:8080/api/orgs/80703382"
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
### Get Classroom
Get a single classroom from an organization.

```http
GET /api/orgs/{orgId}/classrooms/{classroomNumber}
```

#### Parameters
| Name             | Type        | In         | Description                                                                           |
| ---------------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept           | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| orgId            | integer     | path       | The GitHub Organization's unique identifier                                           |
| classroomNumber  | integer     | path       | The classroom's identifier relative to the organization                               |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["classroom"],
  "properties": {
    "id": 1,
    "inviteCode": "inv1",
    "number": 1,
    "name": "Classroom 1",
    "description": "Description of Classroom 1",
    "organization": "i-on-project-codegarten-tests"
  },
  "actions": [
    {
      "name": "edit-classroom",
      "title": "Edit Classroom",
      "method": "PUT",
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1",
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
        },
        {
          "name": "description",
          "type": "text"
        }
      ]
    },
    {
      "name": "delete-classroom",
      "title": "Delete Classroom",
      "method": "DELETE",
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1",
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
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1"
    },
    {
      "rel": ["assignments"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments"
    },
    {
      "rel": ["teams"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/teams"
    },
    {
      "rel": ["users"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/users"
    },
    {
      "rel": ["classrooms"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms"
    },
    {
      "rel": ["organization"],
      "href": "http://localhost:8080/api/orgs/80703382"
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

------
### Create Classroom
Create a classroom inside the organization.

```http
POST /api/orgs/{orgId}/classrooms
```

#### Parameters
| Name         | Type        | In         | Description                                                                           |
| ------------ | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept       | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| content-type | string      | header     | Should be set to `application/json`                                                   |
| orgId        | integer     | path       | The GitHub Organization's unique identifier                                           |
| name         | string      | body       | **Required**. Short name that defines the classroom                                   |
| description  | string      | body       | Short description that characterizes the classroom                                    |

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

#### Resource Not Found
```
Status: 404 Not Found
```

#### Conflict
```
Status: 409 Conflict
```

------
### Edit Classroom
Edit an already existing classroom.

```http
PUT /api/orgs/{orgId}/classrooms/{classroomNumber}
```

#### Parameters
| Name             | Type        | In         | Description                                                                                        |
| ---------------- | ----------- | ---------- | ---------------------------------------------------------------------------------------------------|
| accept           | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`                         |
| content-type     | string      | header     | Should be set to `application/x-www-form-urlencoded`                                               |
| orgId            | integer     | path       | The GitHub Organization's unique identifier                                                        |
| classroomNumber  | integer     | path       | The classroom's identifier relative to the organization                                            |
| name             | string      | body       | **Required unless you provide `description`**. Unique and short name that defines the classroom    |
| description      | string      | body       | **Required unless you provide `name`**. Short description that characterizes the classroom         |

#### Default Response
```
Status: 200 OK
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

#### Resource Not Found
```
Status: 404 Not Found
```

#### Conflict
```
Status: 409 Conflict
```

------
### Delete Classroom
Delete an existing classroom from an organization.

```http
DELETE /api/orgs/{orgId}/classrooms/{classroomNumber}
```

#### Parameters
| Name             | Type        | In         | Description                                                                    |
| ---------------- | ----------- | ---------- | ------------------------------------------------------------------------------ |
| accept           | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`     |
| orgId            | integer     | path       | The GitHub Organization's unique identifier                                    |
| classroomNumber  | integer     | path       | The classroom's identifier relative to the organization                        |

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

#### Resource Not Found
```
Status: 404 Not Found
```