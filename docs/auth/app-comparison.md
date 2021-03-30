# Differences between GitHub App and OAuth App

## Comparison
* An OAuth application acts as an authenticated GitHub user (owner of the access token), while GitHub apps use their own identity
* GitHub apps are installed in organizations (by an admin) or in an user, while OAuth apps are authorized to access user resources (e.g repositories that an user can access)
* OAuth apps require the user to have administrator privileges to execute certain actions (e.g create repository in organization) while GitHub apps only need to be installed in the organization

## Notes

* Apparently, GitHub Classroom doesn't use OAuth anymore, instead, it's all done within a GitHub app
* GitHub Classroom installs itself to an organization, but doesn't show up on the installed apps list
* We are still uncertain on how to install a GitHub app automatically like GitHub Classroom does (there is no link to install a GitHub app on an organization in the REST API). The only way to do so is via a web form
* We found a way to gain access to OAuth token using the app, but that only gives us access to public info about the user. This can be used for authentication purposes in our app. The organizations listed using this token are the ones with the GitHub App installed.

## Conclusions

* We can use OAuth to obtain access tokens and then use the teacher's access token to create private repositories inside organizations

OR

* We can use a GitHub App installed in the organization to create the private repositories. The user would have to manually install the GitHub App in the organizations they wanted to use in CodeGarten **(preferred)**