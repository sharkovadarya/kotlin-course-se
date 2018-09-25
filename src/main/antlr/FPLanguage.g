grammar FPLanguage;

file: block EOF;
block: (statements += statement)*;
bracedBlock: LEFT_BRACE block RIGHT_BRACE;

statement: functionDefinition | variableDeclaration | expression |
           whileStatement | ifStatement | assignment | returnStatement;


functionDefinition: FUN name = IDENTIFIER LEFT_PARENTHESIS parameters = parameterNames RIGHT_PARENTHESIS
                    body = bracedBlock;
parameterNames: ((names += IDENTIFIER COMMA)* names += IDENTIFIER) | ;

variableDeclaration: VAR name = IDENTIFIER (ASSIGN initialValue = expression)?;

expression: name = IDENTIFIER LEFT_PARENTHESIS arguments RIGHT_PARENTHESIS #FunctionCallExpr
            | left = expression op = MUL right = expression #MultiplicationExpr
            | left = expression op = ADD right = expression #AdditionExpr
            | left = expression op = CMP right = expression #ComparisonExpr
            | left = expression op = EQ right = expression #EqualityExpr
            | left = expression op = (OR|AND) right = expression #LogicalExpr
            | op = ADD expression #UnaryExpr
            | IDENTIFIER #IdentifierExpr
            | LITERAL #LiteralExpr
            | LEFT_PARENTHESIS expression RIGHT_PARENTHESIS #ParenthesisedExpr;

arguments: ((args += expression COMMA)* args += expression) | ;

whileStatement: WHILE LEFT_PARENTHESIS condition = expression RIGHT_PARENTHESIS body = bracedBlock;

ifStatement: IF LEFT_PARENTHESIS condition = expression RIGHT_PARENTHESIS thenBody = bracedBlock
             (ELSE elseBody = bracedBlock)?;

assignment: name = IDENTIFIER ASSIGN value = expression;

returnStatement: RETURN value = expression;

IF: 'if';
ELSE: 'else';
WHILE: 'while';
RETURN: 'return';
VAR: 'var';
FUN: 'fun';

fragment NON_DIGIT: ('a'..'z' | 'A'..'Z' | UNDERSCORE);

fragment DIGIT: ('0'..'9');

LITERAL: ('1'..'9') DIGIT* | '0';

OR: '||';
AND: '&&';
EQ: '==' | '!=';
CMP: '<' | '>' | '<=' | '>=';
ADD: '-' | '+';
MUL: '*' | '/' | '%';

IDENTIFIER: NON_DIGIT (LITERAL | NON_DIGIT)*;

COMMENT: '//' ~[\r\n]* -> skip;

WHITESPACE: (' ' | '\t' | '\r'| '\n') -> skip;

UNDERSCORE: '_';
COMMA: ',';
ASSIGN: '=';

LEFT_PARENTHESIS: '(';
RIGHT_PARENTHESIS: ')';
LEFT_BRACE: '{';
RIGHT_BRACE: '}';