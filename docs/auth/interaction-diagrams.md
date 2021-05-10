# Interaction Diagrams

These diagrams are explained in the following documentation:
* [GitHub API](github-api.md)
* [CodeGarten API](codegarten-api.md)

## Keywords

* **B** -> Browser
* **CG** -> CodeGarten 
* **IM** -> Interaction Manager 
* **GH** -> GitHub
* **AIF** -> App Installation Form
* **AS** -> Authorization Server
* **CA** -> Client App

## GitHub App Installation Diagram

```
Client opens browser with an URI containing a GitHub App installation request
- B --> CG/IM: GET request to GH App Installation endpoint
- B <-- CG/IM: Redirect with an URI containing an installation request to GH
- B --> GH/AIF: GET request to the GH app installation form
- B <-> GH/AIF: Interactive user consent
- B <-- GH/AIF: Redirect back to the CG app
- B --> CG/IM: GET request to CG (contains installation ID issued by GH)
  - CG/IM --> GH: GET request containing installation ID, to obtain the associated organization 
  - CG/IM <-- GH: Info about the organization related to the installation ID
  - CG/IM: Associate installation ID with the organization in the database  
- B <-- CG/IM: Request to close the window/browser
```

## CodeGarten Authentication Diagram

```
Client opens browser with an URI containing an authorization request to CG
- B --> CG/IM: GET authorization request CG
- B <-- CG/IM: Redirect with an URI containing authorization request to GH
- B --> GH/AS: GET authorization request GH
- B <-> GH/AS: Interactive user authentication and consent
- B <-- GH/AS: Redirect with an URI containing authorization code
- B --> CG/IM: GET GH authorization response (contains authorization code issued by GH)
  - CG/IM --> GH/AS: POST request containing the Client ID, Client Secret, and authorization code in order to obtain an access token
  - CG/IM <-- GH/AS: Response with GH access token 
  - CG/IM: Store GH access token alongside user information
  - CG/IM: Generate code associated with the user and the client, with an expiration date
- B <-- CG/IM: Redirect with an URI containing the client's authorization response
- B --> CA: GET CG authorization response (contains authorization code issued by CG)
  - CA --> CG/API: POST request containing the Client ID, Client Secret, and authorization code in order to obtain an access token
  - CG/API: Verify authorization code validity and, if valid, generate an access token and associate it with a user, and an expiration date
  - CA <-- CG/API: Response with the CG access token and its validity
```


