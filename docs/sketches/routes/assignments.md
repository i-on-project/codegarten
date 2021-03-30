# Assignments
An assignment has:
  - Assignment ID (auto-generated, non-editable)
  - Name (editable)
  - Description (editable, optional)
  - Repository prefix (non-editable)
  - Information about whether its an individual or group assignment (non-editable)
  - Template repository (optional, non-editable)
  - Planned deliveries (editable)
  - ...

Each delivery plan has:
  - Tag (editable)
  - Due Date (editable)

## /orgs/{oid}/classrooms/{cid}/assignments (supports POST to create)
  - Accepts a query parameter to specify the current page 
  - Paginated list with all student assignments
    - Each item has the assignment name, the due date and a link to `/orgs/{oid}/classrooms/{cid}/assignments/{aid}`
    - Each item has information about whether its an individual or group assignment
    - Each item may show the number of students that finished the assignment (what if the assignment has multiple deliveries?)
  - Button to create a new assignment (`/orgs/{oid}/classrooms/{cid}/assignments/new`)

## /orgs/{oid}/classrooms/{cid}/assignments/new
  - Form to create a new assignment
  - Creates by making a POST request to (`/orgs/{oid}/classrooms/{cid}/assignments`)

## /orgs/{oid}/classrooms/{cid}/assignments/{aid} (supports PUT to edit)
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

## /orgs/{oid}/classrooms/{cid}/assignments/{aid}/{repo-name}
  - Information about the repository status
    - Link to the GitHub repository
    - Participants
    - Created deliveries
    - Delivery status
    - Last commit
    - ...