package iamedu.raml.exception

import org.eel.kitchen.jsonschema.report.ValidationReport
import org.codehaus.groovy.grails.web.json.JSONElement

import grails.converters.JSON

class RamlResponseValidationException extends RuntimeException {
  String method
  String serviceName
  Integer statusCode
  String mimeType
  String body
  ErrorReason reason
  Map jsonError
  ValidationReport validationReport

  enum ErrorReason {
    INVALID_BODY,
    INVALID_STATUS_CODE,
    INVALID_MIME_TYPE
  }

  RamlResponseValidationException(String message, String serviceName, String method, Integer statusCode, String mimeType, String body, ErrorReason reason) {
    super(message)
    this.method = method
    this.serviceName = serviceName
    this.statusCode = statusCode
    this.body = body
    this.reason = reason
  }

  RamlResponseValidationException(String message, String serviceName, String method, Integer statusCode, String mimeType, String body, ErrorReason reason, ValidationReport validationReport) {
    this(message, serviceName, method, statusCode, body, mimeType, reason)
    this.validationReport = validationReport
    if(validationReport) {
      def error = JSON.parse(validationReport.asJsonNode().toString())
      this.jsonError = error
    }
  }

}

