# Participations

A participation is a registry of the type of presence of a user in a [classroom](classrooms.md) or [assignment](assignment.md).

## Properties
* `id` - Unique and stable global identifier of a participant
    * mandatory
    * non editable, auto-assigned
    * type: **number**
    * example: `1`
* `type` - The type of presence of the participant
    * mandatory
    * non editable, auto-assigned
    * type: **text**
    * example: `student`
* `name` - The name of the authenticated user
    * mandatory
    * editable, auto-assigned
    * type: **text**
    * example: `codegartenStudent`
* `participantsType` - The type of participants in an assignment
    * mandatory
    * non-editable, auto-assigned
    * type: **text**
    * example: `team`

## Link Relations
* self ([classroom](classrooms.md/#get-classroom) or [assignment](assignments.md/#get-assignment))
* [user](users.md/#get-user)
* repo (GitHub Repository)
* [deliveries](deliveries.md/#get-deliveries-of-participant)
* [team](teams.md/#get-team)
* [assignment](assignments.md/#get-assignment)
* [classroom](classrooms.md/#get-classroom)
* [organization](organizations.md/#get-organization)

## Actions
* [Get Authenticated User Participation In Assignment](#get-authenticated-user-participation-in-assignment)
* [Get Authenticated User Participation In Classroom](#get-authenticated-user-participation-in-classroom)
* [List Assignment Participants](#list-assignment-participants)
* [Add Assignment Participant](#add-assignment-participant)
* [Delete Assignment Participant](#delete-assignment-participant)

------
### Get Authenticated User Participation In Assignment
Get the type of presence of a authenticated user in an assignment.

```http
GET /api/user/assignments/{assignmentId}/participation
```

#### Parameters
| Name          | Type        | In         | Description                                                                           |
| -----------   | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept        | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| assignmentId  | integer     | path       | The assignment's unique identifier                                                    |

#### Response when the user is in an individual assignment
```
Status: 200 OK
```
```json
{
  "class": ["participant"],
  "properties": {
    "type": "user",
    "id": 1,
    "name": "codegartenStudent"
  },
  "links": [
    {
      "rel": ["self"],
      "href": "/api/user/assignments/3/participation"
    },
    {
      "rel": ["repo"],
      "href": "https://github.com/test/test-repo"
    },
    {
      "rel": ["deliveries"],
      "href": "/api/orgs/80703382/classrooms/1/assignments/1/participants/1/deliveries"
    },
    {
      "rel": ["user"],
      "href": "/api/users/1"
    },
    {
      "rel": ["assignment"],
      "href": "/api/orgs/80703382/classrooms/1/assignments/1"
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

#### Response when the user is in a group assignment

```
Status: 200 OK
```
```json
{
  "class": ["participant"],
  "properties": {
    "type": "team",
    "id": 1,
    "name": "codegartenTeam1"
  },
  "links": [
    {
      "rel": ["self"],
      "href": "/api/user/assignments/3/participation"
    },
    {
      "rel": ["repo"],
      "href": "https://github.com/test/test-repo"
    },
    {
      "rel": ["deliveries"],
      "href": "/api/orgs/80703382/classrooms/1/assignments/1/participants/1/deliveries"
    },
    {
      "rel": ["team"],
      "href": "/api/orgs/80703382/classrooms/1/teams/1"
    },
    {
      "rel": ["assignment"],
      "href": "/api/orgs/80703382/classrooms/1/assignments/1"
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
### Get Authenticated User Participation In Classroom
Get the type of presence of the authenticated user in a classroom.

```http
GET /api/user/classrooms/{classroomId}/participation
```

#### Parameters
| Name          | Type        | In         | Description                                                                           |
| -----------   | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept        | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| classroomId   | integer     | path       | The classroom's unique identifier                                                     |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["participant"],
  "properties": {
    "type": "teacher",
    "id": 1,
    "name": "codegartenTeacher"
  },
  "links": [
    {
      "rel": ["self"],
      "href": "/api/user/classrooms/1/participation"
    },
    {
      "rel": ["user"],
      "href": "/api/users/1"
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
### List Assignment Participants
List the participants of an assignment.

```http
GET /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/participants
```

#### Parameters
| Name              | Type        | In         | Description                                                                           |
| ------------------| ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept            | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| orgId             | integer     | path       | The GitHub Organization's unique identifier                                           |
| classroomNumber   | integer     | path       | The classroom's identifier relative to the organization                               |
| assignmentNumber  | integer     | path       | The assignment's identifier relative to the classroom                                 |
| page              | integer     | query      | Specifies the current page of the list                                                |
| limit             | integer     | query      | Specifies the number of results per page (max. 100)                                   |

#### Response when the assignment is an individual assignment
```
Status: 200 OK
```
```json
{
  "class": ["participant", "collection"],
  "properties": {
    "participantsType": "user",
    "collectionSize": 2,
    "pageIndex": 0,
    "pageSize": 2
  },
  "entities": [
    {
      "class": ["participant"],
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
          "href": "https://avatars.githubusercontent.com/u/1"
        },
        {
          "rel": ["deliveries"],
          "href": "/api/orgs/80703382/classrooms/1/assignments/1/participants/1/deliveries"
        },
        {
          "rel": ["assignment"],
          "href": "/api/orgs/80703382/classrooms/1/assignments/1"
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
      "class": ["participant"],
      "rel": ["item"],
      "properties": {
        "id": 2,
        "name": "codegartenStudent2"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "/api/users/2"
        },
        {
          "rel": ["avatar"],
          "href": "https://avatars.githubusercontent.com/u/2"
        },
        {
          "rel": ["deliveries"],
          "href": "/api/orgs/80703382/classrooms/1/assignments/1/participants/2/deliveries"
        },
        {
          "rel": ["assignment"],
          "href": "/api/orgs/80703382/classrooms/1/assignments/1"
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
      "name": "add-participant-to-assignment",
      "title": "Add Participant To Assignment",
      "method": "PUT",
      "hrefTemplate": "/api/orgs/80703382/classrooms/1/assignments/1/participants/{userId}",
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
          "name": "userId",
          "type": "number"
        }
      ]
    },
    {
      "name": "remove-participant-from-assignment",
      "title": "Remove Participant From Assignment",
      "method": "DELETE",
      "hrefTemplate": "/api/orgs/80703382/classrooms/1/assignments/1/participants/{userId}",
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
          "name": "userId",
          "type": "number"
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "/api/orgs/80703382/classrooms/1/assignments/1/participants?page=0&limit=10"
    },
    {
      "rel": ["page"],
      "hrefTemplate": "/api/orgs/80703382/classrooms/1/assignments/1/participants{?page,limit}"
    },
    {
      "rel": ["assignment"],
      "href": "/api/orgs/80703382/classrooms/1/assignments/1"
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
#### Response when the assignment is a group assignment
```
Status: 200 OK
```
```json
{
  "class": ["participant", "collection"],
  "properties": {
    "participantsType": "team",
    "collectionSize": 2,
    "pageIndex": 0,
    "pageSize": 2
  },
  "entities": [
    {
      "class": ["participant"],
      "rel": ["item"],
      "properties": {
        "id": 1,
        "name": "codegartenTeam1"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "/api/orgs/80703382/classrooms/1/teams/1"
        },
        {
          "rel": ["avatar"],
          "href": "https://avatars.githubusercontent.com/t/1"
        },
        {
          "rel": ["deliveries"],
          "href": "/api/orgs/80703382/classrooms/1/assignments/1/participants/1/deliveries"
        },
        {
          "rel": ["assignment"],
          "href": "/api/orgs/80703382/classrooms/1/assignments/1"
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
      "class": ["participant"],
      "rel": ["item"],
      "properties": {
        "id": 2,
        "name": "codegartenTeam2"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "/api/orgs/80703382/classrooms/1/teams/2"
        },
        {
          "rel": ["avatar"],
          "href": "https://avatars.githubusercontent.com/t/2"
        },
        {
          "rel": ["deliveries"],
          "href": "/api/orgs/80703382/classrooms/1/assignments/1/participants/2/deliveries"
        },
        {
          "rel": ["assignment"],
          "href": "/api/orgs/80703382/classrooms/1/assignments/1"
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
      "name": "add-participant-to-assignment",
      "title": "Add Participant To Assignment",
      "method": "PUT",
      "hrefTemplate": "/api/orgs/80703382/classrooms/1/assignments/1/participants/{teamNumber}",
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
          "name": "teamNumber",
          "type": "number"
        }
      ]
    },
    {
      "name": "remove-participant-from-assignment",
      "title": "Remove Participant From Assignment",
      "method": "DELETE",
      "hrefTemplate": "/api/orgs/80703382/classrooms/1/assignments/1/participants/{teamNumber}",
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
          "name": "teamNumber",
          "type": "number"
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "/api/orgs/80703382/classrooms/1/assignments/1/participants?page=0&limit=10"
    },
    {
      "rel": ["page"],
      "hrefTemplate": "/api/orgs/80703382/classrooms/1/assignments/1/participants{?page,limit}"
    },
    {
      "rel": ["assignment"],
      "href": "/api/orgs/80703382/classrooms/1/assignments/1"
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
### Add Assignment Participant
Add a participant to an assignment.

```http
PUT /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/participants/{participantId}
```

#### Parameters
| Name              | Type        | In         | Description                                                                                                |
| ------------------| ----------- | ---------- | -----------------------------------------------------------------------------------------------------------|
| accept            | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`                                 |
| content-type      | string      | header     | Should be set to `application/json`                                                                        |
| orgId             | integer     | path       | The GitHub Organization's unique identifier                                                                |
| classroomNumber   | integer     | path       | The classroom's identifier relative to the organization                                                    |
| assignmentNumber  | integer     | path       | The assignment's identifier relative to the classroom                                                      |
| participantId     | integer     | path       | The participant's identifier. Can either be the user's ID or the team's number relative to the classroom   |

#### Default Response
```
Status: 201 Created
Location: https://github.com/test/test-repo
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
### Delete Assignment Participant
Delete a participant from an assignment.

```http
DELETE /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/participants/{participantId}
```

#### Parameters
| Name              | Type        | In         | Description                                                                                                |
| ------------------| ----------- | ---------- | -----------------------------------------------------------------------------------------------------------|
| accept            | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`                                 |
| content-type      | string      | header     | Should be set to `application/json`                                                                        |
| orgId             | integer     | path       | The GitHub Organization's unique identifier                                                                |
| classroomNumber   | integer     | path       | The classroom's identifier relative to the organization                                                    |
| assignmentNumber  | integer     | path       | The assignment's identifier relative to the classroom                                                      |
| participantId     | integer     | path       | The participant's identifier. Can either be the user's ID or the team's number relative to the classroom   |

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