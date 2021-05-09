# Deliveries

A delivery defines the moment in time in which the participant must deliver a version of the assignment to the Teacher.

## Properties
* `id` - Unique and stable global identifier of a delivery
    * mandatory
    * non editable, auto-assigned
    * type: **number**
    * example: `1`
* `tag` - Name of the tag that's associated with this delivery. Must be used when creating git tags for the assignment
    * mandatory
    * editable
    * type: **text**
    * example: `"v1.0"`
* `dueDate` - Date that defines the last moment in time in which a delivery was delivered on-time (in ISO format)
    * non mandatory
    * editable
    * type: **datetime**
    * example: `"2021-04-20T20:00:00.123456+01:00"`
* `isDelivered` - Defines if the delivery has been delivered by the participant
    * mandatory
    * non editable, auto-assigned
    * type: **boolean**
    * example: `false`
* `deliverDate` - Date in which the participant delivered the delivery (in ISO format)
    * non mandatory
    * non editable, auto-assigned
    * type: **datetime**
    * example: `"2021-04-20T20:00:00.123456+01:00"`
* `assignment` - Name of the assignment where the delivery is contained
    * mandatory
    * non editable
    * type: **text**
    * example: `"assignment 1"`
* `classroom` - Name of the classroom where the delivery is contained
    * mandatory
    * non editable
    * type: **text**
    * example: `"classroom 1"`
* `organization` - Name of the organization where the delivery is contained
    * mandatory
    * non editable
    * type: **text**
    * example: `"organization 1"`

## Link Relations
* self ([delivery](#get-delivery) or [participant-delivery](#get-participant-delivery))
* [participant-deliveries](#list-participant-deliveries)
* [deliveries](#list-deliveries)
* [assignment](assignments.md#get-assignment)
* [classroom](classrooms.md#get-classroom)
* [organization](organizations.md#get-organization)
* participant ([user](users.md#get-user) or [team](teams.md#get-team))

## Actions
* [List Deliveries](#list-deliveries)
* [List Participant Deliveries](#list-participant-deliveries)
* [Get Delivery](get-delivery)
* [Get Participant Delivery](#get-participant-delivery)
* [Create Delivery](#create-delivery)
* [Edit Delivery](#edit-delivery)
* [Delete Delivery](#delete-delivery)

------
### List Deliveries
Lists all deliveries that exist in an assignment.

```http
GET /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/deliveries
```

#### Parameters
| Name                  | Type        | In         | Description                                                                           |
| --------------------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| orgId                 | integer     | path       | The organization's unique identifier                                                  |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                               |
| assignmentNumber      | integer     | path       | The assignment's identifier relative to the classroom                                 |
| page                  | integer     | query      | Specifies the current page of the list                                                |
| limit                 | integer     | query      | Specifies the number of results per page (max. 100)                                   |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["delivery", "collection"],
  "properties": {
    "collectionSize": 2,
    "pageIndex": 0,
    "pageSize": 2
  },
  "entities": [
    {
      "class": ["delivery"],
      "rel": ["item"],
      "properties": {
        "id": 1,
        "tag": "Delivery1A1",
        "dueDate": "2023-05-17T00:00:00+01:00",
        "assignment": "Assignment C1 1",
        "classroom": "Classroom 1",
        "organization": "i-on-project-codegarten-tests"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/deliveries/1"
        },
        {
          "rel": [ "deliveries"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/deliveries"
        },
        {
          "rel": ["assignment"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1"
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
      "class": [
        "delivery"
      ],
      "rel": [
        "item"
      ],
      "properties": {
        "id": 2,
        "tag": "Delivery2A1",
        "dueDate": "2023-05-17T00:00:00+01:00",
        "assignment": "Assignment C1 1",
        "classroom": "Classroom 1",
        "organization": "i-on-project-codegarten-tests"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/deliveries/2"
        },
        {
          "rel": ["deliveries"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/deliveries"
        },
        {
          "rel": ["assignment"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1"
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
      "name": "create-delivery",
      "title": "Create Delivery",
      "method": "POST",
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/deliveries",
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
          "name": "tag",
          "type": "text"
        },
        {
          "name": "dueDate",
          "type": "datetime"
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
      "rel": ["assignment"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1"
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
### List Participant Deliveries
Lists all deliveries of a participant in an assignment. This route allows checking if the participant has delivered the various deliveries.

```http
GET /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/participants/{participantId}/deliveries
```

#### Parameters
| Name                  | Type        | In         | Description                                                                                                 |
| --------------------- | ----------- | ---------- | ----------------------------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`                                  |
| orgId                 | integer     | path       | The organization's unique identifier                                                                        |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                                                     |
| assignmentNumber      | integer     | path       | The assignment's identifier relative to the classroom                                                       |
| participantId         | integer     | path       | The participant's identifier. Can either be the user's ID or the team's number relative to the classroom    |
| page                  | integer     | query      | Specifies the current page of the list                                                                      |
| limit                 | integer     | query      | Specifies the number of results per page (max. 100)                                                         |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["delivery", "collection"],
  "properties": {
    "collectionSize": 2,
    "pageIndex": 0,
    "pageSize": 2
  },
  "entities": [
    {
      "class": ["delivery"],
      "rel": ["item"],
      "properties": {
        "id": 1,
        "tag": "Delivery1A1",
        "dueDate": "2023-05-17T00:00:00+01:00",
        "isDelivered": true,
        "assignment": "Assignment C1 1",
        "classroom": "Classroom 1",
        "organization": "i-on-project-codegarten-tests"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/participants/1/deliveries/1"
        },
        {
          "rel": ["participant-deliveries"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/participants/1/deliveries"
        },
        {
          "rel": ["deliveries"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/deliveries"
        },
        {
          "rel": ["assignment"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1"
        },
        {
          "rel": ["classroom"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1"
        },
        {
          "rel": ["organization"],
          "href": "http://localhost:8080/api/orgs/80703382"
        },
        {
          "rel": ["participant"],
          "href": "/api/orgs/80703382/classrooms/1/teams/1"
        }
      ]
    },
    {
      "class": ["delivery"],
      "rel": ["item"],
      "properties": {
        "id": 2,
        "tag": "Delivery2A1",
        "dueDate": "2023-05-17T00:00:00+01:00",
        "isDelivered": false,
        "assignment": "Assignment C1 1",
        "classroom": "Classroom 1",
        "organization": "i-on-project-codegarten-tests"
      },
      "links": [
        {
          "rel": ["self"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/participants/1/deliveries/2"
        },
        {
          "rel": ["participant-deliveries"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/participants/1/deliveries"
        },
        {
          "rel": ["deliveries"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/deliveries"
        },
        {
          "rel": ["assignment"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1"
        },
        {
          "rel": ["classroom"],
          "href": "http://localhost:8080/api/orgs/80703382/classrooms/1"
        },
        {
          "rel": ["organization"],
          "href": "http://localhost:8080/api/orgs/80703382"
        },
        {
          "rel": ["participant"],
          "href": "/api/orgs/80703382/classrooms/1/teams/1"
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
      "rel": ["deliveries"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/deliveries"
    },
    {
      "rel": ["assignment"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1"
    },
    {
      "rel": ["classroom"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1"
    },
    {
      "rel": ["organization"],
      "href": "http://localhost:8080/api/orgs/80703382"
    },
    {
      "rel": ["participant"],
      "href": "/api/orgs/80703382/classrooms/1/teams/1"
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
### Get Delivery
Get a delivery from an assignment.

```http
GET /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/deliveries/{deliveryNumber}
```

#### Parameters
| Name                  | Type        | In         | Description                                                                                                 |
| --------------------- | ----------- | ---------- | ----------------------------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`                                  |
| orgId                 | integer     | path       | The organization's unique identifier                                                                        |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                                                     |
| assignmentNumber      | integer     | path       | The assignment's identifier relative to the classroom                                                       |
| deliveryNumber        | integer     | path       | The delivery's identifier relative to the assignment                                                        |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["delivery"],
  "properties": {
    "id": 1,
    "tag": "Delivery1A1",
    "dueDate": "2023-05-17T00:00:00+01:00",
    "assignment": "Assignment C1 1",
    "classroom": "Classroom 1",
    "organization": "i-on-project-codegarten-tests"
  },
  "actions": [
    {
      "name": "edit-delivery",
      "title": "Edit Delivery",
      "method": "PUT",
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/deliveries/1",
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
          "name": "deliveryNumber",
          "type": "hidden",
          "value": 1
        },
        {
          "name": "tag",
          "type": "text"
        },
        {
          "name": "dueDate",
          "type": "datetime"
        }
      ]
    },
    {
      "name": "delete-delivery",
      "title": "Delete Delivery",
      "method": "DELETE",
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/deliveries/1",
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
          "name": "deliveryNumber",
          "type": "hidden",
          "value": 1
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/deliveries/1"
    },
    {
      "rel": ["deliveries"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/deliveries"
    },
    {
      "rel": ["assignment"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1"
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
### Get Participant Delivery
Get a delivery of a participant. This route allows checking if the participant has delivered the delivery, and if so, when it was delivered.

```http
GET /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/participant/{participantId}/deliveries/{deliveryNumber}
```

#### Parameters
| Name                  | Type        | In         | Description                                                                                                 |
| --------------------- | ----------- | ---------- | ----------------------------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`                                  |
| orgId                 | integer     | path       | The organization's unique identifier                                                                        |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                                                     |
| assignmentNumber      | integer     | path       | The assignment's identifier relative to the classroom                                                       |
| participantId         | integer     | path       | The participant's identifier. Can either be the user's ID or the team's number relative to the classroom    |
| deliveryNumber        | integer     | path       | The delivery's identifier relative to the assignment                                                        |

#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["delivery"],
  "properties": {
    "id": 1,
    "tag": "Delivery1A1",
    "dueDate": "2023-05-17T00:00:00+01:00",
    "isDelivered": true,
    "deliverDate": "2021-05-09T16:40:12Z",
    "assignment": "Assignment C1 1",
    "classroom": "Classroom 1",
    "organization": "i-on-project-codegarten-tests"
  },
  "links": [
    {
      "rel": ["self"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/participants/1/deliveries/1"
    },
    {
      "rel": ["participant-deliveries"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/participants/1/deliveries"
    },
    {
      "rel": ["deliveries"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1/deliveries"
    },
    {
      "rel": ["assignment"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1/assignments/1"
    },
    {
      "rel": ["classroom"],
      "href": "http://localhost:8080/api/orgs/80703382/classrooms/1"
    },
    {
      "rel": ["organization"],
      "href": "http://localhost:8080/api/orgs/80703382"
    },
    {
      "rel": ["participant"],
      "href": "/api/orgs/80703382/classrooms/1/teams/1"
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
### Create Delivery
Create a delivery in the assignment. The authenticated user must be a Teacher in the classroom in order to perform this action.

```http
POST /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/deliveries
```

#### Parameters
| Name                  | Type        | In         | Description                                                                                     |
| --------------------- | ----------- | ---------- | ----------------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`                      |
| content-type          | string      | header     | Should be set to `application/json`                                                             |
| orgId                 | integer     | path       | The organization's unique identifier                                                            |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                                         |
| assignmentNumber      | integer     | path       | The assignment's identifier relative to the classroom                                           |
| tag                   | string      | body       | **Required**. Name of the tag that's associated with this delivery                              |
| dueDate               | string      | body       | Date that defines the last moment in time in which a delivery was delivered on-time             |

#### Default Response
```
Status: 201 Created
Location: /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/deliveries/{deliveryNumber}
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
### Edit Delivery
Edit already existing delivery. The authenticated user must be a Teacher in the classroom in order to perform this action.

```http
PUT /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/deliveries/{deliveryNumber}
```

#### Parameters
| Name                  | Type        | In         | Description                                                                                                                            |
| --------------------- | ----------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`                                                             |
| content-type          | string      | header     | Should be set to `application/json`                                                                                                    |
| orgId                 | integer     | path       | The organization's unique identifier                                                                                                   |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                                                                                |
| assignmentNumber      | integer     | path       | The assignment's identifier relative to the classroom                                                                                  |
| deliveryNumber        | integer     | path       | The delivery's identifier relative to the assignment                                                                                   |
| tag                   | string      | body       | **Required unless you provide `dueDate`**. Name of the tag that's associated with this delivery                                        |
| dueDate               | string      | body       | **Required unless you provide `tag`**. Date that defines the last moment in time in which a delivery was delivered on-time             |

#### Default Response
```
Status: 201 OK
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
### Delete Delivery
Delete an existing delivery from the assignment. The authenticated user must be a Teacher in the classroom in order to perform this action.

```http
DELETE /api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/deliveries/{deliveryNumber}
```

#### Parameters
| Name                  | Type        | In         | Description                                                                                                                            |
| --------------------- | ----------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| accept                | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`                                                             |
| orgId                 | integer     | path       | The organization's unique identifier                                                                                                   |
| classroomNumber       | integer     | path       | The classroom's identifier relative to the organization                                                                                |
| assignmentNumber      | integer     | path       | The assignment's identifier relative to the classroom                                                                                  |
| deliveryNumber        | integer     | path       | The delivery's identifier relative to the assignment                                                                                   |

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