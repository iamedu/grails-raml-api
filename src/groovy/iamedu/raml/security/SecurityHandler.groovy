package iamedu.raml.security

interface SecurityHandler {
  boolean userAuthenticated()
  boolean authorizedExecution(String serviceName, String method)
}
