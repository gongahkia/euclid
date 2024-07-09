open token

let tokenize input =
  let re = Str.regexp {|[ \t\n\r]+|} in
  let tokens = ref [] in
  let add_token tok = tokens := tok :: !tokens in
  let i = ref 0 in
  while !i < String.length input do
    let c = input.[!i] in
    if Str.string_match re input !i then
      i := Str.match_end ()
    else begin
      match c with
      | 'P' when String.sub input !i 2 = "PI" -> add_token PI; i := !i + 2
      | 'E' -> add_token E; incr i
      | 'I' -> add_token I; incr i
      (* Handle other constants and symbols similarly *)
      | _ -> add_token (TEXT (String.make 1 c)); incr i
    end
  done;
  add_token EOF;
  List.rev !tokens
