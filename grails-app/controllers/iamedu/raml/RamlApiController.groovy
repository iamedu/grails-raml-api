package iamedu.raml


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
}
