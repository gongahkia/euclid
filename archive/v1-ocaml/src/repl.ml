open token
open lexer

let rec repl () =
  print_string ">>> ";
  flush stdout;
  let input = read_line () in
  let tokens = tokenize input in
  let transpiled = transpile tokens in
  print_endline ("Transpiled to Markdown:");
  print_endline transpiled;
  print_newline ();
  repl ()

let () = repl ()
