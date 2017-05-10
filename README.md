# salesforce-oauth
Salesforce oauth authorization example

# Before running 
Create a Salesforce connected app: 

Go to your org --> Setup -->Build-->Create-->Apps-->Connected apps-->New. 

Enable oauth settings.
Callback URL: http://localhost:8080/salesforce-oauth/oauth.xhtml
Selected OAuth Scopes: 
- Access your basic information (id, profile, email, address, phone)
- Access and manage your data (api)
- Perform requests on your behalf at any time (refresh_token, offline_access)

After saving we get the Consumer Key (client id) and Consumer Secret (client secret)

Open the class Credentials and fill the attributes.
