package iamedu.raml

import iamedu.raml.validator.*
import grails.transaction.Transactional
import grails.util.Holders

@Transactional
class RamlHandlerService {

  def config = Holders.config
  def validator

  def doBuildValidator(def ramlDefinition) {
    if(!ramlDefinition) {
      throw new RuntimeException("Raml definition is not set")
    }
    def builder = new ApiValidatorBuilder()
    builder.setRamlLocation('raml/' + ramlDefinition)

    builder.build()
  }

  def buildValidator() {
    def ramlDefinition = config.iamedu.raml.ramlDefinition
    def reloadRaml = config.iamedu.raml.reload

    if(!validator || reloadRaml) {
      validator = doBuildValidator(ramlDefinition)
    }

    validator
  }
}
