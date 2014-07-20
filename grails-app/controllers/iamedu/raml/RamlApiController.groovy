package iamedu.raml

import iamedu.raml.exception.handlers.*
import iamedu.raml.exception.*

import grails.converters.JSON

class RamlApiController {

  def grailsApplication
  RamlHandlerService ramlHandlerService
  RamlValidationExceptionHandler ramlValidationExceptionHandler
  RamlRequestExceptionHandler ramlRequestExceptionHandler

  def handle() {
    def jsonRequest = request.JSON

    def validator = ramlHandlerService.buildValidator()
    def endpointValidator = validator.handleResource(request.forwardURI)

    def request = endpointValidator.handleRequest(request)
    def service = grailsApplication.mainContext.getBean(request.serviceName)

    log.debug "About to invoke service ${request.serviceName} method $request.method}"

    render "Hola mundo"
  }

  def handleRamlRequestException(RamlRequestException ex) {
    response.status = 400

    def errorResponse = ramlRequestExceptionHandler.handleRequestException(ex)

    render errorResponse as JSON
  }

  def handleRamlValidationException(RamlValidationException ex) {
    response.status = 500

    log.error "Invalid raml definition", ex
    def errorResponse = ramlValidationExceptionHandler.handleValidationException(ex)

    render errorResponse as JSON
  }

}
