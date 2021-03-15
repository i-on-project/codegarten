# Sketch

- `/`
  - Generic information about the project
  - Link for login action
  - Redirects to /orgs if logged in

- `/login`
  - Logs in and adds the user information to the DB if it doesn't exist

- `/orgs`
  - List with the user's organizations
    - Each item has the organization name, picture, and a link to `/orgs/{org-name}`
  - Button to create a new organization (Redirect to GitHub)?
  - Question: How to get the user's organizations?
    - Should we synchronously use the GitHub API to retrieve that
    - Have that information in be in the DB and updated only if stale
    - That information was obtained during login

- `/orgs/{org-name}`
  - Information about the organization (e.g. name, link to github)
  - List with classrooms
    - Each item has the classroom name and a link to `/orgs/{org-name}/classrooms/{cid}`
    - Each item also has the number of students, and a list of the most recent assignments
  - Button to create a new classroom (`/orgs/{org-name}/classrooms/create`)

- `/orgs/{org-name}/classrooms/{cid}`
  - Information about the classroom (e.g. name, description)
  - List with classroom assignments
    - Each item has the assignment name, the due date and a link to `/orgs/{org-name}/classrooms/{cid}/assignments/{aid}`
    - Each item has information about whether its an individual or group assignment
    - Each item may show the number of students that finished the assignment (what if the assignment has multiple deliveries?)
  - Button to create a new assignment (`/orgs/{org-name}/classrooms/{cid}/assignments/create`)

- `/orgs/{org-name}/classrooms/{cid}/assignments/{aid}`
  - Information about the assignment (e.g. name, description, due date)
  - List with student repositories
    - Each item has a link to the student's GitHub repository
    - Each item also shows:
      - Number of participants
      - Date of the last change
      - Created releases/deliveries
      - Number of commits
      - Status of assignment tests (if applicable)
      - Grade (if applicable)
      - ...
  - Button to download script to clone all repositories
  - Button to download script to pull latest changes from all repositories

- `/orgs/{org-name}/classrooms/{cid}/assignments/{aid}/{repo-name}`
  - Information about the repository status
    - Link to the GitHub repository
    - Participants
    - Created deliveries
    - Delivery status
    - Last commit
    - ...

- `/orgs/{org-name}/classrooms/create`
  - Supports POST method
  - Form to create a new classroom
  - A classroom has:
    - Name
    - Description (optional)
    - ...

- `/orgs/{org-name}/classrooms/{cid}/edit`
  - Supports PUT method
  - Edits a classroom

- `/orgs/{org-name}/classrooms/{cid}/assignments/create`
  - Supports POST method
  - Form to create a new assignment
  - An assignment has:
    - Name
    - Description (optional)
    - Repository prefix
    - Information about whether its an individual or group assignment
    - Template repository (optional)
    - Creation mode (via link or list of students)
    - Planned deliveries (tag and due date)
    - ...

- `/orgs/{org-name}/classrooms/{cid}/assignments/{aid}/edit`
  - Supports PUT method