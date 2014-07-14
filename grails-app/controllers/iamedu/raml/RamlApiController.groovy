package iamedu.raml

class RamlApiController {

  def ramlHandlerService

  def handle() {
    def jsonRequest = request.JSON

    def validator = ramlHandlerService.buildValidator()
    def resource = validator.handleResource(request.forwardURI)

    println resource

    render "Hola mundo"
  }
}
