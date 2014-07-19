package iamedu.raml.exception

import org.eel.kitchen.jsonschema.report.ValidationReport
import org.codehaus.groovy.grails.web.json.JSONElement

import grails.converters.JSON

class RamlRequestException extends RuntimeException {
  String method
  String requestUrl
  String body
  Map jsonError
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
    if(validationReport) {
      def error = JSON.parse(validationReport.asJsonNode().toString())
      this.jsonError = error
    }
  }

}
