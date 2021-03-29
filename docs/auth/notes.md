# Authentication and access token architecture

## Differences between GitHub app and OAuth app

### GitHub Apps
* Installation Token (created via a REST API endpoint and have an expiration date)
* To create an installation access token (unique for that installation), we need a signed (with the app's private key) JWT. It contains the app's ID
* OAuth token (optional, can be requested, but only gives access to private information if the app is installed in the authenticated user)

### OAuth Apps
* OAuth token

### Comparison
* An OAuth application acts as an authenticated GitHub user (owner of the access token), while GitHub apps use their own identity
* GitHub apps are installed in organizations (by an admin) or in an user, while OAuth apps are authorized to access user resources (e.g repositories that an user can access)
* OAuth apps require the user to have administrator privileges to execute certain actions (e.g create repository in organization) while GitHub apps only need to be installed in the organization (**not yet tested**)

## Notes

* Apparently, GitHub Classroom doesn't use OAuth anymore, instead, it's all done within a GitHub app
* GitHub Classroom installs itself to an organization, but doesn't show up on the installed apps list
* We are still uncertain on how to install a GitHub app automatically like GitHub Classroom does (there is no link to install a GitHub app on an organization in the REST API). The only way to do so is via a web form
* We found a way to gain access to OAuth token using the app, but that only gives us access to public info about the user. This can be used for authentication purposes in our app

## Conclusions

* We can use OAuth to obtain access tokens and then use the teacher's access token to create private repositories inside organizations
OR
* We can use a GitHub App installed in the organization to create the private repositories (**not enough information about this for now**) 


