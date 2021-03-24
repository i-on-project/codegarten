# Organizations

## /orgs
  - List with the user's organizations
    - Each item has the organization name, picture, and a link to `/orgs/{oid}`
  - Button to create a new organization (Redirect to GitHub)?
  - Question: How to get the user's organizations?
    - Should we synchronously use the GitHub API to retrieve that (maybe this in order to synchronize with the user (e.g user left org, org changed name...))
    - Have that information in be in the DB and updated only if stale
    - That information was obtained during login

## /orgs/{oid}
  - Information about the organization (e.g. name, link to github)
  - List with most recent classrooms
    - Each item has the classroom name and a link to `/orgs/{oid}/classrooms/{cid}`
    - Each item also has the number of students, and a list of the most recent assignments
    - Each item has a small icon to indicate if the user is a student or a teacher
  - Button to show paginated list of classrooms (`/orgs/{oid}/classrooms`)
  - If the user is the organization owner, button to create a new classroom (`/orgs/{oid}/classrooms/new`)