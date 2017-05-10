# Salesforce oauth
Salesforce oauth authorization example using a **Java** webapp (JSF)

Shows how to authorize a Salesforce connected app to access to an org and retrive some data.
It just displays a home page with a link that redirects to a page with some org info (after doing the authorization process)

## Before running 
Create a Salesforce connected app: 

Go to your org --> Setup -->Build-->Create-->Apps-->Connected apps-->New. 

Enable oauth settings.
Callback URL: http://localhost:8080/salesforce-oauth/oauth.xhtml
Selected OAuth Scopes: 
- Access your basic information (id, profile, email, address, phone)
- Access and manage your data (api)
- Perform requests on your behalf at any time (refresh_token, offline_access)

After saving we get the **Consumer Key** (client id) and **Consumer Secret** (client secret)

Open the class Credentials and fill the attributes.

## Run

It's a **Maven** webapp, so clone or download the source code and open with your favorite IDE (I used Netbeans 8.2). Deploy the webapp in a servlet container such as **Tomcat** and open this URL http://localhost:8080/salesforce-oauth/ in your browser

## Contribute

Feel free to send pull requests
