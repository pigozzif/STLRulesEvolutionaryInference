package Expressions.MonitorExpressions;

import BuildingBlocks.STLFormulaMapper;
import BuildingBlocks.TreeNode;
import Expressions.ValueExpressions.CompareSign;
import Expressions.ValueExpressions.Digit;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.List;
import java.util.stream.Collectors;


public class NumericalVariable implements MonitorExpression {

    private final String string;

    public NumericalVariable(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    public TreeNode createMonitor(List<Tree<String>> siblings, List<Tree<String>> ancestors, Tree<String> root) {
        CompareSign firstSibling = (CompareSign) STLFormulaMapper.fromStringToValueExpression(siblings.get(0).childStream().collect(Collectors.toList()).get(0)).get();
        double number = parseNumber(siblings.get(1).childStream().collect(Collectors.toList()));
        TreeNode newNode = new TreeNode(root.toString());
        newNode.setSymbol(this.string + " " + firstSibling.toString() + " " + number);
        newNode.setOperator(x -> TemporalMonitor.atomicMonitor(y -> firstSibling.getValue().apply(y.getDouble(this.string),
                number)));
        return newNode;
    }

    private double parseNumber(List<Tree<String>> leaves) {
        double number = 0.0;
        for (int i=0; i < leaves.size(); ++i) {
            Digit temp = (Digit) STLFormulaMapper.fromStringToValueExpression(leaves.get(i)).get();
            number += temp.getValue() * Math.pow(10, - (i + 1));
        }
        return number;
    }

}
