package iamedu.raml.exception

import org.eel.kitchen.jsonschema.report.ValidationReport

class RamlRequestException extends RuntimeException {
  String method
  String requestUrl
  String body
  ValidationReport validationReport

  RamlRequestException(String message, String requestUrl) {
    super(message)
    this.requestUrl = requestUrl
  }

  RamlRequestException(String message, String requestUrl, String method) {
    this(message, requestUrl)
    this.method = method
  }

  RamlRequestException(String message, String requestUrl, String method, ValidationReport validationReport, String body) {
    this(message, requestUrl)
    this.method = method
    this.body = body
    this.validationReport = validationReport
  }
  RamlRequestException(String message, String requestUrl, String method) {
    this(message, requestUrl)
    this.method = method
  }

}
