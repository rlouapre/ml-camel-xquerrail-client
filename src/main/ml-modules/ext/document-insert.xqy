xquery version "1.0-ml";

declare namespace domain = "http://xquerrail.com/domain";

declare option xdmp:mapping "false";

declare variable $domain:URI as xs:string external := "";
declare variable $domain:CONTENT external := ();

if (fn:doc-available($domain:URI)) then
  xdmp:log(text{"Document", $domain:URI, "already exists."}, "warning")
else
  xdmp:document-insert($domain:URI, $domain:CONTENT, xdmp:default-permissions())
