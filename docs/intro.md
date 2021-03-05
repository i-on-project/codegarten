# Introduction

This document contains a short introduction to the i-on CodeGarten project, namely its main goals and uses cases.

# Goals

The main goal of the i-on CodeGarten project is to createe and deploy a system to create and manage Git repos used by students while working on course assignments.
It is mainly inspired by the functionality and limitations of [GitHub Classroom](https://classroom.github.com/).

# Uses cases

* User goes to the CodeGarten application.
  * No functionality is available without a user being logged in.

* User logs in using GitHub OAuth-based authentication and authorization process. 
  * A future version may support other Git-based systems (e.g. GitLab). For the first version, CodeGarten will be coupled to GitHub.
  * The login process is used to authenticate the user and obtain access tokens to access the GitHub API.
  
* When logged in, the user sees a listing with his GitHub organizations.

* After selecting an organization
  * The user sees a list of assignments created in the organization.
  * The user also has the ability to create a new assignment.

* An assignment represents a work that is being done one or more students or groups of students.

* The creation of a new assignment requires the following information.
  * Assignment name - a short human readable description of the assignment (e.g. "2020/2021 Summer - Concurrent Programming Set 1").
  * Repository name prefix - the prefix that will used for all repositories created for this assignment (e.g. `s2021-2-set1-`).
  * Individual or group assigment:
    * Individual assignments will have a repository created for each user.
    * Group assignments will have multiple users for each repository.
  * Optional GitHub template repository
  * Creation mode:
    * Invitation URI to be shared with the students. When following the link, 
      * a new repository will be created for the GitHub user.
      * or the user can join an already existing repository.
    * List of students or groups, provided via an uploaded file.
  * One or more planned deliveries, where each delivery is characterized by a tag name and a due date.

* For each assignment, it should be possible to:
  * List the created repositories, with some summary information, such as:
    * Date of last change.
    * Number of participants.
    * Created releases/deliveries.
    * Number of commits.
    * ...
  * Sort the assignment list according to some of this criteria (e.g. sort by data of last change)
  * Download a script file that clones all repos into a local folder.
  * Download a script file that pulls the latest changes on all repos of a local folder.

* It should be possible to easily correlate a GitHub user identifier with a student identifier.

* For group assignments, the group information (group number, participants, and associated repo) should be made available.
