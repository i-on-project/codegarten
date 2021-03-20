# Users
A user has:
- Name (preferably the real name)
- Id (institutional ID)
- GitHub Username

## /login
  - Logs in via GitHub and adds the user information to the DB if it doesn't exist
  - If the account does not exist, request a username (real name) for their CodeGarten account

## /{user}
  - User profile that shows his GitHub profile pic (for now), his CodeGarten username and his institutions

## /{user}/settings
  - Only accessible by the user
  - Can change:
    - Username
    - Link/unlink institutions (?)
  - Can also delete his account

## /orgs/{oid}/classrooms/{cid}/users
  - Paginated list with all students and teachers of a classroom

## /orgs/{oid}/classrooms/{cid}/users/add
  - Form for a teacher to add a new user (student or teacher) or a group of users to the classroom
  - A user can be added via:
    - Institutional ID
    OR
    - File with all Institutional IDs