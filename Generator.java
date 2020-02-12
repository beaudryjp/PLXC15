public class Generator{

    public static final boolean DEBUG   = false;
    public static final String ONE      = "1";
    public static final int LT          = 1;
    public static final int LTOREQ      = 2;
    public static final int GT          = 3;
    public static final int GTOREQ      = 4;
    public static final int EQ          = 5;
    public static final int NOTEQ       = 6;
    public static final int AND         = 7;
    public static final int OR          = 8;
    public static final int NOT         = 9;
    public static final int PLUS        = 10;
    public static final int MINUS       = 11;
    public static final int MUL         = 12;
    public static final int DIV         = 13;
    public static final int MIN         = 14;
    public static final int MOD         = 15;
    public static final int INCR        = 16;
    public static final int DECR        = 17;
    public static final int INCL        = 18;
    public static final int DECL        = 19;
    public static int numTempVars       = -1;
    public static int numLabels         = -1;
    public static int arrPosition       = -1;
    public static String arrTemp        = "";
    
    /* Error controling */
    public static void error(String message, boolean exit){
        if(message.isEmpty())
            System.out.println("\terror;\n\thalt;");
        else
            System.out.println("\t" + message + "\n\terror;\n\thalt;");
        if(exit) 
            System.exit(0);
    }

    public static void varNotDeclared(String variable){
        error("# Variable '" + variable + "' not declared.", true);
    }

    public static void varDeclared(String variable){
        error("# Variable '" + variable + "' has been declared already.", true);
    }

    /* New temporal variables or labels */
    /* Create a new temporal variable */
    public static String newTempVar(){
        numTempVars++;
        return "t" + numTempVars;
    }

    /* Create a new label */
    public static String newLabel(){
        numLabels++;
        return "L" + numLabels;
    }

    /* Array methods */
    /* Return the temporal variable for the array */
    public static String getArrTemp(){
        return arrTemp;
    }

    /* Return the next position for the array */
    public static String nextArrPosition(){
        arrPosition++;
        return arrTemp + "[" + arrPosition + "]";
    }

    /* Return the current position of the array */
    public static String getArrPosition(){
        return Integer.toString(arrPosition);
    }

    /* Return the string [position] */
    public static String arrIndex(String position){
        return "[" + position + "]";
    }

    /* Range check */
    public static String rangeCheck(String var, Tag exp){
        int sizeArray = SymbolTable.sizeArray(var);
        String currentPosition = exp.getT();
        Tag result = new Tag(newLabel(), newLabel());
        System.out.println("# Range check");
        System.out.println("\tif (" + currentPosition + " < 0) goto " + result.getT() + ";");
        System.out.println("\tif (" + sizeArray + " < " + currentPosition + ") goto " + result.getT() + ";");
        System.out.println("\tif (" + sizeArray + " == " + currentPosition + ") goto " + result.getT() + ";");
        gotoLabel(result.getF());
        label(result.getT());
        error("# ERROR: Incorrect access to position " + var + "[" + currentPosition + "].", false);
        //error("", false);
        label(result.getF());
        return arrIndex(currentPosition);
    }

    /* Array initialization */
    public static void initArrTemp(String name){
        arrTemp = name;
    }
      
    /* Reset array variables */
    public static void resetArray(){
        arrPosition = -1;
        arrTemp = "";
    }

    /* Print array length */
    public static void arrLength(String var, String number){
        System.out.println("\t" + var + "_length = " + number.substring(1, number.length() - 1) + ";");
    }

    /* Labels */
    /* Print label */
    public static void label(String label){
        System.out.println(label + ":");
    }

    /* Print goto label */
    public static void gotoLabel(String label){
        System.out.println("\tgoto " + label + ";");
    }

    /* Prints */
    public static String print(Tag exp){
        String t = newTempVar();
        assignment(t, exp.getT());
        System.out.println("\tprint " + t + ";");
        return t;
    }

    /* Comments */
    public static void comment(String msg) {
        System.out.println("\t# " + msg);
    }

    /* Assignments */
    public static String assignment(String var, String exp){
        System.out.println("\t" + var + " = " + exp + ";");
        return var;
    }

    /* For In loop */
    public static Tag forInLoop(Tag exp, Tag array){
        String t0 = newTempVar();
        String temp = newTempVar();
        String increment = newLabel();
        /** True label */
        String t = newLabel();
        /** False label */
        String f = newLabel();
        int size = 0;

        System.out.println("\t" + t0 + " = -1;");
        label(increment);
        System.out.println("\t" + t0 + " = " + t0 + " + 1;");
        if(!getArrTemp().isEmpty()){
            size = arrPosition + 1;
            resetArray();
        }
        else{
            size = SymbolTable.sizeArray(array.getT());
        }
        System.out.println("\tif (" + t0 + " < " + size + ") goto " + t + ";");
        System.out.println("\tgoto " + f + ";");
        label(t);
        assignment(temp, array.getT() + arrIndex(t0));
        assignment(exp.getT(), temp);
        /*
        gotoLabel(increment);
        label(f);
        */
        return new Tag(increment, f);
    }

    /* Type initialization */
    public static void initTypes(String var, Tag exp, String typeA, String typeB){
        /*  typeA, typeB = INT
            typeA, typeB = FLOAT
        */
        if(typeA.equals(typeB) && !typeA.contains(Type.ARRAY)){
            assignment(var, exp.getT());
        }
        /*  typeA = FLOAT
            typeB = INT
        */
        else if(typeA.equals(Type.FLOAT) && typeB.equals(Type.INT)){
            assignment(var, "(float) " + exp.getT());
        }
        /*  typeA = INT ARRAY
            typeB = INT
        */
        else if(typeA.contains(Type.ARRAY) && typeA.contains(Type.INT) && typeB.equals(Type.INT)){
            assignment(var, exp.getT());
        }
        /*  typeA = FLOAT ARRAY
            typeB = INT
        */
        else if(typeA.contains(Type.ARRAY) && typeA.contains(Type.FLOAT) && typeB.equals(Type.INT)){
            String t0 = newTempVar();
            String t1 = newTempVar();
            assignment(t0, exp.getT());
            assignment(t1, "(float) " + t0);
            assignment(var, t1);
        }
        /*  typeA = FLOAT ARRAY
            typeB = FLOAT
        */
        else if(typeA.contains(Type.ARRAY) && typeA.contains(Type.FLOAT) && typeB.equals(Type.FLOAT)){
            assignment(var, exp.getT());
        }
        /*  typeA = FLOAT ARRAY
            typeB = FLOAT ARRAY
        */
        else if(typeA.contains(Type.ARRAY) && typeA.contains(Type.FLOAT) && typeB.contains(Type.ARRAY) && typeB.contains(Type.FLOAT)){
            String t0 = newTempVar();
            assignment(t0, exp.getT());
            assignment(var, t0);
        }
        /*  typeA = FLOAT ARRAY
            typeB = INT ARRAY
        */
        else if(typeA.contains(Type.ARRAY) && typeA.contains(Type.FLOAT) && typeB.contains(Type.ARRAY) && typeB.equals(Type.INT)){
            String t0 = newTempVar();
            String t1 = newTempVar();
            assignment(t0, exp.getT());
            assignment(t1, "(float) " + t0);
            assignment(var, t1);
        }
        /*  typeA = INT ARRAY
            typeB = INT ARRAY
        */
        else if(typeA.contains(Type.ARRAY) && typeA.contains(Type.INT) && typeB.contains(Type.ARRAY) && typeB.contains(Type.INT)){
            String t0 = newTempVar();
            assignment(t0, exp.getT());
            assignment(var, t0);
        }
        /*  typeA = INT
            typeB = INT ARRAY
        */
        else if(typeA.equals(Type.INT) && typeB.contains(Type.ARRAY) && typeB.contains(Type.INT)){
            String t0 = newTempVar();
            assignment(t0, exp.getT());
            assignment(var, t0);
        }
        /*  typeA = FLOAT
            typeB = FLOAT ARRAY
        */
        else if(typeA.equals(Type.FLOAT) && typeB.contains(Type.ARRAY) && typeB.contains(Type.FLOAT)){
            String t0 = newTempVar();
            assignment(t0, exp.getT());
            assignment(var, t0);
        }
        /*  typeA = FLOAT
            typeB = INT ARRAY
        */
        else if(typeA.equals(Type.FLOAT) && typeB.contains(Type.ARRAY) && typeB.contains(Type.FLOAT)){
            String t0 = newTempVar();
            assignment(t0, exp.getT());
            assignment(var, "(float) " + t0);
        }
        /** Errors */
        /*  typeA = INT
            typeB = FLOAT
        */
        else if(typeA.equals(Type.INT) && typeB.equals(Type.FLOAT)){
            error("# ERROR: Variable '" + var + "' is of type INT, not FLOAT.", true);
        }
        /*  typeA = INT ARRAY
            typeB = FLOAT or FLOAT ARRAY
        */
        else if(typeA.contains(Type.ARRAY) && typeA.contains(Type.INT) && typeB.contains(Type.FLOAT)){
            error("# ERROR: Array '" + var + "' is of type INT, not FLOAT.", true);
        }
        
    }

    /* Casting 
        - op: print different outputs depending on the case
        - type: type of the expression
        - exp1, exp2: tuples of expressions
        - a: used only for casting to output the operator +, -, *, /
    */
    public static Tag cast(int op, String type, Tag exp1, Tag exp2, String a){
        String tmp = newTempVar();
        Tag result = new Tag(tmp, type);
        switch(op){
            case MIN:
                if(type.equals(Type.INT) && !exp1.getF().equals(Type.INT))
                    assignment(tmp, "(int) " + exp1.getT());
                    //System.out.println("\t" + tmp + " = (int) " + exp1.getT() + ";");
                else if(type.equals(Type.FLOAT) && !exp1.getF().equals(Type.FLOAT))
                    assignment(tmp, "(float) " + exp1.getT());
                    //System.out.println("\t" + tmp + " = (float) " + exp1.getT() + ";");
                break;
            case MUL:
            case DIV:
                String t0 = newTempVar();
                String t1 = newTempVar();
                System.out.println("\t" + t0 + " = (" + type + ") " + exp1.getT() + ";");
                System.out.println("\t" + t1 + " = (" + type + ") " + exp2.getT() + ";");
                System.out.println("\t" + tmp + " = " + t0 + " " + a + "r " + t1 + ";");
                result.setF("checked");
                break;
        }
        
        return result;
    }

    /* Increments */
    public static Tag increment(int op, String var, String step){
        Tag temp = new Tag(newTempVar(), SymbolTable.get(var));
        switch(op){
            case INCL:
                System.out.println("\t" + var + " = " + var + " + " + step + ";");
                temp.setT(var);
                break;
            case DECL:
                System.out.println("\t" + var + " = " + var + " - " + step + ";");
                temp.setT(var);
                break;
            case INCR:
                Generator.assignment(temp.getT(), var);
                System.out.println("\t" + var + " = " + var + " + " + step + ";");
                break;
            case DECR:
                Generator.assignment(temp.getT(), var);
                System.out.println("\t" + var + " = " + var + " - " + step + ";");
                break;   
            default:
                error("Error: code generation failed with arguments: \toperation:" + op + "\tvar: " + var + "\ttemp: " + temp, true);
                break;
        }
        return temp;
    }

    /* Arithmetic operations */
    public static Tag arithmetic(int t, String op, Tag exp1, Tag exp2){
        Tag result = null;
        /*
        String temp = newTempVar(); 
        String type = "";
        String opr = "";
        String temp1 = exp1.getT() + "";
        String temp2 = "";
        if (exp2 != null){
            temp2 = exp2.getT() + "";
        }
        if (exp1.getF().equals(Type.FLOAT) && exp2.getF().equals(Type.FLOAT)) {
            type = "float";
            opr = "r ";
        }
        else if (exp1.getF().equals(Type.INT) && exp2.getF().equals(Type.INT)) {
            type = "int";
        }
        else if (exp1.getF().equals(Type.FLOAT) && exp2.getF().equals(Type.INT)) {
            type = "float";
            opr = "r ";
            temp2 = newTempVar(); 
            System.out.println("\t" + temp2 + " = (float) " + exp2.getT() + ";");
        }
        else if (exp1.getF().equals(Type.INT) && exp2.getF().equals(Type.FLOAT)) {
            type = "float";
            opr = "r ";
            temp1 = newTempVar(); 
            System.out.println("\t" + temp1 + " = (float) " + exp1.getT() + ";");
        }    
        switch(op){
            case PLUS:
                System.out.println("\t" + temp + " = " + temp1 + " +" + opr + " " + temp2 + ";");
                break;
            case MINUS:
                System.out.println("\t" + temp + " = " + temp1 + " -" + opr + " " + temp2 + ";");
                break;
            case MUL:
                System.out.println("\t" + temp + " = " + temp1 + " *" + opr + " " + temp2 + ";");
                break;
            case DIV:
                System.out.println("\t" + temp + " = " + temp1 + " /" + opr + " " + temp2 + ";");
                break;
            case MIN:
                System.out.println("\t" + temp + " = -" + temp1 + ";");
                break;
            case MOD:
                String t0 = newTempVar();
                String t1 = newTempVar();
                System.out.println("\t" + t0 + " = " + temp1 + " /" + opr + " " + temp2 + ";");
                System.out.println("\t" + t1 + " = " + t0 + " *" + opr + " " + temp2 + ";");
                System.out.println("\t" + temp + " = " + temp1 + " -" + opr + " " + t1 + ";");
                break;
            default:
                error("Error: code generation failed with arguments: \toperation:" + op + "\tnumber 1: " + exp1 + "\tnumber 2: " + exp2, true);
                break;
        }
        return new Tag(temp, type);
        */
        String t0 = newTempVar(); 
        String type = "";
        switch(t){
            case PLUS:
            case MINUS:
            case MUL:
            case DIV:
                /** exp1 = FLOAT or exp2 = FLOAT */
                if(exp1.getF().equals(Type.FLOAT) || exp2.getF().equals(Type.FLOAT)){
                    /** Casting
                     *  exp1 = INT
                     *  exp2 = FLOAT
                     */
                    if(exp1.getF().contains(Type.INT)){
                        String temp = newTempVar();
                        String firstOp = newTempVar();
                        assignment(firstOp, exp1.getT());
                        assignment(temp, "(float) " + firstOp);
                        System.out.println("\t" + t0 + " = " + temp + " " + op + "r "  + exp2.getT() + ";");
                    }
                    /** Casting
                     *  exp1 = FLOAT
                     *  exp2 = INT
                     */
                    else if(exp2.getF().contains(Type.INT)){
                        String temp = newTempVar();
                        String secondOp = newTempVar();
                        assignment(secondOp, exp2.getT());
                        assignment(temp, "(float) " + secondOp);
                        System.out.println("\t" + t0 + " = " + exp1.getT() + " " + op + "r "  + temp + ";");
                    }
                    /**
                     *  exp1 = FLOAT
                     *  exp2 = FLOAT
                     */
                    else if(!exp1.getF().contains(Type.ARRAY) && !exp2.getF().contains(Type.ARRAY)){
                        System.out.println("\t" + t0 + " = " + exp1.getT() + " " + op + "r "  + exp2.getT() + ";");
                    }
                    /**
                     *  exp1 = ARRAY
                     *  exp2 = FLOAT
                     */
                    else if(exp1.getF().contains(Type.ARRAY)){
                        String firstOp = newTempVar();
                        assignment(firstOp, exp1.getT());
                        System.out.println("\t" + t0 + " = " + firstOp + " " + op + "r "  + exp2.getT() + ";");
                    }
                    /**
                     *  exp1 = FLOAT
                     *  exp2 = ARRAY
                     */
                    else{
                        String secondOp = newTempVar();
                        assignment(secondOp, exp2.getT());
                        System.out.println("\t" + t0 + " = " + exp1.getT() + " " + op + "r "  + secondOp + ";");
                    }
                    type = "float";
                }
                /**
                *  exp1 = INT
                *  exp2 = INT
                */
                else if(!exp1.getF().contains(Type.ARRAY) && !exp2.getF().contains(Type.ARRAY)){
                    System.out.println("\t" + t0 + " = " + exp1.getT() + " " + op + " " + exp2.getT() + ";");
                    type = "int";
                }
                else{
                    /**
                    *  exp1 = FLOAT ARRAY
                    */
                    if(exp1.getF().contains(Type.ARRAY) && exp1.getF().contains(Type.FLOAT)){
                        String firstOp = newTempVar();
                        assignment(firstOp, exp1.getT());
                        /**
                        *  exp2 = INT or INT ARRAY
                        */
                        if(exp2.getF().equals(Type.INT) || (exp2.getF().contains(Type.ARRAY) && exp2.getF().contains(Type.INT))){
                            String temp = newTempVar();
                            String cast = newTempVar();
                            assignment(cast, exp2.getT());
                            assignment(temp, "(float) " + cast);
                            System.out.println("\t" + t0 + " = " + firstOp + " " + op + "r "  + temp + ";");
                        }
                        /**
                        *  exp2 = FLOAT or FLOAT ARRAY
                        */
                        else{
                            String secondOp = newTempVar();
                            assignment(secondOp, exp2.getT());
                            System.out.println("\t" + t0 + " = " + firstOp + " " + op + "r "  + secondOp + ";");
                        }
                        type = "float";
                    }
                    /**
                    *  exp2 = FLOAT ARRAY
                    */
                    else if(exp2.getF().contains(Type.ARRAY) && exp2.getF().contains(Type.FLOAT)){
                        String secondOp = newTempVar();
                        assignment(secondOp, exp2.getT());
                        /**
                        *  exp1 = INT or INT ARRAY
                        */
                        if(exp1.getF().equals(Type.INT) || (exp1.getF().contains(Type.ARRAY) && exp1.getF().contains(Type.INT))){
                            String temp = newTempVar();
                            String cast = newTempVar();
                            assignment(cast, exp1.getT());
                            assignment(temp, "(float) " + cast);
                            System.out.println("\t" + t0 + " = " + temp + " " + op + "r "  + secondOp + ";");
                        }
                        /**
                        *  exp1 = FLOAT or FLOAT ARRAY
                        */
                        else{
                            String firstOp = newTempVar();
                            assignment(firstOp, exp1.getT());
                            System.out.println("\t" + t0 + " = " + firstOp + " " + op + "r "  + secondOp + ";");
                        }
                        type = "float";
                    }
                    /**
                    *  exp1 = INT ARRAY
                    *  exp2 = INT ARRAY
                    */
                    else{
                        String firstOp = newTempVar();
                        String secondOp = newTempVar();
                        assignment(firstOp, exp1.getT());
                        assignment(secondOp, exp2.getT());
                        System.out.println("\t" + t0 + " = " + firstOp + " " + op + " " + secondOp + ";");
                        type = "int";
                    }
                }
                result = new Tag(t0, type);
                break;
            case MIN:
                System.out.println("\t" + t0 + " = -" + exp1.getT() + ";");
                result = new Tag(t0, exp1.getF());
                break;
            case MOD:
                String temp1 = newTempVar();
                String temp2 = newTempVar();
                System.out.println("\t" + t0 + " = " + exp1.getT() + " / " + exp2.getT() + ";");
                System.out.println("\t" + temp1 + " = " + t0 + " * " + exp2.getT() + ";");
                System.out.println("\t" + temp2 + " = " + exp1.getT() + " - " + temp1 + ";");
                result = new Tag(temp2, "int");
                break;
            default:
                error("Error: code generation failed with arguments: \toperation:" + op + "\tnumber 1: " + exp1 + "\tnumber 2: " + exp2, true);
                break;
        }
        
        return result;
    }

    /* Logical conditions EQ, NOTEQ, LT, LTOREQ, GT, GTOREQ */
    public static Tag condition(int cond, Tag arg1, Tag arg2){
        Tag result = new Tag(newLabel(), newLabel());
        switch(cond){
            case EQ: 
                System.out.println("\tif (" + arg1.getT() + " == " + arg2.getT() + ") goto " + result.getT() + ";");
                System.out.println("\tgoto " + result.getF() + ";");
                break;
            case NOTEQ:
                System.out.println("\tif (" + arg1.getT() + " == " + arg2.getT() + ") goto " + result.getF() + ";");
                System.out.println("\tgoto " + result.getT() + ";");
                break;
            case LT:
                System.out.println("\tif (" + arg1.getT() + " < " + arg2.getT() + ") goto " + result.getT() + ";");
                System.out.println("\tgoto " + result.getF() + ";");
                break;
            case LTOREQ:
                System.out.println("\tif (" + arg2.getT() + " < " + arg1.getT() + ") goto " + result.getF() + ";");
                System.out.println("\tgoto " + result.getT() + ";");
                break;
            case GT:
                System.out.println("\tif (" + arg2.getT() + " < " + arg1.getT() + ") goto " + result.getT() + ";");
                System.out.println("\tgoto " + result.getF() + ";");
                break;
            case GTOREQ:
                System.out.println("\tif (" + arg1.getT() + " < " + arg2.getT() + ") goto " + result.getF() + ";");
                System.out.println("\tgoto " + result.getT() + ";");
                break;
            default:
                error("Error: code generation failed with arguments: \tcondition:" + cond + "\targ1: " + arg1 + "\targ2: " + arg2, true);
                break;
        }
        return result;
    }

    /* Logical operators NOT, AND, OR */
    public static Tag operator(int cond, Tag c1, Tag c2){
        Tag result = c2;
        switch(cond){
            case NOT:
                result = new Tag(c1.getF(), c1.getT());
                break;
            case AND:
                label(c1.getF()); 
                gotoLabel(c2.getT());
                break;
            case OR:
                label(c1.getT()); 
                gotoLabel(c2.getF());
                break;
            default:
                break;
        }
        return result;
    }

}