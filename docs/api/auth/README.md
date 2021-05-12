# Authorization

Below are the steps required to obtain a user's access token. In order to perform these steps, a registered [client](clients.md) is necessary.

## 1. Obtaining a user's auth code

```http
GET /im/oauth/authorize
```

### Parameters
| Name             | Type        | In         | Description                                                                           |
| ---------------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| client_id        | integer     | query      | **Required**. The client's unique identifier                                          |
| state            | string      | query      | A string that will be returned to the client                                          |

## 2. Obtaining a user's access token

If the user accepted the authorization request in GitHub, they will be redirected to the client's redirect URI with an authorization code in the `code` query parameter. This code will expire after 1 minute. If the user denied the request or an authorization error occurs, the error will be in the `error` query parameter. In both cases the `state` parameter passed by the client will be returned in the `state` query parameter.

Exchange the received code for an access token:
```http 
POST /api/oauth/access_token 
```

### Parameters
| Name             | Type        | In         | Description                                                                                      |
| ---------------- | ----------- | ---------- | ------------------------------------------------------------------------------------------------ |
| content-type     | string      | header     | Should be set to `application/x-www-form-urlencoded`                                             |
| code             | integer     | query      | **Required**. The auth code received as a response to [step 1](#1.-Obtaining-a-user's-auth-code) |
| client_id        | integer     | query      | **Required**. The client's unique identifier                                                     |
| client_secret    | string      | query      | **Required**. The client's secret                                                                |

### Response

If the request is completed successfully, the client will receive an access token that expires after 2 weeks.
```json
{
    "access_token": "5fd6511e24d02b76efeee6ea16215885bedfc99bd0b6854a60440e69af04704c",
    "expires_in": 1209600
}
```

## 3. Use the access token to access the API
The access token allows the client to make requests to the API on behalf of a user. To use the access token, define the following header:

```
Authorization: Bearer ACCESS-TOKEN
```