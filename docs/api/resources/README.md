# Authorization
All API routes (besides [Home](#home)) require authentication to access them.

This authentication information is passed via the `Authorization` header in all requests, using the `Bearer` scheme.
More information about the authorization can be found [here](#auth/README.md).

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
      "href": "http://localhost:8080/api/orgs"
    },
    {
      "rel": ["organization"],
      "hrefTemplate": "http://localhost:8080/api/orgs/{orgId}"
    },
    {
      "rel": ["classrooms"],
      "hrefTemplate": "http://localhost:8080/api/orgs/{orgId}/classrooms"
    },
    {
      "rel": ["classroom"],
      "hrefTemplate": "http://localhost:8080/api/orgs/{orgId}/classrooms/{classroomNumber}"
    },
    {
      "rel": ["classroomUsers"],
      "hrefTemplate": "http://localhost:8080/api/orgs/{orgId}/classrooms/{classroomNumber}/users"
    },
    {
      "rel": ["classroomTeams"],
      "hrefTemplate": "http://localhost:8080/api/orgs/{orgId}/classrooms/{classroomNumber}/teams"
    },
    {
      "rel": ["classroomTeam"],
      "hrefTemplate": "http://localhost:8080/api/orgs/{orgId}/classrooms/{classroomNumber}/teams/{teamNumber}"
    },
    {
      "rel": ["classroomTeamUsers"],
      "hrefTemplate": "http://localhost:8080/api/orgs/{orgId}/classrooms/{classroomNumber}/teams/{teamNumber}/users"
    },
    {
      "rel": ["assignments"],
      "hrefTemplate": "http://localhost:8080/api/orgs/{orgId}/classrooms/{classroomNumber}/assignments"
    },
    {
      "rel": ["assignment"],
      "hrefTemplate": "http://localhost:8080/api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}"
    },
    {
      "rel": ["assignmentParticipants"],
      "hrefTemplate": "http://localhost:8080/api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/participants"
    },
    {
      "rel": ["deliveries"],
      "hrefTemplate": "http://localhost:8080/api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/deliveries"
    },
    {
      "rel": ["delivery"],
      "hrefTemplate": "http://localhost:8080/api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/deliveries/{deliveryNumber}"
    },
    {
      "rel": ["participantDeliveries"],
      "hrefTemplate": "http://localhost:8080/api/orgs/{orgId}/classrooms/{classroomNumber}/assignments/{assignmentNumber}/participants/{participantId}/deliveries"
    },
    {
      "rel": ["user"],
      "hrefTemplate": "http://localhost:8080/api/users/{userId}"
    },
    {
      "rel": ["authenticatedUser"],
      "href": "http://localhost:8080/api/user"
    },
    {
      "rel": ["invitation"],
      "hrefTemplate": "http://localhost:8080/api/user/invites/{inviteCode}"
    },
    {
      "rel": ["invitationTeams"],
      "hrefTemplate": "http://localhost:8080/api/user/invites/{inviteCode}/classroom/teams"
    },
    {
      "rel": ["classroomParticipation"],
      "hrefTemplate": "http://localhost:8080/api/user/classrooms/{classroomId}/participation"
    },
    {
      "rel": ["assignmentParticipation"],
      "hrefTemplate": "http://localhost:8080/api/user/assignments/{assignmentId}/participation"
    }
  ]
}
```

# References
[Siren](https://github.com/kevinswiber/siren)