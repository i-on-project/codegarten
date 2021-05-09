# Resource

Description.

## Properties
* `id` - Unique and stable global identifier of....
    * mandatory
    * non editable, auto-assigned
    * type: **number**
    * example: `1`

## Link Relations
* [self](#get-...)

## Actions
* [List ...](#list-...)
* [Get ...](#get-...)
* [Create ...](#create-...)
* [Edit ...](#edit-...)
* [Delete ...](#delete-...)

------
### Endpoint
Description.

```http
METHOD path
```

#### Parameters
| Name        | Type        | In         | Description                                                                           |
| ----------- | ----------- | ---------- | ------------------------------------------------------------------------------------- |
| accept      | string      | header     | Should be set to either `application/json` or `application/vnd.siren+json`            |
| id          | integer     | path       | The ... unique identifier                                                             |
| page        | integer     | query      | Specifies the current page of the list                                                |
| limit       | integer     | query      | Specifies the number of results per page (max. 100)                                   |

#### Default Response
```
Status: 200 OK
```
```json

```

#### Bad Request
```
Status: 400 Bad Request
```

#### Requires Authentication
```
Status: 401 Unauthorized
```

#### Resource Not Found
```
Status: 404 Not Found
```

------