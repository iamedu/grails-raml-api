package iamedu.raml.exception

import org.raml.parser.rule.*

class RamlValidationException extends RuntimeException {

  String ramlLocation
  List<ValidationResult> validationResults

  RamlValidationException(String message, String ramlLocation, List<ValidationResult> validationResults) {
    super(message)
    this.ramlLocation = ramlLocation
    this.validationResults = validationResults
  }

  String toString() {
    "message[${message}] file[${ramlLocation}]"
  }
    
}
