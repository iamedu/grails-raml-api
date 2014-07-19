package iamedu.raml

import iamedu.raml.exception.*
import grails.converters.JSON

class RamlApiController {

  def ramlHandlerService

  def handle() {
    def jsonRequest = request.JSON

    def validator = ramlHandlerService.buildValidator()
    def resource = validator.handleResource(request.forwardURI)

    resource.handleRequest(request)

    log.debug "About to invoke service ${resource.serviceName} method ${request.method}"

    render "Hola mundo"
  }

  def handleRamlRequestException(RamlRequestException ex) {
    response.status = 400
    render ex.jsonError as JSON
  }

  def handleRamlValidationException(RamlValidationException ex) {
    response.status = 500
    render ex.validationResults as JSON
  }

}
