OCAMLC = ocamlc
OCAMLDEP = ocamldep
TOKEN_SRC = src/token.ml
LEXER_SRC = src/lexer.ml
TRANSPILER_SRC = src/transpiler.ml
REPL_SRC = src/repl.ml

all: euclid_transpiler euclid_repl

euclid_transpiler: $(TOKEN_SRC:.ml=.cmo) $(LEXER_SRC:.ml=.cmo) $(TRANSPILER_SRC:.ml=.cmo)
	$(OCAMLC) -o $@ $^

euclid_repl: $(TOKEN_SRC:.ml=.cmo) $(LEXER_SRC:.cmo) $(REPL_SRC:.ml=.cmo)
	$(OCAMLC) -o $@ $^

%.cmo: %.ml
	$(OCAMLC) -c $<

depend:
	$(OCAMLDEP) $(TOKEN_SRC) $(LEXER_SRC) $(TRANSPILER_SRC) $(REPL_SRC) > .depend

-include .depend

clean:
	rm -f *.cmo *.cmi euclid_transpiler euclid_repl .depend

run_transpiler: euclid_transpiler
	./euclid_transpiler input.ed > output.md

run_repl: euclid_repl
	./euclid_repl

.PHONY: all depend clean run_transpiler run_repl
