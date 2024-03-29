package com.edgefintrack.expense.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.edgefintrack.expense.R;

import java.text.DecimalFormat;
import java.util.LinkedList;

public class CalculatorActivity extends AppCompatActivity {
    public static final String ADD = "\u002B";
    public static final String SUB = "\u2212";
    public static final String DIV = "\u00F7";
    public static final String MUL = "\u2715";
    public String value = "";
    public LinkedList<String> operators = new LinkedList<String>();

    private String return_value = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
    }

    // Event handlers must be public, void, and have a view object as their only parameter!
    public void registerKey(View view) {
        switch (view.getId()) {
            case R.id.button0:
                safelyPlaceOperand("0");
                break;
            case R.id.button1:
                safelyPlaceOperand("1");
                break;
            case R.id.button2:
                safelyPlaceOperand("2");
                break;
            case R.id.button3:
                safelyPlaceOperand("3");
                break;
            case R.id.button4:
                safelyPlaceOperand("4");
                break;
            case R.id.button5:
                safelyPlaceOperand("5");
                break;
            case R.id.button6:
                safelyPlaceOperand("6");
                break;
            case R.id.button7:
                safelyPlaceOperand("7");
                break;
            case R.id.button8:
                safelyPlaceOperand("8");
                break;
            case R.id.button9:
                safelyPlaceOperand("9");
                break;
            case R.id.buttonAdd:
                safelyPlaceOperator(ADD);
                break;
            case R.id.buttonSub:
                safelyPlaceOperator(SUB);
                break;
            case R.id.buttonDiv:
                safelyPlaceOperator(DIV);
                break;
            case R.id.buttonMul:
                safelyPlaceOperator(MUL);
                break;
            case R.id.buttonDel:
                deleteFromLeft();
                break;
        }
        display();
    }

    private void display() {
        TextView tvAns = (TextView) findViewById(R.id.textViewAns);
        tvAns.setText(value);
    }

    private void display(String s) {
        return_value = s;
        TextView tvAns = (TextView) findViewById(R.id.textViewAns);
        tvAns.setText(s);
    }

    private void safelyPlaceOperand(String op) {
        int operator_idx = findLastOperator();
        // Avoid double zeroes in cases where it's illegal (e.g. 00 -> 0, 01 -> 1, 1+01 -> 1+1).
        if (operator_idx != value.length() - 1 && value.charAt(operator_idx + 1) == '0')
            deleteTrailingZero();
        value += op;
    }

    private void safelyPlaceOperator(String op) {
        if (endsWithOperator())  // Avoid double operators by replacing operator.
        {
            deleteFromLeft();
            value += op;
            operators.add(op);
        } else if (endsWithNumber())  // Safe to place operator right away.
        {
            value += op;
            operators.add(op);
        }
        // else: Operator by itself is an illegal operation, do not place operator.
    }

    private void deleteTrailingZero() {
        if (value.endsWith("0")) deleteFromLeft();
    }

    private void deleteFromLeft() {
        if (value.length() > 0) {
            if (endsWithOperator()) operators.removeLast();
            value = value.substring(0, value.length() - 1);
        }
    }

    private boolean endsWithNumber() {
        // If we had a decimal point button, we'd have to grab the digit before it.
        return value.length() > 0 && Character.isDigit(value.charAt(value.length() - 1));
    }

    private boolean endsWithOperator() {
        if (value.endsWith(ADD) || value.endsWith(SUB) || value.endsWith(MUL) || value.endsWith(DIV))
            return true;
        else return false;
    }

    private int findLastOperator() {
        int add_idx = value.lastIndexOf(ADD);
        int sub_idx = value.lastIndexOf(SUB);
        int mul_idx = value.lastIndexOf(MUL);
        int div_idx = value.lastIndexOf(DIV);
        return Math.max(add_idx, Math.max(sub_idx, Math.max(mul_idx, div_idx)));
    }

    public void calculate(View view)  // Note: only called by the '=' button.
    {
        // StringTokenizer is deprecated. Using String.split instead.
        String[] operands;
        int i;
        double ans;
        DecimalFormat df = new DecimalFormat("0.###");
        switch (view.getId()) {
            case R.id.textViewDone:

                if (operators.isEmpty()) {
                    Intent setData = new Intent();
                    setData.putExtra("data", return_value);
                    setResult(RESULT_OK, setData);
                    finish();
                    return;  // There is no operation to perform yet.
                }
                if (endsWithOperator()) {
                    display("incorrect format");
                    resetCalculator();
                    return;
                }
                operands = value.split("\\u002B|\\u2212|\\u00F7|\\u2715");
                i = 0;
                ans = Double.parseDouble(operands[i]); // TODO: catch NumberFormatException?
                for (String operator : operators)
                    ans = applyOperation(operator, ans, Double.parseDouble(operands[++i]));

                display(df.format(ans));

                Intent setData = new Intent();
                setData.putExtra("data", df.format(ans));
                setResult(RESULT_OK, setData);
                finish();

                resetCalculator();
                break;

            case R.id.buttonEql:

                if (operators.isEmpty()) return;  // There is no operation to perform yet.
                if (endsWithOperator()) {
                    display("incorrect format");
                    resetCalculator();
                    return;
                }

                // StringTokenizer is deprecated. Using String.split instead.
                operands = value.split("\\u002B|\\u2212|\\u00F7|\\u2715");

                i = 0;
                ans = Double.parseDouble(operands[i]); // TODO: catch NumberFormatException?
                for (String operator : operators)
                    ans = applyOperation(operator, ans, Double.parseDouble(operands[++i]));
                display(df.format(ans));
                resetCalculator();
                // TODO: overwrite value with ans. reset value on next keypress if it's an operand (leave it if it's an operator)

                break;
        }
    }

    private double applyOperation(String operator, double operand1, double operand2) {
        // Not using the string with switch syntax because Android doesn't support JDK 7 (JRE 1.7) yet.
        if (operator.equals(ADD)) return operand1 + operand2;
        if (operator.equals(SUB)) return operand1 - operand2;
        if (operator.equals(MUL)) return operand1 * operand2;
        if (operator.equals(DIV))
            return operand1 / operand2;  // Don't need to check for div by 0, Java already does.
        return 0.0;
    }

    private void resetCalculator() {
        value = "";
        operators.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
    }

    public void onCancel(View view) {
        Intent setData = new Intent();
        setData.putExtra("data", return_value);
        setResult(RESULT_OK, setData);
        finish();
    }
}
