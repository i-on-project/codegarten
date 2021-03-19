# Sketch

## GET Requests
- `/`
  - Generic information about the project
  - Link for login action
  - Redirects to /orgs if logged in

- `/login`
  - Logs in and adds the user information to the DB if it doesn't exist

- `/institutions`
  - Paginated list with all available institutions 
    - Each item has institution name and a button to link it to the user's CodeGarten account
  - Allows search by name
  
- `/orgs`
  - List with the user's organizations
    - Each item has the organization name, picture, and a link to `/orgs/{oid}`
  - Button to create a new organization (Redirect to GitHub)?
  - Question: How to get the user's organizations?
    - Should we synchronously use the GitHub API to retrieve that
    - Have that information in be in the DB and updated only if stale
    - That information was obtained during login

- `/orgs/{oid}`
  - Information about the organization (e.g. name, link to github)
  - List with most recent classrooms
    - Each item has the classroom name and a link to `/orgs/{oid}/classrooms/{cid}`
    - Each item also has the number of students, and a list of the most recent assignments
    - Each item has a small icon to indicate if the user is a student or a teacher
  - Button to show paginated list of classrooms (`/orgs/{oid}/classrooms`)
  - If the user is the organization owner, button to create a new classroom (`/orgs/{oid}/classrooms/new`)

- `/orgs/{oid}/classrooms`
  - Accepts a query parameter to specify the current page 
  - Paginated list with all classrooms
    - Each item has the classroom name and a link to `/orgs/{oid}/classrooms/{cid}`
    - Each item also has the number of students, and a list of the most recent assignments
    - Each item has a small icon to indicate if the user is a student or a teacher
  - Button to create a new classroom (`/orgs/{oid}/classrooms/new`)

- `/orgs/{oid}/classrooms/new`
  - Form to create a new classroom
  - A classroom has:
    - Name
    - Description (optional)
    - Institution
    - ...
  - Creates by making a POST request to (`/orgs/{oid}/classrooms`)

- `/orgs/{oid}/classrooms/{cid}`
  - Information about the classroom (e.g. name, description)
  - List with most recent classroom assignments
    - Each item has the assignment name, the due date and a link to `/orgs/{oid}/classrooms/{cid}/assignments/{aid}`
    - Each item has information about whether its an individual or group assignment
    - Each item may show the number of students that finished the assignment (what if the assignment has multiple deliveries?)
  - Button to show paginated list of assignments (`/orgs/{oid}/classrooms/{cid}/assignments`)
  - Button to create a new assignment (`/orgs/{oid}/classrooms/{cid}/assignments/new`)
  - Button for a student to join classroom (`TODO`)

- `/orgs/{oid}/classrooms/{cid}/students`
  - Paginated list with all students of a classroom
    - A student has:
      - Name
      - Id (e.g student number, depends on institution?)
      - GitHub username

- `/orgs/{oid}/classrooms/{cid}/students/add`
  - Form for a teacher to add a new student or a group of students to the classroom
  - A student can be added via:
    - Student ID
    OR
    - File with all Student IDs

- `/orgs/{oid}/classrooms/{cid}/assignments`
  - Accepts a query parameter to specify the current page 
  - Paginated list with all student assignments
    - Each item has the assignment name, the due date and a link to `/orgs/{oid}/classrooms/{cid}/assignments/{aid}`
    - Each item has information about whether its an individual or group assignment
    - Each item may show the number of students that finished the assignment (what if the assignment has multiple deliveries?)
  - Button to create a new assignment (`/orgs/{oid}/classrooms/{cid}/assignments/new`)

- `/orgs/{oid}/classrooms/{cid}/assignments/new`
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
  - Creates by making a POST request to (`/orgs/{oid}/classrooms/{cid}/assignments`)

- `/orgs/{oid}/classrooms/{cid}/assignments/{aid}`
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

- `/orgs/{oid}/classrooms/{cid}/assignments/{aid}/{repo-name}`
  - Information about the repository status
    - Link to the GitHub repository
    - Participants
    - Created deliveries
    - Delivery status
    - Last commit
    - ...

## PUT Requests

- `/orgs/{oid}/classrooms/{cid}`
  - Edits a classroom
  - A classrom has:
    - Name
    - Description
    - ...

- `/orgs/{oid}/classrooms/{cid}/assignments/{aid}`
  - Edits an assigment
  - An assignment has:
    - Name
    - Description (optional)
    - Repository prefix
    - Information about whether its an individual or group assignment
    - Template repository (optional)
    - Creation mode (via link or list of students)
    - Planned deliveries (tag and due date)
    - ...

## POST Requests

- `/orgs/{oid}/classrooms/{cid}/assignments`
  - Creates a new assignment
  - An assignment has:
    - Name
    - Description (optional)
    - Repository prefix
    - Information about whether its an individual or group assignment
    - Template repository (optional)
    - Creation mode (via link or list of students)
    - Planned deliveries (tag and due date)
    - ...

- `/orgs/{oid}/classrooms`
  - Creates a new classroom
  - A classroom has:
    - Name
    - Description (optional)
    - ...
    