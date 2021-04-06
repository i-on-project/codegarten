# CodeGarten API Access

## Required Information
In order to manage access to the CodeGarten API, it's required to store the following information:
* CodeGarten Client - A client that's registered to use the API. This client will have:
    * Client ID - Used to identify what client is requesting an authorization code/access token
    * Client Name - User friendly way of identifying the client
    * Client Secret - Used when exchanging an authorization code for an access token
    * Client Redirect URI - The URI that will receive the authorization code

* CodeGarten API Access Token - Used to access the API endpoints that require authentication. It's associated with a user and a client
* CodeGarten API Access Token Expiration Date - Used to determine when to request a new Access Token

## Obtaining an Access Token
In order to obtain a CodeGarten API Access Token, it's required to authenticate via GitHub. This process is described in the [corresponding documentation](github-api.md#user-access-token). It's also required to specify which client is requesting an access token by sending the client ID through the query string.

After the **CodeGarten Interaction Manager (IM)** gets a GitHub OAuth Access Token from the user, and after that token is stored in the database, the CodeGarten IM will generate a valid code to be exchanged by an API Access Token. This is possible due to the IM being contained inside the API. After generating the code, the IM will redirect the browser to the redirect URI specified in the query string of the authorization request. This redirect URI should be valid and trusted by the API. Finally, the App will then request an access token from the API, giving the code obtained through the CodeGarten IM and both the client ID and secret. If this is a valid code, and if the client information is correct, the API will then respond with an API Access Token, as well as giving its expiration date. The API will also store this access token in the database, associating it with an user and a client.