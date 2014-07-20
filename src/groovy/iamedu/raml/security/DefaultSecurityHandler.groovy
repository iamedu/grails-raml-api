package iamedu.raml.security

class DefaultSecurityHandler implements SecurityHandler {

  @Override
  boolean userAuthenticated(Map request) {
    true
  }

  @Override
  boolean authorizedExecution(Map request) {
    true
  }

}

