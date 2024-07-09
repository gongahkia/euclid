open OUnit2
open Euclid_token
open Euclid_lexer
open Euclid_transpiler

;; helper functions

let run_test input expected ctxt =
  let tokens = tokenize input in
  let transpiled = transpile tokens in
  assert_equal ~ctxt ~printer:(fun x -> x) expected transpiled

;; test cases
    ;; i probably want to add more case variations for each test

let test_constants ctxt =
  run_test "PI" "\\pi" ctxt;
  run_test "E" "e" ctxt;

let test_basic_operations ctxt =
  run_test "+-*/" "+-*/" ctxt;
  run_test "pow(x, y)" "x^y" ctxt;

let test_symbols ctxt =
  run_test "lt(3, 5)" "3 < 5" ctxt;
  run_test "neq(a, b)" "a \\neq b" ctxt;

let test_derivatives ctxt =
  run_test "diff(f(x), x)" "\\frac{d}{dx} f(x)" ctxt;

let suite =
  "Euclid Tests" >::: [
    "Constants" >:: test_constants;
    "Basic Operations" >:: test_basic_operations;
    "Symbols" >:: test_symbols;
    "Derivatives" >:: test_derivatives;
  ]

;; actual running of tests

let () =
  run_test_tt_main suite
