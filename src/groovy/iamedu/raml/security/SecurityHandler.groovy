package iamedu.raml.security

interface SecurityHandler {
  boolean userAuthenticated(Map request)
  boolean authorizedExecution(Map request)
}
