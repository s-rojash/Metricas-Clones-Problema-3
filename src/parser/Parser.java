package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class Parser {

	private final Set<String> operators = new HashSet<>(Arrays.asList("+", "-", "*", "/"));
    public ArrayList<Object> parseExpression(String expression) {
        String[] tokens = tokenize(expression);
        ArrayList<Object> value = new ArrayList<>();
        try {
            value = readFromTokens(tokens);
        } catch (Exception e) {
            value.add("ERROR");
            e.printStackTrace();
        }
        return value;
    }

    public String[] tokenize(String expression) {
        return expression.replace("(", "( ").replace(")", " )").split(" ");
    }

    public Float atomize(String token) {
        return Float.parseFloat(token);
    }

    public ArrayList<Object> readFromTokens(String[] tokens) throws Exception {
        if (tokens.length == 0)
            throw new Exception("SYNTAX ERROR - Unexpected end of expression");

        String token = tokens[0];
        tokens = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (token.equals("(")) {
            ArrayList<Object> nestedExpression = new ArrayList<>();
            while (tokens.length > 0 && !tokens[0].equals(")")) {
                ArrayList<Object> parsedNested = readFromTokens(tokens);
                if (parsedNested.size() == 1) {
                    nestedExpression.add(parsedNested.get(0));
                    tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
                } else {
                    nestedExpression.add(parsedNested);
                    tokens = Arrays.copyOfRange(tokens, parsedNested.size() + 2, tokens.length);
                }
            }
            if (tokens.length == 0)
                throw new Exception("SYNTAX ERROR - Unexpected end of expression");
            else if (nestedExpression.size() < 3)
                throw new Exception("SYNTAX ERROR - Invalid Expression");
            else
                tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
            return nestedExpression;
        } else if (token.equals(")"))
            throw new Exception("SYNTAX ERROR - Unexpected closing parenthesis");
        else {
            ArrayList<Object> res = new ArrayList<>();
            res.add(token);
            return res;
        }
    }


    public float add(float[] args) {
        return Arrays.stream(args).sum(); // Aqui
    }

    public float multiply(float[] args) {
        return Arrays.stream(args).reduce(1, (a, b) -> a * b); // Aqui
    }

    public float subtract(float[] args) {
        float[] tail = Arrays.copyOfRange(args, 1, args.length);
        return args[0] - add(tail);
    }

    public float divide(float[] args) {
        float[] tail = Arrays.copyOfRange(args, 1, args.length);
        return args[0] / multiply(tail);
    }

    public float evaluate(ArrayList<Object> expression) throws Exception {
        String operator = (String) expression.get(0);
        if (!operators.contains(operator))
            throw new Exception("SYNTAX ERROR - Unknown operator " + operator);

        float[] operands = expression.stream().skip(1).mapToFloat(this::evaluateElement).toArray(); // Aqui

        switch (operator) {
            case "+":
                return add(operands);
            case "-":
                return subtract(operands);
            case "*":
                return multiply(operands);
            case "/":
                return divide(operands);
        }
        return 0;
    }

    private float evaluateElement(Object expression) throws Exception {
        if (expression instanceof String) {
            return Float.parseFloat((String) expression);
        } else {
            return evaluate((ArrayList<Object>) expression);
        }
    }

    public float evaluate(String expression) {
        return Integer.parseInt(expression);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Expression parser, exit with: exit || quit");
        System.out.println("Valid Expressions examples:");
        System.out.println("\t (+ 10 10 10)");
        System.out.println("\t (+ 10 (* 5 2) (- 8 3) (/ 20 4))");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String exp;
        Parser p = new Parser();
        while (true) {
            exp = br.readLine();
            if (exp.equals("exit") || exp.equals("quit"))
                break;
            else {
                try {
                    ArrayList<Object> parsed = p.parseExpression(exp);
                    float result = p.evaluate(parsed);
                    System.out.println("Result: " + result);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
