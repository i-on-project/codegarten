# Installations

The GitHub App associated with the server application needs to be installed in organizations in order to function. Below are the steps to install the GitHub App in an organization:

## 1. Access GitHub's installation form

In order to select which organization to install the app to, it's required to access the GitHub App's installation form.

```http
GET /im/github/install
```

## 2. App installed

After the installation is completed, the browser's window/tab is closed automatically if step 1 was performed in a new window/tab opened by the client.