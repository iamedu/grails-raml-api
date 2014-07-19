package iamedu.raml.exception.handlers

class RamlErrorResponse implements Serializable {
  String errorCode
  String message
  Map errorMeta

  @Override
  String toString() {
    message
  }

}
