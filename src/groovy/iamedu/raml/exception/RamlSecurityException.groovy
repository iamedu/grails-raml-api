package iamedu.raml.exception

class RamlSecurityException extends RuntimeException {
  String serviceName
  String method

  RamlSecurityException(String message) {
    super(message)
  }

  RamlSecurityException(String message, String serviceName, String method) {
    this(message)
    this.serviceName = serviceName
    this.method = method
  }

}

