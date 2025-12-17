type token =
  | TEXT of string
  | PI
  | E
  | I
  | GAMMA
  | PHI
  | INFINITY
  | ALPHA
  | BETA
  | DELTA
  | EPSILON
  | ZETA
  | ETA
  | THETA
  | KAPPA
  | LAMBDA
  | MU
  | NU
  | XI
  | OMICRON
  | RHO
  | SIGMA
  | TAU
  | UPSILON
  | CHI
  | PSI
  | OMEGA
  | LT of (string * string)
  | GT of (string * string)
  | LEQ of (string * string)
  | GEQ of (string * string)
  | APPROX of (string * string)
  | NEQ of (string * string)
  | EQUIV of (string * string)
  | PM of (string * string)
  | TIMES of (string * string)
  | DIV of (string * string)
  | CDOT of (string * string)
  | AST of (string * string)
  | STAR of (string * string)
  | CIRC of (string * string)
  | BULLET of (string * string)
  | CAP of (string * string)
  | CUP of (string * string)
  | EOF
