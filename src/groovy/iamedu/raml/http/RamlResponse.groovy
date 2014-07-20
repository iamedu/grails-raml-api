package iamedu.raml.http

class RamlResponse {
  Integer statusCode
  Map headers
  String contentType
  Object body

  private RamlResponse() {
    headers = new HashMap()
  }

  static RamlResponse create() {
    new RamlResponse().ok().json()
  }

  RamlResponse statusCode(int statusCode) {
    this.statusCode = statusCode
    this
  }

  RamlResponse ok() {
    statusCode = 200
    this
  }

  RamlResponse contentType(String ct) {
    contentType = ct
    this
  }

  RamlResponse json() {
    contentType = "application/json; charset=utf-8"
    this
  }

  RamlResponse body(Object data) {
    this.body = data
    this
  }

}


