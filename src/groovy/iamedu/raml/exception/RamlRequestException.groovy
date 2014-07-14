package iamedu.raml.exception

class RamlRequestException extends RuntimeException {
  String method
  String requestUrl

  RamlRequestException(String message, String requestUrl) {
    super(message)
    this.requestUrl = requestUrl
  }

  RamlRequestException(String message, String requestUrl, String method) {
    this(message, requestUrl)
    this.method = method
  }

}
