# GitHub API Access

## Required Information
Since we're using a GitHub App, it's required to store the following persistent information:
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

### Installation Access Token
To obtain an installation access token, the user will need to be redirected to the GitHub App's installation form, in order for them to pick which organization they want to install the application to. This redirection is supported through an endpoint that's part of what we call **CodeGarten Interaction Manager (IM)**, which is neither the API nor a client app. Its only responsibility is to respond to requests related to GitHub Installations and GitHub/CodeGarten Authentications. The browser will then present the installation form to the user and, after they pick and install the app into an organization, the browser will be redirected to a new URI (part of IM), where the installation ID is present in its query string. Via this endpoint, the API will be able to receive the installation ID (since the IM communicates directly with the API), and store this information in the database alongside the organization's ID. This interaction should be made on a new browser window, which will close itself in the end.

The process described above is available as a diagram [here](interaction-diagrams.md#GitHub-App-Installation-Diagram).

### User Access Token
To obtain a user's access token, the API will have an endpoint which will respond with an URI from the CodeGarten IM module. This URI will redirect the browser to the GitHub App's OAuth URI, which in turn will request the user to authenticate itself in GitHub. After completing the authentication, the browser will be redirected back to CodeGarten IM, passing along a authorization code in the query string. This code will be exchanged for a GitHub OAuth Access Token related to the GitHub user. This access token will then be stored in the database alongside the user's information, replacing the old one if it exists.