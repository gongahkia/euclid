open token

let rec transpile tokens =
  match tokens with
  | [] -> ""
  | TEXT t :: rest -> t ^ transpile rest
  | PI :: rest -> "\\pi" ^ transpile rest
  | E :: rest -> "e" ^ transpile rest
  | EOF :: _ -> ""

let process_file filename =
  let input = really_input_string (open_in filename) (in_channel_length (open_in filename)) in
  let tokens = Euclid_lexer.tokenize input in
  transpile tokens

let () =
  let filename = Sys.argv.(1) in
  let output = process_file filename in
  print_string output
