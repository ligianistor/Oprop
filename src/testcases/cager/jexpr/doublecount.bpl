type Ref;
CompilationUnits:
  CompilationUnit:
    ClassDeclaration: DoubleCount
      FieldDeclaration: int val
      FieldDeclaration: int dbl
      PredicateDeclaration: OK
        FormalParameters:
        QuantificationExpression: exists
          QuantifierVariables:
            QuantifierVariable: v type: int
            QuantifierVariable: d type: int
          BinaryExpression: boolean
            Operator &&
            BinaryExpression: boolean
              Operator &&
              BinaryExpression: boolean
                Operator ->
                PrimaryExpression (Type: int)
                  class cager.jexpr.ast.KeywordExpression [Type: DoubleCount]
                  FieldSelection (Type: int)
                    ID=val
                PrimaryExpression (Type: int)
                  Identifier: v
              BinaryExpression: boolean
                Operator ->
                PrimaryExpression (Type: int)
                  class cager.jexpr.ast.KeywordExpression [Type: DoubleCount]
                  FieldSelection (Type: int)
                    ID=dbl
                PrimaryExpression (Type: int)
                  Identifier: d
            BinaryExpression: boolean
              Operator ==
              PrimaryExpression (Type: int)
                Identifier: d
              BinaryExpression: int
                Operator *
                PrimaryExpression (Type: int)
                  Literal: 2 (Type: : int)
                PrimaryExpression (Type: int)
                  Identifier: d
      MethodDeclaration: increment(void)
        FormalParameters:
        MethodSpecExpression: 
          MethodSpecVariables:
            MethodSpecVariable: k type: int
          ObjectProposition: 
            PrimaryExpression (Type: DoubleCount)
              class cager.jexpr.ast.KeywordExpression [Type: DoubleCount]
            PrimaryExpression (Type: int)
              Identifier: k
            PrimaryExpression (Type: )
              Identifier: OK
              ArgumentList: 
          ObjectProposition: 
            PrimaryExpression (Type: DoubleCount)
              class cager.jexpr.ast.KeywordExpression [Type: DoubleCount]
            PrimaryExpression (Type: int)
              Identifier: k
            PrimaryExpression (Type: )
              Identifier: OK
              ArgumentList: 
        Block
          StatementExpression
            BinaryExpression: boolean
              Operator =
              PrimaryExpression (Type: int)
                Identifier: val
              BinaryExpression: int
                Operator +
                PrimaryExpression (Type: int)
                  Identifier: val
                PrimaryExpression (Type: int)
                  Literal: 1 (Type: : int)
          StatementExpression
            BinaryExpression: boolean
              Operator =
              PrimaryExpression (Type: int)
                Identifier: dbl
              BinaryExpression: int
                Operator +
                PrimaryExpression (Type: int)
                  Identifier: dbl
                PrimaryExpression (Type: int)
                  Literal: 2 (Type: : int)
