# Assignments
An assignment is a task allocated for [users](users.md) or [teams](teams.md) in a classroom.

## Properties
* `id` - Unique and stable global identifier of an assignment
    * mandatory
    * non editable, auto-assigned
    * type: **number**
    * example: `1`
* `inviteCode` - Unique code associated with an assignment
    * mandatory
    * non editable
    * type: **text**
    * example: `inv123`
* `number` - Stable identifier of a classroom relative to a classroom
    * mandatory
    * non editable, auto-assigned
    * type: **number**
    * example: `1`
* `name` - Unique and short name that defines an assignment
    * mandatory
    * editable
    * type: **text**
    * example: `My Assignment`
* `description` - Short description that characterizes an assignment
    * non mandatory
    * editable
    * type: **text**
    * example: `This is my assignment`
* `type` - Type of the assignment
    * mandatory
    * non editable
    * type: **text** (Can be one of the following: `["individual", "group"]`)
    * example: `group`
* `classroom` - Name of the classroom where the assignment is contained
    * mandatory
    * non editable, auto-assigned
    * type: **text**
    * example: `My Classroom`
* `organization` - Name of the organization where the assignment is contained
    * mandatory
    * non editable, auto-assigned
    * type: **text**
    * example: `My Organization`

## Link Relations
* [self](#get-assignment)
* [deliveries](deliveries.md#list-deliveries)
* [participants](participations.md#list-participants)
* [assignments](#list-assignments)
* [classroom](classrooms.md#get-classroom)
* [organization](organizations.md#get-organization)

## Actions
* [List Assignments](#list-assignments)
* [Get Assignment](#get-assignment)
* [Create Assignment](#create-assignment)
* [Edit Assignment](#edit-assignment)
* [Delete Assignment](#delete-assignment)

------
### List Assignments
List all the assignments of the classroom in which the user is in.

```http
GET /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments
```

#### Parameters
| Name             | Type        | In         | Description                                                                           |
| ---------------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept           | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| orgId            | integer     | path       | The GitHub Organization's unique identifier                                           |
| classroomNumber  | integer     | path       | The classroom's identifier relative to the organization                               |
| page             | integer     | query      | Specifies the current page of the list                                                |
| limit            | integer     | query      | Specifies the number of results per page (max. 100)                                   |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["assignment", "collection"],
  "properties": {
    "collectionSize": 2,
    "pageIndex": 0,
    "pageSize": 2
  },
  "entities": [
    {
      "class": ["assignment"],
      "rel": ["item"],
      "properties": {
        "id": 1,
        "inviteCode": "inv3",
        "number": 1,
        "name": "Assignment C1 1",
        "description": "Description of Assignment C1 1",
        "type": "individual",
        "classroom": "Classroom 1",
        "organization": "i-on-project-codegarten-tests"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1"
        },
        {
          "rel": ["deliveries"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/deliveries"
        },
        {
          "rel": ["participants"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/participants"
        },
        {
          "rel": ["assignments"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments"
        },
        {
          "rel": ["classroom"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1"
        },
        {
          "rel": ["organization"],
          "href": "http://localhost:8080/api/orgs/80703382"
        }
      ]
    },
    {
      "class": ["assignment"],
      "rel": ["item"],
      "properties": {
        "id": 2,
        "inviteCode": "inv4",
        "number": 2,
        "name": "Assignment C1 2",
        "description": "Description of Assignment C1 2",
        "type": "group",
        "classroom": "Classroom 1",
        "organization": "i-on-project-codegarten-tests"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/2"
        },
        {
          "rel": ["deliveries"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/2/deliveries"
        },
        {
          "rel": ["participants"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/2/participants"
        },
        {
          "rel": ["assignments"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments"
        },
        {
          "rel": ["classroom"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1"
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
      "name": "create-assignment",
      "title": "Create Assignment",
      "method": "POST",
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments",
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
        },
        {
          "name": "type",
          "type": "text",
          "value": {
            "oneOf": [
              "individual",
              "group"
            ]
          }
        },
        {
          "name": "repoPrefix",
          "type": "text"
        },
        {
          "name": "repoTemplate",
          "type": "text"
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments?page=0&limit=10"
    },
    {
      "rel": ["page"],
      "hrefTemplate": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments{?page,limit}"
    },
    {
      "rel": ["classroom"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1"
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
### Get Assignment
Get a single assignment from a classroom.

```http
GET /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}
```

#### Parameters
| Name             | Type        | In         | Description                                                                           |
| ---------------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept           | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| orgId            | integer     | path       | The GitHub Organization's unique identifier                                           |
| classroomNumber  | integer     | path       | The classroom's identifier relative to the organization                               |
| assignmentNumber | integer     | path       | The assignment's identifier relative to the classroom                                 |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["assignment"],
  "properties": {
    "id": 1,
    "inviteCode": "inv3",
    "number": 1,
    "name": "Assignment C1 1",
    "description": "Description of Assignment C1 1",
    "type": "individual",
    "repoPrefix": "Assignment1C1",
    "repoTemplate": null,
    "classroom": "Classroom 1",
    "organization": "i-on-project-codegarten-tests"
  },
  "actions": [
    {
      "name": "edit-assignment",
      "title": "Edit Assignment",
      "method": "PUT",
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1",
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
          "name": "assignmentNumber",
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
      "name": "delete-assignment",
      "title": "Delete Assignment",
      "method": "DELETE",
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1",
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
          "name": "assignmentNumber",
          "type": "hidden",
          "value": 1
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1"
    },
    {
      "rel": ["deliveries"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/deliveries"
    },
    {
      "rel": ["participants"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/participants"
    },
    {
      "rel": ["assignments"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments"
    },
    {
      "rel": ["classroom"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1"
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
### Create Assignment
Create an assignment inside a classroom.

```http
POST /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments
```

#### Parameters
| Name            | Type        | In         | Description                                                                                  |
| --------------- | ----------- | ---------- | -------------------------------------------------------------------------------------        |
| accept          | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`                   |
| content-type    | string      | header     | Should be set to `application/json`                                                          |
| orgId           | integer     | path       | The GitHub Organization's unique identifier                                                  |
| classroomNumber | integer     | path       | The classroom's identifier relative to the organization                                      |
| name            | string      | body       | **Required**. Short name that defines the assignment                                         |
| description     | string      | body       | Short description that characterizes the assignment                                          |
| type            | string      | body       | **Required**. Type of the assignment. Can be one of the following: `["individual", "group"]` |
| repoPrefix      | string      | body       | **Required**. Prefix to be used when creating GitHub repositories for the assignment         |
| repoTemplate    | string      | body       | GitHub repository template to be used when creating GitHub repositories for the assignment   |

#### Default Response
```
Status: 201 Created
Location: /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}
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
### Edit Assignment
Edit an already existing assignment.

```http
PUT /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}
```

#### Parameters
| Name             | Type        | In         | Description                                                                                        |
| ---------------- | ----------- | ---------- | ---------------------------------------------------------------------------------------------------|
| accept           | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`                         |
| content-type     | string      | header     | Should be set to `application/x-www-form-urlencoded`                                               |
| orgId            | integer     | path       | The GitHub Organization's unique identifier                                                        |
| classroomNumber  | integer     | path       | The classroom's identifier relative to the organization                                            |
| assignmentNumber | integer     | path       | The assignment's identifier relative to the classroom                                              |
| name             | string      | body       | **Required unless you provide `description`**. Unique and short name that defines the assignment   |
| description      | string      | body       | **Required unless you provide `name`**. Short description that characterizes the assignment        |

#### Default Response
```
Status: 200 OK
Location: /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}
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
### Delete Assignment
Delete an existing assignment from a classroom.

```http
DELETE /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}
```

#### Parameters
| Name             | Type        | In         | Description                                                                    |
| ---------------- | ----------- | ---------- | ------------------------------------------------------------------------------ |
| accept           | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`     |
| orgId            | integer     | path       | The GitHub Organization's unique identifier                                    |
| classroomNumber  | integer     | path       | The classroom's identifier relative to the organization                        |
| assignmentNumber | integer     | path       | The assignment's identifier relative to the classroom                          |

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