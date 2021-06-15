<div align="center" style="margin-bottom: 30px;">
    <div style="margin-bottom: 30px">
        <img src="./resources/graphics/logo-color.png" height="300px" alt="i-on CodeGarten logo" />
    </div>
    <div>
        <a href="/LICENSE">
            <img src="https://img.shields.io/github/license/i-on-project/codegarten" />
        </a>
        <a href="../../graphs/contributors">
            <img src="https://img.shields.io/github/contributors/i-on-project/codegarten" />
        </a>
        <a href="../../stargazers">
            <img src="https://img.shields.io/github/stars/i-on-project/codegarten" />
        </a>
        <a href="../../issues">
            <img src="https://img.shields.io/github/issues/i-on-project/codegarten" />
        </a>
        <a href="../../pulls">
            <img src="https://img.shields.io/github/issues-pr/i-on-project/codegarten" />
        </a>
    </div>
</div>

# Overview
The i-on initiative seeks to build an extensible platform in order to support academic activities. The i-on CodeGarten project provides a system for both students and teachers that aims to help with the management of GitHub repositories, in an academic context.

# Table Of Contents
- [Functionalities](#functionalities)
- [Components](#components)
- [Getting Started](#getting-started)
    - [Running the server application](#running-the-server-application)
        - [Creating a GitHub App](#creating-a-github-app)
        - [Setting up the secrets directory](#setting-up-the-secrets-directory)
            - [Cipher key](#cipher-key)
            - [GitHub App private key](#github-app-private-key)
            - [GitHub App properties JSON](#github-app-properties-json)
        - [Warning to Windows users](#warning-to-windows-users)
        - [Running with Docker](#running-with-docker)
        - [Building the server application](#building-the-server-application)
        - [Running without Docker](#running-without-docker)
    - [Running the web application](#running-the-web-application)
        - [Registering the client](#registering-the-client)
        - [Setting up the environment variables](#setting-up-the-environment-variables)
        - [Setting up the cipher key](#setting-up-the-cipher-key)
        - [Running with Docker](#running-with-docker-1)
        - [Building the web application](#building-the-server-application)
        - [Running without Docker](#running-without-docker-1)

# Functionalities
i-on CodeGarten exposes the following functionalities:

- Authentication through a GitHub account
- Listing of the user's GitHub Organizations
- Classroom creation and listing in a GitHub Organization
- Student and team management in a classroom
- Assignment management in a classroom
- Deliveries management in an assignment
- Automatic creation of GitHub repositories by joining an assignment
- Student delivery checking by teachers
- Invite support for both classrooms and assignments

# Components
The i-on CodeGarten system is composed by a server application and a web application, which serves as a client for the server app.

The i-on CodeGarten server application is an application that exposes an web API. All i-on CodeGarten clients (web, mobile...) communicate with this server application. It is also here where all the system logic is implemented and where the system information is stored. This application is also responsible for performing all communication with the web API provided by GitHub, through the use of a GitHub App.

The CodeGarten web application uses the API to expose its functionalities to the user in a friendly manner, acting as a client of the server application.

# Getting Started
The following sections document the requires procedures to run the i-on CodeGarten system.

## Running the server application
There's different ways to run the server application, although both of them require the `secrets` directory to be set up (in the case of Docker, in `code/jvm`), as well as the creation of a GitHub App.

### Creating a GitHub App
In order for the server application to be able to make requests to the GitHub API, it needs a GitHub App. To do so, follow the instructions detailed [here](https://docs.github.com/en/developers/apps/building-github-apps/creating-a-github-app). Then, set up these options accordingly:

- Callback URL: This should be set to the `/im/github/authorize/cb` path relative to the server app's domain (e.g. `http://localhost:8080/im/github/authorize/cb`)
- Setup URL: This should be set to the `im/github/install/cb` path relative to the server app's domain (e.g. `http://localhost:8080/im/github/install/cb`)
- Permissions: Please set the following permissions:
    - Repository Permissions
        - Administration (Read & Write)
        - Contents (Read & Write)
        - Metadata (Read-only)
        - Commit Statuses (Read-only)
    - Organization permissions
        - Members (Read & Write)
        - Administration (Read & Write)
- Disable "User-to-server token expiration"

### Setting up the secrets directory
The server application requires some secret information in order to be able to be executed (including running the tests). This information is composed by (the file names are the ones specified in the Dockerfile):

- `cipher-key.txt` - A cipher key that'll be used to cipher sensitive information in the database
- `gh-app-private-key.pem` - The GitHub App's private key, in order to be able to sign JWTs
- `github-app-properties.json` - A GitHub App Properties JSON that specifies information about the Github App

The path for these files can be specified through the use of the following environment variables, respectively:

- `CODEGARTEN_CIPHER_KEY_PATH`
- `CODEGARTEN_GITHUB_APP_PRIVATE_KEY_PATH`
- `CODEGARTEN_GITHUB_APP_PROPERTIES_PATH`

#### Cipher key
The cipher key is specified in a simple text file. It will be padded or cut in order to have exactly 16, 24 or 32 bytes.

#### GitHub App private key
The GitHub App's private key can be obtained from its configuration page. Since it auto-downloads a `.pem` file, it can be easily copied over to the `secrets` directory.

#### GitHub App properties JSON
This file needs to follow the following scheme:
```json
{
    "name": "GitHub App Name",
    "id": 1234,
    "clientId": "The Client ID",
    "clientSecret": "The Client Secret"
}
```
- name: The GitHub App's client name
- id: The GitHub App's "App ID"
- clientId: The GitHub App's "Client ID"
- clientSecret: The GitHub App's "Client Secret"

This information can be obtained through the GitHub App's configuration page. The client secret can be obtained by generating one in that same page.

### Warning to Windows users
Since Windows uses CRLF (carriage-return line-feed) line endings, there may be problems while trying to run and build the server application.

In order to fix these issues, the `Checkout as-is, commit Unix-style line endings` option must be enabled during the Git installation process. This guarantees that every file is converted to LF line endings once committed, and that checkouts don't result in line-ending conversions, keeping them as LF. In case of having Git already installed, the `core.autocrlf` configuration key can be changed as shown below:

```
git config --global core.autocrlf input
```

### Running with Docker

To run the i-on CodeGarten server app locally with Docker, both the Dockerfile and Docker Compose files present in the `code/jvm` directory can be used to start the database and the server app itself. That can be achieved through the execution of the following commands in the `code/jvm` directory:

```
docker-compose -p codegarten up -d 

gradlew extractUberJar -x test
docker build -t codegarten-server .
docker run -e "JDBC_DATABASE_URL=jdbc:postgresql://codegarten-db:5432/db?user=codegarten&password=changeit" -e PORT=8080 -p 8080:8080 --network=codegarten_default -t --name codegarten-server codegarten-server
```

After running these commands, the server application should be available on port `8080` (can be changed through the `PORT` environment variable and port mapping option) and the database on port `5432`. The connection string for the database can also be changed, as seen in the `JDBC_DATABASE_URL` environment variable passed in the last command.

In order to access the API, an access token is required. More info about how to obtain one is available [here](/docs/api/auth/README.md).

To clean up the application and database containers, the following commands can be executed in the `code/jvm` directory:

```
docker rm -f codegarten-server
docker-compose -p codegarten down
```

### Building the server application
The server application will connect to a PostgreSQL database specified with the `JDBC_DATABASE_URL` environment variable. The database URI can also be specified using `DATABASE_URL` environment variable, although it needs to be formatted as specified [here](https://www.postgresql.org/docs/current/libpq-connect.html#LIBPQ-CONNSTRING). The test database also needs to be specified using the `CODEGARTEN_TEST_DB_CONNECTION_STRING` environment variable, as well as the [secret files and their paths](#setting-up-the-secrets-directory)

The execution of this command in the `code/jvm` directory will build the application.

```
gradlew build
```

The above command will attempt to start a test database using Docker on port `5433`. If that's not the desired behavior, the following command can be executed instead.

```
gradlew build -x dbTestsWait -x dbTestsDown
```

### Running without Docker
The previous section explained how to build the server application. Running the server app requires the database to be specified using the environment variables stated [here](#building-the-server-application), as well as the [secret files and their paths](#setting-up-the-secrets-directory). The database must be initialized using the SQL scripts provided [here](../../tree/main/code/sql)

To run the built application, the following command can be executed in the `code/jvm` directory:
```
java -server -jar ./build/libs/codegarten-0.1.jar
```

## Running the web application
The web application can be executed in multiple ways, described in the following sections.

### Registering the client
The web application needs to be a registered client in the server application's database. In order to register it, the CLIENT table needs to have a row dedicated to it. For this, the following information is required:

- Client Name: A name for the client (e.g. `Web App`)
- Client Secret: A secret that will be used by the client when obtaining user access tokens. This must be hashed using the SHA-256 hash function. (e.g. `change me` -> `64ba009cceac4ab8b3c877ae1b6b010e8fc5a15ab567238e3a6107cd4dfe147d`)
- Redirect URI: A URI that identifies an endpoint, which will receive authorization codes via the `code` query parameter, which can be used to obtain access tokens. This URI is part of the authentication procedure, and the user will be redirected to it after the server application obtains their GitHub access token. The web application's redirect URI is located in `/login/cb`, relative to its host (e.g. `http://localhost/login/cb`)

This information can be inserted into the database by executing the following SQL statement:

```sql
-- Replace the values with the intended ones
INSERT INTO CLIENT(name, secret, redirect_uri) VALUES('Web App', '64ba009cceac4ab8b3c877ae1b6b010e8fc5a15ab567238e3a6107cd4dfe147d', 'http://localhost/login/cb');
```

A Client ID will be auto generated by the database management system. The web application needs to know its value.

### Setting up the environment variables
The web application requires the following environment variables to be set:

- `CG_CLIENT_ID`: The Client ID that was auto generated when registering the client (e.g. `1`)
- `CG_CLIENT_SECRET`: The secret that was used when registering the client. This value must **not** be hashed (e.g. `change me`)
- `CG_SERVER_API_HOST`: The server application host (e.g. `http://localhost:8080`)
- `CG_SERVER_IM_HOST`: The server application host, but only used in user redirection. This is required when running the application with Docker. More info [here](#running-with-docker-1) (e.g. `http://localhost:8080`)
- `CG_WEB_CIPHER_KEY_PATH`: The path for the cipher key text file. This is not required when running the web application with Docker. More info [here](#setting-up-the-cipher-key) and [here](#running-with-docker-1) (e.g. `secrets/cipher-key.txt`)

### Setting up the cipher key
The cipher key must be contained in a simple text file. This key must be exactly 32 bytes long, or else it won't work. The path for this key must be specified in the `CG_WEB_CIPHER_KEY_PATH` environment variable, which is already done in advance when running the application with Docker, via its Dockerfile.

If running with Docker, the cipher key file must be:

- Named `cipher-key.txt`
- Placed in the `code/js/secrets` directory

### Running with Docker

To run the i-on CodeGarten web application locally with Docker, the Dockerfile present in the `code/js` directory can be used. However, in contrast with running it without Docker, the `CG_SERVER_IM_HOST` environment variable must be set **if** the server application is also running locally via Docker. This is required due to the web application needing to connect to the API via a Docker hostname and redirecting the app users to the server app, without using the Docker hostname. Below is the recommended values for these variables if running both apps locally via Docker:

- `CG_SERVER_API_HOST`: `http://codegarten-server:8080`
- `CG_SERVER_IM_HOST`: `http://localhost:8080`


Execution of the web application via Docker can be achieved through the execution of one of the following command list, both of them in the `code/js` directory:

1 - If running the API locally with Docker (execution of the commands specified [here](#running-with-docker) is required first)
```
npm install
npm run tsc
docker build -t codegarten-web .
docker run -e PORT=80 -p 80:80 --network=codegarten_default -t --name codegarten-web codegarten-web
```

2 - If running the API locally without Docker or remotely
```
npm install
npm run tsc
docker build -t codegarten-web .
docker run -e PORT=80 -p 80:80 -t --name codegarten-web codegarten-web
```

To cleanup the web application's container:
```
docker rm -f codegarten-web
```
NOTE: If doing a complete cleanup (server application and web application), **execute the above command first** and then the [server application's cleanup commands](#running-with-docker)

### Building the web application
Before being executed, the web application needs to undergo a transpilation process, which will convert the TypeScript code into JavaScript code. In order to do this, the following commands must be executed in the `code/js` directory:

```
npm install
npm run tsc
```

### Running without Docker
The previous section explained how to build the web application. Running the web app requires the environment variables stated [here](#setting-up-the-environment-variables) to be set accordingly, as well as the [cipher key](#setting-up-the-cipher-key). The server application must be running in order for the web application to function.

To run the built application, the following command can be executed in the `code/js` directory:
```
node dist/index.js
```
