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

- `/orgs/{org-name}`
  - Information about the organization (e.g. name, link to github)
  - List with classrooms
    - Each item has the classroom name and a link to `/classrooms/{cid}`

- `/classrooms/{cid}`
  - Information about the classroom: TODO
  - List with classroom assignments
    - Each item has the assignment name and a link to `/assignments/{aid}`


