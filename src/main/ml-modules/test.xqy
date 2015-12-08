xquery version "1.0-ml";

module namespace domain = "http://xquerrail.com/domain";

declare option xdmp:mapping "false";

declare function domain:log(
  $message as xs:string
) {
  xdmp:log($message)
};