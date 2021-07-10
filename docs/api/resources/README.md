# Authorization
All API routes (besides [Home](#home)) require authentication to access them. More information about the authorization can be found [here](../auth/README.md).

# Installations
In order for the server application to function, its GitHub App needs to be installed in organizations. More information about installations can be found [here](../auth/installations.md). 

# Collections
Listing resources in the API (classrooms, assignments...) returns an [application/vnd.siren+json](#references) representation containing the various existent resources. If there are more pages to the result set, there are extra link relations present:

* next - URI to the next page
* previous - URI to the previous page

These link relations are hidden when there are no next and/or previous pages.

It's also possible to limit the amount of resources in the collection by using the `limit` query parameter. The current page of the collection (starts at 0) is specified in the `page` query parameter. In case these parameters are not provided, their values are set to:
```
page - 0
limit - 10
```
### Example
```http
GET /api/orgs?page=0&limit=5
```

# Home
The home resource presents information about the server application and lists all available GET routes

```http
GET /api
```
#### Default Response
```
Status: 200 OK
```
```json
{
  "class": ["home"],
  "properties": {
    "name": "i-on CodeGarten",
    "description": "CodeGarten is a system to create and manage Git repos used by students while working on course assignments",
    "uptimeMs": 10972,
    "time": "2021-05-09T15:34:16.7520916+01:00",
    "authors": [
      "Diogo Sousa LEIC 20/21",
      "João Moura LEIC 20/21",
      "Tiago David LEIC 20/21"
    ]
  },
  "links": [
    {
      "rel": ["organizations"],
      "href": "/api/orgs"
    },
    {
      "rel": ["organization"],
      "hrefTemplate": "/api/orgs/{orgId}"
    },
    {
      "rel": ["searchRepos"],
      "hrefTemplate": "/api/orgs/{orgId}/templaterepos"
    },
    {
      "rel": ["classrooms"],
      "hrefTemplate": "/api/orgs/{orgId}/classrooms"
    },
    {
      "rel": ["classroom"],
      "hrefTemplate": "/api/orgs/{orgId}/classrooms/{classroomNumber}"
    },
    {
      "rel": ["classroomUsers"],
      "hrefTemplate": "/api/orgs/{orgId}/classrooms/{classroomNumber}/users"
    },
    {
      "rel": ["classroomTeams"],
      "hrefTemplate": "/api/orgs/{orgId}/classrooms/{classroomNumber}/teams"
    },
    {
      "rel": ["classroomTeam"],
      "hrefTemplate": "/api/orgs/{orgId}/classrooms/{classroomNumber}/teams/{teamNumber}"
    },
    {
      "rel": ["classroomTeamUsers"],
      "hrefTemplate": "/api/orgs/{orgId}/classrooms/{classroomNumber}/teams/{teamNumber}/users"
    },
    {
      "rel": ["assignments"],
      "hrefTemplate": "/api/orgs/{orgId}/classrooms/{classroomNumber}/assignments"
    },
    {
      "rel": ["assignment"],
      "hrefTemplate": "/api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}"
    },
    {
      "rel": ["assignmentParticipants"],
      "hrefTemplate": "/api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/participants"
    },
    {
      "rel": ["deliveries"],
      "hrefTemplate": "/api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/deliveries"
    },
    {
      "rel": ["delivery"],
      "hrefTemplate": "/api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/deliveries/{deliveryNumber}"
    },
    {
      "rel": ["participantDeliveries"],
      "hrefTemplate": "/api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/participants/{participantId}/deliveries"
    },
    {
      "rel": ["user"],
      "hrefTemplate": "/api/users/{userId}"
    },
    {
      "rel": ["authenticatedUser"],
      "href": "/api/user"
    },
    {
      "rel": ["invitation"],
      "hrefTemplate": "/api/user/invites/{inviteCode}"
    },
    {
      "rel": ["invitationTeams"],
      "hrefTemplate": "/api/user/invites/{inviteCode}/classroom/teams"
    },
    {
      "rel": ["classroomParticipation"],
      "hrefTemplate": "/api/user/classrooms/{classroomId}/participation"
    },
    {
      "rel": ["assignmentParticipation"],
      "hrefTemplate": "/api/user/assignments/{assignmentId}/participation"
    }
  ]
}
```

# References
[Siren](https://github.com/kevinswiber/siren)