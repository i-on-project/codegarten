# Sketch

- `/`
  - Generic information about the project
  - Link for login action

- `/login`
  - ...


- `/orgs`
  - List with the user's organizations
    - Each item has the organization name and a link to `/orgs/{org-name}`
    - Each item also has the number of classrooms in the organization?
  - Question: How to get the user's organizations?
    - Should we synchronously use the GitHub API to retrieve that
    - Have that information in be in the DB and updated only if stale
    - That information was obtained during login

- `/orgs/{org-name}`
  - Information about the organization (e.g. name, link to github)
  - List with classrooms
    - Each item has the classroom name and a link to `/classrooms/{cid}`

- `/classrooms/{cid}`
  - Information about the classroom: TODO
  - List with classroom assignments
    - Each item has the assignment name and a link to `/assignments/{aid}`


