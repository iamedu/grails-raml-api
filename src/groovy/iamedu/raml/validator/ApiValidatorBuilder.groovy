package iamedu.raml.validator

import iamedu.raml.exception.*

import org.raml.model.*
import org.raml.parser.visitor.*
import org.raml.parser.loader.*

class ApiValidatorBuilder {

  Raml raml
  RamlDocumentBuilder documentBuilder
  ResourceLoader resourceLoader

  ApiValidatorBuilder() {
    this(new GrailsResourceLoader())
  }

  ApiValidatorBuilder(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader
    documentBuilder = new RamlDocumentBuilder(resourceLoader)
  }

  ApiValidatorBuilder setRamlLocation(String ramlLocation) {
    validateRaml(ramlLocation)
    raml = documentBuilder.build(ramlLocation)
    this
  }

  private def validateRaml(String ramlLocation) {
    def validationService = RamlValidationService.createDefault(resourceLoader)
    def results = validationService.validate(ramlLocation)
    if(results.size() > 0) {
      println results
      throw new RamlValidationException("Invalid raml file", ramlLocation, results)
    }
  }

  ApiValidator build() {
    new ApiValidator(raml, resourceLoader)
  }
}

