package BuildingBlocks;

import Expressions.*;
import Expressions.MonitorExpressions.MonitorExpression;
import Expressions.ValueExpressions.ValueExpression;
import it.units.malelab.jgea.core.Node;
import it.units.malelab.jgea.core.function.Function;
import it.units.malelab.jgea.core.listener.Listener;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;

import java.util.List;
import java.util.Optional;


public class STLFormulaMapper implements Function<Node<String>, TemporalMonitor<TrajectoryRecord, Double>> {

    private static final List<ValueExpression<?>> valueExpressions = ExpressionsFactory.createValueExpressions();
    private static final List<MonitorExpression> monitorExpressions = ExpressionsFactory.createMonitorExpressions();

    @Override
    public TemporalMonitor<TrajectoryRecord, Double> apply(Node<String> root, Listener listener) {
        root.propagateParentship();
        return parseSubTree(root);
    }

    public static TemporalMonitor<TrajectoryRecord, Double> parseSubTree(Node<String> currentNode) {
        List<Node<String>> children = currentNode.getChildren();
        try {
            Node<String> testChild = children.get(0);
            Optional<MonitorExpression> expression = fromStringToMonitorExpression(testChild);
            if (expression.isPresent()) {
                return expression.get().createMonitor(getSiblings(testChild));
            }
            return parseSubTree(testChild);
        }
        catch (Exception e) {
            System.out.println(currentNode.getContent() + " and children: " + currentNode.getChildren());
            throw e;
        }
    }

    public static Optional<MonitorExpression> fromStringToMonitorExpression(Node<String> string) {
        if (Expression.singletonExpressions.contains(string.getContent())) {
            return fromStringToMonitorExpression(string.getChildren().get(0));
        }
        return monitorExpressions.stream().filter(x -> x.toString().equals(string.getContent())).findAny();
    }

    public static Optional<ValueExpression<?>> fromStringToValueExpression(Node<String> string) {
        if (Expression.singletonExpressions.contains(string.getContent())) {
            return fromStringToValueExpression(string.getChildren().get(0));
        }
        return valueExpressions.stream().filter(x -> x.toString().equals(string.getContent())).findAny();
    }

    private static List<Node<String>> getSiblings(Node<String> node) {
        Node<String> parent = node.getParent();
        List<Node<String>> res = parent.getChildren();
        res.remove(node);
        return res;
    }

}
