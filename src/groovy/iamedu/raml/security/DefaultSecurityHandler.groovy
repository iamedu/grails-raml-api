package iamedu.raml.security

class DefaultSecurityHandler implements SecurityHandler {

  @Override
  boolean userAuthenticated() {
    true
  }

  @Override
  boolean authorizedExecution(String serviceName, String method) {
    true
  }

}

