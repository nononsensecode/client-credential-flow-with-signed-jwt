# OAuth 2.0 - Client Credential Flow - Client Authentication using Signed JWT

In this example, I am creating a client credential flow based client. The difference is unlike the traditional client secret, here I am using JWT signed by the client private key. In the authorization server when a request comes from the client, the authorization server will verify the signature of the JWT using client's public key.

There are two ways we can make available the client's public key to the authorization server

- We can upload the client certificate to the authorization server
- Or we can provide a Json Web Key Set (JWKS) url to the authorization server, where the client's public key will be available in the form of JSON Web Key Set.

In this example, I am using the latter one. 

**Note** :
1. For the code to work following things are needed
    - A key store in the form of `JKS` in the `resources` directory with the name `keystore.jks`
    - A `credentials.yaml` file in the `resources` directory in the format of `credentials.yaml.example`
    - Change the values inside the `application.yaml` according to your configuration
2. I used `keycloak` server for this project.    