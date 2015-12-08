xquery version "1.0-ml";

module namespace domain = "http://xquerrail.com/domain";

declare option xdmp:mapping "false";

declare function domain:log(
  $message as xs:string
) {
  xdmp:log($message)
};

declare function domain:insert(
  $uri as xs:string,
  $content as item()
) as empty-sequence() {
  let $content := 
    if ($content instance of xs:string) then
      text{$content}
    else
      $content
  return 
    if (fn:doc-available($uri)) then
      xdmp:log(text{"Document", $uri, "already exists."},"warning")
    else
      xdmp:document-insert($uri, $content, xdmp:default-permissions())
};