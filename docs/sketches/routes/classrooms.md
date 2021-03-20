# Classrooms
A classroom has:
  - Classroom ID (auto-generated, non-editable)
  - Name (editable)
  - Description (editable, optional)
  - Assignments
  - Users (teachers/students)
  - ...

## /orgs/{oid}/classrooms (supports POST to create)
  - Accepts a query parameter to specify the current page 
  - Paginated list with all classrooms
    - Each item has the classroom name and a link to `/orgs/{oid}/classrooms/{cid}`
    - Each item also has the number of students, and a list of the most recent assignments
    - Each item has a small icon to indicate if the user is a student or a teacher
  - Button to create a new classroom (`/orgs/{oid}/classrooms/new`)
  - If the user is the organization owner, button to create a new classroom (`/orgs/{oid}/classrooms/new`)

## /orgs/{oid}/classrooms/new
  - Form to create a new classroom
  - Creates by making a POST request to (`/orgs/{oid}/classrooms`)

## /orgs/{oid}/classrooms/{cid} (supports PUT to edit)
  - Information about the classroom (e.g. name, description)
  - List with most recent classroom assignments
    - Each item has the assignment name, the due date and a link to `/orgs/{oid}/classrooms/{cid}/assignments/{aid}`
    - Each item has information about whether its an individual or group assignment
    - Each item may show the number of students that finished the assignment (what if the assignment has multiple deliveries?)
  - Button to show paginated list of assignments (`/orgs/{oid}/classrooms/{cid}/assignments`)
  - Button to create a new assignment (`/orgs/{oid}/classrooms/{cid}/assignments/new`)
  - Button for a student to join classroom (only if allowed, e.g. is in institution, was invited...)