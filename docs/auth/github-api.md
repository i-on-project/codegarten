# GitHub API access

## Required Information
Since we're using a GitHub App we need to store the following persistent information:
* GitHub App Name - Used in HTTP Requests in the `User-Agent` header
* GitHub App Id - Used in the JWT, which is used to request an installation token
* GitHub App Client Id - Used while requesting an OAuth access token   
* GitHub App Client Secret - Used while requesting an OAuth access token
* GitHub App Private Key - Used to sign the JWT, which is used to request an installation token

We also need to store the following information:
* GitHub User Access Token - Used to validate each user's identity and their corresponding information
* GitHub App Installations
    * GitHub App Installation Id - Used to identify a GitHub App Installation in an organization
    * GitHub App Installation Access Token - Used to execute actions in an installation
    * Github App Installation Access Token Expiration Date - Used to determine when to request a new Installation Access Token

## Obtaining the Information
The persistent information described above is obtained during the GitHub App's creation. Some of this information should remain private, most notably the app's private key and client secret. This information should also be easily modifiable since the GitHub App can change at any time.

To obtain a user's access token, the CodeGarten API will only receive the authorization code which will be exchanged for an access token. That code should be obtained using a Client App (e.g. CodeGarten Web App). The Client App will need the GitHub App Client Id as well as a CodeGarten API Access Token that has enough privileges to create users. Since a GitHub User Access Token doesn't expire, we can store it next to the user information in our database.

**TODO:** How to send Installation Id to CodeGarten API and stay on the client app
* Perhaps we can use `state` as a redirect URI?