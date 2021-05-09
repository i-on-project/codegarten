# Invitations

An invitation represents an invite to a classroom or an assignment.

## Properties

### Properties for a classroom invite
* `id` - Unique and stable global identifier of a classroom
    * mandatory
    * non editable, auto-assigned
    * type: **number**
    * example: `1`
* `name` - The name of the classroom
    * mandatory
    * non editable, auto-assigned
    * type: **text**
    * example: `Classroom`
* `description` - The description of the classroom
    * mandatory
    * non editable, auto-assigned
    * type: **text**
    * example: `Description of Classroom`
* `organization` - The name of the organization
    * mandatory
    * non-editable, auto-assigned
    * type: **text**
    * example: `Organization`

### Properties for an assignment invite
* `id` - Unique and stable global identifier of a assignment
    * mandatory
    * non editable, auto-assigned
    * type: **number**
    * example: `1`
* `name` - The name of the assignment
    * mandatory
    * non editable, auto-assigned
    * type: **text**
    * example: `Classroom`
* `description` - The description of the classroom
    * mandatory
    * non editable, auto-assigned
    * type: **text**
    * example: `Description of Classroom`
* `type` - The assignment type
    * mandatory
    * non editable, auto-assigned
    * type: **text**
    * example: `individual`
* `classroom` - The name of the classroom where the assignment is present
    * mandatory
    * non-editable, auto-assigned
    * type: **text**
    * example: `Classroom 1`
* `organization` - The name of the organization
    * mandatory
    * non-editable, auto-assigned
    * type: **text**
    * example: `Organization`

## Link Relations

### Link relations for a classroom invite
* [self](#get-invite-code-information)
* [teams](teams.md/#get-teams)
* [classroom](classrooms.md/#get-classroom)

### Link relations for an assignment invite
* [self](#get-invite-code-information)
* [teams](teams.md/#get-teams)
* [assignment](assignment.md/#get-teams)


## Actions
* [Get invite code information](#get-invite-code-information)
* [Get invite code classroom teams](#get-invite-code-classroom-teams)
* [Join classroom through invite](#join-classroom-through-invite)
------
### Get invite code information
Get the detailed information of an invitation.

```http
GET /api/user/invites/{inviteCode}
```

#### Parameters
| Name        | Type        | In         | Description                                                                           |
| ----------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept      | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| inviteCode  | string      | path        | The invite code's unique identifier                                                   |
#### Response if the invite is from a classroom
```
Status: 200 OK
```
```json
{
  "class": ["classroomInvitation"],
  "properties": {
    "id": 1,
    "name": "Classroom",
    "description": "Description",
    "organization": "i-on-project-codegarten-tests"
  },
  "actions": [
    {
      "name": "join-classroom",
      "title": "Join Classroom",
      "method": "PUT",
      "href": "http://localhost:8080/api/user/invites/inv",
      "type": "application/json",
      "fields": [
        {
          "name": "inviteCode",
          "type": "hidden",
          "value": "inv"
        },
        {
          "name": "teamId",
          "type": "number"
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "http://localhost:8080/api/user/invites/inv"
    },
    {
      "rel": ["teams"],
      "href": "http://localhost:8080/api/user/invites/inv/classroom/teams"
    },
    {
      "rel": ["classroom"],
      "href": "/api/orgs/80703382/classrooms/1"
    }
  ]
}
```
#### Response if the invite is from an assignment
```
Status: 200 OK
```
```json
{
  "class": ["assignmentInvitation"],
  "properties": {
    "id": 1,
    "name": "Assignment",
    "description": "Description of Assignment",
    "type": "individual",
    "classroom": "Classroom",
    "organization": "i-on-project-codegarten-tests"
  },
  "actions": [
    {
      "name": "join-assignment",
      "title": "Join Assignment",
      "method": "PUT",
      "href": "http://localhost:8080/api/user/invites/inv",
      "type": "application/json",
      "fields": [
        {
          "name": "inviteCode",
          "type": "hidden",
          "value": "inv"
        },
        {
          "name": "teamId",
          "type": "number"
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "http://localhost:8080/api/user/invites/inv"
    },
    {
      "rel": ["teams"],
      "href": "http://localhost:8080/api/user/invites/inv3/classroom/teams"
    },
    {
      "rel": ["assignment"],
      "href": "/api/orgs/80703382/classrooms/1/assignments/1"
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
### Get invite code classroom teams
Get all teams of a classroom using an invitation.

```http
GET /api/user/invites/{inviteCode}/classroom/teams
```

#### Parameters
| Name        | Type        | In         | Description                                                                           |
| ----------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept      | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| inviteCode  | string      | path       | The invite code's unique identifier                                                   |
#### Default Response
```
Status: 200 OK
```
```json
{
  "class": [
    "team",
    "collection"
  ],
  "properties": {
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
        "name": "team",
        "classroom": "Classroom 1",
        "organization": "i-on-project-codegarten-tests"
      },
      "links": [
        {
          "rel": ["avatar"],
          "href": "https://avatars.githubusercontent.com/t/123"
        }
      ]
    },
    {
      "class": ["team"],
      "rel": ["item"],
      "properties": {
        "id": 2,
        "number": 2,
        "name": "team 2",
        "classroom": "Classroom 1",
        "organization": "i-on-project-codegarten-tests"
      },
      "links": [
        {
          "rel": ["avatar"],
          "href": "https://avatars.githubusercontent.com/t/321"
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "http://localhost:8080/api/user/invites/inv1/classroom/teams?page=0&limit=10"
    },
    {
      "rel": ["page"],
      "hrefTemplate": "http://localhost:8080/api/user/invites/inv1/classroom/teams{?page,limit}"
    },
    {
      "rel": ["invite"],
      "href": "http://localhost:8080/api/user/invites/inv"
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
### Join classroom through invite
Add the authenticated user to a classroom using the invite code. 
In case the user wants to join a team, the `teamId` can be placed in the body.

```http
PUT /api/user/invites/{inviteCode}
```

#### Parameters
| Name         | Type        | In         | Description                                                                           |
| -------------| ----------- | ---------- | --------------------------------------------------------------------------------------|
| accept       | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| content-type | string      | header     | Should be set to `application/json`                                                   |
| inviteCode   | string      | path       | The invite code's unique identifier                                                   |
| teamId       | integer     | body       | The team's unique identifier                                                          |
#### Default Response
```
Status: 201 Created
Location: https://github.com/i-on-project-codegarten-tests/repo
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
